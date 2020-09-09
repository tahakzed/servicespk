package com.example.servicespk;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

public class MapsFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    private SupportMapFragment supportMapFragment;
    private LocationRequest locationRequest;
    private final int LOCATION_PERMISSION_REQUEST_CODE = 44;
    private FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
    private Marker loc0;
    private Bundle UserArgs;
    private String currentUserPhone;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();
        UserArgs =getArguments();
        currentUserPhone= UserArgs.getString("phone");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragView=inflater.inflate(R.layout.fragment_home,container,false);
        supportMapFragment=(SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);
        return fragView;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:
                if(grantResults.length!=0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED){
                        LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                        mMap.setMyLocationEnabled(true);}
                }
                else
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Location Permission Needed")
                            .setMessage("This app needs the Location permission, please accept to use location functionality")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //Prompt the user once explanation has been shown
                                    ActivityCompat.requestPermissions(getActivity(),
                                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                            LOCATION_PERMISSION_REQUEST_CODE );
                                }
                            })
                            .create()
                            .show();
        }
    }
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                new AlertDialog.Builder(getActivity())
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        LOCATION_PERMISSION_REQUEST_CODE );
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION_REQUEST_CODE );
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // addWorker();
        showWorkers(mMap);
        locationRequest=new LocationRequest();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                mMap.setMyLocationEnabled(true);

            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        }
        else {
            mMap.setMyLocationEnabled(true);
        }

    }
    String currentUserName;


    private void showWorkers(final GoogleMap maps){
        UserArgs =getArguments();
        currentUserPhone= UserArgs.getString("phone");
        currentUserName=UserArgs.getString("name");
        firebaseFirestore.collection("workers").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (final QueryDocumentSnapshot document : task.getResult()) {
                        if(currentUserPhone.equals(document.getId())){
                            continue;
                        }
                        final double lat=Double.parseDouble(document.getString("lat"));
                        final double lng=Double.parseDouble(document.getString("lng"));
                        loc0=maps.addMarker(new MarkerOptions()
                                .title(document.getId())
                                .snippet(document.getString("service"))
                                .position(new LatLng(lat,lng)));
                        maps.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(Marker marker) {
                                if(marker.equals(loc0)){
                                    //starting a new activity
                                    Intent startProfileAcc=new Intent(getContext(),WorkerPage.class);
                                    startProfileAcc.putExtra("name",document.getString("name"));
                                    HashMap<String,String> services=(HashMap)document.get("services");
                                    startProfileAcc.putExtra("services",services);
                                    Object[] individualServices=services.keySet().toArray();
                                    for(int i=0;i<individualServices.length;i++){
                                        startProfileAcc.putExtra(String.valueOf(individualServices[i]),(HashMap)document.get(String.valueOf(individualServices[i])));
                                    }
                                    startProfileAcc.putExtra("city",document.getString("city"));
                                    startProfileAcc.putExtra("phone",document.getId());
                                    startProfileAcc.putExtra("bio",document.getString("bio"));
                                    startProfileAcc.putExtra("lat",new Double(lat).toString());
                                    startProfileAcc.putExtra("lng",new Double(lng).toString());
                                    startProfileAcc.putExtra("currentUser", currentUserName);
                                    startActivity(startProfileAcc);
                                    return true;
                                }
                                return false;
                            }
                        });
                    }
                }
            }
        });
    }
}
