package comuiappcenter.facebook.m.legacy.Messaging;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashSet;

import comuiappcenter.facebook.m.legacy.User.userInfo;
import comuiappcenter.facebook.m.legacy.dataContainer.INUClasses;

import static comuiappcenter.facebook.m.legacy.SplashActivity.PREFS_NAME;

/**
 * Created by Administrator on 2/8/2017.
 */

public class MessageSubscription
{
    public static void subscribeTopic()
    {
        //사용자의 관심수업 목록으로 부터 Topic을 가져와서 구독합니다. 헉 ㅋㅋㅋ FCM은 한글을 인식 못한다.
        for(int i=0; i<userInfo.InterestedClass.size(); i++)
        {
            String temp = userInfo.InterestedClass.get(i);
            String result = INUClasses.generateCodePosition(temp); // 수업명을 subscribe 할 수 있게 변환합니다.
            clearSubscription();
            FirebaseMessaging.getInstance().subscribeToTopic(result);
            Log.d("MessageSubscription", "구독했습니다: "+temp+"을(를)"+result+"로 변환했어요.");
        }
    }

    public static void clearSubscription()
    { // 일단 모든 구독 정보를 없앱니다. 구독정보를 다 없앤다는 것은 곧 InstanceId가 쓸모 없어진다는 뜻이고, 그냥 InstanceID를 지우면 됩니다.
        try
        {
            FirebaseInstanceId.getInstance().deleteInstanceId();
        }catch (IOException e) {e.printStackTrace();}
    }

}
