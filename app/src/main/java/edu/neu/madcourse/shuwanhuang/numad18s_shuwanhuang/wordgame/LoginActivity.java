package edu.neu.madcourse.shuwanhuang.numad18s_shuwanhuang.wordgame;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import edu.neu.madcourse.shuwanhuang.numad18s_shuwanhuang.R;

public class LoginActivity extends AppCompatActivity {

    public static final String DEFAULT_USERNAME = "anonymous";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText usernameText = findViewById(R.id.editText2);
        View loginButton = findViewById(R.id.button_login);
        View skipButton = findViewById(R.id.button_skip);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernameText.getText().toString();
                if (username.equals("")) {
                    return;
                }
                if (!GameUtils.isAlphanumeric(username)) {
                    usernameText.setText("");
                    Toast.makeText(LoginActivity.this, R.string.toast_username_requirement,
                            Toast.LENGTH_LONG).show();
                    return;
                }
                setUsername(username);
                finish();
            }
        });

        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUsername(DEFAULT_USERNAME);
                finish();
            }
        });
    }

    private void setUsername(String username) {
        getSharedPreferences(GameActivity.PREF_NAME, Context.MODE_PRIVATE).edit()
                .putString(GameActivity.PREF_USERNAME, username).commit();
    }

}
