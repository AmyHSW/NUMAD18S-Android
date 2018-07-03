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
    private static final String QUERY_ROOT = "scores";
    private static final int QUERY_LIMIT = 3;

    private ListView listView;
    private Query queryTopTen;
    private ValueEventListener scoresListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        listView = findViewById(R.id.leaderboard);
        queryTopTen = FirebaseDatabase.getInstance().getReference(QUERY_ROOT)
                .orderByValue().limitToLast(QUERY_LIMIT);
        scoresListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "data changed");
                List<String> list = new ArrayList<>();
                for (DataSnapshot score: dataSnapshot.getChildren()) {
                    list.add(0, String.valueOf(score.getValue(Long.class)));
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
    protected void onResume() {
        super.onResume();
        queryTopTen.addValueEventListener(scoresListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        queryTopTen.removeEventListener(scoresListener);
    }
}
