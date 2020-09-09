package com.example.servicespk;

import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class GetDataFromFirestore {
    Bundle getDataBundle=new Bundle();
    String phone;
    GetDataFromFirestore(String phone){
        this.phone=phone;
    }
     void readDataFromUsersCollection(final FirestoreCallback firestoreCallback){
        FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
        final DocumentReference documentReference=firebaseFirestore.document("users/"+phone);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document=task.getResult();
                    if(!document.exists()){
                        return;
                    }
                    getDataBundle.putString("name",document.getString("name"));
                    getDataBundle.putString("phone",document.getId());
                    getDataBundle.putString("bio",document.getString("bio"));
                    getDataBundle.putString("city",document.getString("city"));
                    firestoreCallback.onCallback(getDataBundle);
                }
            }
        });
    }

    void readDataFromWorkersCollection(final FirestoreCallback firestoreCallback){
        FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
        final DocumentReference documentReference=firebaseFirestore.document("workers/"+phone);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document=task.getResult();
                    if(!document.exists()){
                        return;
                    }
                    getDataBundle.putString("name",document.getString("name"));
                    getDataBundle.putString("phone",document.getId());
                    getDataBundle.putString("bio",document.getString("bio"));
                    getDataBundle.putString("city",document.getString("city"));
                    HashMap<String,String> services=(HashMap)document.get("services");
                    getDataBundle.putSerializable("services",services);
                    Object[] individualServices=services.keySet().toArray();
                    for(int i=0;i<individualServices.length;i++){
                        getDataBundle.putSerializable(String.valueOf(individualServices[i]),(HashMap)document.get(String.valueOf(individualServices[i])));
                    }
                    double lat=Double.parseDouble(document.getString("lat"));
                    double lng=Double.parseDouble(document.getString("lng"));
                    getDataBundle.putString("lat",new Double(lat).toString());
                    getDataBundle.putString("lng",new Double(lng).toString());
                    firestoreCallback.onCallback(getDataBundle);
                }
            }
        });
    }

}
