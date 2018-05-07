package com.juniata.ifpizza.todoapp;

import android.provider.BaseColumns;

public class SubContract {

    public SubContract() {

    }

    public static abstract class SubEntry implements BaseColumns {
        public static final String TABLE_NAME = "subtask";
        public static final String COLUMN_TASK_ID = "taskid";
        public static final String COLUMN_SUB_NAME = "subname";
        public static final String COLUMN_COMP_FLAG = "completedflag";
        public static final String COLUMN_DEL_FLAG = "deletedflag";
    }
}
