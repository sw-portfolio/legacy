package comuiappcenter.facebook.m.legacy.Messaging;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.IOException;


public class FirebaseInstanceIDService extends FirebaseInstanceIdService
{

    private static final String TAG = "MyFirebaseIIDService";

    // [START refresh_token]
    @Override
    public void onTokenRefresh()
    {
        // Get updated InstanceID token.
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + token);

        // 생성등록된 토큰을 개인 앱서버에 보내 저장해 두었다가 추가 뭔가를 하고 싶으면 할 수 있도록 한다.
        //sendRegistrationToServer(token);
    }

/*    private void sendRegistrationToServer(String token)
    {
        // Add custom implementation, as needed.
        //OkHttp는 편의를 위해 사용했다. server에 토큰을 보내는데 사용한다.

        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("Token", token)
                .build();

        //request
        Request request = new Request.Builder()
                .url("http://서버주소/fcm/register.php")
                .post(body)
                .build();

        try {
                client.newCall(request).execute();
            } catch (IOException e) {e.printStackTrace();}

    }*/
}
