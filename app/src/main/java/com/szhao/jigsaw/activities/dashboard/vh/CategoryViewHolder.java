package com.szhao.jigsaw.activities.dashboard.vh;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.szhao.jigsaw.R;

import java.io.File;

/**
 * Created by Owner on 8/7/2017.
 */

public class CategoryViewHolder extends RecyclerView.ViewHolder {
    private ImageView categoryImage;
    private Context context;
    private TextView categoryCount;
    private TextView categoryDescription;

    public CategoryViewHolder(Context context, View view) {
        super(view);
        this.context = context;
        categoryImage = (ImageView)view.findViewById(R.id.category_image);
        categoryCount = (TextView)view.findViewById(R.id.category_image_count);
        categoryDescription = (TextView)view.findViewById(R.id.category_description);
    }

    public void setImage(String filePath){
        Glide.with(context)
                .load(new File(filePath))
                .override(380,240)
                .centerCrop()
                .into(categoryImage);
    }

    public void setCount(int curr, int total){
        categoryCount.setText(curr + "/" + total);
    }

    public void setDescription(String description){
        categoryDescription.setText(description);
    }

}
