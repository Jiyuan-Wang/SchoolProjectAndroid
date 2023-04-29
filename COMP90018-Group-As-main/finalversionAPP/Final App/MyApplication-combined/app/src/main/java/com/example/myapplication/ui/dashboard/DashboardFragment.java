package com.example.myapplication.ui.dashboard;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;

import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;


import com.example.myapplication.R;
import com.example.myapplication.Utils.ImageUtils;

import com.example.myapplication.databinding.FragmentDashboardBinding;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class DashboardFragment extends Fragment {




    private FragmentDashboardBinding binding;
    private EditText Title_Task, Location_Save, Description_Task,Time_Task;
    private Button  Task_Post, addImage, viewVolunteer,completeTask,Date_Task;
    private String Document_Path;
    private ImageView viewPic;
    private TextView taskState;

    private DatePickerDialog datePickerDialog;

    private static final String KEY_TITLE = "Title", KEY_DESCRIPTION = "Description",KEY_DATE= "Date",KEY_TIME ="Time", KEY_ADDRESS="Address";

    private static String volunteerEmail;

    private FirebaseFirestore Fstore;

    Bitmap photo;

    private final int REQUEST_CODE = 22;



    private DocumentReference TaskDataRef;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        Title_Task = root.findViewById(R.id.title_task_elder);
        Description_Task = root.findViewById(R.id.description_task_elder);
        Date_Task = root.findViewById(R.id.date_task_elder);
        Time_Task = root.findViewById(R.id.time_task_elder);
        viewVolunteer = root.findViewById(R.id.button_viewVolunteer);
        viewVolunteer.setVisibility(View.INVISIBLE);

        Location_Save = root.findViewById(R.id.time_task_elder3);
        completeTask = root.findViewById(R.id.button_completeTask);
        Task_Post = root.findViewById(R.id.PostTask_elder);
        taskState = root.findViewById(R.id.textView_taskState);

        addImage = root.findViewById(R.id.button_addImage);

        viewPic = root.findViewById(R.id.imageView);
        viewPic.setVisibility(View.INVISIBLE);

        Document_Path = com.example.myapplication.ElderlyLoginActivity.GetUserEmail();


        Firebase_Init();
        initDatePicker();

        Load_Task();

        Date_Task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();
            }
        });


        viewVolunteer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /* This part for loading volunteer information reuses some code from "Get started with Cloud Firestore" (Firebase, 2022a). */
                Fstore.collection("Profile").document(volunteerEmail).get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()) {
                                    String email = documentSnapshot.getString("Email");
                                    String phone = documentSnapshot.getString("Phone");
                                    new AlertDialog.Builder(getActivity()).setTitle("volunteer contact details")
                                            .setMessage("Email: " + email + "\n" + "Phone: " + phone).setPositiveButton("OK", null).show();

                                } else {
                                    Toast.makeText(getActivity(), "failed to get volunteer detail", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show();
                                Log.d("TAG", e.toString());
                            }
                        });

            }
        });

        completeTask.setOnClickListener(new View.OnClickListener() {
            @Override
            /* This part for checking and updating task status reuses some code from "Get started with Cloud Firestore" (Firebase, 2022a). */
            public void onClick(View view) {
                String Status = taskState.getText().toString();
                if (Status.equals("Not Accepted")){
                    /* This part for showing alert reuses some code from "AlertDialog.Builder." (Android developer, 2022a). */
                    new AlertDialog.Builder(getActivity()).setTitle("Cancel the task")
                            .setMessage("The task has not been accepted yet. Do you want to cancel it?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Toast.makeText(getActivity(), "The task is Canceled!", Toast.LENGTH_SHORT).show();
                                    Title_Task.setText("");
                                    Description_Task.setText("");
                                    Time_Task.setText("");
                                    Location_Save.setText("");
                                    taskState.setText("Not Accepted");
                                    viewVolunteer.setVisibility(View.INVISIBLE);
                                    Task_Post.setText("Post task");
                                    completeTask.setVisibility(View.INVISIBLE);
                                    Fstore.collection("Task").document(Document_Path).update("Finished", "true");
                                    Fstore.collection("Task").document(Document_Path).update("Address", "");
                                }
                            }).setNegativeButton("Back", null).show();
                }
                else{
                    /* This part for showing alert reuses some code from "AlertDialog.Builder." (Android developer, 2022a). */
                    new AlertDialog.Builder(getActivity()).setTitle("Complete the task")
                            .setMessage("Are you sure the task is completed?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Fstore.collection("Task").document(Document_Path).update("Finished", "true")
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Fstore.collection("Profile").document(volunteerEmail).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                            DocumentSnapshot document = task.getResult();
                                                            List<String> tasks = (List<String>) document.get("Task");
                                                            tasks.remove(Document_Path);
                                                            Fstore.collection("Profile").document(volunteerEmail).update("Task", tasks);
                                                            Toast.makeText(getActivity(), "The task is Completed!", Toast.LENGTH_SHORT).show();
                                                            Title_Task.setText("");
                                                            Description_Task.setText("");
                                                            Date_Task.setText("");
                                                            Time_Task.setText("");
                                                            Location_Save.setText("");
                                                            Task_Post.setText("Post task");
                                                            taskState.setText("Not Accepted");
                                                            viewVolunteer.setVisibility(View.INVISIBLE);
                                                            completeTask.setVisibility(View.INVISIBLE);
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(getActivity(), "Something went wrong, please try again", Toast.LENGTH_SHORT).show();

                                                        }
                                                    });

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getActivity(), "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            }).setNegativeButton("Back", null).show();
                }


            }
        });

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(camera, REQUEST_CODE);
            }
        });

        Task_Post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String address = Location_Save.getText().toString();
                if(address.isEmpty())
                {
                    Location_Save.setError("Address can not be empty");
                }
                else{

                    /* This part for checking whether address is valid reuses some code from "Geocoder" (Android developer, 2022c). */
                    Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                    List<Address> coordinates = null;
                    try {
                        coordinates = geocoder.getFromLocationName(address, 1);
                        if (coordinates.size() == 1)
                        {
                            Task_Post.setText("Update task");
                            SendPost();
                        }
                        else {
                            Toast.makeText(getActivity(), "Invalid Address", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {

                    }
                }
            }
        });





        return root;
    }

    public void Firebase_Init()
    {
        Fstore = FirebaseFirestore.getInstance();

        TaskDataRef= Fstore.collection("Task").document(Document_Path);

    }

    /* The below parts for the date picker reuses some code from "Pop Up Date Picker Android Studio Tutorial" on YouTube (Code with Cal, 2020). */
    private void initDatePicker()
    {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener()
        {

            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day)
            {
                month = month + 1;
                String date = makeDateString(day, month, year);
                Date_Task.setText(date);
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(getActivity(), style, dateSetListener, year, month, day);
    }

    private String makeDateString(int day, int month, int year)
    {
        return getMonthFormat(month) + " " + day + " " + year;
    }

    private String getMonthFormat(int month)
    {
        if(month == 1)
            return "JAN";
        if(month == 2)
            return "FEB";
        if(month == 3)
            return "MAR";
        if(month == 4)
            return "APR";
        if(month == 5)
            return "MAY";
        if(month == 6)
            return "JUN";
        if(month == 7)
            return "JUL";
        if(month == 8)
            return "AUG";
        if(month == 9)
            return "SEP";
        if(month == 10)
            return "OCT";
        if(month == 11)
            return "NOV";
        if(month == 12)
            return "DEC";

        return "JAN";
    }


    public void SendPost()
    {
        /* This part for posting tasks reuses some code from "Get started with Cloud Firestore" (Firebase, 2022a). */
        String title =  Title_Task.getText().toString();
        String description =  Description_Task.getText().toString();
        String date =  Date_Task.getText().toString();
        String time = Time_Task.getText().toString();
        String address = Location_Save.getText().toString();

        if (title.isEmpty()){
            Title_Task.setError("Title cannot be empty");
            return;
        }
        if (description.isEmpty()){
            Description_Task.setError("Description cannot be empty");
            return;
        }
        if (date.isEmpty()){
            Date_Task.setError("Date cannot be empty");
            return;
        }
        if (time.isEmpty()){
            Time_Task.setError("Time cannot be empty");
            return;
        }



        HashMap<String,Object> TaskInfo = new HashMap<>();
        TaskInfo.put(KEY_TITLE,title);
        TaskInfo.put(KEY_DESCRIPTION,description);
        TaskInfo.put(KEY_DATE,date);
        TaskInfo.put(KEY_TIME,time);
        TaskInfo.put(KEY_ADDRESS,address);

        if (photo != null){
            TaskInfo.put("Image", ImageUtils.compressPhoto(photo));
        }
        else{
            TaskInfo.put("Image", "");
        }

        TaskInfo.put("Accepted", "false");
        TaskInfo.put("Finished", "false");


        Fstore.collection("Task").document(Document_Path).set(TaskInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "Successful", Toast.LENGTH_SHORT).show();
                        completeTask.setVisibility(View.VISIBLE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity() ,"Failed", Toast.LENGTH_SHORT).show();
                        Log.d("TAG", e.toString());
                    }
                });

    }


    public void Load_Task() {

        TaskDataRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            if (documentSnapshot.getString("Finished").equals("true")){
                                completeTask.setVisibility(View.INVISIBLE);
                                return;
                            }
                            completeTask.setVisibility(View.VISIBLE);
                            Task_Post.setText("Update task");
                            String title = documentSnapshot.getString(KEY_TITLE);
                            String description =  documentSnapshot.getString(KEY_DESCRIPTION);
                            String date =  documentSnapshot.getString(KEY_DATE);
                            String time = documentSnapshot.getString(KEY_TIME);
                            String location = documentSnapshot.getString(KEY_ADDRESS);
                            if (documentSnapshot.contains("Accepted")
                                    && documentSnapshot.getString("Accepted").equals("true")){
                                viewVolunteer.setVisibility(View.VISIBLE);
                                volunteerEmail = documentSnapshot.getString("Volunteer");
                                taskState.setText("Accepted by " + volunteerEmail);
                            }



                            Title_Task.setText(title);
                            Description_Task.setText(description);
                            Date_Task.setText(date);
                            Time_Task.setText(time);
                            viewPic.setVisibility(View.INVISIBLE);
                            Location_Save.setText(location);

                        } else {
                            completeTask.setVisibility(View.INVISIBLE);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show();
                        Log.d("TAG", e.toString());
                    }
                });
    }

    /* This part for the camera reuses some code from "How to Build a Camera App in Android Studio | Simple Camera App (2022)" on YouTube (AllCodingTutorials, 2022). */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK)
        {
            photo = (Bitmap) data.getExtras().get("data");
            viewPic.setImageBitmap(photo);
            viewPic.setVisibility(View.VISIBLE);
        }
        else {
            Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_SHORT).show();
            super.onActivityResult(requestCode, resultCode, data);
        }
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}

/*
Reference list:
Android developers. (2022a). AlertDialog.Builder. https://developer.android.com/reference/android/location/Geocoder
Android developers. (2022c). Geocoder. https://developer.android.com/reference/android/app/AlertDialog.Builder
Firebase. (2022a). Get started with Cloud Firestore. https://firebase.google.com/docs/firestore/quickstart
[AllCodingTutorials].(2022, October 17). How to Build a Camera App in Android Studio | Simple Camera App (2022) [YouTube Video]. YouTube. https://www.youtube.com/watch?v=59taMJThsFU
[Code with Cal].(2020, December 20). Pop Up Date Picker Android Studio Tutorial [YouTube Video]. YouTube. https://www.youtube.com/watch?v=qCoidM98zNk
 */