package com.pauli.justdanceproject;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;

public class SleepActivity extends WearableActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep);
    }
}
