package edu.neu.madcourse.shuwanhuang.numad18s_shuwanhuang.wordgame;

import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.neu.madcourse.shuwanhuang.numad18s_shuwanhuang.R;

public class ControlFragment extends Fragment {

    private static final int MUSIC_ON = 0;
    private static final int MUSIC_OFF = 1;
    private boolean musicOn = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =
                inflater.inflate(R.layout.fragment_control, container, false);
        final View musicButton = rootView.findViewById(R.id.button_music);
        View submit = rootView.findViewById(R.id.button_submit);

        musicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Drawable drawable = musicButton.getBackground();
                if (musicOn) {
                    ((GameActivity)getActivity()).pauseMusic();
                    drawable.setLevel(MUSIC_OFF);
                } else {
                    ((GameActivity)getActivity()).resumeMusic();
                    drawable.setLevel(MUSIC_ON);
                }
                musicOn = !musicOn;
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((GameActivity) getActivity()).onSelectWord();
            }
        });
        return rootView;
    }

}