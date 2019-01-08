package com.pauli.justdanceproject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class CreuseFragment extends Fragment {

    private final String TAG = this.getClass().getSimpleName();
    AlertDialog.Builder builder;
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
            case R.id.txt_edit_profile:
                Intent intent_edit = new Intent(getActivity(), EditUser.class);
                intent_edit.putExtra(LaunchActivity.USER_ID, userID);
                intent_edit.putExtra(EditUser.ACTIVITY_NAME,MainActivity.ACTIVITY_NAME);
                startActivity(intent_edit);
                getActivity().finish();
                break;
            case R.id.txt_change_user:                            // to copy in main activity and the three fragments
                // Check if user really wants to change
                builder = new AlertDialog.Builder(getActivity());

                //Uncomment the below code to Set the message and title from the strings.xml file
                builder.setMessage("Change user").setTitle("userChange");

                //Setting message manually and performing action on button click
                builder.setMessage(R.string.QuestionChangeUser)
                        .setCancelable(false)
                        .setPositiveButton(R.string.Yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Toast.makeText(getActivity().getApplicationContext(),getString(R.string.YesChangeUserPopUp),
                                        Toast.LENGTH_SHORT).show();
                                // Leave
                                Intent intent_change = new Intent(getActivity().getApplication(), LaunchActivity.class);
                                startActivity(intent_change);
                                getActivity().finish();

                            }
                        })
                        .setNegativeButton(R.string.No, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //  Action for 'NO' Button
                                dialog.cancel();
                                Toast.makeText(getActivity().getApplicationContext(),getString(R.string.NoChangeUserPopUp),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                //Creating dialog box
                AlertDialog alert = builder.create();
                //Setting the title manually
                alert.setTitle(R.string.ChangeOfDancerTitle);
                alert.show();

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
        fragmentView = inflater.inflate(R.layout.fragment_history, container, false);

        listView = fragmentView.findViewById(R.id.myHistoryList);
        adapter = new RecordingAdapter(getActivity(), R.layout.row_history_layout);
        listView.setAdapter(adapter);

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
