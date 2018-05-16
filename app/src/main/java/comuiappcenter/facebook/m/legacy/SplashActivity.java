package comuiappcenter.facebook.m.legacy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

import comuiappcenter.facebook.m.legacy.User.LoginActivity;
import comuiappcenter.facebook.m.legacy.User.userInfo;
import comuiappcenter.facebook.m.legacy.customView.MainRecyclerAdapter;
import comuiappcenter.facebook.m.legacy.customView.QuestionPreview;
import comuiappcenter.facebook.m.legacy.dataContainer.Device;
import cz.msebera.android.httpclient.Header;

//test 라는 패스로 get 요청을 해서 서버 접속 여부를 확인합니다.

public class SplashActivity extends AppCompatActivity implements View.OnClickListener
{
    ImageView imageView;
    TextView textView;
    TextView bottomText;
    int randInt = 0;
    RestClient client;
    public static final String PREFS_NAME = "UserInfo";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        textView = (TextView) findViewById(R.id.text_splash);
        imageView = (ImageView) findViewById(R.id.imageView_splash);
        bottomText =  (TextView) findViewById(R.id.textView_splash_bottom);

        //스플래시 이미지 형성
        Random random = new Random();
        randInt = random.nextInt(4); // 0~4 까지 난수 생성.
        setImgages();
        textView.setVisibility(View.INVISIBLE);

        //서버 연결 확인
        client = new RestClient(this); // RestClient 객체 생성.
        client.get("/test", null, new TextHttpResponseHandler()
        {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable)
                    {
                        Toast.makeText(getApplicationContext(), "서버 접속 실패"+client.BASE_URL, Toast.LENGTH_SHORT).show();
                        bottomText.setText("서버접속 실패"+client.BASE_URL);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString)
                    {
                        Toast.makeText(getApplicationContext(), responseString, Toast.LENGTH_SHORT).show();
                        bottomText.setText("서버접속 성공"+client.BASE_URL);
                        userInfo.isOnline = true;
                    }
        });

        //FCM 구독
        FirebaseInstanceId.getInstance().getToken();
        FirebaseMessaging.getInstance().subscribeToTopic("news");

        //핸드폰 내에 저장된 로그인 정보가 있는지 확인합니다. 만약 없으면 LoginActivity로 intent 됩니다.
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        userInfo.StudentID = settings.getString("StudentID", null);
        userInfo.Password = settings.getString("Password", null);
        userInfo.NickName = settings.getString("NickName", null);
        if(userInfo.StudentID == null)
        {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        else if (userInfo.StudentID != null) // 만약 학번이 존재하면 로그인을 시도합니다.
        {
            Toast.makeText(getApplicationContext(), userInfo.StudentID+"와"+"\n 비밀번호:"+userInfo.Password+"로 로그인을 시도합니다.", Toast.LENGTH_SHORT).show();
            RequestParams params = new RequestParams();
            params.put("StudentID", userInfo.StudentID);
            params.put("Password", userInfo.Password);
            client.post("/login", params, new TextHttpResponseHandler()
            {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable)
                {
                    Toast.makeText(SplashActivity.this, "서버 연결 실패", Toast.LENGTH_SHORT).show();
                    //만약 등록되지 않은 사용자라면 WelcomeActivity로 인텐트(서버에서 등록된 사용자인지 아닌지 알려주게 해야겠네)
                    Log.d("kimchi_splash", "응답 실패");
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString)
                {
                    Log.d("kimchi_splash", "onSuccess 호출"+responseString);
                    if(responseString.contains("성공"))
                    {
                        Toast.makeText(SplashActivity.this, "환영합니다.", Toast.LENGTH_SHORT).show();
                        requestAccountInfo(); //계정 정보를 요청합니다.
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                    else
                    {
                        Toast.makeText(SplashActivity.this, "아이디 혹은 패스워드를 다시 확인해 주세요", Toast.LENGTH_SHORT).show();
                        Log.d("kimchi", responseString);
                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                }
            });
        }

        //저장된 사용자 정보를 불러옵니다.
        loadUserInfo(settings);

    }

    @Override
    public void onClick(View v)
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void setImgages()
    {
        Resources res = getResources();
        String[] quotes = res.getStringArray(R.array.splash_quotes);
        // 문자열 배열을 가져오려면 Resources 객체도 필요하고, 참조할때 array로 시작해야 됨 string으로 시작하면 안됨.

        switch(randInt)
        {
            case 0: imageView.setImageResource(R.drawable.splash_01);  break;
            case 1: imageView.setImageResource(R.drawable.splash_02);  break;
            case 2: imageView.setImageResource(R.drawable.splash_03);  break;
            case 3: imageView.setImageResource(R.drawable.splash_04);  break;
            case 4: imageView.setImageResource(R.drawable.cozy_room4);  break;
        }
    }

    public void loadUserInfo(SharedPreferences settings)
    {
        if(settings.getStringSet("InterestedClass", null) != null)
        {
            userInfo.InterestedClass.clear();
            userInfo.InterestedClass.addAll(settings.getStringSet("InterestedClass", null));

            /*for(int i =0; i<userInfo.InterestedClass.size(); i++)
            {
                Log.d("kimchi_retrive", userInfo.InterestedClass.get(i).toString());
            }*/
        }
    }

    public void requestAccountInfo()
    {
        Log.d("kimchi", "requestAccountInfo 호출됨");

        RequestParams params = new RequestParams();
        params.put("StudentID", userInfo.StudentID);

        client.post("/request_account_info", params, new JsonHttpResponseHandler()
        {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse)
            {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.d("kimchi_splash", "서버에서 계정 정보를 불러오는데 실패했습니다.");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) //서버 통신에 성공하면 내 질문들을 불러와요.
            {
                for (int i = 0; i< response.length(); i++)
                {
                    try
                    {
                        JSONObject result = response.getJSONObject(i);
                        userInfo.point = result.getInt("point"); // point 정보를 서버에서 가져옵니다.
                        Log.d("kimchi_splash", "서버에서 가져온 point 정보 "+Integer.toString(userInfo.point)+"점");
                    }catch(JSONException e) {e.printStackTrace();}
                }
                super.onSuccess(statusCode, headers, response);
            }
        });


    }

}
