package com.pauli.justdanceproject;

import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ShowProfile extends AppCompatActivity implements MusicalinetteFragment
        .OnFragmentInteractionListener, CreuseFragment.OnFragmentInteractionListener,
        HerculeFragment.OnFragmentInteractionListener{

    private final String TAG = this.getClass().getSimpleName();

    private MusicalinetteFragment chantajeFragment;
    private CreuseFragment lalalandFragment;
    private HerculeFragment herculeFragment;
    private SectionsStatePagerAdapter mSectionStatePagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_profile);

        mSectionStatePagerAdapter = new SectionsStatePagerAdapter(getSupportFragmentManager());

        // Do this in case of detaching of Fragments
        chantajeFragment = new MusicalinetteFragment();
        lalalandFragment = new CreuseFragment();
        herculeFragment = new HerculeFragment();

        ViewPager mViewPager = findViewById(R.id.profileViewPager);
        setUpViewPager(mViewPager);

        // Set NewRecordingFragment as default tab once started the activity
        mViewPager.setCurrentItem(mSectionStatePagerAdapter.getPositionByTitle("User level"));

    }


    private void setUpViewPager(ViewPager mViewPager) {
        mSectionStatePagerAdapter.addFragment(herculeFragment,getString(R.string.HerculeName));
        mSectionStatePagerAdapter.addFragment(lalalandFragment, getString(R.string.LalalandName));
        mSectionStatePagerAdapter.addFragment(chantajeFragment, getString(R.string.ShakiraName));
        mViewPager.setAdapter(mSectionStatePagerAdapter);
    }

    @Override
        public void onFragmentInteraction(Uri uri) {
    }
}
