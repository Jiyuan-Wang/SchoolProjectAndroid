package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;

public class VolunteerProfile extends AppCompatActivity implements View.OnClickListener {
    private EditText Name_Profile,phone_Profile,Address_Profile,Description_Profile;
    private TextView email_Profile;
    private Button save;
    private String user;
    FirebaseFirestore firestore;
    Query firequery;
    private DocumentReference DataRef;
    private String Document_Path;
    private static final int LIMIT = 50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_volunteer_profile);
        save = findViewById(R.id.update_button2);
        save.setOnClickListener(this);
        email_Profile = findViewById(R.id.email_profile_elder2);
        Name_Profile = findViewById(R.id.name_profile_elder2);
        Address_Profile = findViewById(R.id.address_profile_elder2);
        phone_Profile = findViewById(R.id.phone_profile_elder2);
        Description_Profile = findViewById(R.id.description_profile_elder2);

        /* This part for loading user information reuses some code from "Get started with Cloud Firestore" (Firebase, 2022a). */
        user= VolunteerLoginActivity.GetUserEmail();
        Document_Path = com.example.myapplication.VolunteerLoginActivity.GetUserEmail();
        firestore = FirebaseFirestore.getInstance();
        firequery = firestore.collection("restaurants")
                .orderBy("avgRating", Query.Direction.DESCENDING).limit(LIMIT);
        DataRef= firestore.collection("Profile").document(Document_Path);
        DataRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("Name");
                            String email =  documentSnapshot.getString("Email");
                            String phone =  documentSnapshot.getString("Phone");
                            String address = documentSnapshot.getString("Address");
                            String description = documentSnapshot.getString("Description");


                            Name_Profile.setText(name);
                            email_Profile.setText(email);
                            phone_Profile.setText(phone);
                            Address_Profile.setText(address);
                            Description_Profile.setText(description);
                        } else {
                            Toast.makeText(VolunteerProfile.this, "Your Profile is empty!", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(VolunteerProfile.this, "Error!", Toast.LENGTH_SHORT).show();
                        Log.d("TAG", e.toString());
                    }
                });

    }
    public void onClick(View view) {

        /* This part for updating user information reuses some code from "Get started with Cloud Firestore" (Firebase, 2022a). */
        if (view.getId() == R.id.update_button2) {
            String name =  Name_Profile.getText().toString();
            String phone =  phone_Profile.getText().toString().trim();
            String address = Address_Profile.getText().toString();
            String description = Description_Profile.getText().toString();
            FirebaseFirestore.getInstance().collection("Profile").document(user).update("Name", name);
            FirebaseFirestore.getInstance().collection("Profile").document(user).update("Phone", phone);
            FirebaseFirestore.getInstance().collection("Profile").document(user).update("Description", description);
            FirebaseFirestore.getInstance().collection("Profile").document(user).update("Address", address);
            Toast.makeText(VolunteerProfile.this, "Successful", Toast.LENGTH_SHORT).show();
        }
    }
}
/*
Reference list:
Firebase. (2022a). Get started with Cloud Firestore. https://firebase.google.com/docs/firestore/quickstart
 */