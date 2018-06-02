package edu.neu.madcourse.shuwanhuang.numad18s_shuwanhuang;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.TextView;
import android.widget.Toast;

public class AboutActivity extends AppCompatActivity {

    private static final int REQUEST_PHONE_STATE = 1;
    private String deviceId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        getDeviceId();
        // Use Android ID as the unique phone ID
        if (deviceId == null || deviceId.length() == 0) {
            deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        }

        // Capture the layout's TextView and set the string as its text
        TextView textView = findViewById(R.id.phoneId);
        textView.setText("Device ID: " + deviceId);
    }

    private void getDeviceId() {
        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                        != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_PHONE_STATE},
                            REQUEST_PHONE_STATE);
            } else {
                deviceId = telephonyManager.getDeviceId();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode){
            case REQUEST_PHONE_STATE:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // .. Can now obtain the device ID
                    getDeviceId();
                } else {
                    Toast.makeText(this,
                            "Unable to get IMEI/MEID without granting permission! "
                            + "Showing Android ID instead",
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
