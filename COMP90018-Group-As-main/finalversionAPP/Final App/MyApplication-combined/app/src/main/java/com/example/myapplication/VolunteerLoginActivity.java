package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class VolunteerLoginActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView elderly;
    private TextView login;
    private TextView register;
    private static String UserEmail;

    private EditText Log_email,Log_password;
    FirebaseAuth FBauth;
    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_volunteer_login);
        elderly = findViewById(R.id.textView_asElderly);
        elderly.setOnClickListener(this);
        login = findViewById(R.id.button_volunteerLogin);
        login.setOnClickListener(this);
        register = findViewById(R.id.button_volunteerRegister);
        register.setOnClickListener(this);


        Log_email = findViewById(R.id.Email_Log_volunteer);
        Log_password = findViewById(R.id.Password_Log_volunteer);
        FBauth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.textView_asElderly){
            startActivity(new Intent(this, ElderlyLoginActivity.class));
        }
        else if (view.getId() == R.id.button_volunteerLogin){
            Login();

        }
        else if (view.getId() == R.id.button_volunteerRegister){
            startActivity(new Intent(this, RegisterActivity.class));
        }
    }

    public void Login()
    {
        String email = Log_email.getText().toString().trim();

        String password = Log_password.getText().toString().trim();

        if(email.isEmpty())
        {
            Log_email.setError("USERName can not be empty");
        }
        else if(password.isEmpty())
        {
            Log_password.setError("Password should not be empty");

        }
        else {

            /* This part for checking user type reuses some code from "Get started with Cloud Firestore" (Firebase, 2022a). */
            FirebaseFirestore.getInstance().collection("Profile").document(email).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()){
                                    type = document.getString("Type");
                                    if (type.equals("Volunteer")){

                                        /* This part for signing in  reuses some code from "Get Started with Firebase Authentication on Android" (Firebase, 2022b). */
                                        FBauth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if(task.isSuccessful())
                                                {
                                                    Toast.makeText(VolunteerLoginActivity.this,"Login Succeed!",Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(VolunteerLoginActivity.this, VolunteerActivity.class));
                                                    UserEmail = email;
                                                }
                                                else
                                                {
                                                    Toast.makeText(VolunteerLoginActivity.this,"Incorrect Email or PassWord!",Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                                    }
                                    else{
                                        Toast.makeText(VolunteerLoginActivity.this, "Incorrect user type, please login as an elderly!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else {
                                    Toast.makeText(VolunteerLoginActivity.this, "Incorrect Email or Password!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(VolunteerLoginActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                            Log.d("TAG", e.toString());
                        }
                    });

        }
    }
    public static String GetUserEmail() {
        return  UserEmail;
    }
}

/*
Reference list:
Firebase. (2022a). Get started with Cloud Firestore. https://firebase.google.com/docs/firestore/quickstart
Firebase. (2022b). Get Started with Firebase Authentication on Android. https://firebase.google.com/docs/auth/android/start
 */