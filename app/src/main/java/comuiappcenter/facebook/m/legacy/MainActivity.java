package comuiappcenter.facebook.m.legacy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.plattysoft.leonids.ParticleSystem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

import comuiappcenter.facebook.m.legacy.User.InterestedClassSettingActivity;
import comuiappcenter.facebook.m.legacy.User.LoginActivity;
import comuiappcenter.facebook.m.legacy.User.userInfo;
import comuiappcenter.facebook.m.legacy.customView.MainRecyclerAdapter;
import comuiappcenter.facebook.m.legacy.customView.MyStatus;
import comuiappcenter.facebook.m.legacy.customView.QuestionPreview;
import comuiappcenter.facebook.m.legacy.dataContainer.MyData;
import cz.msebera.android.httpclient.Header;

import static comuiappcenter.facebook.m.legacy.User.userInfo.InterestedClass;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
{

    LinearLayout questionsWaitingMeLayout;
    RestClient client;
    TextView NavNickName;
    View HeaderLayout;
    TextView moreWaitingQuestions;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    static ArrayList<MyData> MyDataset;
    MyData myQuestionData;

    private long backPressedTime = 0;

    @Override
    protected void onCreate(final Bundle savedInstanceState) // 서버 통신으로 질문을 받아오고 View를 구현합니다.
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        client = new RestClient(this);
        //프리퍼런스를 가져옵니다.
        SharedPreferences settings = getSharedPreferences(SplashActivity.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();



        //floating 버튼을 구현합니다. 질문하기 버튼입니다.
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, QuestionActivity.class);
                startActivity(intent);
            }
        });

        //NavigationDrawer를 구현합니다.
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        HeaderLayout = navigationView.getHeaderView(0);
        NavNickName = (TextView) HeaderLayout.findViewById(R.id.nav_nick_name);
        if(userInfo.NickName != null) NavNickName.setText(sayHi()+" "+userInfo.NickName+"님");
        HeaderLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(MainActivity.this, userInfo.class);
                startActivity(intent);
            }
        });

        //RecyclerView와 CardView를 구현합니다.
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_my_questions);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        MyDataset = new ArrayList<>();
        mAdapter = new MainRecyclerAdapter(this, MyDataset);
        mRecyclerView.setAdapter(mAdapter);

        //내 정보를 구현합니다. (ListView 1번째)
        MyDataset.add(new MyData("내 정보", new ArrayList<QuestionPreview>()));
        RequestParams paramsUser = new RequestParams();
        paramsUser.put("StudentID", userInfo.StudentID);
        client.post("/request_account_info", paramsUser, new JsonHttpResponseHandler()
        {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse)
                    {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        Toast.makeText(MainActivity.this, "내 정보를 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) //서버 통신에 성공하면 내 질문들을 불러와요.
                    {
                        Toast.makeText(MainActivity.this, "내 정보 불러옴", Toast.LENGTH_SHORT).show();
                        for(int i = 0; i<response.length(); i++)
                        {
                            try
                            {
                                JSONObject result = response.getJSONObject(i);
                                int point = result.getInt("point");
                                MyStatus myStatus = new MyStatus(MainActivity.this);
                                myStatus.point.append(Integer.toString(point));
                                MainRecyclerAdapter.ViewHolder Viewholder = (MainRecyclerAdapter.ViewHolder) mRecyclerView.findViewHolderForLayoutPosition(0);
                                Viewholder.mLinearLayout.addView(myStatus);

                            }catch(JSONException e) {e.printStackTrace();}
                        }
                        super.onSuccess(statusCode, headers, response);
                    }
        });



        //내가한 질문들을 구현합니다. (서버에서 불러옵니다.)
        MyDataset.add(new MyData("내 질문들", new ArrayList<QuestionPreview>()));
        //서버와의 통신
        RequestParams params = new RequestParams();
        params.put("StudentID", userInfo.StudentID);
        client.post("/my_question", params, new JsonHttpResponseHandler()
        {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse)
            {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(MainActivity.this, "내 질문을 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) //서버 통신에 성공하면 내 질문들을 불러와요.
            {
                Toast.makeText(MainActivity.this, "검색 결과"+response.length()+"개", Toast.LENGTH_SHORT).show();
                for (int i = 0; i< 4; i++) // 내 질문은 4개 까지만 불러오자.
                {
                    try
                    {
                        JSONObject result = response.getJSONObject(i);
                        String title = result.getString("title");
                        String body = result.getString("body");
                        String category = result.getString("category");
                        //서버에서 가져온 정보를 바탕으로 QuestionPreview 뷰를 만듭니다.
                        QuestionPreview question = new QuestionPreview(MainActivity.this);
                        question.title.setText(title);
                        question.body.setText(body);
                        question.categoryTextView.setText(category);
                        //preview를 cardview 안에 넣습니다.
                        MainRecyclerAdapter.ViewHolder Viewholder = (MainRecyclerAdapter.ViewHolder) mRecyclerView.findViewHolderForLayoutPosition(1);
                        Viewholder.mLinearLayout.addView(question);
                    }catch(JSONException e) {e.printStackTrace();}
                }
                super.onSuccess(statusCode, headers, response);
            }
        });


        //내 답변을 기다리는 질문들을 구현합니다. (서버에서 불러옵니다.)
        MyDataset.add(new MyData("나의 답변을 기다리는 질문들", new ArrayList<QuestionPreview>()));
        RequestParams params2 = new RequestParams();
        if(InterestedClass.size() != 0)
        {
            String ClassSent = userInfo.randInterest();
            params2.put("Interest", ClassSent); // 랜덤한 관심수업을 전송합니다.
        }
        client.post("/waiting_question", params2, new JsonHttpResponseHandler(){
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse)
            {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(MainActivity.this, "답변을 기다리는 질문을 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) //서버 통신에 성공하면 내 질문들을 불러와요.
            {
                Toast.makeText(MainActivity.this, "날 기다리는 질문"+response.length()+"개", Toast.LENGTH_SHORT).show();
                for (int i = 0; i< response.length(); i++)
                {
                    try
                    {
                        JSONObject result = response.getJSONObject(i);
                        String title = result.getString("title");
                        String body = result.getString("body");
                        String category = result.getString("category");
                        final String QuestionID = result.getString("_id"); // 이런식으로 id를 가져올 수 있나? string이 아니던데
                        QuestionPreview question = new QuestionPreview(MainActivity.this);
                        question.title.setText(title);
                        question.body.setText(body);
                        question.categoryTextView.setText(category);
                        question.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                Intent intent = new Intent(MainActivity.this, QuestionViewActivity.class);
                                intent.putExtra("QuestionWas", QuestionID);
                                startActivity(intent);
                                // 나중에 margin도 설정해야 할듯.
                            }
                        });
                        //preview를 cardview 안에 넣습니다.
                        MainRecyclerAdapter.ViewHolder Viewholder = (MainRecyclerAdapter.ViewHolder) mRecyclerView.findViewHolderForLayoutPosition(2);
                        Viewholder.mLinearLayout.addView(question);

                    }catch(JSONException e) {e.printStackTrace();}
                }
                super.onSuccess(statusCode, headers, response);
            }
        });


        Menu menuNav = navigationView.getMenu();
        for(int i = 0; i< InterestedClass.size(); i++)
        {
            if(InterestedClass.get(i) != null)
            {
                menuNav.add(R.id.interested_class_menu, Menu.NONE, Menu.NONE, InterestedClass.get(i));
            } //일단 item id는
        }

        //if(settings.getBoolean("isNewbie", false) == true)
        //{
        makeParticle(fab);
        Toast.makeText(this, "질문하기 버튼으로 질문해보세요", Toast.LENGTH_LONG).show();
        //editor.putBoolean("isNewbie", false).commit(); // 이제부터 넌 뉴비가 아니야
        //}
    }

    @Override
    public void onBackPressed() // Back키를 눌렀을때 만약 drawer가 열려 있으면 drawer를 닫습니다.
    {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else
        {
            long t = System.currentTimeMillis();
            if (t - backPressedTime > 2000)
            {
                backPressedTime = t;
                Toast.makeText(this, "종료하시려면 back키를 한 번 더 눌러주세요.",
                        Toast.LENGTH_SHORT).show();
            }
            else // 진짜 종료합니다.
            {
                this.finishAffinity();
                //super.onBackPressed();
            }
        }
    }

/*    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id != R.id.nav_interestd_class_setting && id != R.id.nav_setting && id != R.id.nav_logout) // 동적으로 생성된 아이템이 아니라면
        {
            Intent intent = new Intent(MainActivity.this, EclassActivity.class);
            intent.putExtra("ClassName",item.getTitle());
            startActivity(intent);
        }

        //동적으로 변하지 않는 Navigation Menu
        if (id == R.id.nav_interestd_class_setting)
        {
            Intent intent = new Intent(this, InterestedClassSettingActivity.class);
            startActivity(intent);
        }

        else if (id == R.id.nav_setting)
        {
            Intent intent = new Intent(this, OptionsActivity.class);
            startActivity(intent);
        }

        if (id == R.id.nav_logout)
        {
            //서버에 logout요청을 한 뒤 성공하면 preference를 모두 지우고, login 화면으로 이동합니다.
            client = new RestClient(MainActivity.this);
            client.get("/logout", null, new TextHttpResponseHandler()
            {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable)
                {
                    Toast.makeText(MainActivity.this, "서버에 요청 실패", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString)
                {
                    MainActivity.this.getSharedPreferences(SplashActivity.PREFS_NAME, 0).edit().clear().commit();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            });
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public String sayHi()
    {
        String[] greeting = new String[5];
        int rand = new Random().nextInt(5);
        greeting[0] = "환영합니다";
        greeting[1] = "오늘 하루도 화이팅!";
        greeting[2] = "잠깐 쉬고 공부해도 괜찮아요";
        greeting[3] = "힘차게 공부해 볼까요? :)";
        greeting[4] = "맛있는 거 먹고 공부 시작할까요?";

        return greeting[rand];
    }

    public void makeParticle(View view)
    {
        //2017-02-23 파티클이 버튼에서 안 터지고 화면 왼쪽 상단 엉뚱한 위치에서 터지는 데 어떠헥 고치는지 모르겄다 ^^
        new ParticleSystem(this, 100, R.drawable.yellow_particle, 800)
                .setSpeedRange(0.1f, 0.25f)
                .oneShot(view, 100);
    }

}
