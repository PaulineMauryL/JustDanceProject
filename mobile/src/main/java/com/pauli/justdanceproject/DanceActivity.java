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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.atomic.AtomicBoolean;

public class DanceActivity extends AppCompatActivity {

    static final String NUMBER_POINTS = "Number_points";  //Added by Pauline for finish activity
    // temp
    private int counter;
    private TextView mText;
    private MusicDance musical = null;
    private Handler mHandler;
    //private int[] music = null;
    private Boolean resume = true;
    private ImageView imageButtonView = null;
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

    public static final String RECEIVE_COUNTER = "RECEIVE_COUNTER";
    public static final String COUNTER = "COUNTER";

    private AccRateBroadcastReceiver accRateBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dance);
        // Temp Hugo
        mText = findViewById(R.id.textViewMovements);
        counter =0;

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
                        startActivity(intentFinishDance);
                        isRunning.set(false);
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
    }

    @Override
    public void onStart() {
        super.onStart();
        mHandler.postDelayed(startMusic,1000);
        // initialisation de la ProgressBar
        bar.setProgress(0);
        // Définition de la Thread (peut être effectuée dans une classe externe ou interne)
        Thread background = new Thread(progressRunnable);
        //Lancement de la Thread
        isPausing.set(true);
        isRunning.set(true);
        background.start();// Get the accelerometer datas back from the watch
        accRateBroadcastReceiver = new AccRateBroadcastReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(accRateBroadcastReceiver, new
                IntentFilter(RECEIVE_COUNTER));
    }

    @Override
    protected void onPause() {
        super.onPause();
        isPausing.set(true);
        Communication.stopRecordingOnWear(DanceActivity.this, BuildConfig.W_dance_activity);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(accRateBroadcastReceiver);

    }

    @Override
    protected void onResume() {
        super.onResume();
        isPausing.set(false);
        // Get the accelerometer datas back from the watch
        accRateBroadcastReceiver = new AccRateBroadcastReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(accRateBroadcastReceiver, new
                IntentFilter(RECEIVE_COUNTER));
    }

    private class AccRateBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // Show AR in a TextView
            actualPosition = intent.getIntExtra(COUNTER,0);
            counter++;
            mText.setText("mode: " + actualPosition+"\n"
            +"counter: " + counter);
        }
    }

    private void startWatchActivity(){
        Intent intent = new Intent(DanceActivity.this, WearService.class);
        intent.setAction(WearService.ACTION_SEND.STARTACTIVITY.name());
        intent.putExtra(WearService.ACTIVITY_TO_START, BuildConfig.W_dance_activity);
        startService(intent);
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
            mHandler.postDelayed(r2,delay-musical.getSound().getCurrentPosition()+600);
            mHandler.postDelayed(r3,delay-musical.getSound().getCurrentPosition()+800);
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
                        // Leave
                        Intent intent_change = new Intent(getApplication(), MainActivity.class);
                        startActivity(intent_change);
                        finish();
                        isRunning.set(false);

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

    public void UpButton(View view) {
        if (!resume){
            imageButtonView = findViewById(R.id.UpView);
            //actualPosition = 1;
        }
    }

    public void MiddleButton(View view) {
        if (!resume){
            imageButtonView = findViewById(R.id.MiddleView);
            //actualPosition = 2;
        }
    }

    public void DownButton(View view) {
        if (!resume){
            imageButtonView = findViewById(R.id.BottomView);
            //actualPosition = 3;
        }
    }

    private Runnable progressRunnable = new Runnable() {
        /**
         * Le Bundle qui porte les données du Message et sera transmis au Handler
         */
        Bundle messageBundle=new Bundle();
        /**
         * Le message échangé entre la Thread et le Handler
         */
        Message myMessage;
        // Surcharge de la méthode run
        public void run() {
            try {
                // Si isRunning est à false, la méthode run doit s'arrêter
                while (isRunning.get()) {
                    // Si l'activité est en pause mais pas morte
                    while (isPausing.get() && (isRunning.get())) {
                        // Faire une pause ou un truc qui soulage le CPU (dépend du traitement)
                        Thread.sleep(2000);
                    }
                    // Effectuer le traitement, pour l'exemple je dors une seconde
                    Thread.sleep(100);
                    // Envoyer le message au Handler (la méthode handler.obtainMessage est plus efficace
                    // que créer un message à partir de rien, optimisation du pool de message du Handler)
                    //Instanciation du message (la bonne méthode):
                    myMessage=handler.obtainMessage();
                    //Ajouter des données à transmettre au Handler via le Bundle
                    if(askedPosition == actualPosition) {
                        messageBundle.putInt(PROGRESS_BAR_INCREMENT,3);
                        score = score+3;
                    }else if(nextPosition == actualPosition){
                        messageBundle.putInt(PROGRESS_BAR_INCREMENT,1);
                        score=score+1;
                    }else{
                        messageBundle.putInt(PROGRESS_BAR_INCREMENT,0);
                    }
                    //Ajouter le Bundle au message
                    myMessage.setData(messageBundle);
                    //Envoyer le message
                    handler.sendMessage(myMessage);
                }
            } catch (Throwable t) {
                // gérer l'exception et arrêter le traitement
            }
        }
    };

    final Runnable r1 = new Runnable() {
        public void run() {
            nextPosition = musical.getTiming()[index*2+1];
            switch (nextPosition){
                case 1:
                    imageButtonView = findViewById(R.id.UpView);
                    break;
                case 2:
                    imageButtonView = findViewById(R.id.MiddleView);
                    break;
                case 3:
                    imageButtonView = findViewById(R.id.BottomView);
                    break;
            }
            imageButtonView.setActivated(true);
            imageButtonView.setEnabled(true);
        }
    };
    final Runnable r2 = new Runnable() {
        public void run() {
            askedPosition = nextPosition;
            imageButtonView.setActivated(true);
            imageButtonView.setEnabled(false);
        }
    };

    final Runnable r3 = new Runnable() {
        public void run() {
            imageButtonView.setActivated(false);
            index = index+1;
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
