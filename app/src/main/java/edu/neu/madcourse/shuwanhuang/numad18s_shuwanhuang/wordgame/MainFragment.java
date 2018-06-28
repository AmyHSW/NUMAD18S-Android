package edu.neu.madcourse.shuwanhuang.numad18s_shuwanhuang.wordgame;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.neu.madcourse.shuwanhuang.numad18s_shuwanhuang.R;

public class MainFragment extends Fragment {

    private AlertDialog dialog;
    private View continueButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        View newButton = rootView.findViewById(R.id.button_new);
        continueButton = rootView.findViewById(R.id.button_continue);
        View aboutButton = rootView.findViewById(R.id.button_about);
        View acknowledgeButton = rootView.findViewById(R.id.button_acknowledge);

        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), GameActivity.class);
                getActivity().startActivity(intent);
            }
        });

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), GameActivity.class);
                intent.putExtra(GameActivity.KEY_RESTORE, true);
                getActivity().startActivity(intent);
            }
        });

        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(R.string.about_text);
                builder.setCancelable(false);
                builder.setPositiveButton(R.string.ok_label,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) { }
                        });
                dialog = builder.show();
            }
        });

        acknowledgeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(R.string.acknowledgements_text);
                builder.setCancelable(false);
                builder.setPositiveButton(R.string.ok_label,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) { }
                        });
                dialog = builder.show();
            }
        });

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (dialog != null)
            dialog.dismiss();
    }

    @Override
    public void onResume() {
        super.onResume();
        String gameData = getActivity()
                .getSharedPreferences(GameActivity.PREF_NAME, Context.MODE_PRIVATE)
                .getString(GameActivity.PREF_RESTORE, null);
        if (gameData == null) {
            Log.d(GameFragment.GAME_NAME, "no game data");
            continueButton.setVisibility(View.GONE);
        } else {
            Log.d(GameFragment.GAME_NAME, "has game data");
            continueButton.setVisibility(View.VISIBLE);
        }
    }
}
