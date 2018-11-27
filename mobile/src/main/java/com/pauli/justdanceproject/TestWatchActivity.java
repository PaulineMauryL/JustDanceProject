package com.pauli.justdanceproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TestWatchActivity extends AppCompatActivity {
    /* TAG */
    private final String TAG = this.getClass().getSimpleName();

    /* Flags */
    private static final boolean DEBUG_FLAG = true;
    private static final int PICK_IMAGE = 1;

    /* Enum */
    private enum Send2WatchAction {
        SEND_IMAGE, SEND_MESSAGE, START_ACTIVITY
    }

    private final String[] button_message = { "Back to the Main Activity","Start sleep Mode"};
    private int count_sleep = 1;
    private File imageFile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_watch);
// Button Send Image Callback
        final Button b_sendImage = findViewById(R.id.buttonSendImage);
        b_sendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DEBUG_FLAG) {
                    Log.d(TAG, "buttonSendImage clicked -> call send2Watch()");
                }
                send2Watch(Send2WatchAction.SEND_IMAGE);
            }
        });

        // Button Send Message
        final Button b_sendMessage = findViewById(R.id.buttonSendMessage);
        b_sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DEBUG_FLAG) {
                    Log.d(TAG, "buttonSendMessage clicked -> call send2Watch()");
                }
                send2Watch(Send2WatchAction.SEND_MESSAGE);
            }
        });
        // Floating Button choose image
        android.support.design.widget.FloatingActionButton b_chooseImage = findViewById(R.id.buttonChooseImage);
        b_chooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DEBUG_FLAG) {
                    Log.d(TAG, "buttonChooseImage clicked -> call chooseImage()");
                }
                chooseImage();
            }
        });

        // Button Start Activity
        final Button b_changingActivity = findViewById(R.id.buttonChangingActivity);
        b_changingActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DEBUG_FLAG) {
                    Log.d(TAG, "buttonSleepMode clicked -> call send2Watch()");
                }
                send2Watch(Send2WatchAction.START_ACTIVITY);
                sendStartChangeActivity();
                if (count_sleep == 0) {
                    b_sendMessage.setEnabled(true);
                    b_sendImage.setEnabled(true);
                    count_sleep = 1;
                } else {
                    b_sendMessage.setEnabled(false);
                    b_sendImage.setEnabled(false);
                    count_sleep = 0;
                }
                b_changingActivity.setText(button_message[count_sleep]);
            }
        });
    }

    private void chooseImage() {
        Intent it = new Intent();
        it.setType("image/*");
        it.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(it, "Select an Image."), PICK_IMAGE);
    }

    private void send2Watch(Send2WatchAction mode) {
        boolean flag = false; // Set true if the case has been implemented
        switch (mode) {
            case START_ACTIVITY:
                if (DEBUG_FLAG) {
                    Log.d(TAG, "CASE: START_SLEEP");
                    Toast.makeText(this, "CASE: START_SLEEP", Toast.LENGTH_SHORT).show();
                }
                flag = true; // This case has been implemented
                if (DEBUG_FLAG) {
                    Log.d(TAG, "CASE: START_SLEEP, Watch Activity changed!");
                    Toast.makeText(this, "CASE: START_SLEEP, Watch Activity changed!", Toast.LENGTH_SHORT).show();
                }
                break;
            case SEND_IMAGE:
                if (DEBUG_FLAG) {
                    Log.d(TAG, "CASE: SEND_IMAGE");
                    Toast.makeText(this, "CASE: SEND_IMAGE", Toast.LENGTH_SHORT).show();
                }
                flag = true; // This case has been implemented
                if (imageFile != null) {
                    sendImage();
                    if (DEBUG_FLAG) {
                        Log.d(TAG, "CASE: SEND_IMAGE, image sent!");
                        Toast.makeText(this, "CASE: SEND_IMAGE, image sent!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (DEBUG_FLAG) {
                        Log.d(TAG, "CASE: SEND_MESSAGE, message empty");
                        Toast.makeText(this, "CASE: SEND_MESSAGE, message empty", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case SEND_MESSAGE:
                if (DEBUG_FLAG) {
                    Log.d(TAG, "CASE: SEND_MESSAGE");
                    Toast.makeText(this, "CASE: SEND_MESSAGE", Toast.LENGTH_SHORT).show();
                }
                flag = true; // This case has been implemented
                EditText t_message = findViewById(R.id.editTextMessage);
                String s_message = t_message.getText().toString();
                if (s_message != null) {
                    sendMessage(s_message);
                    if (DEBUG_FLAG) {
                        Log.d(TAG, "CASE: SEND_MESSAGE, message sent!");
                        Toast.makeText(this, "CASE: SEND_MESSAGE, message sent!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (DEBUG_FLAG) {
                        Log.d(TAG, "CASE: SEND_MESSAGE, message empty");
                        Toast.makeText(this, "CASE: SEND_MESSAGE, message empty", Toast.LENGTH_SHORT).show();
                    }
                }

                break;
            default:
                if (DEBUG_FLAG) {
                    Log.d(TAG, "Error: Action not defined!");
                    Toast.makeText(this, "ERROR: Case undefined!", Toast.LENGTH_SHORT).show();
                }
                break;
        }

        if (!flag) {
            // The selected case hasn't been implemented yet
            if (DEBUG_FLAG) {
                Log.d(TAG, "This case hasn't been implemented yet!");
                Toast.makeText(this, "Not implemented yet!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendStartChangeActivity() {
        Intent intent = new Intent(this, WearService.class);
        intent.setAction(WearService.ACTION_SEND.STARTACTIVITY.name());
        String path;
        if (count_sleep == 1) {
            path = BuildConfig.W_sleepactivity;
        } else {
            path = BuildConfig.W_watchmainactivity;
        }
        intent.putExtra(WearService.ACTIVITY_TO_START, path);
        startService(intent);
    }

    private void sendImage() {
        String imagePath = imageFile.getPath();
        // Create the intent to communique with the service
        Intent intentWear = new Intent(TestWatchActivity.this, WearService.class);
        intentWear.setAction(WearService.ACTION_SEND.SEND_IMAGE.name());
        intentWear.putExtra(WearService.IMAGE, imagePath);
        startService(intentWear);
    }

    private void sendMessage(String s_message2Watch) {
        Intent intent = new Intent(this, WearService.class);
        intent.setAction(WearService.ACTION_SEND.MESSAGE.name());
        intent.putExtra(WearService.MESSAGE, s_message2Watch);
        intent.putExtra(WearService.PATH, BuildConfig.W_path_message);
        startService(intent);
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
                imageStream = getContentResolver().openInputStream(Uri.fromFile(imageFile));
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                ImageView imageView = findViewById(R.id.imageTest);
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
}

