package com.example.myapplication;


import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.annotation.NonNull;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import com.google.android.material.navigation.NavigationView;

public class VolunteerActivity extends AppCompatActivity {

    Button Tasklist;
    Button acceptedTaskList;

    DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;

    private static String taskType = "";
    private NavigationView navigationView;


    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer);
        getSupportActionBar().setTitle("Homepage");

        drawerLayout = findViewById(R.id.drawerlayout);
        navigationView = findViewById(R.id.volnavigationview);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {



                switch (item.getItemId())
                {
                    case R.id.NaviVol_profile:
                        startActivity(new Intent(VolunteerActivity.this,VolunteerProfile.class));
                        break;
                    case R.id.navilogout_item:
                        startActivity(new Intent(VolunteerActivity.this,LoginActivity.class));
                        break;
                    case R.id.NaviVol_accpet:
                        taskType = "Accepted";
                        startActivity(new Intent(VolunteerActivity.this,TasklistLoadEvent.class));
                        break;
                    case R.id.NaviVol_tasklist:
                        taskType = "General";
                        startActivity(new Intent(VolunteerActivity.this,TasklistLoadEvent.class));
                        break;
                }


                return true;
            }
        });



        Tasklist = findViewById(R.id.Button_TaskList_Volunteer);
        acceptedTaskList = findViewById(R.id.button_acceptedTasks);
        acceptedTaskList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                taskType = "Accepted";
                startActivity(new Intent(VolunteerActivity.this,TasklistLoadEvent.class));

            }
        });
        Tasklist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                taskType = "General";
                startActivity(new Intent(VolunteerActivity.this,TasklistLoadEvent.class));
            }
        });

    }


    public static String getTaskType(){
        return taskType;
    }

}