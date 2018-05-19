package edu.neu.madcourse.shuwanhuang.numad18s_shuwanhuang;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;

public class DisplayAboutActivity extends AppCompatActivity {

    private static final int MY_REQUEST_READ_PHONE_STATE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        // Capture the layout's TextView and set the string as its text
        TextView textView = findViewById(R.id.phoneId);

        // Use Android ID as the unique phone ID
        //String phoneId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        //String text = "Phone ID: " + phoneId;

        // Use IMEI or MEID as the unique phone ID
        textView.setText(getDeviceId());
    }

    /**
     * Get the unique telephony number. For example, the IMEI for GSM and the MEID or ESN
     * for CDMA phones.
     * @return the unique device ID
     */
    private String getDeviceId() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {
                String[] permissions = new String[]{Manifest.permission.READ_PHONE_STATE};
                requestPermissions(permissions, MY_REQUEST_READ_PHONE_STATE);
            }
        }

        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = telephonyManager.getDeviceId();
        Log.v("Device", deviceId);
        return deviceId == null ? "Device ID: Not Available!" : "Device ID: " + deviceId;
    }
}
