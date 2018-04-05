package com.juniata.ifpizza.todoapp;


import android.provider.BaseColumns;

public final class TaskContract {

    public TaskContract() {

    }

    public static abstract class TaskEntry implements BaseColumns {
        public static final String TABLE_NAME = "task";
        public static final String COLUMN_LIST_ID = "listid";
        public static final String COLUMN_TASK_NAME = "taskname";
        public static final String COLUMN_COMP_FLAG = "completedflag";
        public static final String COLUMN_DESCRIPTION = "description";
    }
}