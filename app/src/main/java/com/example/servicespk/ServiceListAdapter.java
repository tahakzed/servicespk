package com.example.servicespk;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class ServiceListAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final String[] service;
    private final String[] rating;

    public ServiceListAdapter(Activity context,
                             String[] service, String[] rating) {
        super(context, R.layout.list_element, service);
        this.context = context;
        this.service = service;
        this.rating = rating;

    }

    @Nullable
    @Override
    public String getItem(int position) {
        return service[position];
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View serviceView= inflater.inflate(R.layout.service_list_element, null, true);
        TextView serviceTV = (TextView) serviceView.findViewById(R.id.service_list_element_serviceName);
        TextView chargesTV = (TextView) serviceView.findViewById(R.id.service_list_element_serviceRating);
        ImageView serviceIcon=(ImageView) serviceView.findViewById(R.id.service_list_element_serviceIcon);
        if(service[position].equals("Computer Technician"))
            serviceIcon.setImageResource(R.drawable.ic_computer_tech);
        else if(service[position].equals("Plumber"))
            serviceIcon.setImageResource(R.drawable.ic_plumber);
        else if(service[position].equals("Mobile Technician"))
            serviceIcon.setImageResource(R.drawable.ic_mobile_technician);
        else if(service[position].equals("Electrician"))
            serviceIcon.setImageResource(R.drawable.ic_computer_tech);
        else
            serviceIcon.setImageResource(R.drawable.ic_rating_star);
        serviceTV.setText(service[position]);
        chargesTV.setText(rating[position]);
        return serviceView;
    }
}
