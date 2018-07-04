package edu.neu.madcourse.shuwanhuang.numad18s_shuwanhuang.wordgame;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class WordgameInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "WordgameInstanceID";

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
    }
}
