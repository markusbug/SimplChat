package com.simplchat.simplchat.simplchat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {
    Button b;
    private EditText emailET, passET;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        b = (Button) findViewById(R.id.button);
        emailET = (EditText) findViewById(R.id.editText);
        passET = (EditText) findViewById(R.id.editText2);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginStart();
            }
        });
    }

    private void loginStart() {

    }
}
