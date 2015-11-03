package com.polymorphic_solutions.popularmovies.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.polymorphic_solutions.popularmovies.R;
import com.polymorphic_solutions.popularmovies.db.Review;

import java.util.ArrayList;

/**
 * Created by cmorgan on 11/2/15.
 */
public class ReviewListAdapter extends ArrayAdapter<Review> {
    private static final String LOG_TAG = ReviewListAdapter.class.getSimpleName();
    
    private LayoutInflater mLayoutInflater;
    private int layoutResourceId;
    private ArrayList<Review> reviews;
    private Context context;

    public ReviewListAdapter(Context context, int layoutResourceId, ArrayList<Review> reviews) {
        super(context, layoutResourceId, reviews);
        this.mLayoutInflater = LayoutInflater.from(context);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.reviews = reviews;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if(view == null) view = mLayoutInflater.inflate(layoutResourceId, parent, false);

        Review review = reviews.get(position);
        TextView author = (TextView) view.findViewById(R.id.review_author);
        TextView content = (TextView) view.findViewById(R.id.review_content);
        author.setText(review.getAuthor());
        content.setText(review.getContent());

        return view;
    }
}
