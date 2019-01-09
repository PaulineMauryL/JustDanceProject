package com.pauli.justdanceproject;

import android.os.AsyncTask;

import java.util.List;

public class ReadRoomDatabase extends AsyncTask<Object, Void,List<DatabaseEntity>> {

    private final CloneDanceRoomDatabase db;

    ReadRoomDatabase(CloneDanceRoomDatabase db) {
        this.db = db;
    }

    @Override
    protected List<DatabaseEntity> doInBackground(Object... params) {
        List<DatabaseEntity> DatabaseEntityList = db.dataDao().getHallOfFame((String) params[0]);
        return DatabaseEntityList;
    }

    @Override
    protected void onPostExecute(List<DatabaseEntity> hrValues) {
        super.onPostExecute(hrValues);
    }
}
