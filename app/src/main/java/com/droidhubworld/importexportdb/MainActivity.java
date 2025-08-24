package com.droidhubworld.importexportdb;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;

import com.droidhubworld.dbexporter.DbExporterHelper;
import com.droidhubworld.dbexporter.ExporterListener;
import com.droidhubworld.importexportdb.db.entity.FirstTable;
import com.droidhubworld.importexportdb.db.repo.FirstTableRepo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity implements ExporterListener {
    private DbExporterHelper exportDbUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        exportDbUtil = new DbExporterHelper.Builder(this, null, "sampleDb", "ANAND_DB", this).build();
        //exportDbUtil = new DbExporterHelper(this, "sampleDb", "ANAND_DB", this);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void success(String s) {
        Log.e("TAG","FILE PATH : "+s);
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void fail(String message, String exception) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void exportToCsv(View view) {
        exportDbUtil.exportAllTables("test.csv");
    }

    public void exportSingleTable(View view) {
        exportDbUtil.exportSingleTable("firstTabel", "test.csv");
    }

    public void importDb(View view) {
        if (exportDbUtil.isBackupExist(Environment.getExternalStorageDirectory().getAbsolutePath() + "/ANAND_DB")) {
            exportDbUtil.importDBFile("/data/com.droidhubworld.importexportdb/databases/");
        } else {
            Toast.makeText(this, "no Backup", Toast.LENGTH_SHORT).show();
        }
    }

    public void exportDB(View view) {
        exportDbUtil.exportToZip("/data/com.droidhubworld.importexportdb/", "abc.zip", false);
    }

    public void checkData(View view) {
        FirstTableRepo firstTableRepo = new FirstTableRepo(getApplication());
        firstTableRepo.getAllSystemData().observe(this, firstTables -> {
            Log.e("Firest Table Data :", ">>>> " + firstTables.size());
            for (FirstTable table : firstTables) {
                Log.e("Firest Table Data :", table.getName());
            }
        });
    }

    public void insertData(View view) {
        FirstTable firstTable = new FirstTable();
        firstTable.setName("Anand");
        firstTable.setType("ADMIN");
        FirstTableRepo firstTableRepo = new FirstTableRepo(getApplication());
        firstTableRepo.insert(firstTable);
    }
}
