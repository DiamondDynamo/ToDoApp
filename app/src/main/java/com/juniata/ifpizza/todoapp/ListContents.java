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
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import java.util.List;


//**********LIST**********
public class ListContents extends AppCompatActivity {

    long taskNum;
    static final String TASKNUM = "tasknum";
    int ActiveList;
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_contents);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActiveList = getIntent().getIntExtra("listnum", 0);

        final SharedPreferences sharedPreferences = getSharedPreferences("preferences", Context.MODE_PRIVATE);

        final CheckBox checkBox = (CheckBox) findViewById(R.id.taskComplete);

        final ListView listView = (ListView) findViewById(R.id.tasksList);
        TextView emptyView = findViewById(R.id.noTasks);

        refreshDisplay();

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.listRefresh);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshDisplay();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.taskFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GeneralDbHelper myDbHelper = new GeneralDbHelper(getApplicationContext());
                SQLiteDatabase db = myDbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();

                taskNum = sharedPreferences.getLong("taskNumber", 0) + 1;

                String name = "Task #" + taskNum;

                values.put(TaskContract.TaskEntry.COLUMN_TASK_NAME, name);
                values.put(TaskContract.TaskEntry.COLUMN_LIST_ID, ActiveList);
                values.put(TaskContract.TaskEntry.COLUMN_DEL_FLAG, 0);
                values.put(TaskContract.TaskEntry.COLUMN_COMP_FLAG, 0);

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




//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    public void refreshDisplay(){

        GeneralDbHelper myDbHelper = new GeneralDbHelper(getApplicationContext());
        SQLiteDatabase db = myDbHelper.getWritableDatabase();

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.listRefresh);

        String[] columns = {ListContract.ListEntry.COLUMN_LIST_NAME, ListContract.ListEntry._ID};

        Cursor titleCursor = db.query(ListContract.ListEntry.TABLE_NAME, columns, ListContract.ListEntry._ID + " = " + ActiveList, null, null, null, null, null);

        titleCursor.moveToFirst();

        String title = titleCursor.getString(titleCursor.getColumnIndexOrThrow(ListContract.ListEntry.COLUMN_LIST_NAME));
        setTitle(title);

        String[] projection = {
//                TaskContract.TaskEntry._ID,
                TaskContract.TaskEntry.COLUMN_TASK_NAME
        };

        String[] bind = {
                TaskContract.TaskEntry._ID,
                TaskContract.TaskEntry.COLUMN_TASK_NAME
        };

//        Cursor cursor = db.query(TaskContract.TaskEntry.TABLE_NAME, bind, null, null, null, null, TaskContract.TaskEntry._ID + " ASC");



        Cursor cursor = db.rawQuery("SELECT " + TaskContract.TaskEntry.COLUMN_TASK_NAME + ", " + TaskContract.TaskEntry._ID + ", " + TaskContract.TaskEntry.COLUMN_COMP_FLAG + " FROM " +  TaskContract.TaskEntry.TABLE_NAME + " WHERE " + TaskContract.TaskEntry.COLUMN_LIST_ID + " = " + ActiveList + " AND " + TaskContract.TaskEntry.COLUMN_DEL_FLAG + " = 0" + " GROUP BY " + TaskContract.TaskEntry.COLUMN_TASK_NAME + " ORDER BY " + TaskContract.TaskEntry._ID + " ASC", null);

        cursor.moveToFirst();

        int [] to = new int[]{R.id.taskName};

        TaskAdapter adapter = new TaskAdapter(getApplicationContext(), cursor);

        final ListView taskView = findViewById(R.id.tasksList);
        taskView.setAdapter(adapter);

        TextView emptyView = findViewById(R.id.noTasks);
        taskView.setEmptyView(emptyView);

        swipeContainer.setRefreshing(false);
    }

}
