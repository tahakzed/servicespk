package com.example.servicespk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;

public class ServiceDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView listView;
    private Intent intent;
    private TextView serviceNameTV;
    private TextView chargesTV;
    private TextView numberOfReviewsTV;
    private Button addReviewButton;
    private EditText reviewEditText;
    private HashMap<String,Object> serviceInfo;
    private HashMap<String,String> reviewList,services;
    private String serviceName,phone,currentUser;
    private int numberOfReviews;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_service_detail);
        initializeComponents();
    }

    private void initializeComponents(){
        intent=getIntent();
        serviceName=intent.getStringExtra("serviceName");
        phone = intent.getStringExtra("phone");
        services=(HashMap)intent.getSerializableExtra("services");
        serviceInfo=(HashMap)intent.getSerializableExtra(serviceName);
        reviewList=(HashMap)serviceInfo.get("review_list");
        Object[] obj1=reviewList.keySet().toArray();
        Object[] obj2=reviewList.values().toArray();
        String[] reviews = Arrays.copyOf(obj1, obj1.length, String[].class);
        String[] users = Arrays.copyOf(obj2, obj2.length, String[].class);
        ReviewListAdapter reviewListAdapter=new ReviewListAdapter(this,reviews,users);
        serviceNameTV=findViewById(R.id.service_detail_serviceName);
        chargesTV=findViewById(R.id.service_detail_chargesTV);
        numberOfReviewsTV=findViewById(R.id.service_detail_numberOfReviewsTV);
        addReviewButton=findViewById(R.id.service_detail_add_review_button);
        reviewEditText=findViewById(R.id.service_detail_review_edit_text);
        addReviewButton.setOnClickListener(this);
        String charges=String.valueOf(serviceInfo.get("charges"));
        String numOfReviews=String.valueOf(reviews.length);
        chargesTV.setText(charges);
        numberOfReviewsTV.setText(numOfReviews);
        serviceNameTV.setText(serviceName);
        listView=(ListView)findViewById(R.id.service_detail_review_list);
        listView.setAdapter(reviewListAdapter);
    }

    @Override
    public void onClick(View view) {
        if(view.equals(addReviewButton)){
            String review=reviewEditText.getText().toString();
            addReviewToDB(review);
            refreshReviewList();

        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }



    HashMap<String,Object> info;
    private void addReviewToDB(String review) {
        currentUser=intent.getStringExtra("name");
        reviewList.put(currentUser,review);
        serviceInfo.put("review_list",reviewList);
        numberOfReviews=reviewList.keySet().size();
        serviceInfo.put("reviews",String.valueOf(reviewList.keySet().size()));
        services.put(serviceName,String.valueOf(reviewList.keySet().size()));
        info=new HashMap<>();
        info.put(serviceName,serviceInfo);
        info.put("services",services);
        FirebaseFirestore firestore=FirebaseFirestore.getInstance();
         DocumentReference documentReference=firestore.document("workers/"+phone);
        documentReference.update(info);
    }
    private void refreshReviewList() {
        Object[] obj1=reviewList.keySet().toArray();
        Object[] obj2=reviewList.values().toArray();
        String[] reviews = Arrays.copyOf(obj1, obj1.length, String[].class);
        String[] users = Arrays.copyOf(obj2, obj2.length, String[].class);
        ReviewListAdapter reviewListAdapter=new ReviewListAdapter(this,reviews,users);
        reviewEditText.setText("");
        numberOfReviewsTV.setText(String.valueOf(numberOfReviews));
        listView=(ListView)findViewById(R.id.service_detail_review_list);
        listView.setAdapter(reviewListAdapter);
    }

}