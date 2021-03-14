package com.droidhubworld.importexportdb.common;

import com.droidhubworld.importexportdb.db.entity.FirstTable;
import com.droidhubworld.importexportdb.db.entity.SecondTable;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DataBeanClass {
    @SerializedName("first_table")
    List<FirstTable> systemFeatures;

    @SerializedName("second_table")
    List<SecondTable> preferenceData;

    public List<FirstTable> getSystemFeatures() {
        return systemFeatures;
    }

    public void setSystemFeatures(List<FirstTable> systemFeatures) {
        this.systemFeatures = systemFeatures;
    }

    public List<SecondTable> getPreferenceData() {
        return preferenceData;
    }

    public void setPreferenceData(List<SecondTable> preferenceData) {
        this.preferenceData = preferenceData;
    }
}
