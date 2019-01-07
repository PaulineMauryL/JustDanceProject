package com.pauli.justdanceproject;

import android.media.MediaPlayer;
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

    public static final String NUMBER_POINTS = "Number_points";  //Added by Pauline for finish activity

    private MusicDance musical = null;
    private Handler mHandler;
    private int[] music = null;
    private Boolean resume = true;
    private ImageView imageButtonView = null;
    private int index = 0;
    private int nextposition = 0;
    private int askedposition = 0;
    private int actualposition = 0;
    private int score = 0;
    private Button button1=null;
    private Button button2=null;
    private Button button3=null;
    private static final String PROGRESS_BAR_INCREMENT="ProgreesBarIncrementId";
    static ProgressBar bar;
    /**     * L'AtomicBoolean qui gère la destruction de la Thread de background     */
    AtomicBoolean isRunning = new AtomicBoolean(false);
    /**     * L'AtomicBoolean qui gère la mise en pause de la Thread de background     */
    AtomicBoolean isPausing = new AtomicBoolean(false);
    static Handler handler = new Handler() {
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

        Bundle bunble = getIntent().getExtras();
        if (bunble!=null){
            music = bunble.getIntArray("musicchosen");
            musical = new MusicDance("musicname", music,this);
            musical.musicsound.start();
            mHandler = new Handler();
            resume = false;
            index = 0;
            score = 0;
            askedposition = 0;
            actualposition = 100;
            bar = (ProgressBar) findViewById(R.id.progress);
            bar.setMax(210);
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
        int i=0;
        int delay=0;
        i=index*2;
        //Initialisation des AtomicBooleans
        isPausing.set(false);
        while(i<musical.musictiming.length){
            delay = musical.musictiming[i];
            mHandler.postDelayed(r1,delay-musical.musicsound.getCurrentPosition());
            mHandler.postDelayed(r2,delay-musical.musicsound.getCurrentPosition()+600);
            mHandler.postDelayed(r3,delay-musical.musicsound.getCurrentPosition()+800);
            i=i+2;
        }
    }

    public void ResumeMusic(View view) {
        Button pauseButton = findViewById(R.id.ResumeButton);
        if (musical.musicsound !=null){
            if (resume){
                musical.musicsound.start();
                resume = false;
                pauseButton.setText(R.string.Resume);
                movementsOnMusic();
                isPausing.set(false);
            }else {
                musical.musicsound.pause();
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
            actualposition = 1;
        }
    }


    public void MiddleButton(View view) {
        if (!resume){
            imageButtonView = findViewById(R.id.MiddleView);
            actualposition = 2;
        }
    }

    public void DownButton(View view) {
        if (!resume){
            imageButtonView = findViewById(R.id.BottomView);
            actualposition = 3;
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
                    if(askedposition == actualposition) {
                        messageBundle.putInt(PROGRESS_BAR_INCREMENT,10);
                    }else if(nextposition == actualposition){
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
            nextposition = musical.musictiming[index*2+1];
            switch (nextposition){
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


            button2 = findViewById(R.id.DownButton);
            button2.setText("actualposition "+actualposition);


            button1 = findViewById(R.id.MiddleButton);
            button1.setText("nextposition "+nextposition);
        }
    };
    final Runnable r2 = new Runnable() {
        public void run() {
            askedposition = nextposition;
            imageButtonView.setActivated(true);
            imageButtonView.setEnabled(false);

            button2 = findViewById(R.id.UpButton);
            button2.setText("askedposition "+askedposition);
        }
    };

    final Runnable r3 = new Runnable() {
        public void run() {
            imageButtonView.setActivated(false);
            index = index+1;
            nextposition = 0;
            askedposition = 0;
        }
    };

    final Runnable startMusic = new Runnable() {
        public void run() {
            movementsOnMusic();
        }
    };
    public void ToggleFilter(){
        imageButtonView.setActivated(true);
        imageButtonView.setEnabled(false);
        imageButtonView.postDelayed(r1,600);
        imageButtonView.postDelayed(r2,1200);
    }
}
