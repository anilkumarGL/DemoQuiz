package com.intimation.demoquiz;

import android.os.Bundle;
import android.webkit.WebView;
import android.widget.TextView;

/**
 * Created by gorillalogic on 7/6/15.
 */
public class WebviewActivity extends NavigationDrawerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_web);

        initializeNavigationDrawer();

        ((WebView)findViewById(R.id.web_view)).loadUrl(getIntent().getStringExtra("url"));
        ((TextView)findViewById(R.id.title)).setText(getIntent().getStringExtra("title"));
    }
}
