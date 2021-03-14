package com.droidhubworld.dbexporter;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import androidx.annotation.NonNull;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

public class DbExporterHelper {
    String dbName;
    String directoryName;
    Context context;
    File dbFile;
    SQLiteDatabase database;
    ArrayList<String> tables;
    File exportDir;
    ExporterListener listener;

    public DbExporterHelper(Context context, String dbName, String directoryName, ExporterListener listener) {
        this.dbName = dbName;
        this.directoryName = directoryName;
        this.context = context;
        this.listener = listener;
        exportToCsv();
    }

    private void exportToCsv() {
        dbFile = context.getDatabasePath(dbName).getAbsoluteFile();
        database = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
        tables = getAllTables(database);
        exportDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + directoryName);
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }
    }

    private ArrayList<String> getAllTables(@NonNull SQLiteDatabase database) {
        tables = new ArrayList<String>();
        Cursor cursor = database.rawQuery("select name from sqlite_master where type='table' order by name", null);
        while (cursor.moveToNext()) {
            tables.add(cursor.getString(0));
        }
        cursor.close();
        return tables;
    }

    /**
     * @param tableName   = export table name
     * @param csvFileName = filemame that you want to show after export
     */
    public void exportSingleTable(String tableName, String csvFileName) {
        File file = new File(exportDir, csvFileName);
        try {

            file.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
            exportTable(tableName, csvWrite);
            csvWrite.close();
            listener.success("$tableName successfully Exported");
        } catch (Exception sqlEx) {
            listener.fail("Export $tableName fail", sqlEx.getLocalizedMessage());
        }
    }

    /**
     * @param csvFileName = filemame that you want to show after export
     */
    public void exportAllTables(String csvFileName) {
        File file = new File(exportDir, csvFileName);
        try {

            file.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));

            for (int i = 0; i < (tables.size() - 1); i++) {
                if (!tables.get(i).equals("android_metadata") && !tables.get(i).equals("room_master_table")) {
                    exportTable(tables.get(i), csvWrite);
                }
            }
            csvWrite.close();
            listener.success(csvFileName + " successfully Exported");
        } catch (Exception sqlEx) {
            listener.fail("Export " + csvFileName + " fail", sqlEx.getLocalizedMessage());
        }
    }


    private void exportTable(String tableName, CSVWriter csvWrite) {
        Cursor curCSV = null;
        curCSV = database.rawQuery("SELECT * FROM " + tableName, null);
        csvWrite.writeNext(curCSV.getColumnNames());
        String[] arrStr = new String[curCSV.getColumnCount()];

        while (curCSV.moveToNext()) {
            //Which column you want to exprort
            for (int i = 0; i < curCSV.getColumnCount(); i++) {
                arrStr[i] = curCSV.getString(i) != null ? curCSV.getString(i) : "";
            }

            csvWrite.writeNext(arrStr);
        }

        curCSV.close();
    }

    /**
     * @param appDBPath = db path of you app. see sample for detail
     */
    public void exportDb(String appDBPath) {
        try {
            File externalStorageDir = new File(Environment.getExternalStorageDirectory(), directoryName);
            if (!externalStorageDir.exists()) {
                externalStorageDir.mkdirs();
            }
            File internalStorageDir = Environment.getDataDirectory();
//             appDBPath = "/data/com.android.dbexporterlibrary/databases/"    //getDatabasePath(DATABASE_NAME).absolutePath;

            //.db file
            File currentDB = new File(internalStorageDir, appDBPath + dbName);
            File backupDB = new File(externalStorageDir, dbName);
            FileChannel source = new FileInputStream(currentDB).getChannel();
            FileChannel destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();

            //-wal file
            File currentWalDB = new File(internalStorageDir, appDBPath + dbName + "-wal");
            File backupWalDB = new File(externalStorageDir, dbName + "-wal");
            FileChannel sourceWal = new FileInputStream(currentWalDB).getChannel();
            FileChannel destinationWal = new FileOutputStream(backupWalDB).getChannel();
            destinationWal.transferFrom(sourceWal, 0, sourceWal.size());
            sourceWal.close();
            destinationWal.close();

            //-shm file
            File currentShmDB = new File(internalStorageDir, appDBPath + dbName + "-shm");
            File backupShmDB = new File(externalStorageDir, dbName + "-shm");
            FileChannel sourceShm = new FileInputStream(currentShmDB).getChannel();
            FileChannel destinationShm = new FileOutputStream(backupShmDB).getChannel();
            destinationShm.transferFrom(sourceShm, 0, sourceShm.size());
            sourceShm.close();
            destinationShm.close();
            listener.success("DB successfully Exported");
        } catch (Exception e) {
            listener.fail("Export DB fail", e.getLocalizedMessage());
        }
    }

    /**
     * @param appDBPath = db path of you app. see sample for detail
     */

    public void importDBFile(String appDBPath) {
        try {
            String externalStorageDirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + directoryName;
            File internalStorageDir = Environment.getDataDirectory();
//             appDBPath = "/data/com.android.dbexporterlibrary/databases/"
            File appDBDirectory = new File(internalStorageDir, appDBPath);

            if (!appDBDirectory.exists())
                appDBDirectory.mkdirs();

            //.db file
            File backupDB = new File(externalStorageDirPath, dbName);
            FileChannel source = new FileInputStream(backupDB).getChannel();
            FileChannel destination = new FileOutputStream(appDBDirectory + File.separator + "dbName").getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();

            //-wal file
            File backupWalDB = new File(externalStorageDirPath, dbName + "-wal");
            FileChannel sourceWal = new FileInputStream(backupWalDB).getChannel();
            FileChannel destinationWal = new FileOutputStream(appDBDirectory + File.separator + dbName+"-wal").getChannel();
            destinationWal.transferFrom(sourceWal, 0, sourceWal.size());
            sourceWal.close();
            destinationWal.close();

            //-shm file
            File backupShmDB = new File(externalStorageDirPath, dbName + "-shm");
            FileChannel sourceShm = new FileInputStream(backupShmDB).getChannel();
            FileChannel destinationShm = new FileOutputStream(appDBDirectory + File.separator + dbName + "-shm").getChannel();
            destinationShm.transferFrom(sourceShm, 0, sourceShm.size());
            sourceShm.close();
            destinationShm.close();
            listener.success("DB successfully Imported");
        } catch (IOException e) {
            e.printStackTrace();
            listener.fail("Couldn't import DB!", e.getLocalizedMessage());
        }
    }

    /**
     * @param dirName = directory name where tou want to check db exist or not
     */
    public Boolean isBackupExist(String dirName) {
        String externalStorageDirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + dirName;
        File backupDB = new File(externalStorageDirPath, dbName);
        return backupDB.exists();
    }
}
