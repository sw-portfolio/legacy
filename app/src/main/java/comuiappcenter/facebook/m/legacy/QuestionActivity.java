package comuiappcenter.facebook.m.legacy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import comuiappcenter.facebook.m.legacy.User.userInfo;
import comuiappcenter.facebook.m.legacy.customView.defaultTextBox;
import comuiappcenter.facebook.m.legacy.dataContainer.INUClasses;
import cz.msebera.android.httpclient.Header;

/* 질문을 서버에 등록하는 액티비티입니다. 질문의 key 이름은 제목 -> QuestionTitle 본문 -> QuestionBody 입니다.
* */

public class QuestionActivity extends AppCompatActivity
{
    ImageView acceptButton;
    Button CategoryButton;
    RestClient client;
    String Category = null;
    public static boolean isEmergency;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        acceptButton = (ImageView) findViewById(R.id.accept_button);
        acceptButton.setOnClickListener(new ButtonListener());

        //카테고리 버튼 설정
        CategoryButton = (Button) findViewById(R.id.button_category);
        CategoryButton.setOnClickListener(new CategoryButtonListener());
    }

    class ButtonListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            //파이어베이스 관련 설정
            String token = FirebaseInstanceId.getInstance().getToken();
            FirebaseMessaging.getInstance().subscribeToTopic("news"); // 이 코드 하나만으로 news에 대해 구독이 되나보네
            Log.d("Refreshed token:", token);

            defaultTextBox titleBox = (defaultTextBox) findViewById(R.id.title_box);
            defaultTextBox bodyBox = (defaultTextBox) findViewById(R.id.body_box);

            if(titleBox != null) // 제목과 질문 내용을 변수에 담아 서버에 전송.
            {
                String title = titleBox.mEditText.getText().toString();
                String body = bodyBox.mEditText.getText().toString();

                //서버 전송 전에 파라미터가 비어 있는지 체크
                if(SafetyGate(title, body, Category)) {return;}

                //파라미터 안에 내용을 담습니다.
                RequestParams params = new RequestParams();
                params.put("QuestionTitle", title);
                params.put("QuestionBody", body);
                params.put("UserStudentID", userInfo.StudentID);
                params.put("UserNickName", userInfo.NickName);
                params.put("Category", Category);
                isEmergency = true;
                params.put("IsEmergency", isEmergency);
                params.put("PushCategory", INUClasses.generateCodePosition(Category));

                //질문을 서버로 전송
                client.post("/new_question", params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody)
                    {
                        Toast.makeText(QuestionActivity.this, "질문 등록 성공!", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(QuestionActivity.this, MainActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error)
                    {
                        Toast.makeText(QuestionActivity.this, "질문 등록 실패", Toast.LENGTH_LONG).show();
                    }
                });
            }
            else {  Toast.makeText(v.getContext(), "객체가 없습니다.", Toast.LENGTH_LONG).show();}
        }

        public boolean SafetyGate(String string, String string2, String category)
        {
            if(string.isEmpty() == true || string2.isEmpty() == true)
            {
                Toast.makeText(QuestionActivity.this, "제목과 본문은 비어 있으면 안되요", Toast.LENGTH_SHORT).show();
                return true; // 야! 이거 비었어! (혹은 카테고리가 없어)
            }

            if(category.contains("카테고리"))
            {
                Toast.makeText(QuestionActivity.this, "카테고리를 지정해 주세요", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        }


    }

    class CategoryButtonListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            PopupMenu popup = new PopupMenu(QuestionActivity.this, v);

            for(int i =0; i < userInfo.InterestedClass.size(); i++)
            {
                if(userInfo.InterestedClass.get(i) !="비어 있음")
                {
                    if(userInfo.InterestedClass.get(i) ==null) break;
                    String InterestedClass = userInfo.InterestedClass.get(i);
                    popup.getMenu().add(InterestedClass);
                }
            }
            //인플레이트 하기 전에, userInfo에서 관심 카테고리 목록을 불러와서 표시해 줘야 함
            //선택된 Item이 무엇인지도 서버에 보내줘야 함.
            //관심 수업 이외에도 사용자가 검색해서 카테고리를 선택할 수 있도록 검색화면으로 intent되는 옵션도 있어야 함

            popup.getMenuInflater().inflate(R.menu.popup_menu_category, popup.getMenu());// popup 메뉴를 inflate

            popup.setOnMenuItemClickListener(
                    new PopupMenu.OnMenuItemClickListener() // popup메뉴에도 리스너를 등록시켜 준다.
                    {
                        @Override
                        public boolean onMenuItemClick(MenuItem item)
                        {
                            CategoryButton.setText(item.getTitle().toString());
                            Category = item.getTitle().toString();
                            return true;
                        }
                    });
            popup.show(); // show가 없으면 메뉴가 안 나타남. 주의할 것.

        }
    }

}
