package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView elderlyButton;
    private TextView volunteerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);
        elderlyButton = findViewById(R.id.button_elderly);
        volunteerButton = findViewById(R.id.button_volunteer);
        elderlyButton.setOnClickListener(this);
        volunteerButton.setOnClickListener(this);



    }

    @Override
    public void onClick(View view) {
        System.out.println(view.getId());
        System.out.println(R.id.button_elderly);
        if (view.getId() == R.id.button_elderly){
            System.out.println("elderly clicked");
            startActivity(new Intent(this, ElderlyLoginActivity.class));
        }
        else if (view.getId() == R.id.button_volunteer){
            startActivity(new Intent(this, VolunteerLoginActivity.class));
        }
    }
}