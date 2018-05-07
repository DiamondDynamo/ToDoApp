package com.juniata.ifpizza.todoapp;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

//**********Charley Bein**********
//*********Timothy Benson*********
//**********Joe Maskell***********
//***********Ben Tipton***********
//***********05/07/2018***********

//**********Task Adapter**********
public class TaskAdapter extends ResourceCursorAdapter {
    TaskAdapter(Context context, Cursor cursor) {
        super(context, R.layout.task_item, cursor, 0);
    }

    @Override
    public void bindView(View view, Context context, final Cursor cursor) {
        TextView taskText = view.findViewById(R.id.taskName);
        final CheckBox taskCheck = view.findViewById(R.id.taskComplete);
        taskText.setText(cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_TASK_NAME)));

        taskCheck.setOnCheckedChangeListener(null);

        if (cursor.getInt(cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_COMP_FLAG)) != 0) {
            taskCheck.setChecked(true);
        }

        else {
            taskCheck.setChecked(false);
        }

        taskCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                GeneralDbHelper dbHelper = new GeneralDbHelper(buttonView.getContext());
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                int id = cursor.getInt(cursor.getColumnIndex(TaskContract.TaskEntry._ID));
                ContentValues values = new ContentValues();
                values.put(TaskContract.TaskEntry.COLUMN_COMP_FLAG, isChecked);
                db.update(TaskContract.TaskEntry.TABLE_NAME, values, TaskContract.TaskEntry._ID + " = " + id, null);
            }
        });
    }
}
