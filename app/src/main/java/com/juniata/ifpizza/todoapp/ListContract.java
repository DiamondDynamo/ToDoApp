package com.juniata.ifpizza.todoapp;
import android.provider.BaseColumns;

//**********Charley Bein**********
//*********Timothy Benson*********
//**********Joe Maskell***********
//***********Ben Tipton***********
//***********05/07/2018***********

//**********LIST ITEM**********
public final class ListContract {
    public static abstract class ListEntry implements BaseColumns {
        public static final String TABLE_NAME = "list";
        public static final String COLUMN_LIST_NAME = "listName";
        public static final String COLUMN_DEL_FLAG = "deletedflag";
    }
}
