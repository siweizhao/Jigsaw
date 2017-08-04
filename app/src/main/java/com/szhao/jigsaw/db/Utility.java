package com.szhao.jigsaw.db;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.szhao.jigsaw.R;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Owner on 5/30/2017.
 */

public class Utility {

    public static final String IMAGE_FILENAME = "puzzle.png";
    public static final int DEFAULT_BACKGROUND = R.drawable.bg_1;
    public static final int MAX_VOLUME = 8;
    public static final int IMAGE_DIMENSIONS = 500;
    public static final int TABLE_CUSTOM = 0;
    public static final int TABLE_COMPLETED = 1;
    public static final int DISPLAY_WIDTH_OFFSET = 200;
    public static final int DISPLAY_HEIGHT_OFFSET = 250;
    public static int[] backgroundIds = new int[]{
            R.drawable.bg_1,
            R.drawable.bg_2,
            R.drawable.bg_3,
            R.drawable.bg_4,
            R.drawable.bg_5,
            R.drawable.bg_6,
            R.drawable.bg_7,
            R.drawable.bg_8,
            R.drawable.bg_9,
            R.drawable.bg_10,
            R.drawable.bg_11,
            R.drawable.bg_12,
            R.drawable.bg_13,
            R.drawable.bg_14,
            R.drawable.bg_15,
            R.drawable.bg_16,
            R.drawable.bg_17,
            R.drawable.bg_18,
            R.drawable.bg_19,
            R.drawable.bg_20,
            R.drawable.bg_21,
            R.drawable.bg_22,
            R.drawable.bg_23,
    };



    private Utility(){
        throw new UnsupportedOperationException("Instantiating a utility class");
    };

    // convert from bitmap to byte array
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    //Store image in internal storage to pass on to next activity
    public static void storeImage(Context context, Bitmap bitmap){
        try {
            //Write file
            FileOutputStream stream = context.openFileOutput(IMAGE_FILENAME, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

            //Cleanup
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}