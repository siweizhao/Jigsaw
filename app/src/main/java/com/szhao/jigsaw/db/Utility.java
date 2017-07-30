package com.szhao.jigsaw.db;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Owner on 5/30/2017.
 */

public class Utility {

    public static final String IMAGE_FILENAME = "puzzle.png";
    public static final int IMAGE_DIMENSIONS = 500;
    public static final int TABLE_CUSTOM = 0;
    public static final int TABLE_COMPLETED = 1;
    public static final int DISPLAY_WIDTH_OFFSET = 200;
    public static final int DISPLAY_HEIGHT_OFFSET = 70;


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