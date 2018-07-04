package edu.neu.madcourse.shuwanhuang.numad18s_shuwanhuang.wordgame;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.messaging.FirebaseMessaging;

import edu.neu.madcourse.shuwanhuang.numad18s_shuwanhuang.R;

public class MainActivity extends AppCompatActivity {

    private static final String FCM_TOPIC = "champion";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wordgame_main);
        FirebaseMessaging.getInstance().subscribeToTopic(FCM_TOPIC);
    }

}
