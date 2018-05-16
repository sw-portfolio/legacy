package comuiappcenter.facebook.m.legacy.User;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import comuiappcenter.facebook.m.legacy.MainActivity;
import comuiappcenter.facebook.m.legacy.R;
import comuiappcenter.facebook.m.legacy.RestClient;
import cz.msebera.android.httpclient.Header;

import static comuiappcenter.facebook.m.legacy.SplashActivity.PREFS_NAME;

public class WelcomeActivity extends AppCompatActivity
{
    RestClient client;
    EditText IDEditText;
    EditText PWEditText;
    EditText nickNameEditText;
    EditText majorEditText;
    Button AcceptButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        client = new RestClient(this);

        //View를 선언합니다.
        IDEditText = (EditText) findViewById(R.id.edittext_welcome_id);
        PWEditText = (EditText) findViewById(R.id.edittext_welcome_pw);
        nickNameEditText  = (EditText) findViewById(R.id.edittext_welcome_nickname);
        majorEditText  = (EditText) findViewById(R.id.edittext_welcome_major);
        AcceptButton = (Button) findViewById(R.id.button_welcom_accept);

        //확인 버튼 구현
        AcceptButton.setOnClickListener(new register());
    }

    class register implements View.OnClickListener
    {
        String ID;
        String PW;
        String nickname;
        String major;


        @Override
        public void onClick(View v)
        {
            Log.d("kimchi", "onClick호출");
            ID = IDEditText.getText().toString();
            PW = PWEditText.getText().toString();
            nickname = nickNameEditText.getText().toString();
            major = majorEditText.getText().toString();
            boolean check = isNull(v.getContext());
            if(check== true) {return;}

            RequestParams params = new RequestParams();
            params.put("StudentID", ID);
            params.put("Password", PW);
            params.put("NickName", nickname);
            params.put("Major", major);

            client.post("/register", params, new TextHttpResponseHandler()
            {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable)
                {
                    Toast.makeText(getApplicationContext(), "서버 접속 실패", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString)
                {
                    Toast.makeText(getApplicationContext(),"회원가입 성공!", Toast.LENGTH_SHORT).show();
                    SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("StudentID", ID);
                    userInfo.StudentID = ID;

                    editor.putString("Password", PW);
                    userInfo.Password =PW;

                    editor.putString("NickName", nickname);
                    userInfo.NickName = nickname;

                    editor.putString("Major", major);
                    userInfo.Major = major;

                    editor.putBoolean("isNewbie", true);

                    editor.commit();

                    Intent intent = new Intent(WelcomeActivity.this, InterestedClassSettingActivity.class);
                    startActivity(intent);
                    Toast.makeText(WelcomeActivity.this, "관심있는 수업을 등록해주세요", Toast.LENGTH_LONG).show();
                    Toast.makeText(WelcomeActivity.this, "Back 키를 누르면 저장 후 메인화면으로 이동합니다.", Toast.LENGTH_LONG).show();
                }
            });
        }

        public boolean isNull(Context context)
        {
            if(ID.isEmpty()) {
            Toast.makeText(context, "학번을 입력해주세요", Toast.LENGTH_SHORT ).show(); return true;}
            if(!isNumeric(ID)) {
                Toast.makeText(context, "숫자로 된 학번을 입력해주세요", Toast.LENGTH_SHORT ).show(); return true;}
            if(ID.length()<9 || ID.length()>9) {
                Toast.makeText(context, "학번은 9자리 입니다.", Toast.LENGTH_SHORT ).show(); return true;}
            if(PW.isEmpty()) {
                Toast.makeText(context, "비밀번호를 입력해주세요", Toast.LENGTH_SHORT ).show(); return true;}
            if(nickname.isEmpty()) {
                Toast.makeText(context, "닉네임을 입력해주세요", Toast.LENGTH_SHORT ).show(); return true;}
            if(major.isEmpty()) {
                Toast.makeText(context, "전공을 입력해주세요", Toast.LENGTH_SHORT ).show(); return true;}

            return false;
        }

        public boolean isNumeric(String str)
        {
            try
            {
                double d = Double.parseDouble(str);
            }
            catch(NumberFormatException nfe)
            {
                return false;
            }
            return true;
        }

    }
}
