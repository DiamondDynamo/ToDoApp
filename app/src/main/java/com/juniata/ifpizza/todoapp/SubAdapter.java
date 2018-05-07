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

//**********Subtask Adapter**********
public class SubAdapter extends ResourceCursorAdapter {
    SubAdapter(Context context, Cursor cursor) {
        super(context,R.layout.sub_item, cursor, 0);
    }

    @Override
    public void bindView(View view, Context context, final Cursor cursor) {
        TextView subText = view.findViewById(R.id.subName);
        CheckBox subCheck = view.findViewById(R.id.subComplete);
        subText.setText(cursor.getString(cursor.getColumnIndex(SubContract.SubEntry.COLUMN_SUB_NAME)));

        if (cursor.getInt(cursor.getColumnIndex(SubContract.SubEntry.COLUMN_COMP_FLAG)) == 0) {
            subCheck.setChecked(false);
        }

        else {
            subCheck.setChecked(true);
        }

        subCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                GeneralDbHelper dbHelper = new GeneralDbHelper(buttonView.getContext());
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                int id = cursor.getInt(cursor.getColumnIndex(SubContract.SubEntry._ID));
                ContentValues values = new ContentValues();
                values.put(SubContract.SubEntry.COLUMN_COMP_FLAG, isChecked);
                values.put(SubContract.SubEntry.COLUMN_DEL_FLAG, isChecked);
                db.update(SubContract.SubEntry.TABLE_NAME, values, SubContract.SubEntry._ID + " = " + id, null);
            }
        });
    }
}