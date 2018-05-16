package comuiappcenter.facebook.m.legacy.dataContainer;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * Created by Administrator on 1/27/2017.
 */

public class Device
{
    Context mContext;
    public String osVersion;
    public String model;
    public String display;
    public String manufacturer;
    public String macAddress;

    public Device( String osVersion, String model, String display, String manufacturer, String macAddress)
    {

        this.osVersion = osVersion;
        this.model = model;
        this.display = display;
        this.manufacturer = manufacturer;
        this.macAddress = macAddress;
    }

    public Device(Context context)
    {
        mContext = context;
    }


    public Device getDeviceInfo()
    {
        Device device = null;

        // 1. 전화번호 가져오기 포기


        // 2. OS 버전 가져오기
        String osVersion = Build.VERSION.RELEASE;

        // 3. 모델명
        String model = Build.MODEL;

        // 4.해상도 가져오기
        String display = getDisplay(mContext.getApplicationContext()); // 아래에 getDisplay를 구현해 놨습니다.

        // 5. manufacturer
        String manufacturer = Build.MANUFACTURER;

        // 6. 맥 어드레스를 가져오는 메소드도 아래에 구현해 놨습니다.
        String macAddress = getMacAddress(mContext.getApplicationContext());

        device = new Device(osVersion, model, display, manufacturer, macAddress);

        return device;
    }

    private static String getDisplay(Context context)
    {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        int deviceWidth = displayMetrics.widthPixels;
        int deviceHeight = displayMetrics.heightPixels;

        return deviceWidth + "x" + deviceHeight;
    }

    private static String getMacAddress(Context context)
    {
        WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();

        return info.getMacAddress();
    }


}
