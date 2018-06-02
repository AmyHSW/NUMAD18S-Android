package edu.neu.madcourse.shuwanhuang.numad18s_shuwanhuang;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class DictionaryActivity extends AppCompatActivity {

    private EditText editText;
    private Button clearButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary);

        hookupButton();
    }

    private void hookupButton() {
        editText = findViewById(R.id.editText);
        clearButton = findViewById(R.id.clearButton);
        clearButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                editText.setText("");
            }
        });
    }
}
