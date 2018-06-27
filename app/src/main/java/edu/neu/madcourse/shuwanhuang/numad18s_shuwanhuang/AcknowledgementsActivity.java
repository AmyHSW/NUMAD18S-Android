package edu.neu.madcourse.shuwanhuang.numad18s_shuwanhuang;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class AcknowledgementsActivity extends AppCompatActivity {

    private List<String> list;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acknowledgements);

        list = new ArrayList<>();
        listView = findViewById(R.id.acknowledge);
        initListView();
    }

    private void initListView() {
        list.add("Android Feature Graphic Generator "
            + "(https://www.norio.be/android-feature-graphic-generator/)");
        list.add("Module 4 Code Snippet " + "(https://bitbucket.org/NUOEL/cs5510_mod4)");
        list.add("How to copy SQLite database from assets folder "
            + "(https://stackoverflow.com/questions/16354154/copy-sqlite-database-from-assets-folder)");
        list.add("ADG: Storing and Searching for Data "
            + "(https://developer.android.com/training/search/search)");
        list.add("Word Game Scrolling View Image "
            + "(https://www.thesprucecrafts.com/scrabble-vowels-two-three-letter-words-412393)");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.acknowledgements_list_item,
                list);
        listView.setAdapter(adapter);
    }
}
