package com.example.servicespk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firestore.v1.WriteResult;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CurrentUserServiceDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView listView;
    private Intent intent;
    private TextView serviceNameTV;
    private TextView chargesTV;
    private TextView numberOfReviewsTV;
    private Button editButton;
    private String phone,
                    serviceName;
    private ImageView deleteIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.current_user_services_detail);
        initializeComponents();
    }

    private void initializeComponents(){
        intent=getIntent();
        serviceName=intent.getStringExtra("serviceName");
        phone=intent.getStringExtra("phone");
        HashMap<String,Object> serviceInfo=(HashMap)intent.getSerializableExtra(serviceName);
        HashMap<String,String> reviewList=(HashMap)serviceInfo.get("review_list");
        Object[] obj1=reviewList.keySet().toArray();
        Object[] obj2=reviewList.values().toArray();
        String[] reviews = Arrays.copyOf(obj1, obj1.length, String[].class);
        String[] users = Arrays.copyOf(obj2, obj2.length, String[].class);
        ReviewListAdapter reviewListAdapter=new ReviewListAdapter(this,reviews,users);
        serviceNameTV=findViewById(R.id.current_user_service_detail_serviceName);
        chargesTV=findViewById(R.id.current_user_service_detail_chargesTV);
        numberOfReviewsTV=findViewById(R.id.current_user_service_detail_numberOfReviewsTV);

        String charges=String.valueOf(serviceInfo.get("charges"));

        chargesTV.setText(charges);
        numberOfReviewsTV.setText(String.valueOf(reviews.length));
        serviceNameTV.setText(serviceName);
        listView=(ListView)findViewById(R.id.current_user_service_detail_review_list);
        listView.setAdapter(reviewListAdapter);
    }

    @Override
    public void onClick(View view) {

    }

    private void deleteServiceInDB() {
        FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
        final DocumentReference docRef=firebaseFirestore.document("workers/"+phone);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot doc=task.getResult();
                    HashMap<String,Object> services=(HashMap) doc.get("services");
                    services.remove(serviceName);
                    info=new HashMap<>();
                    info.put("services",services);
                }
            }
        });
        Map<String, Object> updates = new HashMap<>();
        updates.put(serviceName, FieldValue.delete());
        docRef.update(updates);
        docRef.set(info);

    }

    HashMap<String,Object> info;
    private void changeDataInDB() {
        FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
        final DocumentReference documentReference=firebaseFirestore.document("workers/"+phone);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot doc=task.getResult();
                    HashMap<String,Object> service=(HashMap) doc.get(serviceName);
                    //service.put("charges",changedCharges);
                    info=new HashMap<>();
                    info.put(serviceName,service);
                }
            }
        });
        documentReference.set(info);

    }
}