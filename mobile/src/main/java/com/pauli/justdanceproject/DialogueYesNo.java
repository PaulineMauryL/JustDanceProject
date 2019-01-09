package com.pauli.justdanceproject;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class DialogueYesNo extends AppCompatActivity {
    private Context context_activity;
    private String message;
    private Boolean isFragment;

    public DialogueYesNo(Context context_activity, String message){
        this.context_activity = context_activity;
        this.message = message;
        this.isFragment = false;
    }

    public DialogueYesNo(Context context_activity, String message,Boolean isFragment){
        this.context_activity = context_activity;
        this.message = message;
        this.isFragment = isFragment;
    }

    public void creat(){
        final Dialog dialog = new Dialog(context_activity); // Context, this, etc.
        TextView mText;
        Button dialogButton_yes;
        Button dialogButton_no;
        if(isFragment) {
            FragmentActivity temp = (FragmentActivity)context_activity;
            View view  = temp.getLayoutInflater().inflate(R.layout.dialog_yes_no, null);
            dialog.setContentView(view);
            mText = view.findViewById(R.id.dialog_info_yes_no);
            dialogButton_yes = view.findViewById(R.id.buttonYesDialog);
            dialogButton_no= view.findViewById(R.id.buttonNoDialog);
        }else{
            dialog.setContentView(R.layout.dialog_yes_no);
            mText = dialog.findViewById(R.id.dialog_info_yes_no);
            dialogButton_yes = dialog.findViewById(R.id.buttonYesDialog);
            dialogButton_no= dialog.findViewById(R.id.buttonNoDialog);
        }
        mText.setText(message);
        dialogButton_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yesChangeUser();
                dialog.dismiss();
            }
        });
        dialogButton_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noChangeUser();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void yesChangeUser()
    {
        if(isFragment) {
            FragmentActivity fragmentActivity = (FragmentActivity) context_activity;
            Toast.makeText(fragmentActivity.getApplicationContext(), "petit connard",
                    Toast.LENGTH_SHORT).show();
            // Leave
            Intent intent_change = new Intent(fragmentActivity.getApplication(), LaunchActivity.class);
            startActivity(intent_change);
            fragmentActivity.finish();
        }
    }

    private void noChangeUser()
    {
        FragmentActivity fragmentActivity = (FragmentActivity) context_activity;
        Toast.makeText(fragmentActivity.getApplicationContext(),"Petit Connard!",
                Toast.LENGTH_SHORT).show();
    }
}
