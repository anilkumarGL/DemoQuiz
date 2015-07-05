package com.intimation.demoquiz.rest;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.intimation.demoquiz.model.Data;
import com.intimation.demoquiz.model.Question;
import com.intimation.demoquiz.utils.Utils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;

/**
 * Created by gorillalogic on 6/12/15.
 */
public class RestApi {
    private Activity mActivity;
    private ProgressDialog mProgressDialog;
    private OnPostExecuteListener mExecuteListener;

    private String mProgressDialogMessage="";
    private String mUrl;
    private boolean mIsParseSuccessfull;

    public RestApi(Activity a) {
        mActivity = a;
    }

    public RestApi(Activity a, OnPostExecuteListener listener) {
        mActivity = a;
        mExecuteListener = listener;
    }

    public void setPostExecuteListener(OnPostExecuteListener listener) {
        mExecuteListener = listener;
    }

    public void get(String url) {
        mUrl = url;
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (mProgressDialogMessage != null) {
                    mProgressDialog = new ProgressDialog(mActivity);
                    mProgressDialog.setCancelable(false);
                    mProgressDialog.setMessage(mProgressDialogMessage.isEmpty() ? "Loading..." : mProgressDialogMessage);
                    mProgressDialog.show();
                }
            }

            @Override
            protected Void doInBackground(Void... voids) {
                parseXmlDoc();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if (mProgressDialog != null)
                    mProgressDialog.dismiss();

                if (mExecuteListener != null) {
                    if (mIsParseSuccessfull == true)
                        mExecuteListener.onSuccess();
                    else
                        mExecuteListener.onFailure();
                }
            }
        }.execute();
    }

    private void parseXmlDoc() {
        InputStream in = Api.getData(mUrl);
        String name = null, subject = null;
        ArrayList<Question> questions = new ArrayList<>();
        if (in != null) {
            try {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser parser = factory.newPullParser();
                parser.setInput(in, "UTF-8");
                int eventType = parser.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        name = parser.getName();
                        if (name.equalsIgnoreCase("xml")
                                || name.equalsIgnoreCase("questions")
                                || name.equalsIgnoreCase("question")) {
                            // ignore
                        } else {
                            subject = name;
                        }
                    } else if (eventType == XmlPullParser.TEXT) {
                        if (!parser.getText().isEmpty()
                                && !parser.getText().trim().isEmpty()) {
                            questions.add(parseQuestion(parser.getText(), subject));
                        }
                    }
                    eventType = parser.next();
                }
                mIsParseSuccessfull = true;
                Data.getInstance().setQuestions(questions);
            } catch (XmlPullParserException e) {
                e.printStackTrace();
                mIsParseSuccessfull = false;
            } catch (IOException e) {
                e.printStackTrace();
                mIsParseSuccessfull = false;
            } finally {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Question parseQuestion(String q, String subject) {
        Log.i(".DemoQuiz", "text = " + q + "\nSubject = " + subject);
        Question question = new Question();
        String[] split = q.split("\r");
        String[] temp;
        for (int i=1; i<split.length; i++) {
            if (!split[i].trim().isEmpty()) {
                if (i == 1) {
                    if (split[i].contains("../QPIMages/")) {
                        temp = split[i].split("../QPIMages/");
                        question.question = Utils.PREFIX_IMAGE + temp[1].substring(0, temp[1].indexOf('.')) + ".png";
                    } else {
                        temp = split[i].split(", ");
                        question.question = temp[1];
                    }
                    question.subject = subject;
                    question.qNo = Integer.parseInt(temp[0].substring(0, temp[0].indexOf(',')).trim());
                } else {
                    if (split[i].contains("../QPIMages/")) {
                        temp = split[i].split("../QPIMages/");
                        if (temp[0].contains(",oc,"))
                            question.correctch = i - 2;
                        question.options.add(Utils.PREFIX_IMAGE + temp[1].substring(0, temp[1].indexOf('.')) + ".png");
                    } else {
                        temp = split[i].split(", ");
                        if (temp[0].contains(",oc,"))
                            question.correctch = i - 2;
                        question.options.add(temp[1].trim());
                    }
                }
            }
        }

        return question;
    }

    public void setMessage(String message) {
        mProgressDialogMessage = message;
    }
}
