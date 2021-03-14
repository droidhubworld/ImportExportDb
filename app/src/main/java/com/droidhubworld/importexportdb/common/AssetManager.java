package com.droidhubworld.importexportdb.common;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class AssetManager {
    public static String loadSystemFeatureDataFromAsset(Context context) {
        String json;

        try {
            android.content.res.AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open("dbExporter.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            json = new String(buffer, Charset.forName("UTF-8"));
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }

        return json;
    }
}
