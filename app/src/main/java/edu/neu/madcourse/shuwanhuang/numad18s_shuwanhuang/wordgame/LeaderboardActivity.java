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

    private static final String TAG = "LeaderboardActivity";
    private static final String QUERY_ROOT = "results";
    private static final String QUERY_KEY = "finalScore";
    private static final int QUERY_LIMIT = 5; // TODO: change to 10

    private ListView listView;
    private Query queryTopTen;
    private ValueEventListener scoresListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        listView = findViewById(R.id.leaderboard);
        queryTopTen = FirebaseDatabase.getInstance().getReference(QUERY_ROOT)
                .orderByChild(QUERY_KEY).limitToLast(QUERY_LIMIT);
        scoresListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "data changed");
                List<String> list = new ArrayList<>();
                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    GameResult result = child.getValue(GameResult.class);
                    list.add(0, GameUtils.toResultString(result, true));
                }
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
    protected void onStart() {
        super.onStart();
        queryTopTen.addValueEventListener(scoresListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        queryTopTen.removeEventListener(scoresListener);
    }
}
