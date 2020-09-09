package com.example.servicespk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    Intent intent;
    String name;
    String phone;
    View navLayout;
    TextView nameTV;
    TextView phoneTV;
    ImageView profileImgView;
    Bundle fragData;
    FirebaseFirestore firebaseFirestore;
    DocumentReference documentReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intent = getIntent();
        name = intent.getStringExtra("name");
        phone = intent.getStringExtra("phone");
        fragData = new Bundle();
        fillFragDataBundle();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout_main);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        showProfileData(navigationView.getHeaderView(0));
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        /*if(savedInstanceState==null){
        MapsFragment mapsFragment=new MapsFragment();
        mapsFragment.setArguments(fragData);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,mapsFragment)
                .addToBackStack(null)
                .commit();
        navigationView.setCheckedItem(R.id.nav_home);}*/
    }

    private void showProfileData(View view) {
        navLayout = view;
        nameTV = (TextView) navLayout.findViewById(R.id.nav_layout_name);
        phoneTV = (TextView) navLayout.findViewById(R.id.nav_layout_phone);
        profileImgView = navLayout.findViewById(R.id.nav_layout_profile_pic);

        nameTV.setText(name);
        phoneTV.setText(phone);
        profileImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //edit here
                CurrentUserProfileFragment currentUserProfileFragment = new CurrentUserProfileFragment();
                currentUserProfileFragment.setArguments(fragData);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, currentUserProfileFragment)
                        .addToBackStack(null)
                        .commit();
                drawer.closeDrawer(GravityCompat.START);

            }
        });
    }


    private void fillFragDataBundle() {
        new GetDataFromFirestore(phone).readDataFromUsersCollection(new FirestoreCallback() {
            @Override
            public void onCallback(Bundle bundle) {
                fragData.putString("name", name);
                fragData.putString("city", bundle.getString("city"));
                fragData.putString("phone", phone);
                fragData.putString("bio", bundle.getString("bio"));
            }
        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                MapsFragment mapsFragment = new MapsFragment();
                mapsFragment.setArguments(fragData);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mapsFragment)
                        .addToBackStack(null)
                        .commit();
                break;

            case R.id.nav_service:
                ServiceFragment serviceFragment = new ServiceFragment();
                serviceFragment.setArguments(fragData);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, serviceFragment)
                        .addToBackStack(null)
                        .commit();
                break;

            case R.id.nav_logout:
                deleteSharedPreferences("ServicePkPrefs");
                finish();
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        }
        super.onBackPressed();
    }

}

