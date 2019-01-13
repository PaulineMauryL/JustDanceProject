package com.pauli.justdanceproject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;

public class DanceActivity extends WearableActivity implements SensorEventListener {

    private float[] accRate = new float[3];
    private final int ERROR = 5;
    private int actualPosition;

    private final String TAG = this.getClass().getSimpleName();
    private Handler mHandler;
    private int counter; // use for debug only
    private int count;
    private Boolean isRunning;
    private DialogPause dialogPause;

    private SensorManager sensorManager;
    private Sensor acc_sensor;
    private CommunicationBroadcastReceiver communicationBroadcastReceiver;
    private StartActivityBR startActivityBR;

    private final int SENDING_TIME = 50; // Sending rate in miniseconds
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dance);

        mHandler = new Handler();
        counter = 0;
        count = 0;
        WearService.setToZero();
        /*Sensor SetUp*/

        sensorManager = (SensorManager) getSystemService(WatchMainActivity.SENSOR_SERVICE);
        acc_sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        // Enable registerListener
        Log.d(TAG, "New registerListener");
        sensorManager.registerListener(this, acc_sensor, SensorManager.SENSOR_DELAY_NORMAL);

        communicationBroadcastReceiver = new CommunicationBroadcastReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(communicationBroadcastReceiver, new IntentFilter(WearService.ACTION_RECEIVE_MESSAGE));

        startActivityBR = new StartActivityBR();
        startActivityBR.setCurrentContext(DanceActivity.this);
        LocalBroadcastManager.getInstance(this).registerReceiver(startActivityBR, new IntentFilter(WearService.ACTIVITY_TO_START));

        setAmbientEnabled();
        isRunning = true;
        startSendingData();
    }

    protected void startSendingData() {
        mHandler.postDelayed(sendDataToBroadcast,SENDING_TIME);
    }

    final Runnable sendDataToBroadcast = new Runnable() {
        public void run() {

            //TextView[] mText = {findViewById(R.id.textView1),findViewById(R.id.textView2),findViewById(R.id.textView3)};
            counter ++;
            // Averaged value
            for (int i = 0; i < accRate.length; i++){
                accRate[i] = (count>0?accRate[i]/count:accRate[i]);
            }
            Log.v(TAG, "sendDataToBroadcast : count : " + count);
            // Classifier
            if((accRate[0] == 0) && (accRate[1] == 0) && (accRate[2] == 0)){
                // send the previous value
            }
            else if(Math.abs(accRate[2])>7) {
                actualPosition = 2;
            } else if(Math.abs(accRate[0])>4  && accRate[0]>0){
                actualPosition = 1;
            } else if(Math.abs(accRate[0])>4  && accRate[0]<0) {
                actualPosition = 3;
            } else{
                actualPosition = 4; // undefined
            }
            Log.v(TAG, "sendDataToBroadcast : actualPosition : acc[0] " + accRate[0] + " acc[1] " + accRate[1] + " acc[2] " + accRate[2] );
            Log.v(TAG, "sendDataToBroadcast : actualPosition : " + actualPosition);
            // Set to zeros
            for (int i = 0; i < accRate.length; i++){
                accRate[i] = 0;
            }
            count = 0;
            /* Send Result */
            Intent intent = new Intent(DanceActivity.this, WearService.class);
            intent.setAction(WearService.ACTION_SEND.COUNTER.name());
            intent.putExtra(WearService.COUNTER, actualPosition);
            startService(intent);

            //mText[1].setText("Mode: "+ actualPosition + " Counter:" + counter);
            Log.d(TAG, "counter : "+ counter + "Wearservice counter : " + WearService.getCount());
            if(isRunning){startSendingData();}
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if(sensorManager == null) {
            Log.d(TAG, "New registerListener (On Resume)");
            sensorManager = (SensorManager) getSystemService(WatchMainActivity.SENSOR_SERVICE);
            acc_sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            // Enable registerListener
            sensorManager.registerListener(this, acc_sensor, SensorManager.SENSOR_DELAY_NORMAL);}

        if(communicationBroadcastReceiver==null){
            communicationBroadcastReceiver = new CommunicationBroadcastReceiver();
            LocalBroadcastManager.getInstance(this).registerReceiver(communicationBroadcastReceiver,new IntentFilter(WearService.ACTION_RECEIVE_MESSAGE));
        }
        if(startActivityBR == null) {
            startActivityBR = new StartActivityBR();
            startActivityBR.setCurrentContext(DanceActivity.this);
            LocalBroadcastManager.getInstance(this).registerReceiver(startActivityBR, new IntentFilter(WearService.ACTIVITY_TO_START));
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(communicationBroadcastReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(startActivityBR);

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float sum = 0;
        for (int i = 0; i < event.values.length; i++) {
            accRate[i] += event.values[i];
            sum += accRate[i];
        }
        if(sum != 0){
            count++;
        }
        Log.d(TAG,"Sensor test :"  +counter );

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void stopActivity(){
        sensorManager.unregisterListener(DanceActivity.this);
        isRunning = false;
        finish();
    }

    private class CommunicationBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String s_message = intent.getStringExtra(WearService.MESSAGE);
            Log.d(TAG,"Receive message : " + s_message);
            if(s_message.equals(BuildConfig.W_pause_activity_start)){
                Log.d(TAG, "PAUSE_START");
                dialogPause = new DialogPause(DanceActivity.this);
                dialogPause.create();
            }else if(s_message.equals(BuildConfig.W_pause_activity_stop)){
                Log.d(TAG, "PAUSE_STOP");
                if(dialogPause!=null && dialogPause.isOpen()){dialogPause.close();}
                startSensor();
            }
        }
    }

    public void stopSensor(){
        sensorManager.unregisterListener(this);
        Log.i(TAG, "Sensor test : unregister");
        isRunning = false;
    }

    public void startSensor(){
        sensorManager.registerListener(this, acc_sensor, SensorManager.SENSOR_DELAY_NORMAL);
        Log.i(TAG, "Sensor test : register");
        isRunning = true;
        startSendingData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isRunning = false;
    }
}
