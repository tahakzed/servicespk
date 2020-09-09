package com.example.servicespk;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;


public class CurrentUserProfileFragment extends Fragment {

    private ListView listView;
    private TextView nameTV,
                    numberOfServicesTV,
                    cityTV,
                    bioTV;
    private ImageView profileImage;
    private Button addServicesButton;
    private String changedName,
                    changedCity,
                    name,
                    city,
                    phone,
                    bio;
    private Bundle args;

    private Uri filePath;

    FirebaseStorage storage;
    StorageReference storageReference;


    private final int PICK_IMAGE_REQUEST = 22;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_current_user_profile, container, false);
      //  listView=view.findViewById(R.id.current_user_profile_service_list);
        nameTV=view.findViewById(R.id.current_user_profile_nameTV);
        numberOfServicesTV=view.findViewById(R.id.current_user_profile_numberOfServices);
        cityTV=view.findViewById(R.id.current_user_profile_city_tv);
        bioTV=view.findViewById(R.id.current_user_profile_bio_tv);
        profileImage=view.findViewById(R.id.current_user_profile_image);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        args=getArguments();
        phone=args.getString("phone");
        initializeComponents();
        return view;
    }

    private void initializeComponents(){
        args=getArguments();
        phone=args.getString("phone");
        String numberOfServices="00";
        name=args.getString("name");
        city=args.getString("city");
        bio=args.getString("bio");
        numberOfServicesTV.setText(numberOfServices);
        cityTV.setText(city);
        nameTV.setText(name);
        bioTV.setText(bio);
        new GetDataFromFirestore(phone).readDataFromWorkersCollection(new FirestoreCallback() {
            @Override
            public void onCallback(Bundle bundle) {
                HashMap<String,String> services=(HashMap)bundle.getSerializable("services");
                if(services!=null){
                    numberOfServicesTV.setText(String.valueOf(services.keySet().size()));
                }
                else
                    return;
            }
        });


    }



    private void changeDataInDB() {

        FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
        DocumentReference documentReference=firebaseFirestore.document("users/"+phone);
        HashMap<String,Object> info=new HashMap<>();
        info.put("name",changedName);
        info.put("city",changedCity);
        documentReference.set(info);
        documentReference=firebaseFirestore.document("workers/"+phone);
        documentReference.set(info);
    }


}
