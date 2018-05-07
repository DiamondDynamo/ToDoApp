package com.juniata.ifpizza.todoapp;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

//**********Charley Bein**********
//*********Timothy Benson*********
//**********Joe Maskell***********
//***********Ben Tipton***********
//***********05/07/2018***********

//**********Start Up**********
public class SplashScreenActivity extends AppCompatActivity {
    long listNum;
    static final String LISTNUM = "listnum";
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.title_activity_splash_screen);
        final SharedPreferences sharedPreferences = getSharedPreferences("preferences", Context.MODE_PRIVATE);
        refreshDisplay();
        swipeContainer =  findViewById(R.id.splash_refresh);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshDisplay();
            }
        });
        FloatingActionButton fab = findViewById(R.id.listFab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GeneralDbHelper myDbHelper = new GeneralDbHelper(getApplicationContext());
                SQLiteDatabase db = myDbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                listNum = sharedPreferences.getLong("listNumber", 0) + 1;
                values.put(ListContract.ListEntry.COLUMN_LIST_NAME, "List #" + listNum);
                values.put(ListContract.ListEntry.COLUMN_DEL_FLAG, 0);
                long newRowId = db.insert(ListContract.ListEntry.TABLE_NAME, null, values);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putLong("listNumber", newRowId);
                editor.apply();
                String result;

                if (newRowId != -1)
                {
                    result = "Created List #" + newRowId;
                }

                else
                {
                    result = "ERROR";
                }
                Snackbar.make(findViewById(R.id.displayLists), result, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                refreshDisplay();
            }
        });
    }

    ////*****Refresh*****
    public void refreshDisplay(){
        GeneralDbHelper myDbHelper = new GeneralDbHelper(getApplicationContext());
        SQLiteDatabase db = myDbHelper.getWritableDatabase();
        swipeContainer = findViewById(R.id.splash_refresh);

        String[] projection = {
                ListContract.ListEntry.COLUMN_LIST_NAME
        };

        String[] bind = {
                ListContract.ListEntry._ID,
                ListContract.ListEntry.COLUMN_LIST_NAME,
                ListContract.ListEntry.COLUMN_DEL_FLAG
        };
        Cursor cursor = db.query(ListContract.ListEntry.TABLE_NAME, bind, ListContract.ListEntry.COLUMN_DEL_FLAG + " = 0", null, null, null, ListContract.ListEntry._ID + " ASC");
        int [] to = new int[]{R.id.itemName};
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(getApplicationContext(), R.layout.row_item, cursor, projection, to, 0);
        final ListView listView = findViewById(R.id.listsList);
        listView.setAdapter(adapter);
        TextView emptyView = findViewById(R.id.noLists);
        listView.setEmptyView(emptyView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Cursor cursor1 = (Cursor) adapterView.getItemAtPosition(position);
                int listNumber = cursor1.getInt(cursor1.getColumnIndex(ListContract.ListEntry._ID));
                Intent intent = new Intent(getApplicationContext(), ListContents.class);
                intent.putExtra(LISTNUM, listNumber);
                setResult(RESULT_OK, intent);
                startActivity(intent);
            }
        });
        swipeContainer.setRefreshing(false);
    }
}
