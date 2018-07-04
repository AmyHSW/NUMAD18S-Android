package edu.neu.madcourse.shuwanhuang.numad18s_shuwanhuang.wordgame;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import edu.neu.madcourse.shuwanhuang.numad18s_shuwanhuang.R;

public class GameActivity extends FragmentActivity {

    public static final String PREF_NAME = "GameActivity.GameData";
    public static final String PREF_USERNAME = "pref_username";
    public static final String PREF_RESTORE = "pref_restore";
    public static final String KEY_RESTORE = "key_restore";

    private MediaPlayer mMediaPlayer;
    private DatabaseReference dbRef;
    private GameFragment gameFragment;
    private InfoFragment infoFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(GameFragment.GAME_NAME, "start onCreate GameActivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        dbRef = FirebaseDatabase.getInstance().getReference();
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

    public void showResult(GameResult result) {
        result.dateTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
                .format(Calendar.getInstance().getTime());

        String key = dbRef.child("results").push().getKey();

        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> resultValues = result.toMap();
        childUpdates.put("/results/" + key, resultValues);
        childUpdates.put("/user/" + result.username + "/" + key, resultValues);
        dbRef.updateChildren(childUpdates);

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
}