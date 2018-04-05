package com.juniata.ifpizza.todoapp;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TaskDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "task.db";
    public static final String TEXT_TYPE = " TEXT";
    public static final String BOOL_TYPE = " BOOLEAN";
    public static final String INT_TYPE = " INTEGER";
    public static final String COM_SEP = ",";

    public static final String SQL_CREATE_TASK = "CREATE TABLE " + TaskContract.TaskEntry.TABLE_NAME + " (" + TaskContract.TaskEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 1" + COM_SEP + TaskContract.TaskEntry.COLUMN_TASK_NAME + TEXT_TYPE + COM_SEP + TaskContract.TaskEntry.COLUMN_DESCRIPTION + TEXT_TYPE + COM_SEP + TaskContract.TaskEntry.COLUMN_COMP_FLAG + BOOL_TYPE + COM_SEP + TaskContract.TaskEntry.COLUMN_LIST_ID + INT_TYPE + COM_SEP + "FOREIGN KEY(" + TaskContract.TaskEntry.COLUMN_LIST_ID + ") REFERENCES " + ListContract.ListEntry.TABLE_NAME + "(" + ListContract.ListEntry._ID + ")";

    public static final String SQL_DELETE = "DROP TABLE IF EXISTS " + TaskContract.TaskEntry.TABLE_NAME;


    public TaskDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public TaskDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TASK);
    }

    @Override
    public void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE);
        onCreate(db);
    }
}
