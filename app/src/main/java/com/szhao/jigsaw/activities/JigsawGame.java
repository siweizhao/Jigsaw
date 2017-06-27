package com.szhao.jigsaw.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.szhao.jigsaw.puzzle.GameBoard;
import com.szhao.jigsaw.R;
import com.szhao.jigsaw.db.CompletedPuzzlesDatabaseHelper;

public class JigsawGame extends Activity {

    private RelativeLayout gameLayout;
    private RelativeLayout solutionLayout;
    private int displayWidth, displayHeight;
    private int totalTimeSec = 0;
    private CountDownTimer timer;
    private GameBoard gameBoard;
    private ImageView originalImage;
    private RelativeLayout gameMenuLayout;
    public boolean isMenuOpen = false;
    private int difficulty;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jigsaw_game);
        gameLayout = (RelativeLayout) findViewById(R.id.gameLayout);
        solutionLayout = (RelativeLayout) findViewById(R.id.solutionLayout);
        gameMenuLayout = (RelativeLayout)findViewById(R.id.gameMenuLayout);
        originalImage = (ImageView)findViewById(R.id.originalImage);

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

        Intent intent = getIntent();
        String puzzleUri = intent.getExtras().getString("puzzleUri");
        difficulty = intent.getExtras().getInt("difficulty");
        initGame(puzzleUri,difficulty);
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

    private void initGame(String puzzleUri, int difficulty){
        Bitmap puzzle = ImageLoader.getInstance().loadImageSync(puzzleUri);
        Bitmap scaledPicture  = Bitmap.createScaledBitmap(puzzle, displayWidth - 200, displayWidth - 200, true);
        originalImage.setImageBitmap(scaledPicture);
        gameBoard= new GameBoard(this, difficulty, scaledPicture);
        gameBoard.initGame();

    }

    public void openGameMenu(View view){
        isMenuOpen = true;
        stopTimer();
        gameMenuLayout.setVisibility(View.VISIBLE);
    }

    public void showOriginal(View view){
        originalImage.setVisibility(View.VISIBLE);
    }

    public void resetPuzzle(View view){
        recreate();
    }

    public void returnPuzzleMenu(View view){
        Intent intent = new Intent (this, PuzzlesMenu.class);
        startActivity(intent);
    }

    public void closeGameMenu(View view){
        isMenuOpen = false;
        gameMenuLayout.setVisibility(View.INVISIBLE);
        startTimer();
    }

    public void puzzleComplete(){
        try{
            CompletedPuzzlesDatabaseHelper puzzleDatabaseHelper = new CompletedPuzzlesDatabaseHelper(this);
            SQLiteDatabase db = puzzleDatabaseHelper.getWritableDatabase();
            CompletedPuzzlesDatabaseHelper.insertPuzzle(db, "caption", difficulty, totalTimeSec,System.currentTimeMillis(),
                    ((BitmapDrawable)originalImage.getDrawable()).getBitmap());
        } catch (SQLiteException e){
            Toast toast = Toast.makeText(this, "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
