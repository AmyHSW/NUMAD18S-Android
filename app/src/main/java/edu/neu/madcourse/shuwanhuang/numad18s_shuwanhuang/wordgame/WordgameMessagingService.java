package edu.neu.madcourse.shuwanhuang.numad18s_shuwanhuang.wordgame;

import android.util.Log;

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

    private void sendNotification(String noti) {
        // TODO: send notification
    }
}
