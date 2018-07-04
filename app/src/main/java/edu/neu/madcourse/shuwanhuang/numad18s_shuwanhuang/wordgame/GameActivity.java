package edu.neu.madcourse.shuwanhuang.numad18s_shuwanhuang.wordgame;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.neu.madcourse.shuwanhuang.numad18s_shuwanhuang.R;

public class GameActivity extends FragmentActivity {

    public static final String PREF_NAME = "GameActivity.GameData";
    public static final String PREF_USERNAME = "pref_username";
    public static final String PREF_RESTORE = "pref_restore";
    public static final String KEY_RESTORE = "key_restore";

    private static final String SERVER_KEY = "key=AAAAwU4SqgA:APA91bHYqBEJZ8e-gem1RsAeekl7UndlfW1om7_HHAoelfeAzTB6NDle8Sjm9z76Xc4U7k8U_HOiWqAIhWayrLwDvE_bojo6fcw6XzeuAWG1MsyraDz2yycFEmFn1JCrdxdOGYeKF7ouCzbLEvDKKioc1W8BMD2xhg";

    private static final String TAG = GameActivity.class.getSimpleName();
    private static final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
    private static final Query qChampion =
            dbRef.child("results").orderByChild("finalScore").limitToLast(1);

    private MediaPlayer mMediaPlayer;
    private GameFragment gameFragment;
    private InfoFragment infoFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(GameFragment.GAME_NAME, "start onCreate GameActivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        gameFragment = (GameFragment) getFragmentManager()
                .findFragmentById(R.id.fragment_game);
        infoFragment = (InfoFragment) getFragmentManager()
                .findFragmentById(R.id.fragment_info);
        boolean restore = getIntent().getBooleanExtra(KEY_RESTORE, false);
        if (restore) {
            String gameData = getSharedPreferences(GameActivity.PREF_NAME, MODE_PRIVATE)
                    .getString(PREF_RESTORE, null);
            if (gameData != null) {
                gameFragment.restoreState(gameData);
            }
        }
        Log.d(GameFragment.GAME_NAME, "finish onCreate GameActivity");
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMediaPlayer = MediaPlayer.create(this, R.raw.the_midnight_ninja);
        mMediaPlayer.setVolume(0.5f, 0.5f);
        mMediaPlayer.setLooping(true);
        mMediaPlayer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        mMediaPlayer.release();
    }

    public void pauseMusic() {
        mMediaPlayer.pause();
    }

    public void resumeMusic() {
        mMediaPlayer.start();
    }

    public void onSelectWord() {
        gameFragment.onSelectWord();
    }

    public void updateTime(int seconds) {
        if (infoFragment == null) {
            Log.w(GameFragment.GAME_NAME, "infoFragment not set yet");
            return;
        }
        infoFragment.updateTime(seconds);
    }

    public void updateScore(int score) {
        if (infoFragment == null) {
            Log.w(GameFragment.GAME_NAME, "infoFragment not set yet");
            return;
        }
        infoFragment.updateScore(score);
    }

    public void updateCurrentWord(String currentWord) {
        if (infoFragment == null) {
            Log.w(GameFragment.GAME_NAME, "infoFragment not set yet");
            return;
        }
        infoFragment.updateCurrentWord(currentWord);
    }

    public void showResult(final GameResult result) {
        result.dateTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
                .format(Calendar.getInstance().getTime());

        String key = dbRef.child("results").push().getKey();

        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> resultValues = result.toMap();
        childUpdates.put("/results/" + key, resultValues);
        childUpdates.put("/user/" + result.username + "/" + key, resultValues);
        dbRef.updateChildren(childUpdates);

        qChampion.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    GameResult champion = child.getValue(GameResult.class);
                    if (champion != null
                            && result.username.equals(champion.username)
                            && result.finalScore.equals(champion.finalScore)) {
                        sendToFCMChampionTopicAsync(
                                result.username + " got the new highest score of "
                                + result.finalScore);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, databaseError.toException());
            }
        });
        showScore(result.finalScore.intValue());
    }

    private void showScore(int score) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.show_score, score));
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        final Dialog dialog = builder.create();
        dialog.show();
    }

    private void sendToFCMChampionTopicAsync(final String msg) {
        Log.d(TAG, "sending to FCM /topics/champion: " + msg);
        new Thread(new Runnable() {
            @Override
            public void run() {
                sendToFCMChampionTopic(msg);
            }
        }).start();
    }

    private void sendToFCMChampionTopic(final String msg) {
        JSONObject jPayload = new JSONObject();
        JSONObject jNotification = new JSONObject();
        try {
            jNotification.put("message", "Word Game Message");
            jNotification.put("body", msg);
            jNotification.put("sound", "default");
            jNotification.put("badge", "1");
            jNotification.put("click_action", "OPEN_ACTIVITY_1");

            jPayload.put("to", "/topics/champion");
            jPayload.put("priority", "high");
            jPayload.put("notification", jNotification);

            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", SERVER_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(jPayload.toString().getBytes());
            outputStream.close();

            InputStream inputStream = conn.getInputStream();
            final String resp = convertStreamToString(inputStream);

            Handler h = new Handler(Looper.getMainLooper());
            h.post(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG, "got response: " + resp);
                }
            });

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    private static String convertStreamToString(InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}