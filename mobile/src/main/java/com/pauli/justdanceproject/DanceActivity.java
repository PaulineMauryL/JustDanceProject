package com.pauli.justdanceproject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.util.concurrent.atomic.AtomicBoolean;

public class DanceActivity extends AppCompatActivity {


    private MusicDance musical = null;
    private Handler mHandler;
    //private int[] music = null;
    private Boolean resume = true;
    private ImageView imageButtonView = null;
    private int index = 0;
    private int nextPosition = 0;
    private int askedPosition = 0;
    private int actualPosition = 0;
    private static final String PROGRESS_BAR_INCREMENT="ProgreesBarIncrementId";
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
                mHandler = new Handler();
                resume = false;
                index = 0;
                askedPosition = 0;
                actualPosition = 100;
                bar = findViewById(R.id.progress);
                bar.setMax(210);
            }
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
        background.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isPausing.set(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isPausing.set(false);
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
        Button pauseButton = findViewById(R.id.ResumeButton);

        if (musical.getSound() !=null){
            if (resume){
                musical.getSound().start();
                resume = false;
                pauseButton.setText(R.string.Resume);
                movementsOnMusic();
                isPausing.set(false);
            }else {
                musical.getSound().pause();
                mHandler.removeCallbacks(r1);
                mHandler.removeCallbacks(r2);
                mHandler.removeCallbacks(r3);
                resume = true;
                isPausing.set(true);
                pauseButton.setText(R.string.Restart);
            }
        }

    }


    public void UpButton(View view) {
        if (!resume){
            imageButtonView = findViewById(R.id.UpView);
            actualPosition = 1;
        }
    }


    public void MiddleButton(View view) {
        if (!resume){
            imageButtonView = findViewById(R.id.MiddleView);
            actualPosition = 2;
        }
    }

    public void DownButton(View view) {
        if (!resume){
            imageButtonView = findViewById(R.id.BottomView);
            actualPosition = 3;
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
                        messageBundle.putInt(PROGRESS_BAR_INCREMENT,10);
                    }else if(nextPosition == actualPosition){
                        messageBundle.putInt(PROGRESS_BAR_INCREMENT,5);
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
