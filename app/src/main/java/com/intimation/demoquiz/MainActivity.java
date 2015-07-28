package com.intimation.demoquiz;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.intimation.demoquiz.model.Data;
import com.intimation.demoquiz.model.Question;


public class MainActivity extends Activity implements View.OnClickListener {

    private View mLogin, mSplash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLogin = findViewById(R.id.login_screen);
        mSplash = findViewById(R.id.splash_screen);
        findViewById(R.id.practice_exam).setOnClickListener(this);
        TextView why = (TextView) findViewById(R.id.why_ekalvya);
        TextView news = (TextView) findViewById(R.id.entrance_news);
        why.setOnClickListener(this);
        news.setOnClickListener(this);
        why.setPaintFlags(why.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
        news.setPaintFlags(why.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);

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
        }, 1000);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.practice_exam:
                showQuestionsScreen();
                break;
        }
    }

    private void showQuestionsScreen() {
        Intent i = new Intent(this, QuestionsActivity.class);
        startActivity(i);
    }
}
