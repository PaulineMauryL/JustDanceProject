package com.pauli.justdanceproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.util.concurrent.atomic.AtomicBoolean;

public class DanceActivity extends AppCompatActivity {

    static final String NUMBER_POINTS = "Number_points";  //Added by Pauline for finish activity

    private MusicDance musical = null;
    private Handler mHandler;
    //private int[] music = null;
    private Boolean resume = true;
    private ImageView toCancelImageButtonView = null;
    private ImageView nextImageButtonView = null;
    private ImageView actualImageButtonView = null;
    private int index = 0;
    private int nextPosition = 0;
    private int askedPosition = 0;
    private int actualPosition = 0;
    private int score=0;
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
    public static final String RECEIVE_ACC_RATE = "RECEIVE_ACC_RATE";
    public static final String ACC_RATE = "ACC_RATE";
    private AccRateBroadcastReceiver accRateBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dance);
        int[] music;
        Bundle bunble = getIntent().getExtras();
        if (bunble!=null){
            music = bunble.getIntArray("musicchosen");
            if(music!=null) {
                musical = new MusicDance("musicname", music, this);
                musical.getSound().start();
                musical.getSound().setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
                    public void onCompletion(MediaPlayer mp){
                        Intent intentFinishDance = new Intent(DanceActivity.this, FinishActivity.class);
                        intentFinishDance.putExtra(NUMBER_POINTS,score);
                        isRunning.set(false);
                        startActivity(intentFinishDance);
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
        if(Boolean.parseBoolean(BuildConfig.W_flag_watch_enable)){startWatchActivity();}
    }

    @Override
    public void onStart() {
        super.onStart();
        mHandler.postDelayed(startMusic,1000);
        bar.setProgress(0);
        Thread background = new Thread(progressRunnable);
        isPausing.set(true);
        isRunning.set(true);
        background.start();
        accRateBroadcastReceiver = new AccRateBroadcastReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(accRateBroadcastReceiver, new
                IntentFilter(RECEIVE_ACC_RATE));
    }

    @Override
    protected void onPause() {
        super.onPause();
        isPausing.set(true);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(accRateBroadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isPausing.set(false);
        // Get the accelerometer datas back from the watch
        accRateBroadcastReceiver = new AccRateBroadcastReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(accRateBroadcastReceiver, new
                IntentFilter(RECEIVE_ACC_RATE));
    }

    private class AccRateBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // Show AR in a TextView
            float[] accRateWatch = intent.getFloatArrayExtra(ACC_RATE);
            if(Math.abs(accRateWatch[0])<error && Math.abs(accRateWatch[1])<error) {
                actualPosition = 2;
            } else if(Math.abs(accRateWatch[1])<error && Math.abs(accRateWatch[2])<error && accRateWatch[0]>0){
                actualPosition = 1;
            } else if(Math.abs(accRateWatch[1])<error && Math.abs(accRateWatch[2])<error && accRateWatch[0]>0) {
                actualPosition = 3;
            } else{
                actualPosition = 100;
            }
        }
    }

    private void startWatchActivity(){
        Intent intent = new Intent(DanceActivity.this, WearService.class);
        intent.setAction(WearService.ACTION_SEND.STARTACTIVITY.name());
        intent.putExtra(WearService.ACTIVITY_TO_START, BuildConfig.W_dance_activity);
        startService(intent);
    }

    public void stopRecordingOnWear(View view) {
        /* Store here the datas is needed */
        Intent intentStopRec = new Intent(DanceActivity.this, WearService.class);
        intentStopRec.setAction(WearService.ACTION_SEND.STOPACTIVITY.name());
        intentStopRec.putExtra(WearService.ACTIVITY_TO_STOP, BuildConfig.W_dance_activity);
        startService(intentStopRec);
    }

    public void movementsOnMusic(){
        int i;
        int delay;
        i=index*2;
        //Initialisation des AtomicBooleans
        isPausing.set(false);
        while(i<musical.getTiming().length){
            delay = musical.getTiming()[i];
            mHandler.postDelayed(r1,delay-musical.getSound().getCurrentPosition());
            mHandler.postDelayed(r2,delay-musical.getSound().getCurrentPosition()+500);
            mHandler.postDelayed(r3,delay-musical.getSound().getCurrentPosition()+1100);
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

        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);

        //Uncomment the below code to Set the message and title from the strings.xml file
        builder.setMessage(R.string.TitleQuitOrPause).setTitle(R.string.IdQuitOrPause);

        //Setting message manually and performing action on button click
        builder.setMessage(R.string.QuitOrResumeText)
                .setCancelable(false)
                .setPositiveButton(R.string.ButtonQuit, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        isRunning.set(false);
                        // Leave
                        Intent intent_change = new Intent(getApplication(), MainActivity.class);
                        startActivity(intent_change);
                        finish();

                    }
                })
                .setNegativeButton(R.string.ButtonResume, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.cancel();
                        musical.getSound().start();
                        resume = false;
                        movementsOnMusic();
                        isPausing.set(false);
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle(R.string.TitleQuitOrPause);
        alert.show();

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
                    }else if(nextPosition == actualPosition){
                        messageBundle.putInt(PROGRESS_BAR_INCREMENT,1);
                        score=score+1;
                    }else{
                        messageBundle.putInt(PROGRESS_BAR_INCREMENT,0);
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
        }
    };

    final Runnable startMusic = new Runnable() {
        public void run() {
            movementsOnMusic();
        }
    };
}
