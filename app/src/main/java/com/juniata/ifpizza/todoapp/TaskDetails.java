package com.juniata.ifpizza.todoapp;
import android.content.ContentValues;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

//**********Charlie Bein**********
//*********Timothy Benson*********
//**********Joe Maskell***********
//***********Ben Tipton***********
//***********05/07/2018***********

//**********TASK**********
public class TaskDetails extends AppCompatActivity {

    int ActiveTask;
    String m_Text = "";
    String n_text;

    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        swipeContainer =  findViewById(R.id.swipeTask);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshDisplay();
            }
        });
        ActiveTask = getIntent().getIntExtra("tasknum", 1);
        refreshDisplay();

        FloatingActionButton fab = findViewById(R.id.subFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GeneralDbHelper myDbHelper = new GeneralDbHelper(getApplicationContext());
                final SQLiteDatabase db = myDbHelper.getWritableDatabase();
                final ContentValues values = new ContentValues();
                AlertDialog.Builder builder = new AlertDialog.Builder(TaskDetails.this, R.style.AppTheme_NoActionBar);
                builder.setTitle("Set subtask name");
                final EditText input = new EditText(getApplicationContext());
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        n_text = input.getText().toString();
                        values.put(SubContract.SubEntry.COLUMN_SUB_NAME, n_text);
                        values.put(SubContract.SubEntry.COLUMN_TASK_ID, ActiveTask);
                        values.put(SubContract.SubEntry.COLUMN_DEL_FLAG, 0);
                        values.put(SubContract.SubEntry.COLUMN_COMP_FLAG, 0);
                        db.insert(SubContract.SubEntry.TABLE_NAME, null, values);
                        refreshDisplay();
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        n_text = null;
                    }
                });
                builder.show();
                refreshDisplay();
            }
        });

    }

    //*****Menu*****
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_task, menu);
        return true;
    }

    //*****Activities*****
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
        }

        else if (id == R.id.deleteTask) {
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
        swipeContainer = findViewById(R.id.swipeTask);
        String[] columns = {TaskContract.TaskEntry.COLUMN_TASK_NAME, TaskContract.TaskEntry._ID};
        Cursor titleCursor = db.query(TaskContract.TaskEntry.TABLE_NAME, columns, TaskContract.TaskEntry._ID + " = " + ActiveTask, null, null, null, null, null);
        titleCursor.moveToFirst();
        String title = titleCursor.getString(titleCursor.getColumnIndexOrThrow(TaskContract.TaskEntry.COLUMN_TASK_NAME));
        setTitle(title);
        Cursor cursor = db.rawQuery("SELECT " + SubContract.SubEntry.COLUMN_SUB_NAME + ", " + SubContract.SubEntry._ID + ", " + SubContract.SubEntry.COLUMN_COMP_FLAG + " FROM " +  SubContract.SubEntry.TABLE_NAME + " WHERE " + SubContract.SubEntry.COLUMN_TASK_ID + " = " + ActiveTask + " AND " + SubContract.SubEntry.COLUMN_DEL_FLAG + " = 0" +  " GROUP BY " + SubContract.SubEntry.COLUMN_SUB_NAME + " ORDER BY " + SubContract.SubEntry._ID + " ASC", null);
        int [] to = new int[]{R.id.subName};
        SubAdapter adapter = new SubAdapter(getApplicationContext(), cursor);
        final ListView subView = findViewById(R.id.sub_task_list);
        subView.setAdapter(adapter);
        subView.setEmptyView(emptyText);
        swipeContainer.setRefreshing(false);
    }

}
