package com.pauli.justdanceproject;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Communication {
    private static String oldPath;
    public static String TAG = "COMMUNICATION";

    public static void sendMessage(Context context_application, String message) {
        Intent intent = new Intent(context_application, WearService.class);
        intent.setAction(WearService.ACTION_SEND.MESSAGE.name());
        intent.putExtra(WearService.MESSAGE, message);
        intent.putExtra(WearService.PATH, BuildConfig.W_path_message);
        context_application.startService(intent);
    }
    public static void sendImage(Context context_application, String imagePath) {
        // Create the intent to communique with the service
        Intent intentWear = new Intent(context_application, WearService.class);
        intentWear.setAction(WearService.ACTION_SEND.SEND_IMAGE.name());
        intentWear.putExtra(WearService.IMAGE, imagePath);
        context_application.startService(intentWear);
    }

    public static void changeWatchActivity(Context context_application,String path) {
        Intent intent = new Intent(context_application, WearService.class);
        intent.setAction(WearService.ACTION_SEND.STARTACTIVITY.name());
        intent.putExtra(WearService.ACTIVITY_TO_START, path);
        context_application.startService(intent);
        oldPath = path;
    }
    public static void stopRecordingOnWear(Context context_application,String path) {
        if(oldPath.equals(path)) {
            Intent intentStopRec = new Intent(context_application, WearService.class);
            intentStopRec.setAction(WearService.ACTION_SEND.STOPACTIVITY.name());
            intentStopRec.putExtra(WearService.ACTIVITY_TO_STOP, BuildConfig.W_dance_activity);
            context_application.startService(intentStopRec);
        }else{
            /*DEBUG*/
            Log.d(TAG, "Error in call: Communication.stopRecordingOnWear");
        }
    }
}
