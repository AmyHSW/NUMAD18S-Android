package edu.neu.madcourse.shuwanhuang.numad18s_shuwanhuang.wordgame;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class WordgameMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MessagingService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        if (remoteMessage.getNotification() != null) {
            String noti = remoteMessage.getNotification().getBody();
            Log.d(TAG, "Message Notification Body: " + noti);
            sendNotification(noti);
        }
    }

    private void sendNotification(final String noti) {
        Log.d(TAG, "making toast: " + noti);
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),
                        noti,
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}
