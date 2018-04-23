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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import java.util.List;


//**********LIST**********
public class ListContents extends AppCompatActivity {

    long taskNum;
    static final String TASKNUM = "tasknum";
    int ActiveList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_contents);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActiveList = getIntent().getIntExtra("listnum", 0);

        final SharedPreferences sharedPreferences = getSharedPreferences("preferences", Context.MODE_PRIVATE);

//        final CheckBox checkBox = (CheckBox) findViewById(R.id.taskComplete);

        final ListView listView = (ListView) findViewById(R.id.tasksList);
        TextView emptyView = findViewById(R.id.noTasks);

        refreshDisplay();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GeneralDbHelper myDbHelper = new GeneralDbHelper(getApplicationContext());
                SQLiteDatabase db = myDbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();

                taskNum = sharedPreferences.getLong("taskNumber", 0) + 1;

                values.put(TaskContract.TaskEntry.COLUMN_TASK_NAME, "Task #" + taskNum);
                values.put(TaskContract.TaskEntry.COLUMN_LIST_ID, ActiveList);

                long newRowId = db.insert(TaskContract.TaskEntry.TABLE_NAME, null, values);

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putLong("taskNumber", newRowId);
                editor.apply();

                refreshDisplay();
            }
        });


        listView.setEmptyView(emptyView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);

                int taskNumber = (int) cursor.getInt(cursor.getColumnIndex(TaskContract.TaskEntry._ID));
                Intent intent = new Intent(getApplicationContext(), TaskDetails.class);
                intent.putExtra(TASKNUM, taskNumber);
                setResult(RESULT_OK, intent);
                startActivity(intent);
            }
        });


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    public void refreshDisplay(){

        GeneralDbHelper myDbHelper = new GeneralDbHelper(getApplicationContext());
        SQLiteDatabase db = myDbHelper.getWritableDatabase();

        String[] projection = {
                TaskContract.TaskEntry._ID,
                TaskContract.TaskEntry.COLUMN_TASK_NAME
        };

        String[] bind = {
                TaskContract.TaskEntry._ID,
                TaskContract.TaskEntry.COLUMN_TASK_NAME
        };

//        Cursor cursor = db.query(TaskContract.TaskEntry.TABLE_NAME, bind, null, null, null, null, TaskContract.TaskEntry._ID + " ASC");

        Cursor cursor = db.rawQuery("SELECT " + TaskContract.TaskEntry.COLUMN_TASK_NAME + ", " + TaskContract.TaskEntry._ID + " FROM " +  TaskContract.TaskEntry.TABLE_NAME + " WHERE " + TaskContract.TaskEntry.COLUMN_LIST_ID + " = " + ActiveList + " GROUP BY " + TaskContract.TaskEntry.COLUMN_TASK_NAME + " ORDER BY " + TaskContract.TaskEntry._ID + " ASC", null);

        int [] to = new int[]{R.id.taskName};

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(getApplicationContext(), R.layout.task_item, cursor, projection, to, 0);

        final ListView taskView = findViewById(R.id.tasksList);
        taskView.setAdapter(adapter);

        TextView emptyView = findViewById(R.id.noTasks);
        taskView.setEmptyView(emptyView);

//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
//
//                Cursor cursor1 = (Cursor) adapterView.getItemAtPosition(position);
//
//                int taskNumber = (int) cursor1.getInt(cursor1.getColumnIndex(ListContract.ListEntry._ID));
//
//                Intent intent = new Intent(getApplicationContext(), ListContents.class);
//                intent.putExtra(TASKNUM, listNumber);
//                setResult(RESULT_OK, intent);
//                startActivity(intent);
//
//            }
//        });
    }

}
