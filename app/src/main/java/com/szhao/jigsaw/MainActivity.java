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

    int difficulty;
    private ViewGroup mRrootLayout;
    private ViewGroup puzzleArea;
    private int displayWidth;
    private int displayHeight;
    public PuzzlePiece[][] solutionPieces;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRrootLayout = (ViewGroup) findViewById(R.id.root);
        puzzleArea = (ViewGroup) findViewById(R.id.puzzleArea);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        displayWidth = size.x;
        displayHeight = size.y;
        difficulty = 3;
        initGame(3);
    }

    public void initGame(int difficulty){
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

        if (adjustedHeight < adjustedWidth){
            adjustedWidth = adjustedHeight;
        } else {
            adjustedHeight = adjustedWidth;
        }
        Bitmap scaledPicture  = Bitmap.createScaledBitmap(picture, adjustedWidth, adjustedHeight, true);
        GameBoard board = new GameBoard(this, difficulty, scaledPicture, puzzleArea);
        board.initGame();
    }
}
