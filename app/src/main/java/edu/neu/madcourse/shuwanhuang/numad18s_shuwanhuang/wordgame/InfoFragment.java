package edu.neu.madcourse.shuwanhuang.numad18s_shuwanhuang.wordgame;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import edu.neu.madcourse.shuwanhuang.numad18s_shuwanhuang.R;

public class InfoFragment extends Fragment {

    private TextView timeInfo;
    private TextView scoreInfo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_info, container, false);
        timeInfo = rootView.findViewById(R.id.time_info);
        scoreInfo = rootView.findViewById(R.id.score_info);
        updateTime(GameFragment.SECONDS_PER_PHASE);
        updateScore(GameFragment.STARTING_SCORE);
        return rootView;
    }

    public void updateTime(int seconds) {
        if (timeInfo == null) {
            Log.w(GameFragment.GAME_NAME, "timeInfo not set yet");
            return;
        }
        if (seconds == GameFragment.TIME_REMINDER) {
            Toast.makeText(getActivity(), getString(R.string.toast_time_reminder, seconds),
                    Toast.LENGTH_LONG).show();
        }
        timeInfo.setText(getString(R.string.time_label, seconds / 60, seconds % 60));
    }

    public void updateScore(int score) {
        if (scoreInfo == null) {
            Log.w(GameFragment.GAME_NAME, "scoreInfo not set yet");
            return;
        }
        scoreInfo.setText(getString(R.string.score_label, score));
    }
}
