package com.pauli.justdanceproject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.google.firebase.database.DatabaseReference;

import java.text.DecimalFormat;
import java.util.ArrayList;

import static android.graphics.Color.RED;
import static android.graphics.Color.TRANSPARENT;


public class MusicalinetteFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private String userID;
    private String recID;
    private View fragmentView;

    private DatabaseReference recordingRef;
    AlertDialog.Builder builder;
    public static final String POINTS_PLOT_WATCH = "Points Smart Watch";  //pas sure de comprendre ce que je fait ici..
    private static final int NUMBER_OF_POINTS = 50;
    private static final int MIN_POINTS= 0;
    private static final int MAX_POINTS = 1000;
    private static XYPlot dancePlot;
    private final String TAG = this.getClass().getSimpleName();
    private XYplotSeriesList xyPlotSeriesList;

    private ArrayList<Integer> hrDataArrayList = new ArrayList<>();
    
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
                intent_dance.putExtra(LaunchActivity.USER_ID, userID);
                startActivity(intent_dance);
                getActivity().finish();
                break;
            case R.id.txt_edit_profile:
                Intent intent_edit = new Intent(getActivity(), EditUser.class);
                intent_edit.putExtra(LaunchActivity.USER_ID, userID);
                intent_edit.putExtra(EditUser.ACTIVITY_NAME,MainActivity.ACTIVITY_NAME);
                startActivity(intent_edit);
                getActivity().finish();
                break;
            case R.id.txt_change_user:                            // to copy in main activity and the three fragments
                DialogueYesNo dialogueYesNo = new DialogueYesNo(DialogueYesNo.EDIT_PROFILE,getActivity(), getString(R.string.QuestionChangeUser),true);
                dialogueYesNo.create(getString(R.string.YesChangeUserPopUp), getString(R.string.NoChangeUserPopUp));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public MusicalinetteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_musicalinette, container, false);

        Intent intent = getActivity().getIntent();
        if (intent.hasExtra(LaunchActivity.USER_ID)) {
            userID = intent.getStringExtra(LaunchActivity.USER_ID);
        }
        if (intent.hasExtra(MainActivity.RECORDING_ID)) {
            recID = intent.getStringExtra(MainActivity.RECORDING_ID);
        }

    /*
        // Get recording information from Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference profileGetRef = database.getReference("profiles");

        recordingRef = profileGetRef.child(userID).child("recordings").child(recID);

        recordingRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                TextView danceType = fragmentView.findViewById(R.id.danceTypeLive);
                danceType.setText(dataSnapshot.child("selected_dance").getValue().toString());

                TextView danceDatetime = fragmentView.findViewById(R.id.danceDateTimeLive);
                Long datetime = Long.parseLong(dataSnapshot.child("datetime").getValue().toString());
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.getDefault());
                danceDatetime.setText(formatter.format(new Date(datetime)));

                String nb_pts = dataSnapshot.child("points").getValue().toString();
                TextView txt_points = fragmentView.findViewById(R.id.danceNbPointsLive);
                txt_points.setText(nb_pts);

                String level = dataSnapshot.child("level").getValue().toString();
                TextView hrBelt = fragmentView.findViewById(R.id.danceLevelLive);
                hrBelt.setText(level);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    */
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
