package com.intimation.demoquiz;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.intimation.demoquiz.adapters.NavigationDrawerAdapter;
import com.intimation.demoquiz.adapters.SelectionAdapter;
import com.intimation.demoquiz.model.Data;
import com.intimation.demoquiz.model.ModelClassMapper;
import com.intimation.demoquiz.model.Question;
import com.intimation.demoquiz.model.Questions;
import com.intimation.demoquiz.rest.OnPostExecuteListener;
import com.intimation.demoquiz.rest.RestApi;
import com.intimation.demoquiz.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by gorillalogic on 7/1/15.
 */
public class QuestionsActivity extends ActionBarActivity implements View.OnClickListener, OnPostExecuteListener, AdapterView.OnItemClickListener, NavigationDrawerAdapter.OnItemClickListener {

    private List<Question> mQuestions;
    private TextView mTimeLeftValue;
    private RelativeLayout mQuestionsLayout;
    private Timer mTimer;
    private int hh, mm, ss;
    private int mQNo;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_qpage);

        mQNo = 0;
        mTimeLeftValue = (TextView)findViewById(R.id.time_left);
        mQuestionsLayout = (RelativeLayout) findViewById(R.id.questions_layout);
        findViewById(R.id.finish).setOnClickListener(this);
        findViewById(R.id.next).setOnClickListener(this);
        initializeNavigationDrawer();
        updateTimerText("--:--:--");
        getQuestionsFromServer();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (mDrawerLayout.isDrawerVisible(mRecyclerView))
                mDrawerLayout.closeDrawer(mRecyclerView);
            else
                mDrawerLayout.openDrawer(mRecyclerView);
        } else {
            mDrawerLayout.closeDrawers();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        logout();
    }

    private void getQuestionsFromServer() {
        RestApi api = new RestApi(this);
        api.setMessage("Getting Questions...");
        api.setPostExecuteListener(this);
        api.get(Utils.URL_VIEW_QUESTION);
    }

    private void setCurrentQuestion(Question q) {
        Data.getInstance().setCurrentQuestion(q);
        ((TextView)findViewById(R.id.question)).setText(q.question);
        ((TextView)findViewById(R.id.question_number)).setText((mQNo+1) + " of " + mQuestions.size());
    }

    private void startTimer() {
        mTimer = new Timer();

        ss = 60;
        mm = mQuestions.size() * 1;
        hh = 0;
        if (mm > 59) {
            hh = mm / 60;
            mm = mm % (hh * 60);
        }

        TimerTask t = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (--ss < 0) {
                            mm--;
                            ss = 59;
                        }
                        if (mm < 0) {
                            hh--;
                            mm = 59;
                        }
                        if (hh < 0)
                            timeUp();
                        updateTimerText(hh == 0
                                        ? String.format("%02d", mm) + ":" + String.format("%02d", ss)
                                        : String.format("%02d", hh) + ":" + String.format("%02d", mm) + ":" + String.format("%02d", ss)
                        );
                    }
                });
            }
        };
        mTimer.schedule(t, 50, 1000);
    }

    private void timeUp() {
        Data.getInstance().setQuestions(mQuestions);
        finish();
        // show result page
    }

    private void updateTimerText(String time) {
        mTimeLeftValue.setText(time);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.next:
                mQuestionsLayout.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left));
                if (++mQNo < mQuestions.size()) {
                    setCurrentQuestion(mQuestions.get(mQNo));
                } else {
                    timeUp();
                }
                break;

            case R.id.finish:
                new AlertDialog.Builder(this)
                        .setTitle("Confirm Exit")
                        .setMessage("Are you sure you want to exit?")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                timeUp();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();
                break;


        }
    }

    private void initializeNavigationDrawer() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name, R.string.app_name) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                Log.i(".Exampl", "onDrawerOpened()");
                mDrawerToggle.syncState();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                Log.i(".Exampl", "onDrawerClosed()");
                mDrawerToggle.syncState();
            }
        };
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        setSupportActionBar(toolbar);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        String username = getIntent().getStringExtra("username");
        NavigationDrawerAdapter adapter = new NavigationDrawerAdapter(
                Arrays.asList("Home", "FAQ", "Pricing", "News", "Contact Us")
                , username.isEmpty() ? "" : username
                , "test_user@gmail.com"
        );
        adapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(adapter);
    }

    private void showChoice(Question question) {
        ListView optionsListview = (ListView) findViewById(R.id.listview_options);
        SelectionAdapter adapter = new SelectionAdapter(this, Arrays.asList(question.ch1, question.ch2, question.ch3, question.ch4));
        optionsListview.setAdapter(adapter);
        optionsListview.setOnItemClickListener(this);
    }

    private void logout() {
        new AlertDialog.Builder(this)
                .setTitle("Logout!")
                .setMessage("Are you sure?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    @Override
    public void onSuccess(ModelClassMapper model) {
        ArrayList<Question> questions = ((Questions) model).questions;
        Data.getInstance().setQuestions(questions);
        mQuestions = questions;
        setCurrentQuestion(mQuestions.get(mQNo));
        showChoice(mQuestions.get(mQNo));
        startTimer();
    }

    @Override
    public void onFailure() {
        Toast.makeText(this, "Failure: Getting questions from server.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        SelectionAdapter adapter = (SelectionAdapter)adapterView.getAdapter();
        adapter.setSelectedItemPosition(i);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(View v, int position) {
        switch (position) {
            case 0:
                // Logout
                logout();
                break;

            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                Toast.makeText(this, ((TextView)v.findViewById(R.id.item_text)).getText().toString() + " selected.", Toast.LENGTH_SHORT).show();
                break;
        }
        if (mDrawerLayout != null)
            mDrawerLayout.closeDrawers();
    }
}
