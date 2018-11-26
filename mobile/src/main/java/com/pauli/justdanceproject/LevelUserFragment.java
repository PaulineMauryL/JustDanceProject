package com.pauli.justdanceproject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LevelUserFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class LevelUserFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private String userID;
    private View fragmentView;
    private Profile userProfile;
    AlertDialog.Builder builder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_level_user, container, false);

        Intent intent = getActivity().getIntent();
        if (intent.hasExtra(LaunchActivity.USER_ID)) {
            userID = intent.getStringExtra(LaunchActivity.USER_ID);
        }
        //readUserProfile();
        return fragmentView;
    }

    /*private void readUserProfile() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference profileRef = database.getReference("profiles");
        profileRef.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String user_db = dataSnapshot.child("username").getValue(String.class);
                String photo = dataSnapshot.child("photo").getValue(String.class);
                String language = dataSnapshot.child("language").getValue(String.class);
                userProfile = new Profile(user_db, language);

                userProfile.photoPath = photo;

                setUserImageAndProfileInfo();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Empty
            }
        });
    }

    private void setUserImageAndProfileInfo() {
        //  Reference to an image file in Firebase Storage
        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl
                (userProfile.photoPath);
        storageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                if (isAdded()) {
                    final Bitmap selectedImage = BitmapFactory.decodeByteArray(bytes, 0, bytes
                            .length);
                    ImageView imageView = fragmentView.findViewById(R.id.userImage);
                    imageView.setImageBitmap(selectedImage);
                }
            }
        });

        TextView usernameTextView = fragmentView.findViewById(R.id.txt_usernameValue);
        usernameTextView.setText(userProfile.username);

        TextView levelTextView = fragmentView.findViewById(R.id.txt_levelValue);
        levelTextView.setText(userProfile.level);
    } */

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
                startActivity(intent_dance);
                break;
            case R.id.txt_edit_profile:
                Intent intent_edit = new Intent(getActivity(), EditUser.class);
                intent_edit.putExtra(LaunchActivity.USER_ID, userID);
                startActivity(intent_edit);
                break;
            case R.id.txt_change_user:                            // to copy in main activity and the three fragments
                // Check if user really wants to change
                builder = new AlertDialog.Builder(getActivity());

                //Uncomment the below code to Set the message and title from the strings.xml file
                builder.setMessage("Change user").setTitle("userChange");

                //Setting message manually and performing action on button click
                builder.setMessage("Do you really want to change user ?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                getActivity().finish();
                                Toast.makeText(getActivity().getApplicationContext(),"Ok ! See you another time",
                                        Toast.LENGTH_SHORT).show();
                                // Leave
                                Intent intent_change = new Intent(getActivity().getApplication(), LaunchActivity.class);
                                startActivity(intent_change);
                                getActivity().finish();

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //  Action for 'NO' Button
                                dialog.cancel();
                                Toast.makeText(getActivity().getApplicationContext(),"Good choice",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                //Creating dialog box
                AlertDialog alert = builder.create();
                //Setting the title manually
                alert.setTitle("Change of dancer");
                alert.show();

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public LevelUserFragment() {
        // Required empty public constructor
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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
