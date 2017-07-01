package com.szhao.jigsaw.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.szhao.jigsaw.R;

/**
 * Created by Owner on 5/12/2017.
 */

public class ImageAdapter extends BaseAdapter {
    private Context context;

    private Integer[] images = {
            R.drawable.puzzle_1,
            R.drawable.puzzle_2,
            R.drawable.puzzle_3,
            R.drawable.puzzle_4
    };

    public ImageAdapter(Context context){
        this.context = context;
    }

    @Override
    public int getCount(){
        return images.length;
    }

    @Override
    public Object getItem(int position){
        return images[position];
    }

    @Override
    public long getItemId(int position){
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        ImageView imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        imageView.setLayoutParams(new GridView.LayoutParams(520,520));
        imageView.setImageResource(images[position]);
        return imageView;
    }

    public Integer getImage(int pos){
        return images[pos];
    }

}
