package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class TasklistLoadEvent extends AppCompatActivity {

    static FirebaseFirestore Fb;
    private static List<TaskPost> TaskList = new ArrayList<>();
    private static String accepted = "false";
    private static FirebaseUser currentUser;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_tasklist_load_event);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        Fb = FirebaseFirestore.getInstance();
        ReadlistData();

    }



    /* This part for loading task information reuses some code from "Get started with Cloud Firestore" (Firebase, 2022a). */
    public void ReadlistData()
    {
        if (VolunteerActivity.getTaskType().equals("General")) {

            Fb.collection("Task").get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                TaskList.clear();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("TAG", document.getId() + " => " + document.getString("Title"));

                                    String title;
                                    String ID;
                                    String Description;
                                    String image;
                                    String address;
                                    String Date;
                                    title = document.getString("Title");
                                    ID = document.getId();
                                    Description = document.getString("Description");
                                    image = document.getString("Image");
                                    address = document.getString("Address");
                                    Date = document.getString("Date") +" "+ document.getString("Time");
                                    TaskPost postTask = new TaskPost(title, ID, Description, image, address, Date);
                                    if (!address.equals("")){
                                        if (!document.contains("Accepted") || document.getString("Accepted").equals("false")) {
                                            TaskList.add(postTask);
                                        }
                                    }
                                }
                                startActivity(new Intent(TasklistLoadEvent.this, VolunteerTaskListActivity.class));
                            } else {
                                Log.d("TAG", "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
        else{
            Fb.collection("Profile").document(currentUser.getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    TaskList.clear();
                    DocumentSnapshot document = task.getResult();
                    List<String> taskEmails = new ArrayList<>();
                    if (document.contains("Task")){
                        taskEmails = (List<String>) document.get("Task");
                    }
                    for (String email : taskEmails){
                        Fb.collection("Task").document(email).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task1) {
                                DocumentSnapshot documentSnapshot = task1.getResult();

                                String title;
                                String ID;
                                String Description;
                                String image;
                                String address;
                                String Date;
                                title = documentSnapshot.getString("Title");
                                ID = documentSnapshot.getId();
                                Description = documentSnapshot.getString("Description");
                                image = documentSnapshot.getString("Image");
                                address = documentSnapshot.getString("Address");
                                Date = documentSnapshot.getString("Date") +" "+ documentSnapshot.getString("Time");
                                if (!address.equals("")){
                                    TaskPost postTask = new TaskPost(title, ID, Description, image, address, Date);
                                    TaskList.add(postTask);
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("TAG", "Error getting task ");

                            }
                        });
                    }
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    startActivity(new Intent(TasklistLoadEvent.this, VolunteerTaskListActivity.class));

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("TAG", "Error getting task ");
                }
            });
        }

    }

    public static List<TaskPost> getTaskList(){
        return TaskList;
    }

    /* This part for checking task status reuses some code from "Get started with Cloud Firestore" (Firebase, 2022a). */
    public static Boolean isTaskAccepted(String email) {

        Fb.collection("Task").document(email).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            if (!documentSnapshot.contains("Accepted")){
                                accepted = "false";
                            }
                            else {
                                accepted = documentSnapshot.getString("Accepted");
                            }
                        } else {
                            accepted = "true";
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        accepted = "true";
                    }
                });
        return accepted.equals("true");
    }


}
/*
Reference list:
Firebase. (2022a). Get started with Cloud Firestore. https://firebase.google.com/docs/firestore/quickstart
 */