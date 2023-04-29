package com.example.myapplication.ui.notifications;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentNotificationsBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.DocumentSnapshot;
import java.util.HashMap;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private Button Update_Button;

    private static final int LIMIT = 50;

    FirebaseFirestore firestore;
    Query firequery;
    private EditText Name_Profile,phone_Profile,Address_Profile,Description_Profile;
    private TextView email_Profile;
    private static final String KEY_NAME = "Name", KEY_EMAIL = "Email",KEY_PHONE = "Phone",KEY_ADDRESS ="Address";
    private static final String KEY_DESCRIPTION = "Description";

    private DocumentReference DataRef;
    private String Document_Path;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //------------------------------------------my add-----------------------------------------
        Update_Button = root.findViewById(R.id.update_button);

        Name_Profile = root.findViewById(R.id.name_profile_elder);
        email_Profile = root.findViewById(R.id.email_profile_elder);
        phone_Profile = root.findViewById(R.id.phone_profile_elder);
        Address_Profile = root.findViewById(R.id.address_profile_elder);
        Description_Profile = root.findViewById(R.id.description_profile_elder);

        Document_Path = com.example.myapplication.ElderlyLoginActivity.GetUserEmail();

        initFirestore();

        Load_profile();
        Update_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Update_profile();

            }
        });

        return root;

    }

    public void initFirestore()
    {
        firestore = FirebaseFirestore.getInstance();
        firequery = firestore.collection("restaurants")
                .orderBy("avgRating", Query.Direction.DESCENDING).limit(LIMIT);

        DataRef= firestore.collection("Profile").document(Document_Path);

    }

    public void Update_profile() {


        /* This part for updating user information reuses some code from "Get started with Cloud Firestore" (Firebase, 2022a). */
        String name =  Name_Profile.getText().toString();
        String email =  email_Profile.getText().toString().trim();
        String phone =  phone_Profile.getText().toString().trim();
        String address = Address_Profile.getText().toString();
        String description = Description_Profile.getText().toString();

        HashMap<String,Object> users = new HashMap<>();
        users.put(KEY_NAME,name);
        users.put(KEY_EMAIL,email);
        users.put(KEY_PHONE,phone);
        users.put(KEY_ADDRESS,address);
        users.put(KEY_DESCRIPTION,description);
        users.put("Type","Elderly");
        firestore.collection("Profile").document(Document_Path).set(users)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "Successful", Toast.LENGTH_SHORT).show();
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

    public void Load_profile() {

        /* This part for loading user information reuses some code from "Get started with Cloud Firestore" (Firebase, 2022a). */
        DataRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString(KEY_NAME);
                            String email =  documentSnapshot.getString(KEY_EMAIL);
                            String phone =  documentSnapshot.getString(KEY_PHONE);
                            String address = documentSnapshot.getString(KEY_ADDRESS);
                            String description = documentSnapshot.getString(KEY_DESCRIPTION);


                            Name_Profile.setText(name);
                            email_Profile.setText(email);
                            phone_Profile.setText(phone);
                            Address_Profile.setText(address);
                            Description_Profile.setText(description);
                        } else {
                            Toast.makeText(getActivity(), "Your Profile is empty!", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
/*
Reference list:
Firebase. (2022a). Get started with Cloud Firestore. https://firebase.google.com/docs/firestore/quickstart
 */