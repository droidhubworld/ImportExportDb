package com.droidhubworld.dbexporter;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;

import com.opencsv.CSVWriter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class DbExporterHelper {
    String prefName;
    String dbName;
    String directoryName;
    Context context;
    File dbFile;
    SQLiteDatabase database;
    ArrayList<String> tables;
    File exportDir;
    ExporterListener listener;
    private static final int BUFFER = 2048;

    public DbExporterHelper(Context context, String prefName, String dbName, String directoryName, ExporterListener listener) {
        this.dbName = dbName;
        this.prefName = prefName;
        this.directoryName = directoryName;
        this.context = context;
        this.listener = listener;
        exportToCsv();
    }

    public static class Builder {
        Context mContext;
        String prefName;
        String dbName;
        String directoryName;
        ExporterListener listener;

        public Builder(Context mContext, String prefName, String dbName, String directoryName, ExporterListener listener) {
            this.prefName = prefName;
            this.dbName = dbName;
            this.directoryName = directoryName;
            this.mContext = mContext;
            this.listener = listener;
        }

        public DbExporterHelper build() {
            return new DbExporterHelper(mContext, prefName, dbName, directoryName, listener);
        }
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
     * @param appPath      = db path of you app. see sample for detail
     * @param isBackupPref = if need to backup pref file
     */
    public void exportDb(String appPath, boolean isBackupPref) {
        try {
            File externalStorageDir;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                File path = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS);
                externalStorageDir = new File(path, directoryName);
                if (!externalStorageDir.exists()) {
                    externalStorageDir.mkdirs();
                }
            } else {
                externalStorageDir = new File(Environment.getExternalStorageDirectory(), directoryName);
                if (!externalStorageDir.exists()) {
                    externalStorageDir.mkdirs();
                }
            }
            File internalStorageDir = Environment.getDataDirectory();
//             appDBPath = "/data/com.android.dbexporterlibrary/databases/"    //getDatabasePath(DATABASE_NAME).absolutePath;

            //Pref File
            if (isBackupPref && prefName != null) {
                File currentPref = new File(internalStorageDir, appPath + "shared_prefs/" + prefName);
                File backupPref = new File(externalStorageDir, prefName);
                FileChannel source = new FileInputStream(currentPref).getChannel();
                FileChannel destination = new FileOutputStream(backupPref).getChannel();
                destination.transferFrom(source, 0, source.size());
                source.close();
                destination.close();
            }

            //.db file
            File currentDB = new File(internalStorageDir, appPath + "databases/" + dbName);
            File backupDB = new File(externalStorageDir, dbName);
            FileChannel source = new FileInputStream(currentDB).getChannel();
            FileChannel destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();

            //-wal file
            File currentWalDB = new File(internalStorageDir, appPath + "databases/" + dbName + "-wal");
            if (currentWalDB.exists()) {
                File backupWalDB = new File(externalStorageDir, dbName + "-wal");
                FileChannel sourceWal = new FileInputStream(currentWalDB).getChannel();
                FileChannel destinationWal = new FileOutputStream(backupWalDB).getChannel();
                destinationWal.transferFrom(sourceWal, 0, sourceWal.size());
                sourceWal.close();
                destinationWal.close();
            }

            //-shm file
            File currentShmDB = new File(internalStorageDir, appPath + "databases/" + dbName + "-shm");
            if (currentShmDB.exists()) {
                File backupShmDB = new File(externalStorageDir, dbName + "-shm");
                FileChannel sourceShm = new FileInputStream(currentShmDB).getChannel();
                FileChannel destinationShm = new FileOutputStream(backupShmDB).getChannel();
                destinationShm.transferFrom(sourceShm, 0, sourceShm.size());
                sourceShm.close();
                destinationShm.close();
            }
            listener.success("DB successfully Exported : " + currentDB.getAbsolutePath());
        } catch (Exception e) {
            listener.fail("Export DB fail", e.getLocalizedMessage());
        }
    }

    public void exportToZip(String appPath, String zipFilePath, boolean isBackupPref) {
        try {
            File externalStorageDir;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                File path = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS);
                externalStorageDir = new File(path, directoryName);
                if (!externalStorageDir.exists()) {
                    externalStorageDir.mkdirs();
                }
            } else {
                externalStorageDir = new File(Environment.getExternalStorageDirectory(), directoryName);
                if (!externalStorageDir.exists()) {
                    externalStorageDir.mkdirs();
                }
            }
            File internalStorageDir = Environment.getDataDirectory();
//             appDBPath = "/data/com.android.dbexporterlibrary/databases/"    //getDatabasePath(DATABASE_NAME).absolutePath;
            ArrayList<String> _files = new ArrayList();

            //Pref File
            if (isBackupPref && prefName != null) {
                File currentPref = new File(internalStorageDir, appPath + "shared_prefs/" + prefName);
                _files.add(currentPref.getAbsolutePath());
            }

            //.db file
            File currentDB = new File(internalStorageDir, appPath + "databases/" + dbName);
            _files.add(currentDB.getAbsolutePath());

            //-wal file
            File currentWalDB = new File(internalStorageDir, appPath + "databases/" + dbName + "-wal");
            if (currentWalDB.exists()) {
                _files.add(currentWalDB.getAbsolutePath());
            }

            //-shm file
            File currentShmDB = new File(internalStorageDir, appPath + "databases/" + dbName + "-shm");
            if (currentShmDB.exists()) {
                _files.add(currentShmDB.getAbsolutePath());
            }


            /*
             * Zip Creation
             * */
            File zipFile = new File(externalStorageDir, (zipFilePath.contains(".zip")) ? zipFilePath : zipFilePath + ".zip");
            BufferedInputStream origin = null;

            FileOutputStream dest = new FileOutputStream(zipFile);

            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));

            byte data[] = new byte[BUFFER];

            for (int i = 0; i < _files.size(); i++) {
                Log.v("Compress", "Adding: " + _files.get(i));
                FileInputStream fi = new FileInputStream(_files.get(i));
                origin = new BufferedInputStream(fi, BUFFER);
                ZipEntry entry = new ZipEntry(_files.get(i).substring(_files.get(i).lastIndexOf("/") + 1));
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }

            out.close();
            /*
             * Zip Creation end
             * */


            listener.success("DB successfully Exported : " + currentDB.getAbsolutePath());
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
            FileChannel destination = new FileOutputStream(appDBDirectory + File.separator + dbName).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();

            //-wal file
            File backupWalDB = new File(externalStorageDirPath, dbName + "-wal");
            FileChannel sourceWal = new FileInputStream(backupWalDB).getChannel();
            FileChannel destinationWal = new FileOutputStream(appDBDirectory + File.separator + dbName + "-wal").getChannel();
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
     * @param dirLocation = directory name where tou want to check db exist or not
     */
    public Boolean isBackupExist(String dirLocation) {
//         String externalStorageDirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + dirName;
        File backupDB = new File(dirLocation, dbName);
        return backupDB.exists();
    }
}
