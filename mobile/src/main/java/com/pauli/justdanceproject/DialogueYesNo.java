package com.pauli.justdanceproject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class DialogueYesNo extends AppCompatActivity {
    public static final String SAVE2MAIN = "SAVE2MAIN";
    public static final String EDIT_PROFILE = "EDIT_PROFILE";
    public static final String SAVE = "SAVE";

    private String task;
    private Context context_activity;
    private String message;
    private Boolean isFragment;
    private String text_button_left;
    private String text_button_right;
    private Intent itCallBack;

    private String TAG = "DIALOG_YES_NO";
    public DialogueYesNo(String task,Context context_activity, String message){
        this.task = task;
        this.context_activity = context_activity;
        this.message = message;
        this.isFragment = false;
        this.text_button_left = context_activity.getString(R.string.No);
        this.text_button_right = context_activity.getString(R.string.Yes);
    }

    public DialogueYesNo(String task,Context context_activity, String message,Boolean isFragment){
        this(task,context_activity,message);
        this.isFragment = isFragment;
    }
    public DialogueYesNo(String task,Context context_activity, String message,String text_button_left, String text_button_right){
        this(task,context_activity,message);
        this.text_button_left = text_button_left;
        this.text_button_right = text_button_right;
    }
    public DialogueYesNo(String task,Context context_activity, String message,Intent itCallBack){
        this(task,context_activity,message);
        this.itCallBack = itCallBack;
    }
    public void create(String positive, final String negative){
        final Dialog dialog = new Dialog(context_activity); // Context, this, etc.
        dialog.setCancelable(false);
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
        dialogButton_no.setText(text_button_left);
        dialogButton_yes.setText(text_button_right);
        final String m_pos = positive;
        final String m_neg = negative;
        mText.setText(message);
        dialogButton_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(task.equals(EDIT_PROFILE)){
                    yesChangeUser(m_pos);
                }else if(task.equals(SAVE)){
                    yesSave(m_pos);
                }else if(task.equals(SAVE2MAIN)){
                    yesSave2main(m_pos);
                }
                dialog.dismiss();
            }
        });
        dialogButton_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(task.equals(EDIT_PROFILE)){
                    noChangeUser(m_neg);
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void yesSave2main(String message){
        Activity activity = (Activity) context_activity;
        activity.startActivity(itCallBack);
        if(Boolean.parseBoolean(BuildConfig.W_flag_watch_enable)){
            // Change to dance activity
            Communication.changeWatchActivity(activity,BuildConfig.W_watchmain_activity_start);
        }
        activity.finish();
    }

    private void yesSave(String message){
        Intent intent = new Intent(context_activity, LaunchActivity.class);
        Activity activity = (Activity) context_activity;
        activity.startActivity(intent);
        if(Boolean.parseBoolean(BuildConfig.W_flag_watch_enable)){
            // Change to dance activity
            Communication.changeWatchActivity(activity,BuildConfig.W_watchmain_activity_start);
        }
        activity.finish();
    }

    private void yesChangeUser(String message)
    {
        if(isFragment) {
            FragmentActivity fragmentActivity = (FragmentActivity) context_activity;
            Toast.makeText(fragmentActivity.getApplicationContext(), message,
                    Toast.LENGTH_SHORT).show();
            // Leave
            Intent intent_change = new Intent(fragmentActivity.getApplication(), LaunchActivity.class);
            fragmentActivity.startActivity(intent_change);
            if(Boolean.parseBoolean(BuildConfig.W_flag_watch_enable)){
                // Change to dance activity
                Communication.changeWatchActivity(fragmentActivity,BuildConfig.W_watchmain_activity_start);
            }
            fragmentActivity.finish();
        }else{
            Activity activity = (Activity) context_activity;
            Toast.makeText(activity.getApplicationContext(), message,
                    Toast.LENGTH_SHORT).show();
            // Leave
            Intent intent_change = new Intent(activity.getApplication(), LaunchActivity.class);
            activity.startActivity(intent_change);
            if(Boolean.parseBoolean(BuildConfig.W_flag_watch_enable)){
                // Change to dance activity
                Communication.changeWatchActivity(activity,BuildConfig.W_watchmain_activity_start);
            }
            activity.finish();
        }
    }

    private void noChangeUser(String message)
    {
        FragmentActivity fragmentActivity = (FragmentActivity) context_activity;
        Toast.makeText(fragmentActivity.getApplicationContext(),message,
                Toast.LENGTH_SHORT).show();
    }
}
