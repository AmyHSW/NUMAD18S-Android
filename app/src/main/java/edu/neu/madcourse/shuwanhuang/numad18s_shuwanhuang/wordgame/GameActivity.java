package edu.neu.madcourse.shuwanhuang.numad18s_shuwanhuang.wordgame;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import edu.neu.madcourse.shuwanhuang.numad18s_shuwanhuang.R;

public class GameActivity extends FragmentActivity {

    private static final int MUSIC_ON = 0;
    private static final int MUSIC_OFF = 1;
    public static final String COL_WORD = "WORD";
    public static final String PREF_NAME = "GameActivity.GameData";
    public static final String PREF_RESTORE = "pref_restore";
    public static final String KEY_RESTORE = "key_restore";
    private MediaPlayer mMediaPlayer;
    private int musicLevel = MUSIC_ON;
    private Handler mHandler = new Handler();
    private GameFragment mGameFragment;
    private InfoFragment infoFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(GameFragment.GAME_NAME, "start onCreate GameActivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        mGameFragment = (GameFragment) getFragmentManager()
                .findFragmentById(R.id.fragment_game);
        infoFragment = (InfoFragment) getFragmentManager()
                .findFragmentById(R.id.fragment_info);
        boolean restore = getIntent().getBooleanExtra(KEY_RESTORE, false);
        if (restore) {
            String gameData = getSharedPreferences(GameActivity.PREF_NAME, MODE_PRIVATE)
                    .getString(PREF_RESTORE, null);
            if (gameData != null) {
                Log.d(GameFragment.GAME_NAME, "restore game data");
                mGameFragment.restoreState(gameData);
            }
        }
        Log.d(GameFragment.GAME_NAME, "finish onCreate GameActivity");
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMediaPlayer = MediaPlayer.create(this, R.raw.solar_eclipse);
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
        mGameFragment.onSelectWord();
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

    public void showScore(int score) {
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