package com.intimation.demoquiz;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import com.intimation.demoquiz.adapters.NavigationDrawerAdapter;
import com.intimation.demoquiz.model.Data;

import java.util.Arrays;

/**
 * Created by gorillalogic on 7/6/15.
 */
public class NavigationDrawerActivity extends ActionBarActivity implements NavigationDrawerAdapter.OnItemClickListener {
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private RecyclerView mRecyclerView;
    private static int mPrev = 1; // Default home

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

    protected void initializeNavigationDrawer() {
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

        NavigationDrawerAdapter adapter = new NavigationDrawerAdapter(
                Arrays.asList("Home", "FAQ", "Pricing", "News", "Contact Us")
                , Data.getInstance().getUsername()
                , "test_user@gmail.com"
        );
        adapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(adapter);
    }

    protected void logout() {
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
    public void onItemClick(View v, int position) {
        switch (position) {
            case 0:
                // Logout
                logout();
                break;

            case 1:
                if (mPrev != position) {
                    finish();
                    showQuestionsScreen();
                }
                break;

            case 2:
            case 5:
                Intent i = new Intent(this, WebviewActivity.class);
                i.putExtra("url", position == 2 ? "http://qppacket.com/StudentPages/Faq.aspx" : "http://qppacket.com/StudentPages/ContactUs.aspx");
                i.putExtra("title", position == 2 ? "FAQ" : "Contact Us");
                startActivity(i);
                break;

            case 3:
            case 4:
                Toast.makeText(this, ((TextView) v.findViewById(R.id.item_text)).getText().toString() + " selected.", Toast.LENGTH_SHORT).show();
                break;
        }
        mPrev = position;
        if (mDrawerLayout != null)
            mDrawerLayout.closeDrawers();
    }

    private void showQuestionsScreen() {
        Intent i = new Intent(this, QuestionsActivity.class);
        startActivity(i);
    }
}
