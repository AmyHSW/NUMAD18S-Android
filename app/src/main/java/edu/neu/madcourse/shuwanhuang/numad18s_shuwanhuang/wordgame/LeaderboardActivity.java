package edu.neu.madcourse.shuwanhuang.numad18s_shuwanhuang.wordgame;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Switch;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import edu.neu.madcourse.shuwanhuang.numad18s_shuwanhuang.R;

public class LeaderboardActivity extends AppCompatActivity {

    public static final String USERNAME = "username";

    private static final String TAG = "LeaderboardActivity";
    private static final String LEADERBOARD_ROOT = "results";
    private static final String SCOREBOARD_ROOT = "user";
    private static final String QUERY_FINAL = "finalScore";
    private static final String QUERY_BEST_WORD = "bestWordScore";
    private static final int QUERY_LIMIT = 10;

    private ListView listView;
    private Switch toggle;
    private Query queryTopTen;
    private ValueEventListener scoresListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        toggle = findViewById(R.id.switch1);
        listView = findViewById(R.id.leaderboard);

        toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupListenerToDB();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        setupListenerToDB();
    }

    private void setupListenerToDB() {
        String username = getIntent().getStringExtra(USERNAME);
        if (username == null) {
            initLeaderboard();
        } else {
            initScoreboard(username);
        }
        queryTopTen.addValueEventListener(scoresListener);
    }

    private void initLeaderboard() {
        if (toggle.isChecked()) {
            queryTopTen = FirebaseDatabase.getInstance().getReference(LEADERBOARD_ROOT)
                    .orderByChild(QUERY_BEST_WORD).limitToLast(QUERY_LIMIT);
        } else {
            queryTopTen = FirebaseDatabase.getInstance().getReference(LEADERBOARD_ROOT)
                    .orderByChild(QUERY_FINAL).limitToLast(QUERY_LIMIT);
        }
        scoresListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "leaderboard data changed");
                List<String> list = new ArrayList<>();
                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    GameResult result = child.getValue(GameResult.class);
                    list.add(0, GameUtils.toResultString(result, true));
                }
                GameUtils.addRanks(list);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        LeaderboardActivity.this, R.layout.leaderboard_list_item, list);
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, databaseError.toException());
            }
        };
    }

    private void initScoreboard(String username) {
        if (toggle.isChecked()) {
            queryTopTen = FirebaseDatabase.getInstance()
                    .getReference(SCOREBOARD_ROOT + "/" + username)
                    .orderByChild(QUERY_BEST_WORD).limitToLast(QUERY_LIMIT);
        } else {
            queryTopTen = FirebaseDatabase.getInstance()
                    .getReference(SCOREBOARD_ROOT + "/" + username)
                    .orderByChild(QUERY_FINAL).limitToLast(QUERY_LIMIT);
        }

        scoresListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "scoreboard data changed");
                List<String> list = new ArrayList<>();
                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    GameResult result = child.getValue(GameResult.class);
                    list.add(0, GameUtils.toResultString(result, false));
                }
                GameUtils.addRanks(list);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        LeaderboardActivity.this, R.layout.leaderboard_list_item, list);
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, databaseError.toException());
            }
        };
    }

    @Override
    protected void onStop() {
        super.onStop();
        queryTopTen.removeEventListener(scoresListener);
    }
}
