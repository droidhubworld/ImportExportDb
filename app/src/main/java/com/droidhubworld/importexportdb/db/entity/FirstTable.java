package com.droidhubworld.importexportdb.db.entity;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity
public class FirstTable {
    @PrimaryKey
    @ColumnInfo(name = "id")
    @SerializedName("id")
    int id = 0;

    @ColumnInfo(name = "name")
    @SerializedName("name")
    String name = "";

    @ColumnInfo(name = "type")
    @SerializedName("type")
    String type = "";

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
