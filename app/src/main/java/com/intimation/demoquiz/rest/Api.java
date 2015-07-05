package com.intimation.demoquiz.rest;


import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by gorillalogic on 6/12/15.
 */
public class Api {
    private static InputStream mInStream;

    public static InputStream getData(String url) {
        try {
            URL httpUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) httpUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(20000);
            connection.addRequestProperty("Content-Type", "text/xml");
            connection.connect();
            Log.i(".RestApi", "Response code = " + connection.getResponseCode());
            if (connection.getResponseCode() == 200) {
                mInStream = new BufferedInputStream(connection.getInputStream());
//                mReader = new InputStreamReader(in);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mInStream;
    }
}
