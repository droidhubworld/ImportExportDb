package com.droidhubworld.importexportdb.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.droidhubworld.importexportdb.db.entity.FirstTable;

import java.util.List;

@Dao
public interface FirstTableDao {
    @Insert
    void insertList(List<FirstTable> data);

    @Query("SELECT * FROM FirstTable")
    public LiveData<List<FirstTable>> getAllData();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(FirstTable systemFeatureEntity);
}
