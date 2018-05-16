package comuiappcenter.facebook.m.legacy.Messaging;

/**
 * Created by Administrator on 1/31/2017.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

import comuiappcenter.facebook.m.legacy.MainActivity;
import comuiappcenter.facebook.m.legacy.R;
import comuiappcenter.facebook.m.legacy.User.userInfo;


public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService
{
    private static final String TAG = "FirebaseMsgService";
    public String messageTitle;

    // [START receive_message]
    //sendNotification 으로 캡슐화 한 건 좋지만, onMessageReceived가 별로 할 일이 많지 않은 지금과 같은 상황에선
    //굳이 캡슐화로 나눌 필요 없는 듯. 그래도 일단은 이렇게 두겠다.
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
    {
        if(userInfo.isReceivingPushMessage == false) {return; } // 만약 푸쉬 설정을 끄면 아무것도 안하고 리턴합니다.
        messageTitle = remoteMessage.getNotification().getTitle();
        Log.d(TAG, "title: " + messageTitle);
        sendNotification(remoteMessage.getData().get("message"));
    }

    private void sendNotification(String messageBody) // 커스텀 메소드
    {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.app_icon)
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
