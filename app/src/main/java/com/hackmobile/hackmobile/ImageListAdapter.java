package com.hackmobile.hackmobile;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.ViewHolder> {
    private String LOG_TAG = "ImageListAdapter";
    private ListActivity activity;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View mView;

        public ViewHolder(View v) {
            super(v);
            mView = v;
        }
    }

    // Provide a suitable constructor
    public ImageListAdapter(ListActivity activity) {
        this.activity = activity;
    }


    // Create new views (invoked by the layout manager)
    @Override
    public ImageListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        SharedPreferences prefs = this.activity.getPreferences(Context.MODE_PRIVATE);
        List<String> images = new ArrayList<>(Arrays.asList(prefs.getString(this.activity.FILES, "").split("\n")));

        // calculate size of dp margins in px
        Resources r = holder.mView.getResources();
        int smallMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, r.getDisplayMetrics());
        int bigMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, r.getDisplayMetrics());

        // increase top margin for first item in list
        if (position == 0) {
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)holder.mView.getLayoutParams();
            params.setMargins(bigMargin, bigMargin, bigMargin, smallMargin);
        }
        // increase bottom margin for last item in list
        else if (position == images.size()-1) {
            // increase bottom margin
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)holder.mView.getLayoutParams();
            params.setMargins(bigMargin, smallMargin, bigMargin, bigMargin);
        }
        // set default margins for all other list items
        else {
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)holder.mView.getLayoutParams();
            params.setMargins(bigMargin, smallMargin, bigMargin, smallMargin);
        }

        // get URI to image
        if (images.size() > 0) {
            final String imageUri = images.get(position);
            // update card with correct image
            Picasso.with(holder.mView.getContext()).load(imageUri).centerCrop().resize(400, 800).into((ImageView) holder.mView.findViewById(R.id.image));
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        SharedPreferences prefs = this.activity.getPreferences(Context.MODE_PRIVATE);
        String imageString = prefs.getString(this.activity.FILES, "");
        if(imageString.equals("")) {
            return 0;
        } else {
            List<String> images = new ArrayList<>(Arrays.asList(imageString.split("\n")));
            return images.size();
        }
    }
}