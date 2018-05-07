package com.juniata.ifpizza.todoapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class TaskDetails extends AppCompatActivity {

    int ActiveTask;
    long subNum;
    static final String SUBNUM = "subnum";
    String m_Text = "";
    String d_text = "";
    String n_text = "";

    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeTask);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshDisplay();
            }
        });


        final SharedPreferences sharedPreferences = getSharedPreferences("preferences", Context.MODE_PRIVATE);



//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ActiveTask = getIntent().getIntExtra("tasknum", 1);




        final TextView desc = findViewById(R.id.description);

        refreshDisplay();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.subFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GeneralDbHelper myDbHelper = new GeneralDbHelper(getApplicationContext());
                SQLiteDatabase db = myDbHelper.getWritableDatabase();
                final ContentValues values = new ContentValues();

                subNum = sharedPreferences.getLong("subNumber", 0) + 1;

                AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                builder.setTitle("Set subtask name");
                final EditText input = new EditText(getApplicationContext());

                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                n_text = "";

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        n_text = input.getText().toString();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        n_text = null;
                    }
                });
                if(n_text == ""){
                    String name = "Subtask #" + subNum;
                    values.put(SubContract.SubEntry.COLUMN_SUB_NAME, name);
                    values.put(SubContract.SubEntry.COLUMN_TASK_ID, ActiveTask);
                    values.put(SubContract.SubEntry.COLUMN_DEL_FLAG, 0);
                    values.put(SubContract.SubEntry.COLUMN_COMP_FLAG, 0);

                    long newRowId = db.insert(SubContract.SubEntry.TABLE_NAME, null, values);

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putLong("subNumber", newRowId);
                    editor.apply();
                } else if (n_text != null) {
                    values.put(SubContract.SubEntry.COLUMN_SUB_NAME, n_text);
                    values.put(SubContract.SubEntry.COLUMN_TASK_ID, ActiveTask);
                    values.put(SubContract.SubEntry.COLUMN_DEL_FLAG, 0);
                    values.put(SubContract.SubEntry.COLUMN_COMP_FLAG, 0);

                    long newRowId = db.insert(SubContract.SubEntry.TABLE_NAME, null, values);

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putLong("subNumber", newRowId);
                    editor.apply();
                }

                refreshDisplay();
            }
        });

        //TODO: Get entering description actually working
        desc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                GeneralDbHelper dbHelper = new GeneralDbHelper(getApplicationContext());
                final SQLiteDatabase db = dbHelper.getWritableDatabase();

                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(TaskDetails.this, R.style.AppTheme_NoActionBar));
                builder.setTitle("Set Description");
                final EditText input = new EditText(getApplicationContext());

                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ContentValues values = new ContentValues();
                        d_text = input.getText().toString();
                        values.put(TaskContract.TaskEntry.COLUMN_DESCRIPTION, d_text);
                        db.update(TaskContract.TaskEntry.TABLE_NAME, values, TaskContract.TaskEntry._ID + " = " + ActiveTask, null);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
                refreshDisplay();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_task, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        GeneralDbHelper dbHelper = new GeneralDbHelper(getApplicationContext());
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final String currentTask = TaskContract.TaskEntry._ID + " = " + ActiveTask;


        if(id == R.id.renameTask){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.action_rename_task);

            final EditText input = new EditText(this);


            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ContentValues values = new ContentValues();
                    m_Text = input.getText().toString();
                    values.put(TaskContract.TaskEntry.COLUMN_TASK_NAME, m_Text);
                    db.update(TaskContract.TaskEntry.TABLE_NAME, values, currentTask, null);
                }

            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();

            refreshDisplay();

        } else if (id == R.id.deleteTask) {
            ContentValues values = new ContentValues();
            values.put(TaskContract.TaskEntry.COLUMN_DEL_FLAG, 1);
            db.update(TaskContract.TaskEntry.TABLE_NAME, values, currentTask, null);
            finish();
        }


        return true;
    }



    public void refreshDisplay(){

        GeneralDbHelper myDbHelper = new GeneralDbHelper(getApplicationContext());
        final SQLiteDatabase db = myDbHelper.getWritableDatabase();
        final TextView emptyText = findViewById(R.id.no_subs);
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeTask);

        String[] columns = {TaskContract.TaskEntry.COLUMN_TASK_NAME, TaskContract.TaskEntry._ID};

        Cursor titleCursor = db.query(TaskContract.TaskEntry.TABLE_NAME, columns, TaskContract.TaskEntry._ID + " = " + ActiveTask, null, null, null, null, null);

        titleCursor.moveToFirst();

        String title = titleCursor.getString(titleCursor.getColumnIndexOrThrow(TaskContract.TaskEntry.COLUMN_TASK_NAME));
        setTitle(title);

        String[] projection = {
                SubContract.SubEntry._ID,
                SubContract.SubEntry.COLUMN_SUB_NAME
        };

        Cursor cursor = db.rawQuery("SELECT " + SubContract.SubEntry.COLUMN_SUB_NAME + ", " + SubContract.SubEntry._ID + ", " + SubContract.SubEntry.COLUMN_COMP_FLAG + " FROM " +  SubContract.SubEntry.TABLE_NAME + " WHERE " + SubContract.SubEntry.COLUMN_TASK_ID + " = " + ActiveTask + " AND " + SubContract.SubEntry.COLUMN_DEL_FLAG + " = 0" +  " GROUP BY " + SubContract.SubEntry.COLUMN_SUB_NAME + " ORDER BY " + SubContract.SubEntry._ID + " ASC", null);


        int [] to = new int[]{R.id.subName};

        SubAdapter adapter = new SubAdapter(getApplicationContext(), cursor);

        final ListView subView = findViewById(R.id.sub_task_list);
        subView.setAdapter(adapter);
        subView.setEmptyView(emptyText);

        subView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                CheckBox checkBox = (CheckBox) view.findViewById(R.id.subComplete);
                Boolean checked = checkBox.isChecked();

                ContentValues values = new ContentValues();
                values.put(SubContract.SubEntry.COLUMN_COMP_FLAG, checked);
                values.put(SubContract.SubEntry.COLUMN_DEL_FLAG, checked);

                db.update(SubContract.SubEntry.TABLE_NAME, values, cursor.getInt(cursor.getColumnIndex(SubContract.SubEntry._ID)) + " = " + SubContract.SubEntry._ID, null);
                refreshDisplay();
            }
        });

        TextView emptyView = findViewById(R.id.noTasks);
//        subView.setEmptyView(emptyView);

        swipeContainer.setRefreshing(false);
    }

}
