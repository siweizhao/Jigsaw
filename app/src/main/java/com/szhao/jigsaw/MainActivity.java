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
    private RelativeLayout puzzleArea;
    private int displayWidth;
    private int displayHeight;
    public PuzzlePiece[][] solutionPieces;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRrootLayout = (ViewGroup) findViewById(R.id.root);
        puzzleArea = (RelativeLayout) findViewById(R.id.puzzleArea);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        displayWidth = size.x;
        displayHeight = size.y;
        difficulty = 3;
        initGame(3);
    }

    public void initGame(int difficulty){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap picture =  BitmapFactory.decodeResource(getResources(), R.drawable.tiger,options);

        Bitmap scaledPicture  = Bitmap.createScaledBitmap(picture, displayWidth - 100, displayWidth - 100, true);
        GameBoard board = new GameBoard(this, difficulty, scaledPicture, puzzleArea);
        board.initGame();
    }
}
