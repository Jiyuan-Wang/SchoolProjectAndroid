package com.example.myapplication;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;


public class VolunteerTaskListActivity extends AppCompatActivity {






    static TaskPost CurrentTask;

    private List<TaskPost> TaskList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        initTaskList();
        initTaskList();
        setContentView(R.layout.activity_volunteer_task_list);
        TaskAdapter adapter = new TaskAdapter(VolunteerTaskListActivity.this, R.layout.task_item,TaskList);
        adapter.notifyDataSetChanged();
        ListView lv = (ListView) findViewById(R.id.Tasks_list_data);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                CurrentTask = TaskList.get(position);
                if (VolunteerActivity.getTaskType().equals("General")) {
                    if (!TasklistLoadEvent.isTaskAccepted(CurrentTask.getEmail())) {
                        startActivity(new Intent(VolunteerTaskListActivity.this, VolunteerTaskAcceptActivity.class));
                    } else {
                        Toast.makeText(VolunteerTaskListActivity.this, "Sorry, this task has been accepted by someone else", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(VolunteerTaskListActivity.this, VolunteerActivity.class));
                    }
                }
                else{
                    startActivity(new Intent(VolunteerTaskListActivity.this, VolunteerTaskAcceptActivity.class));
                }
            }
        });

    }







    public void initTaskList() {
        TaskList  = com.example.myapplication.TasklistLoadEvent.getTaskList();


   }

   public static TaskPost GetCurrentTask() {
        return CurrentTask;

    }



}