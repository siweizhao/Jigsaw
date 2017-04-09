package com.szhao.jigsaw;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;

import java.util.Random;

/**
 * Created by Owner on 3/30/2017.
 */

public class PPFactory {

    Path puzzlePath;
    Bitmap originalImage;
    int numRows, numColumns;
    public PPFactory(Bitmap originalImage, int numRows, int numColumns){
        this.originalImage = originalImage;
        this.numRows = numRows;
        this.numColumns = numColumns;
    }

    public int[] getTemplate(){
        int[] template = new int[numRows * numColumns];
        Random random = new Random();



        return template;
    }



    public Bitmap[] getPuzzlePieces(){
        Bitmap[] puzzlePieces = new Bitmap[2];

        Bitmap firstPiece = Bitmap.createBitmap(originalImage);
        Canvas canvas = new Canvas(firstPiece);
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, firstPiece.getWidth(), firstPiece.getHeight());
        getPuzzlePath1(firstPiece.getWidth(), firstPiece.getHeight());
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(0x00FF00);
        canvas.drawPath(puzzlePath, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(firstPiece, rect, rect, paint);
        puzzlePieces[0] = firstPiece;
        puzzlePieces[1] = firstPiece;
        return puzzlePieces;
    }

    public void getPuzzlePath1(int width, int height){
        puzzlePath = new Path();
        puzzlePath.moveTo(width/2, height/2);
        puzzlePath.lineTo(width/4, height/2);
        puzzlePath.lineTo(width/4, height/4);
        puzzlePath.lineTo(width/2, height/4);
        puzzlePath.lineTo(width/2, height/2);
        //puzzlePath.addCircle(width/2, height/4, height/3, Path.Direction.CCW);
    }



    public Bitmap getPuzzleBitmap(Bitmap bitmap)
    {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        calculatePuzzlePath(bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawPath(puzzlePath, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    private void calculatePuzzlePath(int width, int height)
    {
        float radius = (height / 2) - 5;
        float smallRadius = radius / 3;
        radius -= smallRadius * 2;
        float centerX = width/2;
        float centerY = height/2;
        puzzlePath = new Path();
        // Bottom right
        puzzlePath.moveTo(centerX + radius, centerY + radius);
        // Top right
        puzzlePath.lineTo(centerX + radius, centerY - radius);
        // Center top
        puzzlePath.lineTo(centerX, centerY - radius);
        // Add outside circle to center top
        puzzlePath.addCircle(centerX, centerY - radius - ((radius / 3) / 2), radius / 3, Path.Direction.CCW);

        // Top left
        puzzlePath.lineTo(centerX - radius, centerY - radius);
        // Bottom left
        puzzlePath.lineTo(centerX - radius, centerY + radius);
        //Bottom right
        puzzlePath.lineTo(centerX + radius, centerY + radius);
    }
}
