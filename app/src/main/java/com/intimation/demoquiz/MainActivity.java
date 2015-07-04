package com.intimation.demoquiz;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    private View mLogin, mSplash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLogin = findViewById(R.id.login_screen);
        mSplash = findViewById(R.id.splash_screen);
        findViewById(R.id.button_login).setOnClickListener(this);

        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Show signup - Login screen
                        mSplash.setVisibility(View.GONE);
                        mLogin.setVisibility(View.VISIBLE);
                    }
                });
            }
        }, 700);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_login:
                login();
                break;
        }
    }

    private void login() {
        showQuestionsScreen();
    }

    private void showQuestionsScreen() {
        Intent i = new Intent(this, QuestionsActivity.class);
        i.putExtra("username", ((EditText)findViewById(R.id.username)).getText().toString());
        startActivity(i);
    }
}
