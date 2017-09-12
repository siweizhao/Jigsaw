package com.szhao.jigsaw.global;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;

/**
 * Created by Owner on 9/12/2017.
 */

public class DisplayDimensions {
    private static final DisplayDimensions ourInstance = new DisplayDimensions();
    private int width, height, contentRecyclerHeight;

    private DisplayDimensions() {
    }

    public static DisplayDimensions getInstance() {
        return ourInstance;
    }

    public void initDimensions(Context context) {
        Point size = new Point();
        ((Activity) context).getWindowManager().getDefaultDisplay().getSize(size);
        width = size.x;
        height = size.y;

        //Sometimes height and width are switched
        if (width < height) {
            int temp = height;
            height = width;
            width = temp;
        }
    }

    public void initContentRecyclerHeight(int height) {
        this.contentRecyclerHeight = height;
    }

    public int getContentRecyclerHeight() {
        return contentRecyclerHeight;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }


}
