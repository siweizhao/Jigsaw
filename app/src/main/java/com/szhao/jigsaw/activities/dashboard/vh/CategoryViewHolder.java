package com.szhao.jigsaw.activities.dashboard.vh;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.szhao.jigsaw.R;
import com.szhao.jigsaw.global.Constants;
import com.szhao.jigsaw.global.DisplayDimensions;

import java.io.File;

/**
 * Created by Owner on 8/7/2017.
 */

public class CategoryViewHolder extends RecyclerView.ViewHolder {
    private ImageView categoryImage;
    private Context context;
    private TextView categoryCount;
    private TextView categoryDescription;
    private int width, height;

    public CategoryViewHolder(Context context, View view) {
        super(view);
        this.context = context;
        categoryImage = (ImageView)view.findViewById(R.id.category_image);
        categoryCount = (TextView)view.findViewById(R.id.category_image_count);
        categoryDescription = (TextView)view.findViewById(R.id.category_description);
        int categoryRecyclerHeight = (int) (DisplayDimensions.getInstance().getHeight() * Constants.CATEGORY_RECYCLER_HEIGHT);
        height = categoryRecyclerHeight - Constants.CATEGORY_VH_MARGIN;
        width = Math.round(Constants.GOLDEN_RATIO * height);

    }

    public void setImage(String filePath){
        Glide.with(context)
                .load(new File(filePath))
                .override(width, height)
                .centerCrop()
                .into(categoryImage);
    }

    public void increaseCount() {
        int curr = Integer.parseInt((categoryCount.getText().toString()).split("/")[0]);
        int total = Integer.parseInt((categoryCount.getText().toString()).split("/")[1]);
        curr++;
        setCount(curr, total);
    }

    public void setCount(int curr, int total){
        categoryCount.setText(curr + "/" + total);
    }

    public void setDescription(String description){
        categoryDescription.setText(description);
    }

}
