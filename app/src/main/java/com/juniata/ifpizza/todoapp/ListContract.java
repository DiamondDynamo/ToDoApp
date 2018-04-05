package com.juniata.ifpizza.todoapp;


import android.provider.BaseColumns;

public final class ListContract {

    public ListContract(){

    }

    public static abstract class ListEntry implements BaseColumns {
        public static final String TABLE_NAME = "list";
        public static final String COLUMN_LIST_NAME = "listName";
    }
}
