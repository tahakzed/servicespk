package com.example.servicespk;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.Arrays;

public class ReviewListAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final String[] reviews;
    private final String[] users;

    public ReviewListAdapter(Activity context,
                      String[] reviews, String[] users) {
        super(context, R.layout.list_element, reviews);
        this.context = context;
        this.reviews = reviews;
        this.users= users;

    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_element, null, true);
        TextView review = (TextView) rowView.findViewById(R.id.review);
        TextView user = (TextView) rowView.findViewById(R.id.user);
        review.setText(reviews[position]);
        user.setText(users[position]);
        return rowView;
    }
}
