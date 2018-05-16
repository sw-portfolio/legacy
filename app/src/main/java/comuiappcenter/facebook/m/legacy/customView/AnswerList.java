package comuiappcenter.facebook.m.legacy.customView;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.plattysoft.leonids.ParticleSystem;

import org.w3c.dom.Text;

import comuiappcenter.facebook.m.legacy.QuestionViewActivity;
import comuiappcenter.facebook.m.legacy.R;
import comuiappcenter.facebook.m.legacy.RestClient;
import comuiappcenter.facebook.m.legacy.User.userInfo;
import cz.msebera.android.httpclient.Header;

/**
 * Created by Administrator on 2017-01-19.
 */
public class AnswerList extends ArrayAdapter<String > implements AdapterView.OnItemClickListener
{
    //2016-12-11 완전히 독립적인 CustomList 생성 성공!

    String[] titles;
    String[] bodies;
    String[] authors;
    Context mcontext;
    TextView title;
    TextView body;
    TextView author;
    ImageView thumbsUp;
    ImageView chooseImage;
    RestClient client;

    public AnswerList(Context context, int resId, String[] items, String[] body, String[] author)
    {
        super(context, resId, items);
        mcontext = context;
        titles = items; // InterestedClass 들이 들어올 거임.
        bodies = body;
        authors = author;
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent)
    {
        //if (titles[position] == null) {position == titles.length;}
        View v = convertView;
        if(v == null) // View가 없다면 만들어야된다.
        {
            LayoutInflater vi= LayoutInflater.from(getContext());
            //무조건 LayoutInflater 를 엄마 액티비티로 부터 데려와야 됨
            // 수정하지 말아라 이거 고치느라 엄청 애먹음 ㅋㅋ
            v = vi.inflate(R.layout.list_answer, null);
            //convertView = View.inflate(mcontext, R.layout.list_item, parent);
        }

        title = (TextView) v.findViewById(R.id.title_answer_list);
        body = (TextView) v.findViewById(R.id.body_answer_list);
        author = (TextView) v.findViewById(R.id.author_answer_list);
        thumbsUp = (ImageView) v.findViewById(R.id.answer_list_thumbs_up_button);
        chooseImage = (ImageView) v.findViewById(R.id.answer_list_choose);
        chooseImage.setVisibility(View.GONE);

        title.setText(titles[position]);
        body.setText(bodies[position]);
        author.setText("작성자: "+authors[position]);

        //추천 버튼 구현
        thumbsUp.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(mcontext.getApplicationContext(), "춫천", Toast.LENGTH_LONG).show();
            }
        });

        //채택 버튼 구현 (내가 질문자일때만)
        if(QuestionViewActivity.QuestionAuthor == userInfo.StudentID)
        {
            chooseImage.setVisibility(View.VISIBLE);
            chooseImage.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    client = new RestClient(mcontext);
                    RequestParams params = new RequestParams();
                    params.put("QuestionID", QuestionViewActivity.QuestionID); // 질문 ID 와 답변 ID를 서버에 보냅니다.
                    //params.put("AnswerID", QuestionViewActivity.);

                    client.post("/choose_question", params, new TextHttpResponseHandler()
                    {
                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable)
                        {
                            Toast.makeText(mcontext, "채택 실패", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, String responseString)
                        {
                            Toast.makeText(mcontext, "채택 되었습니다", Toast.LENGTH_SHORT).show();
                            notifyDataSetInvalidated(); // 이거 맞나?
                        }
                    });

                }
            });

        }

        return v;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        Toast.makeText(mcontext.getApplicationContext(),
                titles[position],
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getCount()
    {
        int count=0;
        for(int i=0; i < titles.length; i++)
        {
            if(titles[i] != null)
            {
                ++count;
            }
        }
        return count;
    }
}
