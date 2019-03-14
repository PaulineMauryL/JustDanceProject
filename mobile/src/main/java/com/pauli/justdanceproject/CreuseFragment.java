package com.pauli.justdanceproject;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class CreuseFragment extends Fragment {

    private final String TAG = this.getClass().getSimpleName();
    private OnFragmentInteractionListener mListener;
    private View fragmentView;
    private String userID;
    private String username;
    private TextView TxtPlayer;
    private TextView TxtScore;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_show_profile, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.txt_dance:
                Intent intent_dance = new Intent(getActivity(), MainActivity.class);
                intent_dance.putExtra(MainActivity.USER_ID, userID);
                intent_dance.putExtra(MainActivity.USERNAME,username);
                startActivity(intent_dance);
                if(Boolean.parseBoolean(BuildConfig.W_flag_watch_enable)){
                    // Change to dance activity
                    Communication.changeWatchActivity(getActivity(),BuildConfig.W_watchmain_activity_start);
                }
                getActivity().finish();
                break;
            case R.id.txt_edit_profile:
                Intent intent_edit = new Intent(getActivity(), EditUser.class);
                intent_edit.putExtra(MainActivity.USER_ID, userID);
                intent_edit.putExtra(EditUser.ACTIVITY_NAME,MainActivity.ACTIVITY_NAME);
                startActivity(intent_edit);
                if(Boolean.parseBoolean(BuildConfig.W_flag_watch_enable)){
                    // Change to dance activity
                    Communication.changeWatchActivity(getActivity(),BuildConfig.W_edit_activity_start);
                }
                getActivity().finish();
                break;
            case R.id.txt_change_user:                            // to copy in main activity and the three fragments
                DialogueYesNo dialogueYesNo = new DialogueYesNo(DialogueYesNo.EDIT_PROFILE,getActivity(), getString(R.string.QuestionChangeUser),true);
                dialogueYesNo.create(getString(R.string.YesChangeUserPopUp), getString(R.string.NoChangeUserPopUp));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public CreuseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_creuse, container, false);

        Intent intent = getActivity().getIntent();
        if (intent.hasExtra(MainActivity.USER_ID)) {
            userID = intent.getStringExtra(MainActivity.USER_ID);
            username = intent.getStringExtra(MainActivity.USERNAME);
        }
        TxtPlayer = fragmentView.findViewById(R.id.txt_display_user);
        TxtScore = fragmentView.findViewById(R.id.txt_display_points);
        List<DatabaseEntity> entities = LaunchActivity.cloneDanceRD.dataDao().getHallOfFame(String.valueOf(R.raw.creuse));

        String player = "";
        String point = "";

        for(DatabaseEntity dbEnt : entities){
            String user_name = dbEnt.getUser_name();
            int score = dbEnt.getScore();
            player = player + user_name +"\n";
            point = point + score +"\n";
        }

        TxtPlayer.setText(player);
        TxtScore.setText(point);

        return fragmentView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
