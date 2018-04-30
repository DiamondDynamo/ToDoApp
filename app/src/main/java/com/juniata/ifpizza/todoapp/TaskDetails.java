package com.juniata.ifpizza.todoapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

public class TaskDetails extends AppCompatActivity {

    int ActiveList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ActiveList = getIntent().getIntExtra("tasknum", 0);

        final TextInputEditText descField = findViewById(R.id.description_field);
        final ListView subTasks = findViewById(R.id.sub_task_list);
        final Button saveButton = findViewById(R.id.save_task);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_lists_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        return true;
    }

}
