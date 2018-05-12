package edu.neu.madcourse.shuwanhuang.numad18s_shuwanhuang;

import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class DisplayMessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        // Get the Intent that started this activity and extract the string
/*        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);*/

        // Capture the layout's TextView and set the string as its text
        TextView textView = findViewById(R.id.phoneId);
        // textView.setText(message);

        String phoneId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        String text = "Phone ID: " + phoneId;
        textView.setText(text);
    }
}
