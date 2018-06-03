package edu.neu.madcourse.shuwanhuang.numad18s_shuwanhuang;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.LinkedList;

public class DictionaryActivity extends AppCompatActivity {

    private static final int MIN_WORD_LENGTH = 3;

    private EditText inputEditText;
    private LinkedList<String> wordsEntered = new LinkedList<>();
    private ArrayAdapter<String> adapter;
    private DatabaseTable dictionary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary);

        dictionary = new DatabaseTable(this);
        hookupButton();
        //initListView();
        addTextChangedListener();
    }

    private void hookupButton() {
        inputEditText = findViewById(R.id.editText);
        Button clearButton = findViewById(R.id.clearButton);
        clearButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                inputEditText.setText("");
            }
        });
    }

    private void initListView() {
        ListView wordsEnteredListView = findViewById(R.id.words);
        wordsEntered = new LinkedList<>();
        adapter = new ArrayAdapter<>(
                this, R.layout.word_list_item, wordsEntered);
        wordsEnteredListView.setAdapter(adapter);
    }

    private void addTextChangedListener() {
        inputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String word = s.toString();
                if (word.length() >= MIN_WORD_LENGTH && dictionary.containsWord(word)) {
                    beep();
//                    wordsEntered.addFirst(word);
//                    adapter.notifyDataSetChanged();
                    ListView wordsEnteredListView = findViewById(R.id.words);
                    wordsEntered.addFirst(word);
                    adapter = new ArrayAdapter<>(
                            DictionaryActivity.this, R.layout.word_list_item, wordsEntered);
                    wordsEnteredListView.setAdapter(adapter);
                } else {
                    Log.v("Not Exist", word);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void beep() {

    }
}
