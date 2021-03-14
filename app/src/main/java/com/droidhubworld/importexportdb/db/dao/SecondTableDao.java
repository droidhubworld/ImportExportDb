package com.droidhubworld.importexportdb.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.droidhubworld.importexportdb.db.entity.SecondTable;

import java.util.List;

@Dao
public interface SecondTableDao {
    @Insert
    void insertList(List<SecondTable> data);

    @Query("SELECT * FROM FirstTable")
    public LiveData<List<SecondTable>> getAllData();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(SecondTable secondTableEntity);
}
