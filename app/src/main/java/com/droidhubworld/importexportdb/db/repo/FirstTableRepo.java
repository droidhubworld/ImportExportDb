package com.droidhubworld.importexportdb.db.repo;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.droidhubworld.importexportdb.db.AppDatabase;
import com.droidhubworld.importexportdb.db.dao.FirstTableDao;
import com.droidhubworld.importexportdb.db.entity.FirstTable;

import java.util.List;

public class FirstTableRepo {
    Application application;

    public FirstTableRepo(Application application) {
        this.application = application;
        init();
    }

    private FirstTableDao mFirstTableDao;
    private LiveData<List<FirstTable>> mAllData;

    public void init() {
        AppDatabase db = AppDatabase.getInstance(application);
        mFirstTableDao = db.firstTableDao();
        mAllData = mFirstTableDao.getAllData();
    }

    public void insert(FirstTable systemFeatureEntity) {
        new InsertAsyncTask(mFirstTableDao).execute(systemFeatureEntity);
    }

    public void insertData(List<FirstTable> data) {
        mFirstTableDao.insertList(data);
    }

    public LiveData<List<FirstTable>> getAllSystemData() {
        return mAllData;
    }

    private class InsertAsyncTask extends AsyncTask<FirstTable, Void, Void> {
        private FirstTableDao mAsyncTaskDao;

        public InsertAsyncTask(FirstTableDao mAsyncTaskDao) {
            this.mAsyncTaskDao = mAsyncTaskDao;
        }

        @Override
        protected Void doInBackground(FirstTable... firstTables) {
            mAsyncTaskDao.insert(firstTables[0]);
            return null;
        }
    }
}
