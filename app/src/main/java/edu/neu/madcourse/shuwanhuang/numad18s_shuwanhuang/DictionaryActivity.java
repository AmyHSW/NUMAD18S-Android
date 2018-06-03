package edu.neu.madcourse.shuwanhuang.numad18s_shuwanhuang;

import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
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
    private ListView wordsEnteredListView;
    private LinkedList<String> wordsEntered;
    private DatabaseTable dictionary;
    private ToneGenerator beep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary);

        beep = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        dictionary = new DatabaseTable(this);
        wordsEntered = new LinkedList<>();
        hookupButton();
        addTextChangedListener();
    }

    private void hookupButton() {
        inputEditText = findViewById(R.id.editText);
        wordsEnteredListView = findViewById(R.id.words);
        Button clearButton = findViewById(R.id.clearButton);
        clearButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                inputEditText.setText("");
                wordsEntered = new LinkedList<>();
                wordsEnteredListView.setAdapter(null);
            }
        });
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
                    updateWordList(word);
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
        beep.startTone(ToneGenerator.TONE_CDMA_PIP,150);
    }

    private void updateWordList(String word) {
        wordsEntered.addFirst(word);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                DictionaryActivity.this, R.layout.word_list_item, wordsEntered);
        wordsEnteredListView.setAdapter(adapter);
    }

    public void onClickAcknowledgements(View view) {
        Intent intent = new Intent(this, AcknowledgementsActivity.class);
        startActivity(intent);
    }
}
