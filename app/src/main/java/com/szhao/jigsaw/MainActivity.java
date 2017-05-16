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
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Random;

public class MainActivity extends Activity {

    private RelativeLayout gameLayout;
    private RelativeLayout solutionLayout;
    private int displayWidth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gameLayout = (RelativeLayout) findViewById(R.id.gameLayout);
        solutionLayout = (RelativeLayout) findViewById(R.id.solutionLayout);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        displayWidth = size.x;

        Intent intent = getIntent();
        int puzzleId = intent.getExtras().getInt("id");
        int difficulty = intent.getExtras().getInt("difficulty");
        initGame(puzzleId,difficulty);
    }

    public RelativeLayout getGameLayout(){
        return gameLayout;
    }

    public RelativeLayout getSolutionLayout(){
        return solutionLayout;
    }

    private void initGame(int puzzleId, int difficulty){
        ImageAdapter adapter = new ImageAdapter(this);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap picture =  BitmapFactory.decodeResource(getResources(), adapter.images[puzzleId],options);

        Bitmap scaledPicture  = Bitmap.createScaledBitmap(picture, displayWidth - 200, displayWidth - 200, true);
        GameBoard board = new GameBoard(this, difficulty, scaledPicture);
        board.initGame();
    }
}
