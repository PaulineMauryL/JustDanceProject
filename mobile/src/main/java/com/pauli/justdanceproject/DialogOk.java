package com.pauli.justdanceproject;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DialogOk {
    private Context context_activity;
    private String message;

    public DialogOk(Context context_activity, String message){
        this.context_activity = context_activity;
        this.message = message;
    }
    public void creat(){
        final Dialog dialog = new Dialog(context_activity); // Context, this, etc.
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_ok);
        ((TextView) dialog.findViewById(R.id.dialog_info_ok)).setText(message);
        Button dialogButton = dialog.findViewById(R.id.buttonOkDialog);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
