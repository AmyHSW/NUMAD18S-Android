package edu.neu.madcourse.shuwanhuang.numad18s_shuwanhuang.wordgame;

import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.neu.madcourse.shuwanhuang.numad18s_shuwanhuang.R;

public class ControlFragment extends Fragment {

    private static final int LEVEL_MUSIC_ON = 0;
    private static final int LEVEL_MUSIC_OFF = 1;

    private boolean musicOn = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_control, container, false);
        View selectButton = rootView.findViewById(R.id.button_select);
        View pauseButton = rootView.findViewById(R.id.button_pause);
        final View musicButton = rootView.findViewById(R.id.button_music);

        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((GameActivity) getActivity()).onSelectWord();
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((GameActivity) getActivity()).finish();
            }
        });

        musicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Drawable drawable = musicButton.getBackground();
                if (musicOn) {
                    ((GameActivity)getActivity()).pauseMusic();
                    drawable.setLevel(LEVEL_MUSIC_OFF);
                } else {
                    ((GameActivity)getActivity()).resumeMusic();
                    drawable.setLevel(LEVEL_MUSIC_ON);
                }
                musicOn = !musicOn;
            }
        });

        return rootView;
    }

}