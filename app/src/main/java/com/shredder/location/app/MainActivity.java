package com.shredder.location.app;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.shredder.location.app.base.BaseActivity;
import com.shredder.location.app.fragments.PublisherFragment;
import com.shredder.location.app.fragments.SettingsFragment;
import com.shredder.location.app.fragments.SubscribingFragment;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

public class MainActivity extends BaseActivity {

    @Bind(R.id.main_navigation)
    ListView mDrawerList;

    @Bind(R.id.main_drawer)
    DrawerLayout mDrawerLayout;

    @Bind(R.id.main_toolbar)
    Toolbar toolbar;

    private ActionBarDrawerToggle drawerToggle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        setContentView(R.layout.activity_main_toolbar_on_top);
        ButterKnife.bind(this);
        setupNavigationItems();
        setupDrawerAndToggle();
        showBookingList();
    }

    private void setupDrawerAndToggle() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, 0, 0) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                setDrawerIndicatorEnabled(true);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        mDrawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
    }

    private void setupNavigationItems() {
        String[] navigationItems = {"Books", "Random Book", "Settings"};
        ArrayAdapter<String> mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, navigationItems);
        mDrawerList.setAdapter(mAdapter);
    }

    @OnItemClick(R.id.main_navigation)
    public void onItemClick(int index) {
        switch (index) {
            case 0:
                showBookingList();
                break;
            case 1:
                showRandom();
                break;
            case 2:
                showSettings();
                break;
            default:
                break;
        }
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    private void showSettings() {
        add(SettingsFragment.newInstance());
    }

    private void showRandom() {
        String[] titles = getResources().getStringArray(R.array.book_titles);
        long randomIndex = System.currentTimeMillis() % (long) (titles.length - 1);
        add(SubscribingFragment.newInstance(titles[(int) randomIndex]));
    }

    private void showBookingList() {
        add(PublisherFragment.newInstance());
    }

    @Override
    protected DrawerLayout getDrawer() {
        return mDrawerLayout;
    }

    @Override
    protected ActionBarDrawerToggle getDrawerToggle() {
        return drawerToggle;
    }
}
