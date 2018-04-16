package com.juniata.ifpizza.todoapp;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

//**********LIST DATABASE**********
public class ListDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "list.db";
    public static final String TEXT_TYPE = " TEXT";
    public static final String COM_SEP = ", ";
    public static final String SQL_CREATE_LIST = "CREATE TABLE "
            + ListContract.ListEntry.TABLE_NAME
            + " ("
            + ListContract.ListEntry._ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 1"
            +  COM_SEP
            + ListContract.ListEntry.COLUMN_LIST_NAME
            + TEXT_TYPE
            + " )";

    public static final String SQL_DELETE = "DROP TABLE IF EXISTS " + ListContract.ListEntry.TABLE_NAME;
    public static final String SQL_KEY_RESET = "UPDATE SQLITE_SEQUENCE SET SEQ=0 WHERE name = '" + ListContract.ListEntry.TABLE_NAME + "'";

    public ListDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public ListDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_LIST);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL(SQL_DELETE);
        onCreate(db);
    }

    public void resetDb(SQLiteDatabase db) {
        db.execSQL(SQL_DELETE);
        db.execSQL(SQL_KEY_RESET);
        onCreate(db);
    }
}
