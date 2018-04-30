package com.juniata.ifpizza.todoapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class TaskDetails extends AppCompatActivity {

    int ActiveTask;
    long subNum;
    static final String SUBNUM = "subnum";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        final SharedPreferences sharedPreferences = getSharedPreferences("preferences", Context.MODE_PRIVATE);


//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ActiveTask = getIntent().getIntExtra("taskNumber", 1);


        final TextInputEditText descField = findViewById(R.id.description_field);
        final ListView subTasks = findViewById(R.id.sub_task_list);
        final Button saveButton = findViewById(R.id.save_task);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GeneralDbHelper myDbHelper = new GeneralDbHelper(getApplicationContext());
                SQLiteDatabase db = myDbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();

                subNum = sharedPreferences.getLong("subNumber", 0) + 1;

                String name = "Subtask #" + subNum;

                values.put(SubContract.SubEntry.COLUMN_SUB_NAME, name);
                values.put(SubContract.SubEntry.COLUMN_TASK_ID, ActiveTask);

                long newRowId = db.insert(TaskContract.TaskEntry.TABLE_NAME, null, values);

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putLong("subNumber", newRowId);
                editor.apply();

//                refreshDisplay();
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_lists_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        return true;
    }


    public void refreshDisplay(){

        GeneralDbHelper myDbHelper = new GeneralDbHelper(getApplicationContext());
        SQLiteDatabase db = myDbHelper.getWritableDatabase();

        String[] projection = {
                SubContract.SubEntry._ID,
                SubContract.SubEntry.COLUMN_SUB_NAME
        };

        String[] bind = {
                SubContract.SubEntry._ID,
                SubContract.SubEntry.COLUMN_SUB_NAME
        };

        Cursor cursor = db.rawQuery("SELECT " + SubContract.SubEntry.COLUMN_SUB_NAME + ", " + SubContract.SubEntry._ID + " FROM " +  SubContract.SubEntry.TABLE_NAME + " WHERE " + SubContract.SubEntry.COLUMN_TASK_ID + " = " + ActiveTask + " GROUP BY " + SubContract.SubEntry.COLUMN_SUB_NAME + " ORDER BY " + SubContract.SubEntry._ID + " ASC", null);

        int [] to = new int[]{R.id.subName};

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(getApplicationContext(), R.layout.sub_item, cursor, projection, to, 0);

        final ListView subView = findViewById(R.id.sub_task_list);
        subView.setAdapter(adapter);

        TextView emptyView = findViewById(R.id.noTasks);
        subView.setEmptyView(emptyView);

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
