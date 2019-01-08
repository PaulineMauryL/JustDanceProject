package com.pauli.justdanceproject;

import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ShowProfile extends AppCompatActivity implements MusicalinetteFragment
        .OnFragmentInteractionListener, CreuseFragment.OnFragmentInteractionListener,
        HerculeFragment.OnFragmentInteractionListener{

    private final String TAG = this.getClass().getSimpleName();

    private MusicalinetteFragment perfFragment;
    private CreuseFragment historyFragment;
    private HerculeFragment levelFragment;
    private SectionsStatePagerAdapter mSectionStatePagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_profile);

        mSectionStatePagerAdapter = new SectionsStatePagerAdapter(getSupportFragmentManager());

        // Do this in case of detaching of Fragments
        perfFragment = new MusicalinetteFragment();
        historyFragment = new CreuseFragment();
        levelFragment = new HerculeFragment();

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
