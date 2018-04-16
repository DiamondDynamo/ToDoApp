package com.juniata.ifpizza.todoapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GeneralDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "todo.db";
    public static final String TEXT_TYPE = " TEXT";
    public static final String BOOL_TYPE = " BOOLEAN";
    public static final String INT_TYPE = " INTEGER";
    public static final String COM_SEP = ", ";

    public static final String SQL_CREATE_LIST = "CREATE TABLE " + ListContract.ListEntry.TABLE_NAME + " (" + ListContract.ListEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 1" +  COM_SEP + ListContract.ListEntry.COLUMN_LIST_NAME + TEXT_TYPE + " )";
    public static final String SQL_DELETE_LIST = "DROP TABLE IF EXISTS " + ListContract.ListEntry.TABLE_NAME;
    public static final String SQL_KEY_RESET_LIST = "UPDATE SQLITE_SEQUENCE SET SEQ=0 WHERE name = '" + ListContract.ListEntry.TABLE_NAME + "'";

    public static final String SQL_CREATE_TASK = "CREATE TABLE " + TaskContract.TaskEntry.TABLE_NAME + " ( " + TaskContract.TaskEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 1" + COM_SEP + TaskContract.TaskEntry.COLUMN_TASK_NAME + TEXT_TYPE + COM_SEP + TaskContract.TaskEntry.COLUMN_DESCRIPTION + TEXT_TYPE + COM_SEP + TaskContract.TaskEntry.COLUMN_COMP_FLAG + BOOL_TYPE + COM_SEP + TaskContract.TaskEntry.COLUMN_LIST_ID + INT_TYPE + COM_SEP + "FOREIGN KEY(" + TaskContract.TaskEntry.COLUMN_LIST_ID + ") REFERENCES " + ListContract.ListEntry.TABLE_NAME + "(" + ListContract.ListEntry._ID + "))";
    public static final String SQL_DELETE_TASK = "DROP TABLE IF EXISTS " + TaskContract.TaskEntry.TABLE_NAME;

    public GeneralDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public GeneralDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_LIST);
        db.execSQL(SQL_CREATE_TASK);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL(SQL_DELETE_TASK);
        db.execSQL(SQL_DELETE_LIST);
        onCreate(db);
    }

    public void resetDb(SQLiteDatabase db) {
        db.execSQL(SQL_DELETE_LIST);
        db.execSQL(SQL_KEY_RESET_LIST);
        onCreate(db);
    }
}
