package com.pauli.justdanceproject;

import android.content.Context;
import android.content.Intent;

public class Communication {
    public static void sendMessage(Context context_application, String message) {
        Intent intent = new Intent(context_application, WearService.class);
        intent.setAction(WearService.ACTION_SEND.MESSAGE.name());
        intent.putExtra(WearService.MESSAGE, message);
        intent.putExtra(WearService.PATH, BuildConfig.W_path_message);
        context_application.startService(intent);
    }
}
