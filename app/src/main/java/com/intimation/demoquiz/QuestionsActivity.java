package com.intimation.demoquiz;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.intimation.demoquiz.adapters.SelectionAdapter;
import com.intimation.demoquiz.custom.MyListView;
import com.intimation.demoquiz.model.Data;
import com.intimation.demoquiz.model.Question;
import com.intimation.demoquiz.model.Store;
import com.intimation.demoquiz.rest.OnPostExecuteListener;
import com.intimation.demoquiz.rest.RestApi;
import com.intimation.demoquiz.utils.Utils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by gorillalogic on 7/1/15.
 */
public class QuestionsActivity extends NavigationDrawerActivity implements View.OnClickListener, OnPostExecuteListener, AdapterView.OnItemClickListener {

    private List<Question> mQuestions;
    private TextView mTimeLeftValue;
    private RelativeLayout mQuestionsLayout;
    private Timer mTimer;
    private int hh, mm, ss;
    private int mQNo;

    WebView webView;
    int num1, num2, num3;
    private File IMG_DOWNLOAD_DIR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_qpage);

        IMG_DOWNLOAD_DIR = getExternalFilesDir(null) != null ? getExternalFilesDir(null) : getCacheDir();
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
        TextView question_text = (TextView) findViewById(R.id.question);
        ImageView question_image = (ImageView) findViewById(R.id.question_image);
        if (q.question.contains(IMG_DOWNLOAD_DIR.getAbsolutePath())) {
            question_image.setVisibility(View.VISIBLE);
            question_text.setVisibility(View.GONE);
            Uri uri = Uri.fromFile(new File(q.question));
            Picasso.with(QuestionsActivity.this).load(uri).into(question_image);
        } else {
            question_image.setVisibility(View.GONE);
            question_text.setVisibility(View.VISIBLE);
            question_text.setText(q.question);
        }
        ((TextView) findViewById(R.id.question_number)).setText(q.qNo + " of " + mQuestions.size());
    }

    private void startTimer() {
        mTimer = new Timer();

        Store store = new Store(this);
        // total_time in seconds.
        long total_time = mQuestions.size() * store.getAverageTimePerQuestion();

        ss = (int) (total_time % 60);
        mm = (int) (total_time / 60);
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
        // show result page
        showResults();
    }

    private void showResults() {
        findViewById(R.id.qpage).setVisibility(View.GONE);
        findViewById(R.id.result_page).setVisibility(View.VISIBLE);

        ((TextView)findViewById(R.id.total_q)).setText("" + mQuestions.size());

        int a=0, u=0, c=0, w=0;
        for (Question q : mQuestions) {
            a = q.isAnswered() ? a+1 : a;
            u = !q.isAnswered() ? u+1 : u;
            c = q.isAnswerCorrect() ? c+1 : c;
            w = q.isAnswered() && !q.isAnswerCorrect() ? w+1 : w;
        }
        ((TextView)findViewById(R.id.attempted)).setText("" + a);
        ((TextView)findViewById(R.id.unattended)).setText("" + u);
        ((TextView)findViewById(R.id.correct_answers)).setText("" + c);
        ((TextView)findViewById(R.id.wrong_answers)).setText("" + w);

        Store store = new Store(this);
        ((TextView) findViewById(R.id.total_marks)).setText("" + (c * store.getCorrectAnswerMark() + (w * store.getWrongAnswerMark())));
        ((TextView) findViewById(R.id.info)).setText("Correct answer scores " + ((int)store.getCorrectAnswerMark()) + " mark and wrong answer deduct " + ((int)store.getWrongAnswerMark()) + " mark");

        showPieChart(c, w, u);
    }

    private void showPieChart(int c, int w, int u) {
        num1 = c;
        num2 = w;
        num3 = u;

        webView = (WebView)findViewById(R.id.graph_webview);
        webView.addJavascriptInterface(new WebAppInterface(), "Android");

        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("file:///android_asset/pie.html");
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
                    showChoice(mQuestions.get(mQNo));
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

    private void showChoice(Question question) {
        final MyListView optionsListview = (MyListView) findViewById(R.id.listview_options);
        SelectionAdapter adapter = new SelectionAdapter(this, question.options);
        optionsListview.setAdapter(adapter);
        optionsListview.setOnItemClickListener(this);
        optionsListview.post(new Runnable() {
            @Override
            public void run() {
                ((ScrollView)findViewById(R.id.parent_scrollview)).fullScroll(View.FOCUS_UP);
            }
        });
    }

    @Override
    public void onSuccess() {
        mQuestions = Data.getInstance().getAllQuestions();
        Question current = mQuestions.get(mQNo);
        setCurrentQuestion(current);
        showChoice(current);
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

        Question current = mQuestions.get(mQNo);
        current.setSelectedChoice(i);
    }

    public class WebAppInterface {

        @JavascriptInterface
        public int getNum1() {
            return num1;
        }

        @JavascriptInterface
        public int getNum2() {
            return num2;
        }

        @JavascriptInterface
        public int getNum3() {
            return num3;
        }

    }
}
