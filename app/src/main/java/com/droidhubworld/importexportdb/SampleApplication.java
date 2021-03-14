package com.droidhubworld.importexportdb;

import android.app.Application;

import com.droidhubworld.importexportdb.db.AppDatabase;

public class SampleApplication extends Application {
    private static SampleApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        AppDatabase.getInstance(this).getOpenHelper().getWritableDatabase();
    }

    public static SampleApplication getInstance() {
        return mInstance;
    }
}
