package com.szhao.jigsaw.activities.jigsawgame;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.DragEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.szhao.jigsaw.R;
import com.szhao.jigsaw.activities.dashboard.DashboardActivity;
import com.szhao.jigsaw.activities.jigsawgame.adapter.BackgroundViewAdapter;
import com.szhao.jigsaw.activities.jigsawgame.adapter.PuzzlePieceRecyclerViewAdapter;
import com.szhao.jigsaw.activities.jigsawgame.jigsaw.Game;
import com.szhao.jigsaw.activities.jigsawgame.jigsaw.PuzzlePiece;
import com.szhao.jigsaw.db.PuzzleContentProvider;
import com.szhao.jigsaw.global.GameSettings;
import com.szhao.jigsaw.global.PointSystem;
import com.szhao.jigsaw.global.Utility;

import java.io.File;
import java.util.ArrayList;


public class JigsawGameActivity extends AppCompatActivity{

    private FrameLayout masterLayout;
    private RelativeLayout gameLayout;
    private int displayWidth, displayHeight;
    private int totalTimeSec = 0;
    private CountDownTimer timer;
    private Game game;
    private ImageView originalImage;
    private ImageButton showSidePiecesBtn;
    private int difficulty;
    private RecyclerView puzzlePieceRecycler;
    private GameSettings gameSettings;
    private String filePath;
    private String positions;
    private boolean isPuzzleComplete;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jigsaw_game);
        masterLayout = (FrameLayout)findViewById(R.id.masterLayout);
        gameLayout = (RelativeLayout) findViewById(R.id.gameLayout);
        originalImage = (ImageView)findViewById(R.id.originalImage);
        showSidePiecesBtn = (ImageButton)findViewById(R.id.showSidePiecesBtn);
        puzzlePieceRecycler = (RecyclerView)findViewById(R.id.puzzlePieceRecycler);
        puzzlePieceRecycler.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                switch(event.getAction()){
                    case DragEvent.ACTION_DROP:
                        View underView = puzzlePieceRecycler.findChildViewUnder(event.getX(), event.getY());
                        int position = puzzlePieceRecycler.getChildAdapterPosition(underView) < 0 ? 0 : puzzlePieceRecycler.getChildAdapterPosition(underView);
                        ((PuzzlePieceRecyclerViewAdapter)puzzlePieceRecycler.getAdapter()).insertPuzzlePiece(position);
                        break;
                }
                return true;
            }
        });

        initDisplayMetrics();
        adjustLayoutParams(gameLayout);

        Intent intent = getIntent();
        difficulty = intent.getExtras().getInt("difficulty");
        filePath = intent.getExtras().getString("filePath");
        positions = intent.getExtras().getString("positions");
        totalTimeSec = intent.getExtras().getInt("currTime");
        gameSettings = new GameSettings(this);

        loadSharedPreferences();
        startTimer();
        initGame(this);
    }

    private void initDisplayMetrics(){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        displayWidth = size.x;
        displayHeight = size.y;
    }

    private void loadSharedPreferences(){
        loadBackgroundImage(getSavedBackgroundImage());
    }

    private void adjustLayoutParams(View view){
        android.view.ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width = displayWidth - Utility.DISPLAY_WIDTH_OFFSET;
        params.height = displayHeight - Utility.DISPLAY_HEIGHT_OFFSET;
        view.setLayoutParams(params);
    }

    public RecyclerView getPuzzlePieceRecycler(){
        return puzzlePieceRecycler;
    }

    public FrameLayout getMasterLayout(){
        return masterLayout;
    }

    public RelativeLayout getGameLayout(){
        return gameLayout;
    }

    public void startTimer(){
        final TextView timerDisplay = (TextView)findViewById(R.id.timerDisplay);
        timer = new CountDownTimer(Long.MAX_VALUE,1000) {
            @Override
            public void onTick(long l) {
                long numSeconds = totalTimeSec % 60;
                long numMinutes= totalTimeSec / 60;
                totalTimeSec++;
                timerDisplay.setText(numMinutes + " : " + numSeconds);
            }

            @Override
            public void onFinish() {
            }
        };
        timer.start();
    }

    public void stopTimer(){
        timer.cancel();
    }

    private void initGame(final JigsawGameActivity jigsawGameActivity){
        Glide.with(this)
            .load(new File(filePath))
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .override(displayWidth - Utility.DISPLAY_WIDTH_OFFSET, displayHeight - Utility.DISPLAY_HEIGHT_OFFSET)
            .centerCrop()
            .into(new GlideDrawableImageViewTarget(originalImage) {
                @Override public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                    super.onResourceReady(resource, null); // ignores animation, but handles GIFs properly.
                    game = new Game(jigsawGameActivity, difficulty, difficulty);
                    game.initGame();
                    initCondensedImage();
                    if (positions != null){
                        game.loadSavedPositions(positions);
                    }
                }
            });
    }

    public void initCondensedImage(){
        ImageView condensedImage = (ImageView)findViewById(R.id.condensedImage);
        Glide.with(this)
                .load(new File(filePath ))
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .override((displayWidth - Utility.DISPLAY_WIDTH_OFFSET)/2, (displayHeight - Utility.DISPLAY_HEIGHT_OFFSET)/2)
                .centerCrop()
                .into(condensedImage);
    }

    public void puzzleComplete(){
        isPuzzleComplete = true;
        originalImage.setAlpha(1.0f);
        originalImage.setVisibility(View.VISIBLE);
        PointSystem.getInstance().addPoints(this, difficulty * 10);
        Toast.makeText(this, "Puzzle Completed, " + difficulty * 10 + " points have been added", Toast.LENGTH_LONG).show();
        stopTimer();
        ContentValues contentValues = new ContentValues();
        contentValues.put("DIFFICULTY", difficulty);
        contentValues.put("SOLVETIME", totalTimeSec);
        contentValues.put("PUZZLE", filePath);

        String whereClause = "PUZZLE = ? AND DIFFICULTY = ?";
        String[] args = new String[]{filePath, String.valueOf(difficulty)};
        //Remove puzzle from Started
        getContentResolver().delete(PuzzleContentProvider.CONTENT_URI_STARTED, whereClause, args);

        Cursor cursor = getContentResolver().query(PuzzleContentProvider.CONTENT_URI_COMPLETED, null, whereClause, args, null);
        if (cursor != null && cursor.moveToNext()) {
            int lastSolveTime = cursor.getInt(cursor.getColumnIndex("SOLVETIME"));
            //Faster solve time so update db
            if (lastSolveTime > totalTimeSec){
                getContentResolver().update(PuzzleContentProvider.CONTENT_URI_COMPLETED, contentValues, whereClause, args);
            }
        } else {
            getContentResolver().insert(PuzzleContentProvider.CONTENT_URI_COMPLETED, contentValues);
        }
        cursor.close();
    }

    public Bitmap getPuzzleImage(){
        return ((GlideBitmapDrawable)originalImage.getDrawable()).getBitmap();
    }

    @Override
    public void onResume(){
        super.onResume();
        gameSettings.startBGM();
        Utility.startImmersiveMode(this);
    }

    public void showSidePieces(View view) {
        PuzzlePieceRecyclerViewAdapter viewAdapter = (PuzzlePieceRecyclerViewAdapter) puzzlePieceRecycler.getAdapter();
        viewAdapter.showSidePieces();
        if (showSidePiecesBtn.getTag().equals("false")) {
            showSidePiecesBtn.setImageResource(R.drawable.ic_grid_on_black_24dp);
            showSidePiecesBtn.setTag("true");
        }else {
            showSidePiecesBtn.setImageResource(R.drawable.ic_grid_off_black_24dp);
            showSidePiecesBtn.setTag("false");
        }
    }

    public void showSolution(View view){
        if (originalImage.getVisibility() == View.INVISIBLE)
            originalImage.setVisibility(View.VISIBLE);
        else
            originalImage.setVisibility(View.INVISIBLE);
    }

    public void showCondensedImage(View view){
        FrameLayout wrapper = (FrameLayout)findViewById(R.id.condensedImageWrapper);
        wrapper.setVisibility(View.VISIBLE);
    }

    public void hideCondensedImage(View view){
        FrameLayout wrapper = (FrameLayout)findViewById(R.id.condensedImageWrapper);
        wrapper.setVisibility(View.INVISIBLE);
    }

    public void resetGame(View view){
        new MaterialDialog.Builder(this)
                .content("Are you sure you want to reset?")
                .contentGravity(GravityEnum.CENTER)
                .positiveText("Yes")
                .negativeText("No")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        recreate();
                    }
                })
                .dismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        Utility.startImmersiveMode(JigsawGameActivity.this);
                    }
                })
                .show();
    }

    public void changeBackGround(View view){
        MaterialDialog materialDialog = new MaterialDialog.Builder(this)
                .customView(R.layout.dialog_changebackground, false)
                .titleGravity(GravityEnum.CENTER)
                .title("Background")
                .dismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        Utility.startImmersiveMode(JigsawGameActivity.this);
                    }
                })
                .build();

        View customView = materialDialog.getCustomView();
        RecyclerView backgroundRecycler = (RecyclerView)customView.findViewById(R.id.backgroundRecycler);
        backgroundRecycler.setLayoutManager(new GridLayoutManager(this, 4));
        BackgroundViewAdapter bgViewAdapter = new BackgroundViewAdapter(this);
        bgViewAdapter.setListener(new BackgroundViewAdapter.BackgroundSelectListener() {
              @Override
              public void onClick(int bgId) {
                  loadBackgroundImage(bgId);
              }
        });
        backgroundRecycler.setAdapter(bgViewAdapter);
        materialDialog.show();
    }

    private int getSavedBackgroundImage(){
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        int defaultBackground = Utility.DEFAULT_BACKGROUND;
        return sharedPref.getInt(getString(R.string.background), defaultBackground);
    }

    private void loadBackgroundImage(int bgId){
        Utility.setSharedPrefValues(this, getString(R.string.background), bgId);

        //Display bg
        Glide.with(getApplicationContext())
                .load(bgId).asBitmap()
                .into(new SimpleTarget<Bitmap>(displayWidth, displayHeight) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        Drawable drawable = new BitmapDrawable(resource);
                        LinearLayout bg = (LinearLayout)findViewById(R.id.background);
                        bg.setBackground(drawable);
                    }
                });
    }

    public void openSettings(View view) {
        gameSettings.show();
    }

    public void goNavigationActivity(View view){
        new MaterialDialog.Builder(this)
                .content("Are you sure you want to go back to dashboard?")
                .contentGravity(GravityEnum.CENTER)
                .positiveText("Yes")
                .negativeText("No")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        saveCurrentProgress();
                        Intent intent = new Intent(getBaseContext(), DashboardActivity.class);
                        startActivity(intent);
                    }
                })
                .dismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        Utility.startImmersiveMode(JigsawGameActivity.this);
                    }
                })
                .show();
    }

    public void saveCurrentProgress(){
        ArrayList<PuzzlePiece> allPieces = game.getPlacedPieces();
        //If no pieces placed, dont save
        if (allPieces.size() == 0 || isPuzzleComplete)
            return;
        StringBuilder puzzlepiecePositions = new StringBuilder();
        for (PuzzlePiece p : allPieces){
            puzzlepiecePositions.append(p.getCurrentPos().x + "." + p.getCurrentPos().y
                    + ":" + p.getCorrectPos().x + "." + p.getCorrectPos().y + ",");
        }
        //Get rid of last comma
        if (puzzlepiecePositions.length() > 0)
            puzzlepiecePositions.deleteCharAt(puzzlepiecePositions.length() - 1);

        ContentValues contentValues = new ContentValues();
        contentValues.put("DIFFICULTY", difficulty);
        contentValues.put("SOLVETIME", totalTimeSec);
        contentValues.put("PUZZLE", filePath);
        contentValues.put("POSITIONS", puzzlepiecePositions.toString());

        String whereClause = "PUZZLE = ? AND DIFFICULTY = ?";
        String[] args = new String[]{filePath, String.valueOf(difficulty)};
        Cursor cursor = getContentResolver().query(PuzzleContentProvider.CONTENT_URI_STARTED, null, whereClause, args, null);
        if (cursor.moveToNext()) {
            getContentResolver().update(PuzzleContentProvider.CONTENT_URI_STARTED, contentValues, whereClause, args);
        } else {
            getContentResolver().insert(PuzzleContentProvider.CONTENT_URI_STARTED, contentValues);
        }
        cursor.close();
    }

    @Override
    public void onPause() {
        super.onPause();
        gameSettings.stopBGM();
    }
}
