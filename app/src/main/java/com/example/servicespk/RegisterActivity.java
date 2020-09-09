package com.example.servicespk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText fullName,city,bio;
    private Button registerButton;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        fullName=findViewById(R.id.register_activity_name_field);
        city=findViewById(R.id.register_activity_city_field);
        bio=findViewById(R.id.register_activity_bio_field);
        registerButton=findViewById(R.id.register_activity_register_button);
        registerButton.setOnClickListener(this);
        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();


    }

    @Override
    public void onClick(View view) {
            final String fullNameStr=fullName.getText().toString().trim();
            String cityStr=city.getText().toString().trim();
            final String bioStr=bio.getText().toString().trim();
            final String phone = getIntent().getStringExtra("phone");
            Map<String,String> info=new HashMap<>();
            info.put("name",fullNameStr);
            info.put("city",cityStr);
            info.put("bio",bioStr);
            firebaseFirestore.document("users/"+phone).set(info).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                    intent.putExtra("name",fullNameStr);
                    intent.putExtra("phone",phone);
                    startActivity(intent);
                    SharedPreferences sharedPreferences=getSharedPreferences("ServicePkPrefs",MODE_PRIVATE);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putString("name",fullNameStr);
                    editor.putString("phone",phone);
                    editor.commit();
                    finish();
                }
            });

    }
}