package edu.neu.madcourse.shuwanhuang.numad18s_shuwanhuang.wordgame;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
    private static final String QUERY_KEY = "finalScore";
    private static final int QUERY_LIMIT = 10;

    private ListView listView;
    private Query queryTopTen;
    private ValueEventListener scoresListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        listView = findViewById(R.id.leaderboard);
    }

    @Override
    protected void onStart() {
        super.onStart();
        String username = getIntent().getStringExtra(USERNAME);
        if (username == null) {
            initLeaderboard();
        } else {
            initScoreboard(username);
        }
        queryTopTen.addValueEventListener(scoresListener);
    }

    private void initLeaderboard() {
        queryTopTen = FirebaseDatabase.getInstance().getReference(LEADERBOARD_ROOT)
                .orderByChild(QUERY_KEY).limitToLast(QUERY_LIMIT);
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
        queryTopTen = FirebaseDatabase.getInstance()
                .getReference(SCOREBOARD_ROOT + "/" + username)
                .orderByChild(QUERY_KEY).limitToLast(QUERY_LIMIT);
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
