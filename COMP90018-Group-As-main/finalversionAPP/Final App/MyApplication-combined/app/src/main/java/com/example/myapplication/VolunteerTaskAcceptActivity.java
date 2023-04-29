package com.example.myapplication;

import static com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Utils.ImageUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class VolunteerTaskAcceptActivity extends AppCompatActivity implements View.OnClickListener {



    /* This part for GPS sensor reuses some code from Week 5 tutorial (Irlitti, 2022). */
    private FusedLocationProviderClient fusedLocationClient;
    LocationRequest locationRequest;
    LocationCallback locationCallBack;
    private final int Request_Code_Location = 22;


    TextView Email_Task,Title_Task,Description_Task,Phone_Task,Expire_Task, Address_Task;
    Button Accept,BackToHome, Distance, Way;
    ImageView photoView;
    TextView Dist;


    TaskPost CurrentTask;

    FirebaseFirestore Fb;

    String userEmail = com.example.myapplication.VolunteerLoginActivity.GetUserEmail();
    String TaskAddress;

    double latitude1,longitude1,latitude2,longitude2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_volunteer_task_accept);
        Dist=findViewById(R.id.textView15);

        Fb = FirebaseFirestore.getInstance();

        /* This part for GPS sensor reuses some code from Week 5 tutorial (Irlitti, 2022). */
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000);
        locationRequest.setPriority(PRIORITY_HIGH_ACCURACY);
        locationCallBack = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if(locationResult != null) {
                    Log.d("LocationTest", "Location updates");
                    updateUI(locationResult.getLastLocation());
                }else{
                    Log.d("LocationTest", "Location updates fail: null");
                }
            }
        };


        Email_Task = findViewById(R.id.Email_elder_information);
        Title_Task = findViewById(R.id.Title_elder_information);
        Description_Task = findViewById(R.id.Description_Task_volunteer);
        Phone_Task = findViewById(R.id.Phone_elder_information);
        Expire_Task = findViewById(R.id.Expire_elder_Information);
        photoView = findViewById(R.id.imageView_photo);
        Address_Task=findViewById(R.id.textView18);

        Accept = findViewById(R.id.Accept_button);
        Accept.setOnClickListener(this);

        if (VolunteerActivity.getTaskType().equals("Accepted")){
            Accept.setVisibility(View.INVISIBLE);
        }

        BackToHome = findViewById(R.id.Back_to_V_button );
        BackToHome.setOnClickListener(this);

        Distance = findViewById(R.id.accept_button);
        Distance.setOnClickListener(this);
        Way = findViewById(R.id.accept_button2);
        Way.setOnClickListener(this);

        CurrentTask = com.example.myapplication.VolunteerTaskListActivity.GetCurrentTask();

        TaskAddress=CurrentTask.getAddress();
        Email_Task.setText(CurrentTask.getEmail());
        Title_Task.setText(CurrentTask.getTitle());
        Description_Task .setText(CurrentTask.getDescription());
        Expire_Task .setText(CurrentTask.getDate());
        Address_Task.setText(TaskAddress);
        String Elderly = CurrentTask.getEmail();
        Fb.collection("Profile").document(Elderly).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                String phone = document.getString("Phone");
                Phone_Task.setText(phone);
            }
        });
        if (CurrentTask.getImage() != "") {
            photoView.setImageBitmap(ImageUtils.decompressPhoto(CurrentTask.getImage()));
        }
        if (!TaskAddress.equals("")){

            /* This part for getting latitude and longitude of address reuses some code from "Geocoder" (Android developer, 2022c). */
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> coordinates = null;
            try {
                coordinates = geocoder.getFromLocationName(TaskAddress, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Address address = coordinates.get(0);
            longitude2 = address.getLongitude();
            latitude2 = address.getLatitude();
        }
        else{
            Accept.setVisibility(View.INVISIBLE);
            Distance.setVisibility(View.INVISIBLE);
            Way.setVisibility(View.INVISIBLE);
            Toast.makeText(VolunteerTaskAcceptActivity.this, "Sorry, this task has been cancelled", Toast.LENGTH_SHORT).show();
        }



    }

    public void onClick(View view)
    {
        if(view.getId() == R.id.Accept_button)
        {
            /* This part for checking whether a task has been accepted and accepting a task reuses some code from "Get started with Cloud Firestore" (Firebase, 2022a). */
            Fb.collection("Task").document(CurrentTask.getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot document = task.getResult();
                    String accepted = document.getString("Accepted");
                    if (!accepted.equals("true")){
                        /* This part for showing alert reuses some code from "AlertDialog.Builder." (Android developer, 2022a). */
                        new AlertDialog.Builder(VolunteerTaskAcceptActivity.this).setTitle("Accept the task")
                                .setMessage("Are you sure to accept this task?").setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Accept.setVisibility(View.INVISIBLE);
                                        Fb.collection("Task").document(CurrentTask.getEmail()).update("Accepted", "true");
                                        Fb.collection("Task").document(CurrentTask.getEmail()).update("Volunteer", userEmail);
                                        Fb.collection("Profile").document(userEmail).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                DocumentSnapshot document = task.getResult();
                                                List<String> tasks = new ArrayList<>();
                                                if (document.contains("Task")) {
                                                    tasks = (List<String>) document.get("Task");
                                                    String ThisTask = CurrentTask.getEmail();
                                                    if (!tasks.contains(ThisTask)){
                                                        tasks.add(ThisTask);
                                                    }
                                                }
                                                else{
                                                    String ThisTask = CurrentTask.getEmail();
                                                    tasks.add(ThisTask);
                                                }
                                                Fb.collection("Profile").document(userEmail).update("Task", tasks);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(VolunteerTaskAcceptActivity.this,"Something went wrong, please try again",Toast.LENGTH_SHORT).show();

                                            }
                                        });
                                        Toast.makeText(VolunteerTaskAcceptActivity.this,"Successfully accepted",Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(VolunteerTaskAcceptActivity.this, VolunteerActivity.class));
                                    }
                                }).setNegativeButton("no", null).show();

                    }
                    else{
                        Toast.makeText(VolunteerTaskAcceptActivity.this, "Sorry, this task has been accepted by someone else", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(VolunteerTaskAcceptActivity.this, VolunteerActivity.class));
                    }
                }
            });

        }
        else if(view.getId() == R.id.Back_to_V_button)
        {
            startActivity(new Intent(VolunteerTaskAcceptActivity.this,VolunteerActivity.class));
        }
        else if(view.getId() == R.id.accept_button)
        {
            updateLocation();
        }
        else if(view.getId() == R.id.accept_button2)
        {
            String a = latitude1 + "," + longitude1;
            String b = latitude2 + "," + longitude2;
            DisplayTrack(a, b);
        }

    }


    /* The below parts for location update, UI update, location permission request by GPS sensor reuses some code from Week 5 tutorial (Irlitti, 2022). */
    void updateLocation(){

        if (ActivityCompat.checkSelfPermission(VolunteerTaskAcceptActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallBack, null);
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if(location == null) {
                                Log.d("LocationTest", "null");
                            }else {
                                Log.d("LocationTest", "Success");
                                updateUI(location);
                            }
                        }
                    });

        }else{
            ActivityCompat.requestPermissions(VolunteerTaskAcceptActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_Code_Location);
        }

    }
    private void updateUI(Location location){
        float[] results = new float[1];
        latitude1 = location.getLatitude();
        longitude1 = location.getLongitude();
        Location.distanceBetween(latitude1, longitude1,
                latitude2, longitude2,
                results);
        Dist.setText(String.format("%.02f", results[0]/1000)+" km");

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == Request_Code_Location){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                updateLocation();
            }
        }
    }

    /* This part for displaying track on Google map reuses some code from "How to Display Track On Google Map in Android Studio | DisplayTrackOnMap | Android Coding" on YouTube (Android Coding, 2020). */
    private void DisplayTrack(String sSource, String sDestination) {

        try {
            Uri uri = Uri.parse("https://www.google.co.in/maps/dir/" + sSource + "/"
                    +sDestination);

            Intent intent = new Intent(Intent.ACTION_VIEW,uri);
            intent.setPackage("com.google.android.apps.maps");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        }catch(ActivityNotFoundException e){

            Uri uri = Uri.parse("https://play.google.come/store/apps/details?id=com.google.android.apps.maps");
            Intent intent = new Intent(Intent.ACTION_VIEW,uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

    }
}

/*
Reference list:
Android developers. (2022a). AlertDialog.Builder. https://developer.android.com/reference/android/location/Geocoder
Android developers. (2022c). Geocoder. https://developer.android.com/reference/android/location/Geocoder
Firebase. (2022a). Get started with Cloud Firestore. https://firebase.google.com/docs/firestore/quickstart
Firebase. (2022b). Get Started with Firebase Authentication on Android. https://firebase.google.com/docs/auth/android/start
Irlitti, A (2022). airlitti/COMP90018-2022-Tutorial. GitHub. https://github.com/airlitti/COMP90018-2022-Tutorial/blob/main/Week5%20-%20Sensors%2C%20Maps/Source%20Code/comp90018week5gps/app/src/main/java/com/example/comp90018_week5_gps/MainActivity.java
[Android coding].(2020, April 24). How to Display Track On Google Map in Android Studio | DisplayTrackOnMap | Android Coding [YouTube Video]. YouTube. https://www.youtube.com/watch?v=VR8RKM9LTyA&t=513s
 */
