package com.pauli.justdanceproject;

import android.app.Dialog;
import android.arch.persistence.room.Room;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.util.Log;

import java.util.concurrent.atomic.AtomicBoolean;

public class DanceActivity extends AppCompatActivity {

    static final String NUMBER_POINTS = "Number_points";  //Added by Pauline for finish activity
    static final String MUSIC_NAME = "Music_name";

    //private CloneDanceRoomDatabase danceDB;
    private String userID;
    private String music_name;
    private int score=0;
    private String user_db;
    // temp
    private String TAG = this.getClass().getSimpleName();
    private int counter;
    private TextView mText;
    private MusicDance musical = null;
    private Handler mHandler;
    //private int[] music = null;
    private Boolean resume = true;
    private TextView goodOrBad = null;
    private ImageView toCancelImageButtonView = null;
    private ImageView nextImageButtonView = null;
    private ImageView actualImageButtonView = null;
    private int index = 0;
    private int nextPosition = 0;
    private int askedPosition = 0;
    private int actualPosition = 0;
    final int error = 5;
    private static final String PROGRESS_BAR_INCREMENT="ProgressBarIncrementId";
    private ProgressBar bar;
    AtomicBoolean isRunning = new AtomicBoolean(false);
    AtomicBoolean isPausing = new AtomicBoolean(false);
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int progress=msg.getData().getInt(PROGRESS_BAR_INCREMENT);
            // Incrémenter la ProgressBar, on est bien dans la Thread de l'IHM
            bar.incrementProgressBy(progress);
            // On peut faire toute action qui met à jour l'IHM

        }
    };

    public static final String RECEIVE_COUNTER = "RECEIVE_COUNTER";
    public static final String COUNTER = "COUNTER";

    private AccRateBroadcastReceiver accRateBroadcastReceiver;
    public static CloneDanceRoomDatabase cloneDanceRD;

    private static final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static final DatabaseReference profileGetRef = database.getReference("profiles");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dance);
        // Temp Hugo
        mText = findViewById(R.id.textViewMovements);
        counter =0;
        WearService.setToZero();

        ImageButton button = findViewById(R.id.pausebutton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResumeMusic(v);
            }
        });

        // Create instance of Sport Tracker Room DB
        cloneDanceRD = Room.databaseBuilder(getApplicationContext(), CloneDanceRoomDatabase.class, "db").allowMainThreadQueries().build();
        int[] music;
        Bundle bunble = getIntent().getExtras();
        if (bunble!=null){
            music = bunble.getIntArray("musicchosen");
            userID = bunble.getString(LaunchActivity.USER_ID);
            if(music!=null) {
                musical = new MusicDance( music, this);
                musical.getSound().start();
                musical.getSound().setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
                    public void onCompletion(MediaPlayer mp){
                        profileGetRef.child(userID).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String user_db = dataSnapshot.child("username").getValue(String.class);

                                DatabaseEntity dbEntity = new DatabaseEntity();
                                dbEntity.setUser_name(user_db);
                                dbEntity.setMusic(musical.getName());
                                dbEntity.setScore(score);

                                cloneDanceRD.dataDao().insertEntity(dbEntity);
                                //Toast.makeText(getApplicationContext(), "Username" + user_db + "\n Music Name" + musical.getName() + "\n score" + score, Toast.LENGTH_LONG).show();

                            }

                            @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                System.out.println("The read failed: " + databaseError.getCode());
                            }
                        });
                        //String username = database.getReference("profiles").child(userID).child("username").toString();

                        Intent intentFinishDance = new Intent(DanceActivity.this, FinishActivity.class);
                        intentFinishDance.putExtra(LaunchActivity.USER_ID, userID);
                        intentFinishDance.putExtra(NUMBER_POINTS, score);
                        intentFinishDance.putExtra(MUSIC_NAME, musical.getName());
                        isRunning.set(false);
                        startActivity(intentFinishDance);
                        finish();
                    }
                });
                mHandler = new Handler();
                resume = false;
                index = 0;
                askedPosition = 0;
                actualPosition = 100;
                bar = findViewById(R.id.progress);
                bar.setMax(210);
            }
        }
        if(Boolean.parseBoolean(BuildConfig.W_flag_watch_enable)){
            // Change to dance activity
            Communication.changeWatchActivity(DanceActivity.this,BuildConfig.W_dance_activity);
        }
        accRateBroadcastReceiver = new AccRateBroadcastReceiver();
        Log.d(TAG,"New Local BroadCastManager (OnCreat)");
        LocalBroadcastManager.getInstance(this).registerReceiver(accRateBroadcastReceiver, new
                IntentFilter(RECEIVE_COUNTER));
    }

    @Override
    public void onStart() {
        super.onStart();
        mHandler.postDelayed(startMusic,1000);
        bar.setProgress(0);
        Thread background = new Thread(progressRunnable);
        isPausing.set(true);
        isRunning.set(true);
        background.start();// Get the accelerometer datas back from the watch
    }

    @Override
    protected void onPause() {
        super.onPause();
        isPausing.set(true);
        if(Boolean.parseBoolean(BuildConfig.W_flag_watch_enable)) {
            Communication.stopRecordingOnWear(DanceActivity.this, BuildConfig.W_dance_activity);
        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(accRateBroadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isPausing.set(false);
        // Get the accelerometer datas back from the watch
        if(accRateBroadcastReceiver == null){
        accRateBroadcastReceiver = new AccRateBroadcastReceiver();
        Log.d(TAG,"New Local BroadCastManager");
        LocalBroadcastManager.getInstance(this).registerReceiver(accRateBroadcastReceiver, new
                IntentFilter(RECEIVE_COUNTER));}
    }

    private class AccRateBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // Show AR in a TextView
            actualPosition = intent.getIntExtra(COUNTER,0);
            counter++;
            mText.setText("mode: " + actualPosition+"\n"
                    +"counter: " + counter);
            Log.d(TAG, "counter : "+ counter + "Wearservice counter : " + WearService.getCount());
        }
    }

    public void movementsOnMusic(){
        int i;
        int delay;
        i=index*2;
        //Initialisation des AtomicBooleans
        isPausing.set(false);
        while(i<musical.getTiming().length){
            delay = musical.getTiming()[i];
            mHandler.postDelayed(r1,350*i+delay-musical.getSound().getCurrentPosition());
            mHandler.postDelayed(r2,350*i+delay-musical.getSound().getCurrentPosition()+650);
            mHandler.postDelayed(r3,350*i+delay-musical.getSound().getCurrentPosition()+1100);
            i=i+2;
        }
    }

    public void ResumeMusic(View view) {
        musical.getSound().pause();
        mHandler.removeCallbacks(r1);
        mHandler.removeCallbacks(r2);
        mHandler.removeCallbacks(r3);
        resume = true;
        isPausing.set(true);

        final Dialog dialog = new Dialog(this); // Context, this, etc.
        dialog.setContentView(R.layout.dialog_yes_no);
        TextView mTextInfo = dialog.findViewById(R.id.dialog_info_yes_no);
        mTextInfo.setText(R.string.QuitOrResumeText);
        Button quitButton = dialog.findViewById(R.id.buttonYesDialog);
        quitButton.setText(getString(R.string.ButtonQuit));
        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRunning.set(false);
                musical.getSound().stop();
                // Leave
                Intent intent_change = new Intent(getApplication(), MainActivity.class);
                intent_change.putExtra(LaunchActivity.USER_ID,userID);
                startActivity(intent_change);
                finish();
            }
        });

        Button resumeButton = dialog.findViewById(R.id.buttonNoDialog);
        resumeButton.setText(getString(R.string.ButtonResume));
        resumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  Action for 'NO' Button
                dialog.cancel();
                musical.getSound().start();
                resume = false;
                movementsOnMusic();
                isPausing.set(false);
            }
        });
        dialog.show();
    }
    private Runnable progressRunnable = new Runnable() {
        Bundle messageBundle=new Bundle();
        Message myMessage;
        public void run() {
            try {
                while (isRunning.get()) {
                    while (isPausing.get() && (isRunning.get())) {
                        Thread.sleep(100);
                    }
                    Thread.sleep(100);
                    myMessage=handler.obtainMessage();
                    if(askedPosition == actualPosition) {
                        messageBundle.putInt(PROGRESS_BAR_INCREMENT,3);
                        score = score+3;
                        goodOrBad = findViewById(R.id.danceGoodOrOk);
                        goodOrBad.setText(getString(R.string.good));

                    }else if(nextPosition == actualPosition){
                        messageBundle.putInt(PROGRESS_BAR_INCREMENT,1);
                        score=score+1;
                        goodOrBad = findViewById(R.id.danceGoodOrOk);
                        goodOrBad.setText(getString(R.string.ok));
                    }else{
                        messageBundle.putInt(PROGRESS_BAR_INCREMENT,1);
                    }
                    myMessage.setData(messageBundle);
                    handler.sendMessage(myMessage);
                }
            } catch (Throwable t) {
            }
        }
    };

    final Runnable r1 = new Runnable() {
        public void run() {
            nextPosition = musical.getTiming()[index*2+1];
            switch (nextPosition){
                case 1:
                    nextImageButtonView = findViewById(R.id.UpView);
                    break;
                case 2:
                    nextImageButtonView = findViewById(R.id.MiddleView);
                    break;
                case 3:
                    nextImageButtonView = findViewById(R.id.BottomView);
                    break;
            }
            nextImageButtonView.setActivated(true);
            nextImageButtonView.setEnabled(true);
            index = index+1;

            actualImageButtonView = nextImageButtonView;
        }
    };
    final Runnable r2 = new Runnable() {
        public void run() {
            askedPosition = nextPosition;
            actualImageButtonView.setActivated(true);
            actualImageButtonView.setEnabled(false);

            toCancelImageButtonView= actualImageButtonView;
        }
    };

    final Runnable r3 = new Runnable() {
        public void run() {
            toCancelImageButtonView.setActivated(false);
            nextPosition = 0;
            askedPosition = 0;

            goodOrBad = findViewById(R.id.danceGoodOrOk);
            goodOrBad.setText("");
        }
    };

    final Runnable startMusic = new Runnable() {
        public void run() {
            movementsOnMusic();
        }
    };
}

