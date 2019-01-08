/*package com.pauli.justdanceproject;

import android.os.AsyncTask;

public class SaveInDatabase extends AsyncTask<Object, Void, Void> {

    private CloneDanceRoomDatabase db;

    SaveInDatabase(CloneDanceRoomDatabase db) {
        this.db = db;
    }

    @Override
    protected Void doInBackground(Object... params) {
        DatabaseEntity entityData = new DatabaseEntity();
        entityData.firebase_id = (String) params[0];
        entityData.music = (String) params[1];
        entityData.score = (int) params[2];

        db.dataDao().insertEntity(entityData);

        return null;
    }

}*/
