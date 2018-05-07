package com.juniata.ifpizza.todoapp;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

//**********Charlie Bein**********
//*********Timothy Benson*********
//**********Joe Maskell***********
//***********Ben Tipton***********
//***********05/07/2018***********

//**********LIST**********
public class ListContents extends AppCompatActivity {
    long taskNum;
    static final String TASKNUM = "tasknum";
    int ActiveList;
    private SwipeRefreshLayout swipeContainer;
    String m_Text = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_contents);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActiveList = getIntent().getIntExtra("listnum", 0);
        final SharedPreferences sharedPreferences = getSharedPreferences("preferences", Context.MODE_PRIVATE);
        final ListView listView = findViewById(R.id.tasksList);
        TextView emptyView = findViewById(R.id.noTasks);
        refreshDisplay();

        swipeContainer = findViewById(R.id.listRefresh);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshDisplay();
            }
        });

        FloatingActionButton fab = findViewById(R.id.taskFab);
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
                int taskNumber = cursor.getInt(cursor.getColumnIndex(TaskContract.TaskEntry._ID));
                Intent intent = new Intent(getApplicationContext(), TaskDetails.class);
                intent.putExtra(TASKNUM, taskNumber);
                setResult(RESULT_OK, intent);
                startActivity(intent);
            }
        });
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
        Cursor cursor = db.rawQuery("SELECT " + TaskContract.TaskEntry.COLUMN_TASK_NAME + ", " + TaskContract.TaskEntry._ID + ", " + TaskContract.TaskEntry.COLUMN_COMP_FLAG + " FROM " +  TaskContract.TaskEntry.TABLE_NAME + " WHERE " + TaskContract.TaskEntry.COLUMN_LIST_ID + " = " + ActiveList + " AND " + TaskContract.TaskEntry.COLUMN_DEL_FLAG + " = 0" + " GROUP BY " + TaskContract.TaskEntry.COLUMN_TASK_NAME + " ORDER BY " + TaskContract.TaskEntry._ID + " ASC", null);
        cursor.moveToFirst();
        TaskAdapter adapter = new TaskAdapter(getApplicationContext(), cursor);
        final ListView taskView = findViewById(R.id.tasksList);
        taskView.setAdapter(adapter);
        TextView emptyView = findViewById(R.id.noTasks);
        taskView.setEmptyView(emptyView);
        swipeContainer.setRefreshing(false);
    }

    //*****Menu*****
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }

    //*****Activities*****
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        GeneralDbHelper dbHelper = new GeneralDbHelper(getApplicationContext());
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final String currentList = ListContract.ListEntry._ID + " = " + ActiveList;

        if(id == R.id.renameList){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Rename List");
            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ContentValues values = new ContentValues();
                    m_Text = input.getText().toString();
                    values.put(ListContract.ListEntry.COLUMN_LIST_NAME, m_Text);
                    db.update(ListContract.ListEntry.TABLE_NAME, values, currentList, null);
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    dialog.cancel();
                }
            });
            builder.show();
            refreshDisplay();
        }

        else if (id == R.id.deleteList) {
            ContentValues values = new ContentValues();
            values.put(ListContract.ListEntry.COLUMN_DEL_FLAG, 1);
            db.update(ListContract.ListEntry.TABLE_NAME, values, currentList, null);
            finish();
        }
        return true;
    }

}
