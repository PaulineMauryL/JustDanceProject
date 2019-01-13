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
    private final String TAG = this.getClass().getSimpleName();
    private static final String GRADE_DANCE = "GRADE_DANCE";
    public static final String RECEIVE_COUNTER = "RECEIVE_COUNTER";
    public static final String COUNTER = "COUNTER";
    private final int POINTS_ON_TIMES = 3;
    private final int POINTS_IN_ADVANCE = 1;
    private final int MAX_POINTS = 10000;
    private final int MAX_BAR = 210;
    private float points;
    //private CloneDanceRoomDatabase danceDB;
    private String userID;
    private float score=0;
    // temp
    private TextView mText = null;
    private MusicDance musical = null;
    private Handler mHandler;
    private ImageView goodOrBad = null;
    private ImageView toCancelImageButtonView = null;
    private ImageView nextImageButtonView = null;
    private ImageView actualImageButtonView = null;

    private int counter = 0;
    private int index = 0;
    private int nextPosition = 0;
    private int askedPosition = 0;
    private int actualPosition = 0;
    private ButtonState buttonState;
    private ProgressBar bar;
    private Boolean isPausingWatch;
    private Boolean hasCallback;
    private int countGoodMovement;
    //private Boolean resume;
    AtomicBoolean isRunning = new AtomicBoolean(false);  // if true = BackGround Thread is running
    AtomicBoolean isPausing = new AtomicBoolean(false);

    Handler progressBarHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            bar.setProgress((int) (score * MAX_BAR/MAX_POINTS));
            GradeDance gradeDance = (GradeDance) msg.getData().getSerializable(GRADE_DANCE);
           switch(gradeDance){
               case PERFECT:
                   goodOrBad.setImageResource(R.drawable.perfect);
                   break;
               case SUPER:
                   goodOrBad.setImageResource(R.drawable.super_image);
                   break;
               case OK:
                   goodOrBad.setImageResource(R.drawable.ok);
                   break;
               case NOPE:
                   goodOrBad.setImageResource(R.drawable.nope);
                   break;
               case WAIT:
                   goodOrBad.setImageResource(R.drawable.wait);
                   break;
           }
           mText.setText(String.valueOf((int)score));
        }
    };

    private AccRateBroadcastReceiver accRateBroadcastReceiver;
    private CommunicationBroadcastReceiver communicationBroadcastReceiver;

    private static final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static final DatabaseReference profileGetRef = database.getReference("profiles");
    private Dialog dialog;

    private final int TIME = 1000; // in milisecond
    private final String key_userID = "key_userID";
    private final String key_score = "key_score";
    private final String key_counter = "key_counter";
    private final String key_index = "key_index";
    private final String key_nextPosition = "key_nextPosition";
    private final String key_askedPosition = "key_askedPosition";
    private final String key_actualPosition = "key_actualPosition";
    private final String key_musical = "key_musical";
    private final String key_buttonState = "key_buttonState";
    private final String key_isPausingWatch = "key_isPausingWatch";
    private final String key_counterGoodMovement = "key_counterGoodMovement";
    private final String key_points = "key_points";

    private enum ButtonState{R0,R1,R2,R3}
    private enum GradeDance{PERFECT,OK,NOPE,SUPER,WAIT};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dance);
        Log.d(TAG, "DanceActivity : here1");
        if(savedInstanceState != null){
            Log.d(TAG, "DanceActivity : here2");
            musical = (MusicDance) savedInstanceState.getSerializable(key_musical);
            setup_dance();
            userID = savedInstanceState.getString(key_userID);
            score = savedInstanceState.getFloat(key_score);
            counter = savedInstanceState.getInt(key_counter);
            index = savedInstanceState.getInt(key_index);
            nextPosition = savedInstanceState.getInt(key_nextPosition);
            askedPosition = savedInstanceState.getInt(key_askedPosition);
            actualPosition = savedInstanceState.getInt(key_actualPosition);
            buttonState = (ButtonState) savedInstanceState.getSerializable(key_buttonState);
            isPausingWatch = savedInstanceState.getBoolean(key_isPausingWatch);
            countGoodMovement = savedInstanceState.getInt(key_counterGoodMovement);
            points = savedInstanceState.getFloat(key_points);
            isPausing.set(true);
        }else{
            Log.d(TAG, "DanceActivity : here3");
            // Create instance of Sport Tracker Room DB

            int[] music;
            Bundle bunble = getIntent().getExtras();
            if (bunble!=null){
                music = bunble.getIntArray("musicchosen");
                userID = bunble.getString(LaunchActivity.USER_ID);
                Log.d(TAG, "DanceActivity : here4");
                if(music!=null) {
                    musical = new MusicDance(music, this);
                    setup_dance();
                }
            }
            Log.d(TAG, "DanceActivity : here5");
            startWatchDanceActivity();
            points = (MAX_POINTS * TIME)/(float)(musical.getSound().getDuration()*POINTS_ON_TIMES)*(5/(float)3);
            counter = 0;
            countGoodMovement = 0;
        }
        if(communicationBroadcastReceiver == null) {
            Log.d(TAG, "DanceActivity : On Creat : New Local BroadCastManager for mesage");
            communicationBroadcastReceiver = new CommunicationBroadcastReceiver();
            LocalBroadcastManager.getInstance(this).registerReceiver(communicationBroadcastReceiver,
                    new IntentFilter(WearService.ACTION_RECEIVE_MESSAGE));
        }
        if(accRateBroadcastReceiver == null){
            Log.d(TAG, "DanceActivity : On Creat : New Local BroadCastManager for sensors");
            accRateBroadcastReceiver = new AccRateBroadcastReceiver();
            LocalBroadcastManager.getInstance(this).registerReceiver(accRateBroadcastReceiver, new
                    IntentFilter(RECEIVE_COUNTER));
        }
        mText = findViewById(R.id.textViewMovements);
        if(score != 0){
            mText.setText(String.valueOf((int)score));
        }
        WearService.setToZero();
        ImageButton button = findViewById(R.id.pausebutton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pause_dance();
            }
        });

        if(isPausing.get()) {
            pause_dance();
        }else{
            mHandler.postDelayed(start_dance, 200);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "DanceActivity : onSaveInstanceState");
        super.onSaveInstanceState(outState);
        outState.putString(key_userID, userID);
        outState.putFloat(key_score,score);
        outState.putInt(key_counter, counter);
        outState.putInt(key_index,index);
        outState.putInt(key_nextPosition,nextPosition);
        outState.putInt(key_askedPosition,askedPosition);
        outState.putInt(key_actualPosition,actualPosition);
        outState.putSerializable(key_buttonState,buttonState);
        outState.putSerializable(key_musical, musical);
        outState.putBoolean(key_isPausingWatch,isPausingWatch);
        outState.putInt(key_counterGoodMovement,countGoodMovement);
        outState.putFloat(key_points,points);
    }

    @Override
    protected void onStop() {
        super.onStop();
        isRunning.set(false);
        if(dialog !=null){dialog.dismiss();}
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "DanceActivity : onPause");
        super.onPause();
        stopMovementOnMusic();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(accRateBroadcastReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(communicationBroadcastReceiver);
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "DanceActivity : onResume");
        super.onResume();
        // Get the accelerometer datas back from the watch
        if(accRateBroadcastReceiver == null){
        accRateBroadcastReceiver = new AccRateBroadcastReceiver();
        Log.d(TAG, "DanceActivity : New Local BroadCastManager");
        LocalBroadcastManager.getInstance(this).registerReceiver(accRateBroadcastReceiver, new
                IntentFilter(RECEIVE_COUNTER));}
        if(communicationBroadcastReceiver == null){
            communicationBroadcastReceiver = new CommunicationBroadcastReceiver();
            Log.d(TAG, "DanceActivity : New Local BroadCastManager");
            LocalBroadcastManager.getInstance(this).registerReceiver(communicationBroadcastReceiver, new
                    IntentFilter(WearService.ACTION_RECEIVE_MESSAGE));}
    }

    final Runnable r1 = new Runnable() {
        public void run() {
            Log.d(TAG, "DanceActivity : r1");
            buttonState = ButtonState.R1;
            nextPosition = musical.getTiming()[index*2+1];
            nextImageButtonView = getCurrentButton(nextPosition);
            if(nextImageButtonView != null) {
                nextImageButtonView.setActivated(true);
                nextImageButtonView.setEnabled(true);
            }
            index = index+1;

            actualImageButtonView = nextImageButtonView;
        }
    };
    final Runnable r2 = new Runnable() {
        public void run() {
            Log.d(TAG, "DanceActivity : r2");
            buttonState = ButtonState.R2;
            askedPosition = nextPosition;
            nextPosition = 0;
            if(actualImageButtonView != null) {
                actualImageButtonView.setActivated(true);
                actualImageButtonView.setEnabled(false);
            }
            toCancelImageButtonView= actualImageButtonView;
        }
    };

    public ImageView getCurrentButton(int position){
        ImageView currentButton = null;
        switch (position){
            case 1:
                currentButton = findViewById(R.id.UpView);
                break;
            case 2:
                currentButton = findViewById(R.id.MiddleView);
                break;
            case 3:
                currentButton = findViewById(R.id.BottomView);
                break;
        }
        return currentButton;
    }

    final Runnable r3 = new Runnable() {
        public void run() {
            Log.d(TAG, "DanceActivity : r3");
            buttonState = ButtonState.R3;
            if(toCancelImageButtonView!=null) {
                toCancelImageButtonView.setActivated(false);
            }
            askedPosition = 0;
        }
    };

    private void makeDialogBox(){
        Log.d(TAG, "DanceActivity : makeDialogBox");
        dialog = new Dialog(this); // Context, this, etc.
        dialog.setContentView(R.layout.dialog_yes_no);
        dialog.setCancelable(false);
        TextView mTextInfo = dialog.findViewById(R.id.dialog_info_yes_no);
        mTextInfo.setText(R.string.QuitOrResumeText);
        Button quitButton = dialog.findViewById(R.id.buttonYesDialog);
        quitButton.setText(getString(R.string.ButtonQuit));
        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                dialog = null;
               start_main_activity();
            }
        });

        Button resumeButton = dialog.findViewById(R.id.buttonNoDialog);
        resumeButton.setText(getString(R.string.ButtonResume));
        resumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //  Action for 'NO' Button
                dialog.dismiss();
                dialog = null;
                startWatch();
                mHandler.postDelayed(start_dance,100);
            }
        });
        dialog.show();
    }
//--------------------------------------------------------------------------------------------------
    private void setup_dance(){
        Log.d(TAG, "DanceActivity : setup_dance");
        musical.getSound().setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
            public void onCompletion(MediaPlayer mp){
                // Store the score on the data base
                profileGetRef.child(userID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String user_db = dataSnapshot.child("username").getValue(String.class);
                        store_data_roomDB(user_db);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        System.out.println("The read failed: " + databaseError.getCode());
                    }
                });
                // Start Finish Activity
                start_finish_activity();
            }
        });
       initialize_variables();
    }

    private void initialize_variables(){
        // Initialize Variables
        mHandler = new Handler();           // Call the runnable for the movements timing
        hasCallback = false;
        buttonState = ButtonState.R0;
        index = 0;                          // Current movement id
        askedPosition = 0;                  // Next Position
        actualPosition = 0;                 // Current asked position
        countGoodMovement = 0;
        goodOrBad = findViewById(R.id.danceResult);
        bar = findViewById(R.id.progress);  // Score bar
        bar.setMax(MAX_BAR);
        bar.setProgress(0);
        Thread background = new Thread(progressRunnable); // Creat the back ground thread
        isPausing.set(false);                              // Thread wakeUp
        isRunning.set(true);                              // Thread enable
        background.start();
        isPausingWatch = false;
        Log.d(TAG,"Dance Activity : duration : " + musical.getSound().getDuration());
    }

    private void pause_dance(){
        Log.d(TAG, "DanceActivity : pause_dance");
        isPausing.set(true);        // Thread is sleeping
        musical.getSound().pause();
        stopMovementOnMusic();
        makeDialogBox();
        pauseWatch();
    }

    final Runnable start_dance = new Runnable() {
        public void run() {
            Log.d(TAG, "DanceActivity : start_dance");
            // Start music
            musical.getSound().start();
            //  Wake the Thread
            isPausing.set(false);
            // Update movement
            updateMovementsOnMusic();
        }
    };
    private void start_main_activity(){
        Log.d(TAG, "DanceActivity : start_main_activity");
        stopMovementOnMusic();
        isRunning.set(false);
        musical.getSound().release();
        // Leave
        Intent intent_change = new Intent(getApplication(), MainActivity.class);
        intent_change.putExtra(LaunchActivity.USER_ID,userID);
        startActivity(intent_change);
        if(Boolean.parseBoolean(BuildConfig.W_flag_watch_enable)){
            // Change to dance activity
            Communication.changeWatchActivity(DanceActivity.this,BuildConfig.W_watchmain_activity_start);
        }
        finish();
    }

    private void start_finish_activity(){
        Log.d(TAG, "DanceActivity : start_finish_activity");
        stopMovementOnMusic();
        musical.getSound().release();
        // Start Finish Activity
        Intent intentFinishDance = new Intent(DanceActivity.this, FinishActivity.class);
        intentFinishDance.putExtra(LaunchActivity.USER_ID, userID);
        Log.e(TAG,"DanceActivity : start_finish_activity : score : " + score);
        intentFinishDance.putExtra(NUMBER_POINTS, score);
        intentFinishDance.putExtra(MUSIC_NAME, musical.getName());
        // Stop the Thread
        isRunning.set(false);
        startActivity(intentFinishDance);
       if(Boolean.parseBoolean(BuildConfig.W_flag_watch_enable)){
            // Change to dance activity
            Communication.changeWatchActivity(DanceActivity.this,BuildConfig.W_finish_activity_start);
        }
        finish();
    }

    private void store_data_roomDB(String user_db){
        Log.d(TAG, "DanceActivity : store_data_roomDB");
        DatabaseEntity dbEntity = new DatabaseEntity();
        dbEntity.setUser_name(user_db);
        dbEntity.setMusic(musical.getName());
        dbEntity.setScore((int)score);

        LaunchActivity.cloneDanceRD.dataDao().insertEntity(dbEntity);
    }


    private void updateMovementsOnMusic(){
        Log.d(TAG, "DanceActivity : updateMovementsOnMusic");
        int i;
        int time_start;
        int time_stop;
        i=index*2;
        switch (buttonState){
            case R1:
                time_start = musical.getTiming()[i-2];
                time_stop = (i>musical.getTiming().length?650 + time_start:musical.getTiming()[i]);
                actualImageButtonView = getCurrentButton(nextPosition);
                mHandler.postDelayed(r2,time_start-musical.getSound().getCurrentPosition()+650);
                mHandler.postDelayed(r3,time_stop-musical.getSound().getCurrentPosition());
                break;
            case R2:
                time_start = musical.getTiming()[i-2];
                time_stop = (i>musical.getTiming().length?650 + time_start:musical.getTiming()[i]);
                toCancelImageButtonView = getCurrentButton(askedPosition);
                mHandler.postDelayed(r3,time_stop-musical.getSound().getCurrentPosition());
                break;
            case R3:
                break;
        }
        while((!isPausing.get())&&(i<musical.getTiming().length)){
            time_start = musical.getTiming()[i];
            time_stop = (i+2>=musical.getTiming().length?650  + time_start :musical.getTiming()[i+2]);
            mHandler.postDelayed(r1,time_start-musical.getSound().getCurrentPosition());
            mHandler.postDelayed(r2,time_start-musical.getSound().getCurrentPosition()+650);
            mHandler.postDelayed(r3,time_stop-musical.getSound().getCurrentPosition());
            i=i+2;
        }
        hasCallback = true;
    }

    private void stopMovementOnMusic(){
        Log.d(TAG, "DanceActivity : stopMovementOnMusic : " + buttonState);
        if(hasCallback) {
            mHandler.removeCallbacks(r1);
            mHandler.removeCallbacks(r2);
            mHandler.removeCallbacks(r3);
            hasCallback = false;
        }

    }

    private void pauseWatch() {
        Log.d(TAG, "DanceActivity : startWatchPause");
        // Change the watch activity
        if(Boolean.parseBoolean(BuildConfig.W_flag_watch_enable) && !isPausingWatch){
            // Change to dance activity
            Communication.sendMessage(DanceActivity.this,BuildConfig.W_pause_activity_start);
            isPausingWatch = true;
        }
    }

    private void startWatch(){
        Log.d(TAG, "DanceActivity : finishWatchPause");
        if(Boolean.parseBoolean(BuildConfig.W_flag_watch_enable) && isPausingWatch){
            // Change to dance activity
            Communication.sendMessage(DanceActivity.this,BuildConfig.W_pause_activity_stop);
            isPausingWatch = false;
        }
    }

    private void startWatchDanceActivity(){
        Log.d(TAG, "DanceActivity : startWatchDanceActivity");
        if(Boolean.parseBoolean(BuildConfig.W_flag_watch_enable)) {
            // Change to dance activity
            Communication.changeWatchActivity(DanceActivity.this, BuildConfig.W_dance_activity_start);
        }
    }
//--------------------------------------------------------------------------------------------------
// Background Thread: Update the score and the progress bar
private Runnable progressRunnable = new Runnable() {
    private Bundle messageBundle = new Bundle();
    private  Message myMessage;
    public void run() {

        try {
            while (isRunning.get()) { // disable the thread when isRunning is false
                // check is the danse is paused or not
                Log.d(TAG, "DanceActivity : progressRunnable : In the while loop");
                while (isPausing.get() && (isRunning.get())) {
                    Thread.sleep(100);
                }
                Log.d(TAG, "DanceActivity : progressRunnable : after");
                Thread.sleep(TIME);
                GradeDance grade_dance;
                myMessage = progressBarHandler.obtainMessage();
                Log.d(TAG,"DanceActivity : progressRunnable : countGoodMovement " + countGoodMovement + " : counter : " + counter);
                float ratio_norm = counter * POINTS_ON_TIMES/(float)(100);
                Log.d(TAG,"DanceActivity : progressRunnable : ratio_norm " + ratio_norm);
                countGoodMovement = (ratio_norm>0? (int) (countGoodMovement/ratio_norm): countGoodMovement);
                Log.d(TAG, "DanceActivity : progressRunnable : ratio in % " + countGoodMovement);
                float point_bar = MAX_BAR/MAX_POINTS;
                // Increment the score
                if(countGoodMovement>75){
                    grade_dance = GradeDance.PERFECT;
                    score += 3 * points;
                    Log.d(TAG,"DanceActivity : progressRunnable : Perfect");
                }else if (countGoodMovement>50) {
                    grade_dance = GradeDance.SUPER;
                    score += 2 * points;
                    Log.d(TAG,"DanceActivity : progressRunnable : Super");
                }else if (countGoodMovement>25) {
                    grade_dance = GradeDance.OK;
                    score += points;
                    Log.d(TAG,"DanceActivity : progressRunnable : OK");
                }else if (askedPosition== 0) {
                    grade_dance = GradeDance.WAIT;
                    Log.d(TAG,"DanceActivity : progressRunnable : Wait");
                }else {
                    grade_dance = GradeDance.NOPE;
                    Log.d(TAG,"DanceActivity : progressRunnable : Nope");
                }
                Log.d(TAG,"DanceActivity : progressRunnable : increment " + MAX_BAR*points/MAX_POINTS);
                // Update progressBar
                messageBundle.putSerializable(GRADE_DANCE,grade_dance);
                myMessage.setData(messageBundle);
                progressBarHandler.sendMessage(myMessage);
                // Reset the value
                counter = 0;
                countGoodMovement = 0;
            }
        } catch (Throwable t) {
        }
    }
};
// Custom Sensors BroadcastReceiver
    private class AccRateBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "DanceActivity : AccRate -> onReceive");
            // Show AR in a TextView
            actualPosition = intent.getIntExtra(COUNTER,0);
            if( actualPosition == askedPosition){
                countGoodMovement += POINTS_ON_TIMES;
                counter++;
            }else if(actualPosition == nextPosition){
                countGoodMovement += POINTS_IN_ADVANCE;
                counter++;
            }else if(askedPosition != 0){
                counter++;
            }
            Log.e(TAG, "DanceActivity : move : " + actualPosition + " askedPosition : " + askedPosition + " nextPosition : " + nextPosition);
            Log.e(TAG, "DanceActivity : countGoodMovement : " + countGoodMovement);
            //mText.setText("mode: " + actualPosition+"\n" +"counter: " + counter +"\n score : " + score);
            Log.e(TAG, "DanceActivity : counter : "+ counter + "Wearservice counter : " + WearService.getCount());
        }
    }
// Custom Communication BroadcastReceiver
    private class CommunicationBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "DanceActivity : Communicatoin -> onReceive");
            String s_message = intent.getStringExtra(WearService.MESSAGE);
            Log.d(TAG, "DanceActivity : Receive message : " + s_message);
            if(isPausing.get() && s_message.equals(BuildConfig.W_pause_activity_stop)) {
                if (dialog != null){ // Check if there is a dialog box open
                    dialog.dismiss();
                    dialog = null;
                }
                // Start the music
                isPausingWatch = false;
                mHandler.postDelayed(start_dance,50);
            }
        }
    }
}

