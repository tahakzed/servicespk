package com.example.servicespk;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;


public class ServiceInfoFragment extends Fragment implements View.OnClickListener {

    private EditText serviceTypeET;
    private EditText chargesET;
    private Button addButton;
    private Bundle args;
    private double lat = 0, lng = 0;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private FirebaseFirestore firebaseFirestore;
    private DocumentReference docRef;
    private Context context;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 101;
    LocationManager locationManager;

    @Override
    public void onStart() {
        super.onStart();
        setUpLocationListener();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_service_info, container, false);
        args = getArguments();
        phone=args.getString("phone");
        context = view.getContext();
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        serviceTypeET = view.findViewById(R.id.fragment_service_info_service_type);
        chargesET = view.findViewById(R.id.fragment_service_info_charges);
        addButton = view.findViewById(R.id.fragment_service_info_add_button);

        addButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:
                if(grantResults.length!=0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                    setUpLocationListener();

                break;
        }
    }

    @SuppressLint("MissingPermission")
    private void setUpLocationListener() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        // for getting the current location update after every 2 seconds with high accuracy
        locationRequest = new LocationRequest().setInterval(2000).setFastestInterval(2000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        Location location=locationResult.getLastLocation();
                            lat = location.getLatitude();
                            lng = location.getLongitude();
                            Toast.makeText(context,"lat:"+lat+", lng:"+lng,Toast.LENGTH_LONG);

                    }
                }, Looper.myLooper());
    }



    @Override
    public void onClick(View view) {
        if (view.equals(addButton)) {
            serviceTypeET.setEnabled(false);
            chargesET.setEnabled(false);
            addButton.setEnabled(false);
            addServices();

        }
    }


    Map<String,String> services;
    String phone;
    private void addServices() {
        final String name=args.getString("name");
        phone=args.getString("phone");
        final String city=args.getString("city");
        final String bio=args.getString("bio");
        final String charges=chargesET.getText().toString();
        final String service=serviceTypeET.getText().toString();
        firebaseFirestore = FirebaseFirestore.getInstance();
        docRef=firebaseFirestore.document("workers/"+phone);
                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            int flag=0;
                            if(task.isSuccessful()){
                                DocumentSnapshot documentSnapshot=task.getResult();     //check if the already a worker
                                services=new HashMap<>();
                                if(documentSnapshot.exists()){
                                    flag=1;
                                    services= (HashMap)documentSnapshot.get("services");
                                    if(services.containsKey(service)){
                                        Toast.makeText(getContext(),"You are already providing this services",Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                }
                                Map<String,Object> info=new HashMap<>();
                                info.put("name",name);
                                info.put("city",city);
                                info.put("bio",bio);
                                Map<String,String> reviews=new HashMap<>();
                                services.put(service,String.valueOf(reviews.keySet().size()));


                                Map<String,Object> serviceInfo=new HashMap<>();
                                serviceInfo.put("charges",charges+"/hr");
                                serviceInfo.put("reviews",String.valueOf(reviews.keySet().size()));
                                serviceInfo.put("rating","NaN");
                                serviceInfo.put("review_list",reviews);
                                info.put(service,serviceInfo);
                                info.put("services",services);
                                info.put("lat",new Double(lat).toString());
                                info.put("lng",new Double(lng).toString());
                                //Toast.makeText(getContext(),"Worker is added...Phone:"+phone,Toast.LENGTH_SHORT).show();
                                if(flag==0)
                                docRef.set(info);
                                else if(flag==1)
                                    docRef.update(info);
                            }
                            else{
                                Toast.makeText(getContext(),"Task Failed!",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        getActivity().getSupportFragmentManager().popBackStackImmediate();

    }


}