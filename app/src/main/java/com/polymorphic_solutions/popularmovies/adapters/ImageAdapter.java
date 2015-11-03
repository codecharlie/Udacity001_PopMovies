package com.polymorphic_solutions.popularmovies.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/*
*
* Custom ImageAdapter used to used to create the Grid of movie posters
*
* */

public class ImageAdapter extends ArrayAdapter<String> {
    private final String LOG_TAG = ImageAdapter.class.getSimpleName();

    private LayoutInflater mLayoutInflater;
    private Context context;
    private int layoutId;
    private int imageViewID;

    public ImageAdapter(Context context, int layoutId, int imageViewID, ArrayList<String> urls) {
        super(context, 0, urls);
        this.mLayoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.layoutId = layoutId;
        this.imageViewID = imageViewID;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        String url;

        // Just checking to see if it needs to be initialized...
        if (view == null) view = mLayoutInflater.inflate(layoutId, parent, false);

        ImageView imageView = (ImageView) view.findViewById(imageViewID);
        url = getItem(position);
        Picasso.with(context).load(url).into(imageView);
        return view;
    }
}
