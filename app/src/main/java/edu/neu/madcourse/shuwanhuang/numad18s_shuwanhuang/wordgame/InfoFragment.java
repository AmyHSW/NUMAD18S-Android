package edu.neu.madcourse.shuwanhuang.numad18s_shuwanhuang.wordgame;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
        updateScore(0);
        return rootView;
    }

    public void updateTime(int seconds) {
        timeInfo.setText(getString(R.string.time_label, seconds / 60, seconds % 60));
    }

    public void updateScore(int score) {
        scoreInfo.setText(getString(R.string.score_label, score));
    }

}
