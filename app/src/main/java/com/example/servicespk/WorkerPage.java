package com.example.servicespk;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class WorkerPage extends AppCompatActivity {

    private ListView listView;
    private TextView nameTV,
                    distanceTV,
                    numberOfServicesTV,
                    cityTV,
                    bioTV;
    private Button callButton;
    private double userLat,
                    userLng,
                    currLat,
                    currLng,
                    distance;
    private Location userLoc,
                    currLoc;
    private String phone;
    private Intent intent;

    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 101;

    @Override
    public void onStart() {
        super.onStart();
        setUpLocationListener();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_page);
        initViews();
    }



    private void initViews(){
        intent=getIntent();
        phone =intent.getStringExtra("phone");
        listView=(ListView)findViewById(R.id.worker_page_service_list);
        //initialize listView
        final Map<String,String> servicesMap=(HashMap)intent.getSerializableExtra("services");
        Object[] obj1=servicesMap.keySet().toArray();
        Object[] obj2=servicesMap.values().toArray();
        String[] services = Arrays.copyOf(obj1, obj1.length, String[].class);
        String[] rating = Arrays.copyOf(obj2, obj2.length, String[].class);

        listView.setAdapter(new ServiceListAdapter(this,services,rating));

        //setting on click listener on list view
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem=String.valueOf(adapterView.getItemAtPosition(i));
                HashMap<String,Object> individualServiceInfo=(HashMap)intent.getSerializableExtra(selectedItem);
                Intent intent =new Intent(getApplicationContext(),ServiceDetailActivity.class);
                Intent args=getIntent();
                intent.putExtra("serviceName",selectedItem);
                intent.putExtra("phone",phone);
                intent.putExtra("name",args.getStringExtra("currentUser"));
                intent.putExtra(selectedItem,individualServiceInfo);
                intent.putExtra("services",(HashMap)servicesMap);

                startActivityForResult(intent,44);
            }
        });



        //initialize textViews
        nameTV=findViewById(R.id.worker_page_nameTV);
        //distanceTV=findViewById(R.id.worker_page_distanceTV);
        numberOfServicesTV=findViewById(R.id.worker_page_numberOfServices);
        cityTV=findViewById(R.id.worker_page_city_tv);
        bioTV=findViewById(R.id.worker_page_bio_tv);
        String name=intent.getStringExtra("name");
        String city=intent.getStringExtra("city");
        String bio=intent.getStringExtra("bio");
        String numberOfServices=String.valueOf(services.length);
        userLat=Double.parseDouble(intent.getStringExtra("lat"));
        userLng=Double.parseDouble(intent.getStringExtra("lng"));
        setUpLocationListener();
        userLoc=new Location("");
        userLoc.setLatitude(userLat);
        userLoc.setLongitude(userLng);
        currLoc=new Location("");
        currLoc.setLatitude(currLat);

        currLoc.setLongitude(currLng);
        distance=userLoc.distanceTo(currLoc);
       // distanceTV.setText(new Double(distance).toString());
        nameTV.setText(name);
        cityTV.setText(city);
        numberOfServicesTV.setText(numberOfServices);
        bioTV.setText(bio);


        //initialize and set on click listener on button
        callButton=findViewById(R.id.worker_page_callButton);
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+phone));
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            new GetDataFromFirestore(phone).readDataFromWorkersCollection(new FirestoreCallback() {
                @Override
                public void onCallback(final Bundle bundle) {
                    final Map<String,String> servicesMap=(HashMap)bundle.getSerializable("services");
                    Object[] obj1=servicesMap.keySet().toArray();
                    Object[] obj2=servicesMap.values().toArray();
                    String[] services = Arrays.copyOf(obj1, obj1.length, String[].class);
                    String[] rating = Arrays.copyOf(obj2, obj2.length, String[].class);
                    listView.setAdapter(new ServiceListAdapter(WorkerPage.this,services,rating));
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            String selectedItem=String.valueOf(adapterView.getItemAtPosition(i));
                            HashMap<String,Object> individualServiceInfo=(HashMap)bundle.getSerializable(selectedItem);
                            Intent intent =new Intent(getApplicationContext(),ServiceDetailActivity.class);
                            Intent args=getIntent();
                            intent.putExtra("serviceName",selectedItem);
                            intent.putExtra("phone",phone);
                            intent.putExtra("name",args.getStringExtra("currentUser"));
                            intent.putExtra(selectedItem,individualServiceInfo);
                            intent.putExtra("services",(HashMap)servicesMap);
                            startActivityForResult(intent,44);
                        }
                    });
                }
            });
    }

    @Override
    public void onBackPressed() {
        finish();

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
    //for requesting location
    @SuppressLint("MissingPermission")
    private void setUpLocationListener() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = new LocationRequest().setInterval(2000).setFastestInterval(2000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,
                new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        currLat=locationResult.getLastLocation().getLatitude();
                        currLng=locationResult.getLastLocation().getLongitude();
                    }
                }, Looper.myLooper());
    }
}