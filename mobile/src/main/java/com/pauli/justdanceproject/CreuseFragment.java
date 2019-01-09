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
import java.util.Locale;


public class CreuseFragment extends Fragment {

    private final String TAG = this.getClass().getSimpleName();
    private OnFragmentInteractionListener mListener;
    private ListView listView;
    private View fragmentView;
    private RecordingAdapter adapter;
    //private MyFirebaseRecordingListener mFirebaseRecordingListener;
    private DatabaseReference databaseRef;
    private String userID;

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

    public CreuseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_creuse, container, false);

        Intent intent = getActivity().getIntent();
        if (intent.hasExtra(LaunchActivity.USER_ID)) {
            userID = intent.getStringExtra(LaunchActivity.USER_ID);
        }

        return fragmentView;
    }

    private class RecordingAdapter extends ArrayAdapter<Recording> {
        private int row_layout;

        RecordingAdapter(FragmentActivity activity, int row_layout) {
            super(activity, row_layout);
            this.row_layout = row_layout;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            //Reference to the row View
            View row = convertView;

            if (row == null) {
                //Inflate it from layout
                row = LayoutInflater.from(getContext()).inflate(row_layout, parent, false);
            }

            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.getDefault());
            ((TextView) row.findViewById(R.id.danceType)).setText(getItem(position).danceName);
            ((TextView) row.findViewById(R.id.danceDateTime)).setText(formatter.format(new Date(getItem(position).danceDateTime)));

            ((TextView) row.findViewById(R.id.dancePoints)).setText(String.valueOf(getItem(position).nbPoints));
            ((TextView) row.findViewById(R.id.danceLevel)).setText(getItem(position).level);

            return row;
        }
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
