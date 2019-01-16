package com.pauli.justdanceproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class EditUser extends AppCompatActivity {

    public static final String ACTIVITY_NAME = "activity_name";

    private static final int PICK_IMAGE = 1;
    private File imageFile;
    private Profile userProfile;
    private String userID;
    private String username;


    private static final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static final DatabaseReference profileGetRef = database.getReference("profiles");
    private static DatabaseReference profileRef = profileGetRef.push();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        TextView edit_username = findViewById(R.id.editUsername);

        Intent intent = getIntent();

        if (intent.hasExtra(MainActivity.USER_ID)) {
            userID = intent.getStringExtra(MainActivity.USER_ID);
            fetchDataFromFirebase();
        } else if (intent.hasExtra(MainActivity.USERNAME)) {
            username = intent.getStringExtra(MainActivity.USERNAME);
            edit_username.setText(username);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////// Save editted data in Firebase /////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////
    public void edit_ok(View view) {
        // change info in database with username, photo and languageString
        boolean checked = false;
        checked = checkProfile(checked);
        if(checked) {
            TextView username = findViewById(R.id.editUsername);
            userProfile = new Profile(username.getText().toString());

            if (imageFile == null) {
                userProfile.photoPath = "";
            } else {
                userProfile.photoPath = imageFile.getPath();
            }

            if(getIntent().getStringExtra(ACTIVITY_NAME).equals(LaunchActivity.ACTIVITY_NAME)){

                addProfileToFirebaseDB();
            } else if(getIntent().getStringExtra(ACTIVITY_NAME).equals(MainActivity.ACTIVITY_NAME)){
                // Create Intent
            }
        }
    }
    private boolean checkProfile(boolean checked) {
        final String username = ((EditText) findViewById(R.id.editUsername)).getText().toString();
        if(username.equals("")) {           //if no name then not ok
            Toast.makeText(this, R.string.nameEmpty, Toast.LENGTH_SHORT).show();
        }
        else { checked = true; }
        return checked;
    }
    private void addProfileToFirebaseDB() {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) ((ImageView) findViewById(R.id
                .userImage)).getDrawable();
        if (bitmapDrawable == null) {
            Toast.makeText(this, R.string.selectProfilePicture, Toast.LENGTH_SHORT).show();
            return;
        }
        Bitmap bitmap = bitmapDrawable.getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
        byte[] data = baos.toByteArray();

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference photoRef = storageRef.child("photos").child(profileRef.getKey() + ".jpg");
        UploadTask uploadTask = photoRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(EditUser.this, R.string.uploadPictureFailed, Toast
                        .LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new PhotoUploadSuccessListener());
    }
    private class PhotoUploadSuccessListener implements OnSuccessListener<UploadTask.TaskSnapshot> {
        @Override
        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(final Uri uri) {
                    userProfile.photoPath = uri.toString();
                    userID=profileRef.getKey();
                    profileRef.runTransaction(new ProfileDataUploadHandler());
                }
            });
        }
    }
    private class ProfileDataUploadHandler implements Transaction.Handler {
        @NonNull
        @Override
        public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
            mutableData.child("username").setValue(userProfile.username);
            mutableData.child("photo").setValue(userProfile.photoPath);
            return Transaction.success(mutableData);
        }

        @Override
        public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable
                DataSnapshot dataSnapshot) {
            if (b) {
                Toast.makeText(EditUser.this, R.string.registrationSuccessful, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra(MainActivity.USER_ID, userID);
                intent.putExtra(MainActivity.USERNAME,username);
                startActivity(intent);
                if(Boolean.parseBoolean(BuildConfig.W_flag_watch_enable)){
                    // Change to dance activity
                    Communication.changeWatchActivity(EditUser.this,BuildConfig.W_watchmain_activity_start);
                }
                finish();
            } else {
                Toast.makeText(EditUser.this, R.string.registrationFailed, Toast.LENGTH_SHORT).show();
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////// Return to launch or main, nothing kept ///////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////
    public void do_not_edit(View view) {  // Check if user really wants to change
        if(getIntent().getStringExtra(ACTIVITY_NAME).equals(LaunchActivity.ACTIVITY_NAME)){

            DialogueYesNo dialogueYesNo = new DialogueYesNo(DialogueYesNo.SAVE,this, getString(R.string.quit_without_save));
            dialogueYesNo.create("", "");
        } else if(getIntent().getStringExtra(ACTIVITY_NAME).equals(MainActivity.ACTIVITY_NAME)){
            // Create Intent
            Intent intent = new Intent(EditUser.this, MainActivity.class);
            intent.putExtra(MainActivity.USER_ID, userID);
            intent.putExtra(MainActivity.USERNAME,username);

            DialogueYesNo dialogueYesNo = new DialogueYesNo(DialogueYesNo.SAVE2MAIN,this, getString(R.string.quit_without_save),intent);
            dialogueYesNo.create("", "");
        }


    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////// Select and handle Image ////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////
    public void chooseImage(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(intent, getString(R.string.selectPicture)), PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            imageFile = new File(getExternalFilesDir(null), "profileImage");
            try {
                copyImage(imageUri, imageFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            final InputStream imageStream;
            try {
                Uri savedImageUri = Uri.fromFile(imageFile);
                imageStream = getContentResolver().openInputStream(Uri.fromFile(imageFile));
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                ImageView imageView = findViewById(R.id.userImage);
                imageView.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
    private void copyImage(Uri uriInput, File fileOutput) throws IOException {
        InputStream in = null;
        OutputStream out = null;

        try {
            in = getContentResolver().openInputStream(uriInput);
            out = new FileOutputStream(fileOutput);
            // Transfer bytes from in to out
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            in.close();
            out.close();
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////If from mainActivity: Put user info in layout ////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////
    private void fetchDataFromFirebase() {
        final TextView usernameTextView = findViewById(R.id.editUsername);

        profileGetRef.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String user_db = dataSnapshot.child("username").getValue(String.class);
                String photo_db = dataSnapshot.child("photo").getValue(String.class);

                usernameTextView.setText(user_db);

                //  Reference to an image file in Firebase Storage
                StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(photo_db);
                storageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        final Bitmap selectedImage = BitmapFactory.decodeByteArray(bytes, 0,
                                bytes.length);
                        ImageView imageView = findViewById(R.id.userImage);
                        imageView.setImageBitmap(selectedImage);
                    }
                });
                profileRef = profileGetRef.child(userID);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_finish, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.txt_change_user:                            // to copy in main activity and the three fragments
                // Check if user really wants to change
                DialogueYesNo dialogueYesNo = new DialogueYesNo(DialogueYesNo.EDIT_PROFILE,this, getString(R.string.QuestionChangeUser));
                dialogueYesNo.create(getString(R.string.YesChangeUserPopUp), getString(R.string.NoChangeUserPopUp));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

