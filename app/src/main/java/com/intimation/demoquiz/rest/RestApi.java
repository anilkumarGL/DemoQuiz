package com.intimation.demoquiz.rest;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.google.gson.GsonBuilder;
import com.intimation.demoquiz.model.ModelClassMapper;

import java.io.IOException;
import java.io.Reader;

/**
 * Created by gorillalogic on 6/12/15.
 */
public class RestApi {
    private Activity mActivity;
    private ProgressDialog mProgressDialog;
    private ModelClassMapper model;
    private OnPostExecuteListener mExecuteListener;

    private String mProgressDialogMessage="";
    private String mUrl;

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
                Reader reader = Api.getData(mUrl);
                if (reader != null) {
                    model = new GsonBuilder().create().fromJson(reader, ModelClassMapper.getModelClass(mUrl));
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if (mProgressDialog != null)
                    mProgressDialog.dismiss();

                if (mExecuteListener != null) {
                    if (model != null)
                        mExecuteListener.onSuccess(model);
                    else
                        mExecuteListener.onFailure();
                }
            }
        }.execute();
    }

    public void setMessage(String message) {
        mProgressDialogMessage = message;
    }
}
