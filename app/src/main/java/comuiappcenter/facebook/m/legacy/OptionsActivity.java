package comuiappcenter.facebook.m.legacy;

import android.content.Intent;
import android.net.Uri;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import comuiappcenter.facebook.m.legacy.User.userInfo;
import comuiappcenter.facebook.m.legacy.customView.QuestionPreview;

public class OptionsActivity extends AppCompatActivity
{


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        getFragmentManager().beginTransaction() // 설정 화면을 가져옵니다.
                .replace(android.R.id.content, new SettingsFragment())
                .commit();


    }

    public static class SettingsFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_options); // xml을 가져옵니다.

            //각 프리퍼런스에 적합한 설정을 해줍니다.

            //공지사항 프리퍼런스는 누르면 프리퍼런스 Activity로 이동해요.
            Preference NoticePreference = (Preference) findPreference("notice");
            NoticePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
            {
                @Override
                public boolean onPreferenceClick(Preference preference)
                {
                    Intent intent = new Intent(preference.getContext(), NoticeActivity.class);
                    startActivity(intent);
                    return false;
                }
            });

            //건의사항 프리퍼런스는 누르면 개발자에게 이메일을 보냅니다.
            Preference FeedbackPreference = (Preference) findPreference("feedback");
            FeedbackPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
            {
                @Override
                public boolean onPreferenceClick(Preference preference)
                {
                    String email = "aliwo@naver.com";
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setType("message/rfc822");
                    intent.putExtra(Intent.EXTRA_EMAIL, email);
                    intent.putExtra(Intent.EXTRA_TEXT, "R.S.V.P");
                    intent.setData(Uri.parse("mailto:"+email));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_FROM_BACKGROUND);
                    try {startActivity(intent);}
                    catch (android.content.ActivityNotFoundException e)
                    {// TODO Auto-generated catch block
                        e.printStackTrace();
                        Log.d("Email error:",e.toString());
                    }
                    return true;
                }
            });

            //Push메시지 설정은 푸쉬 플래그를 끕니다.
            Preference PushMessagePreference = (Preference) findPreference("Push");
            PushMessagePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
            {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue)
                {
                    boolean checked = Boolean.valueOf(newValue.toString());

                    if (checked == true)
                    {
                        userInfo.isReceivingPushMessage = false;
                    }

                    if (checked == false)
                    {
                        userInfo.isReceivingPushMessage = true;
                        QuestionActivity.isEmergency = false;
                    }

                    return  true;
                }
            });
        }
    }
}
