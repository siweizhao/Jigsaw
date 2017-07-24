package com.szhao.jigsaw.activities;

import android.app.FragmentManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.app.LoaderManager;
import android.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.szhao.jigsaw.R;
import com.szhao.jigsaw.db.PuzzleContentProvider;
import com.szhao.jigsaw.db.Utility;
import com.szhao.jigsaw.fragments.GameMenuDialog;
import com.szhao.jigsaw.puzzle.GameBoard;

public class JigsawGame extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private RelativeLayout gameLayout;
    private RelativeLayout solutionLayout;
    private int displayWidth, displayHeight;
    private int totalTimeSec = 0;
    private CountDownTimer timer;
    private GameBoard gameBoard;
    private ImageView originalImage;
    private int difficulty;
    private GameMenuDialog gameMenuDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jigsaw_game);
        gameLayout = (RelativeLayout) findViewById(R.id.gameLayout);
        solutionLayout = (RelativeLayout) findViewById(R.id.solutionLayout);
        originalImage = (ImageView)findViewById(R.id.originalImage);
        gameMenuDialog = new GameMenuDialog();

        originalImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                originalImage.setVisibility(View.INVISIBLE);
            }
        });

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        displayWidth = size.x;
        displayHeight = size.y;

        getLoaderManager().initLoader(Utility.TABLE_COMPLETED, null, this);

        Intent intent = getIntent();
        difficulty = intent.getExtras().getInt("difficulty");
        initGame(this);
        startTimer();
    }

    public int getDisplayWidth(){
        return displayWidth;
    }

    public int getDisplayHeight(){
        return displayHeight;
    }

    public RelativeLayout getGameLayout(){
        return gameLayout;
    }

    public RelativeLayout getSolutionLayout(){
        return solutionLayout;
    }

    public void startTimer(){
        final TextView timerDisplay = (TextView)findViewById(R.id.timerDisplay);
        timer = new CountDownTimer(Long.MAX_VALUE,1000) {
            @Override
            public void onTick(long l) {
                long numSeconds = totalTimeSec % 60;
                long numMinutes= totalTimeSec / 60;
                long numHours = numMinutes / 60;
                totalTimeSec++;
                timerDisplay.setText(numHours + " : " + numMinutes + " : " + numSeconds);
            }

            @Override
            public void onFinish() {
            }
        };
        timer.start();
    }

    public long getTotalTimeSec(){
        return totalTimeSec;
    }

    public void stopTimer(){
        timer.cancel();
    }

    private void initGame(final JigsawGame game){
        Glide.with(this)
                .load(this.getFilesDir() + "/" + Utility.IMAGE_FILENAME)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .override(displayWidth - 200, displayWidth - 200)
                .centerCrop()
                .into(new GlideDrawableImageViewTarget(originalImage) {
                    @Override public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                        super.onResourceReady(resource, null); // ignores animation, but handles GIFs properly.
                        gameBoard= new GameBoard(game, difficulty);
                        gameBoard.initGame();
                    }
                });

    }

    public void openGameMenu(View view){
        stopTimer();
        gameMenuDialog.show(getSupportFragmentManager(),"Game Menu");
    }

    public void showSolution(){
        originalImage.setVisibility(View.VISIBLE);
    }

    public void resetPuzzle(){
        recreate();
    }

    public void goPuzzleSelector(){
        Intent intent = new Intent (this, PuzzleSelector.class);
        startActivity(intent);
    }

    public void puzzleComplete(){
        ContentValues contentValues = new ContentValues();
        contentValues.put("DESCRIPTION", "caption");
        contentValues.put("DIFFICULTY", difficulty);
        contentValues.put("SOLVETIME", totalTimeSec);
        contentValues.put("DATE", System.currentTimeMillis());
        contentValues.put("PUZZLE", Utility.getBytes(((GlideBitmapDrawable)originalImage.getDrawable()).getBitmap()));
        getContentResolver().insert(PuzzleContentProvider.CONTENT_URI_COMPLETED, contentValues);
    }

    public Bitmap getPuzzleImage(){
        return ((GlideBitmapDrawable)originalImage.getDrawable()).getBitmap();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch(id){
            case Utility.TABLE_COMPLETED:
                return new CursorLoader(this, PuzzleContentProvider.CONTENT_URI_COMPLETED, null, null, null, null);
            default:
                throw new IllegalArgumentException("no id handled!");
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
