package com.szhao.jigsaw;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;

import android.app.Activity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import java.util.Random;

public class MainActivity extends Activity {

    private ViewGroup mRrootLayout;
    private int displayWidth;
    private int displayHeight;
    public PuzzlePiece[] solutionPieces;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRrootLayout = (ViewGroup) findViewById(R.id.root);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        displayWidth = size.x;
        displayHeight = size.y;

        createPieces(3,3);
    }

    public void createPieces(int row, int column){
        GridLayout completedPuzzle = (GridLayout)findViewById(R.id.completedPuzzle);
        completedPuzzle.setColumnCount(column);
        completedPuzzle.setRowCount(row);

        solutionPieces = new PuzzlePiece[row * column];
        Random random = new Random();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap picture =  BitmapFactory.decodeResource(getResources(), R.drawable.tiger,options);
        int adjustedWidth, adjustedHeight;

        //Rescale picture if too large, will change later
        if (picture.getWidth() > displayWidth - 100)
            adjustedWidth = displayWidth - 100;
        else
            adjustedWidth = picture.getWidth();

        if (picture.getHeight() > displayHeight/2 - 100)
            adjustedHeight = displayHeight/2 - 100;
        else
            adjustedHeight = picture.getHeight();


        Bitmap scaledBitmap = Bitmap.createScaledBitmap(picture, adjustedWidth, adjustedHeight, true);
        PPFactory factory = new PPFactory(scaledBitmap, row, column);

        Bitmap[] pieces = new Bitmap[row * column];
        int pieceWidth = adjustedWidth/row;
        int pieceHeight = adjustedHeight/column;
        Bitmap[] testPieces = factory.getPuzzlePieces();
        ImageView img = new ImageView(this);
        img.setImageBitmap(testPieces[0]);
        completedPuzzle.addView(img);
        //ImageView img2 = new ImageView(this);
        //img2.setImageBitmap(testPieces[1]);
        //completedPuzzle.addView(img2);
        /*
        Log.d("pic stats", "x: " + adjustedWidth + " y:"+ adjustedHeight);
        int counter = 0;
        for (int y = 0; y < adjustedHeight; y += pieceHeight) {
            for (int x = 0;  x < adjustedWidth; x += pieceWidth){
                if (x + pieceWidth > adjustedWidth || y + pieceHeight > adjustedHeight) break;

                //pieces[counter++] = Bitmap.createBitmap(scaledBitmap, x, y, pieceWidth, pieceHeight);
            }
        }*/
        /*
        for (int i = 0; i < row * column; i++) {
            PuzzlePiece solution = new PuzzlePiece(this, i);
            solution.setImageBitmap(pieces[i]);
            solution.setLayoutParams(new LayoutParams(pieceWidth, pieceHeight));
            solutionPieces[i] = solution;
            completedPuzzle.addView(solution);
            solution.setVisibility(View.INVISIBLE);

            PuzzlePiece unsolved = new PuzzlePiece(this, i);
            unsolved.setImageBitmap(pieces[i]);
            RelativeLayout.LayoutParams params = new LayoutParams(pieceWidth,pieceHeight);
            params.leftMargin =
                    random.nextInt(displayWidth) * 7 / 8;
            params.topMargin =
                    random.nextInt(displayHeight) * 4 / 5;
            unsolved.setLayoutParams(params);
            unsolved.setOnTouchListener(unsolved);
            mRrootLayout.addView(unsolved);

        }*/
    }
}
