package com.droidhubworld.importexportdb.db;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.droidhubworld.importexportdb.SampleApplication;
import com.droidhubworld.importexportdb.common.AssetManager;
import com.droidhubworld.importexportdb.common.DataBeanClass;
import com.droidhubworld.importexportdb.db.dao.FirstTableDao;
import com.droidhubworld.importexportdb.db.dao.SecondTableDao;
import com.droidhubworld.importexportdb.db.entity.FirstTable;
import com.droidhubworld.importexportdb.db.entity.SecondTable;
import com.droidhubworld.importexportdb.db.repo.FirstTableRepo;
import com.droidhubworld.importexportdb.db.repo.SecondTableRepo;
import com.google.gson.Gson;

import java.util.List;

@Database(entities = {FirstTable.class, SecondTable.class}, version = 1)
abstract public class AppDatabase extends RoomDatabase {
    private static final String LOG_TAG = AppDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static AppDatabase sInstance;

    public static AppDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(LOG_TAG, "Creating new database instance");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, "sampleDb")
                        .addCallback(new Callback() {
                            @Override
                            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                super.onCreate(db);
                                //new InsertDataAsyncTask(context).execute();
                            }

                            @Override
                            public void onOpen(@NonNull SupportSQLiteDatabase db) {
                                super.onOpen(db);
                            }
                        })
                        .build();
            }
        }
        Log.d(LOG_TAG, "Getting the database instance");
        return sInstance;
    }

    public static class InsertDataAsyncTask extends AsyncTask<Void, Void, Void> {
        Context context;
        private FirstTableRepo mFirstTableRepo = new FirstTableRepo(SampleApplication.getInstance());
        private SecondTableRepo mSecondTableRepo = new SecondTableRepo(SampleApplication.getInstance());

        public InsertDataAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Gson mGson = new Gson();
            DataBeanClass systemJsonData = mGson.fromJson(AssetManager.loadSystemFeatureDataFromAsset(context), DataBeanClass.class);

            List<FirstTable> firstTableData = systemJsonData.getSystemFeatures();
            List<SecondTable> secondTableData = systemJsonData.getPreferenceData();

            mFirstTableRepo.insertData(firstTableData);
            mSecondTableRepo.insertData(secondTableData);
            return null;
        }
    }

    public abstract FirstTableDao firstTableDao();

    public abstract SecondTableDao secondTableDao();
}
