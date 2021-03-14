package com.droidhubworld.importexportdb.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity
public class SecondTable {
    @PrimaryKey
    @ColumnInfo(name = "id")
    @SerializedName("id")
    int id = 0;

    @ColumnInfo(name = "name")
    @SerializedName("name")
    String name = "";

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
