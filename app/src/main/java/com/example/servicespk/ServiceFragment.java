package com.example.servicespk;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ServiceFragment extends Fragment implements View.OnClickListener {
    private Button addServiceButton;
    private ListView listView;
    private Bundle  accArgs;
    static Bundle DBArgs;
    private String phone;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_service, container, false);
        accArgs = getArguments();
        phone=accArgs.getString("phone");
        listView = (ListView) view.findViewById(R.id.fragment_service_service_list);
        addItemClickListenerOnListView();
        addServiceButton = view.findViewById(R.id.fragment_service_add_service_button);
        addServiceButton.setOnClickListener(this);

        return view;
    }

    private void addItemClickListenerOnListView() {
        new GetDataFromFirestore(phone).readDataFromWorkersCollection(new FirestoreCallback() {
            @Override
            public void onCallback(Bundle bundle) {
                DBArgs=bundle;}
        });
                if(DBArgs==null){
                    Toast.makeText(getContext(),"You are offering no service",Toast.LENGTH_SHORT).show();
                    return;
                }
                HashMap<String, String> servicesMap = (HashMap) DBArgs.getSerializable("services");

                Object[] obj1 = servicesMap.keySet().toArray();
                Object[] obj2 = servicesMap.values().toArray();
                String[] services = Arrays.copyOf(obj1, obj1.length, String[].class);
                String[] rating = Arrays.copyOf(obj2, obj2.length, String[].class);
                listView.setAdapter(new ServiceListAdapter(getActivity(), services, rating));
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        String selectedItem = String.valueOf(adapterView.getItemAtPosition(i));
                        HashMap<String, Object> individualServiceInfo = (HashMap) DBArgs.getSerializable(selectedItem);
                        Intent intent = new Intent(getContext(), CurrentUserServiceDetailActivity.class);
                        intent.putExtra("phone", DBArgs.getString("phone"));
                        intent.putExtra("serviceName", selectedItem);
                        intent.putExtra(selectedItem, individualServiceInfo);
                        startActivity(intent);
                    }
                });



    }

    @Override
    public void onClick(View view) {
        if (view.equals(addServiceButton)) {
            ServiceInfoFragment serviceInfoFragment = new ServiceInfoFragment();
            serviceInfoFragment.setArguments(accArgs);
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, serviceInfoFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }


}