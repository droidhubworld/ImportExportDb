package com.droidhubworld.importexportdb.db.repo;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.droidhubworld.importexportdb.db.AppDatabase;
import com.droidhubworld.importexportdb.db.dao.SecondTableDao;
import com.droidhubworld.importexportdb.db.entity.SecondTable;

import java.util.List;

public class SecondTableRepo {
    Application application;
    private SecondTableDao mSecondTableDao;
    private LiveData<List<SecondTable>> mAllData;

    public SecondTableRepo(Application application) {
        this.application = application;
    }

    public void init() {
        AppDatabase db = AppDatabase.getInstance(application);
        mSecondTableDao = db.secondTableDao();
        mAllData = mSecondTableDao.getAllData();
    }

    public void insert(SecondTable secondTableEntity) {
        new InsertAsyncTask(mSecondTableDao).execute(secondTableEntity);
    }

    public void insertData(List<SecondTable> data) {
        mSecondTableDao.insertList(data);
    }

    public LiveData<List<SecondTable>> getAllSystemData() {
        return mAllData;
    }


    private class InsertAsyncTask extends AsyncTask<SecondTable, Void, Void> {
        private SecondTableDao mAsyncTaskDao;

        public InsertAsyncTask(SecondTableDao mAsyncTaskDao) {
            this.mAsyncTaskDao = mAsyncTaskDao;
        }

        @Override
        protected Void doInBackground(SecondTable... secondTables) {
            mAsyncTaskDao.insert(secondTables[0]);
            return null;
        }
    }
}
