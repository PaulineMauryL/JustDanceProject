package com.pauli.justdanceproject;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class ShowProfile extends AppCompatActivity implements PerformanceFragment
        .OnFragmentInteractionListener, HistoryFragment.OnFragmentInteractionListener,
        LevelUserFragment.OnFragmentInteractionListener{

    private final String TAG = this.getClass().getSimpleName();

    private PerformanceFragment perfFragment;
    private HistoryFragment historyFragment;
    private LevelUserFragment levelFragment;
    private SectionsStatePagerAdapter mSectionStatePagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_profile);

        mSectionStatePagerAdapter = new SectionsStatePagerAdapter(getSupportFragmentManager());

        // Do this in case of detaching of Fragments
        perfFragment = new PerformanceFragment();
        historyFragment = new HistoryFragment();
        levelFragment = new LevelUserFragment();

        ViewPager mViewPager = findViewById(R.id.profileViewPager);
        setUpViewPager(mViewPager);

        // Set NewRecordingFragment as default tab once started the activity
        mViewPager.setCurrentItem(mSectionStatePagerAdapter.getPositionByTitle("User level"));

    }


    private void setUpViewPager(ViewPager mViewPager) {
        mSectionStatePagerAdapter.addFragment(levelFragment, "User level");
        mSectionStatePagerAdapter.addFragment(perfFragment, "Performance");
        mSectionStatePagerAdapter.addFragment(historyFragment, "History");
        mViewPager.setAdapter(mSectionStatePagerAdapter);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }

}
