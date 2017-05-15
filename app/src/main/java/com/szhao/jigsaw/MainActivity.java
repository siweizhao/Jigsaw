package com.szhao.jigsaw;

import android.content.Intent;
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

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Random;

public class MainActivity extends Activity {

    int difficulty;
    private ViewGroup mRrootLayout;
    private RelativeLayout puzzleArea;
    private int displayWidth;
    private int displayHeight;

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

        Intent intent = getIntent();
        int puzzleId = intent.getExtras().getInt("id");
        difficulty = intent.getExtras().getInt("difficulty");
        initGame(puzzleId,difficulty);
    }

    public void initGame(int puzzleId, int difficulty){
        ImageAdapter adapter = new ImageAdapter(this);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap picture =  BitmapFactory.decodeResource(getResources(), adapter.images[puzzleId],options);

        Bitmap scaledPicture  = Bitmap.createScaledBitmap(picture, displayWidth - 200, displayWidth - 200, true);
        GameBoard board = new GameBoard(this, difficulty, scaledPicture, puzzleArea);
        board.initGame();
    }
}
