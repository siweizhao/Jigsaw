package com.szhao.jigsaw.activities.jigsawgame;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
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
import com.szhao.jigsaw.global.Constants;
import com.szhao.jigsaw.global.DisplayDimensions;
import com.szhao.jigsaw.global.PointSystem;
import com.szhao.jigsaw.global.SoundSettings;
import com.szhao.jigsaw.global.Utility;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;


public class JigsawGameActivity extends AppCompatActivity{

    private FrameLayout masterLayout;
    private RelativeLayout gameLayout;
    private int totalTimeSec = 0;
    private CountDownTimer timer;
    private Game game;
    private ImageView originalImage;
    private ImageButton showSidePiecesBtn;
    private int difficulty, puzzleWidth, puzzleHeight;
    private RecyclerView puzzlePieceRecycler;
    private SoundSettings soundSettings;
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
                    default:
                        break;
                }
                return true;
            }
        });

        setPuzzleImageDim();
        Intent intent = getIntent();
        difficulty = intent.getExtras().getInt(Constants.INTENT_DIFFICULTY);
        filePath = intent.getExtras().getString(Constants.INTENT_FILE_PATH);
        positions = intent.getExtras().getString(Constants.INTENT_POSITIONS);
        totalTimeSec = intent.getExtras().getInt(Constants.INTENT_CURR_TIME);
        soundSettings = new SoundSettings(this);

        loadBackgroundImage(getSavedBackgroundImage());
        startTimer();
        initGame(this);
    }

    private void setPuzzleImageDim() {

        ViewGroup.LayoutParams recyclerParams = puzzlePieceRecycler.getLayoutParams();
        recyclerParams.height = DisplayDimensions.getInstance().getHeight() / 6;
        puzzlePieceRecycler.setLayoutParams(recyclerParams);

        int marginHeight = 50;
        puzzleHeight = DisplayDimensions.getInstance().getHeight() * 5 / 6 - marginHeight;
        puzzleWidth = Math.round(puzzleHeight * Constants.GOLDEN_RATIO);

        //Prevent puzzle from overlapping with buttons on the side
        int sideButtonsSpace = 120;
        if (puzzleWidth > DisplayDimensions.getInstance().getWidth() - sideButtonsSpace) {
            puzzleWidth = DisplayDimensions.getInstance().getWidth() - sideButtonsSpace;
            puzzleHeight = Math.round(puzzleWidth / Constants.GOLDEN_RATIO);
        }

        android.view.ViewGroup.LayoutParams params = gameLayout.getLayoutParams();
        params.width = puzzleWidth;
        params.height = puzzleHeight;
        gameLayout.setLayoutParams(params);
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
        timer = new CountDownTimer(Long.MAX_VALUE,1000) {
            @Override
            public void onTick(long l) {
                totalTimeSec++;
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
                .override(puzzleWidth, puzzleHeight)
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
                .override(puzzleWidth / 2, puzzleHeight / 2)
                .centerCrop()
                .into(condensedImage);
    }

    public void puzzleComplete(){
        isPuzzleComplete = true;
        soundSettings.playCheer();
        originalImage.setAlpha(1.0f);
        originalImage.setVisibility(View.VISIBLE);
        PointSystem.getInstance().addPoints(this, difficulty * 10);

        //Make other buttons invisible
        findViewById(R.id.goMenuBtn).setVisibility(View.INVISIBLE);
        findViewById(R.id.puzzlePieceRecycler).setVisibility(View.INVISIBLE);
        findViewById(R.id.rightSideBtnWrapper).setVisibility(View.INVISIBLE);
        findViewById(R.id.leftSideBtnWrapper).setVisibility(View.INVISIBLE);
        findViewById(R.id.finishBtn).setVisibility(View.VISIBLE);

        //Updating db
        stopTimer();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constants.DB_DIFFICULTY, difficulty);
        contentValues.put(Constants.DB_SOLVETIME, totalTimeSec);
        contentValues.put(Constants.DB_PUZZLE, filePath);

        String whereClause = "PUZZLE = ? AND DIFFICULTY = ?";
        String[] args = new String[]{filePath, String.valueOf(difficulty)};
        getContentResolver().delete(PuzzleContentProvider.CONTENT_URI_STARTED, whereClause, args);

        Cursor cursor = getContentResolver().query(PuzzleContentProvider.CONTENT_URI_COMPLETED, null, whereClause, args, null);
        if (cursor != null && cursor.moveToNext()) {
            //Faster solve time so update db
            if (cursor.getInt(cursor.getColumnIndex(Constants.DB_SOLVETIME)) > totalTimeSec)
                getContentResolver().update(PuzzleContentProvider.CONTENT_URI_COMPLETED, contentValues, whereClause, args);
            cursor.close();
        } else {
            getContentResolver().insert(PuzzleContentProvider.CONTENT_URI_COMPLETED, contentValues);
        }
    }

    public void showFinishDialog(View view) {
        MaterialDialog materialDialog = new MaterialDialog.Builder(this)
                .customView(R.layout.dialog_puzzlecomplete, false)
                .titleGravity(GravityEnum.CENTER)
                .title("Puzzle Complete")
                .dismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        Utility.startImmersiveMode(JigsawGameActivity.this);
                    }
                })
                .positiveText("Finish")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Intent intent = new Intent(getBaseContext(), DashboardActivity.class);
                        startActivity(intent);
                    }
                })
                .build();

        View customView = materialDialog.getCustomView();
        String difficultyStr = difficulty + " x " + difficulty;
        ((TextView) customView.findViewById(R.id.dialogPuzzleComplete_difficultyTxt)).setText(difficultyStr);
        ((TextView) customView.findViewById(R.id.dialogPuzzleComplete_TimeTxt)).setText(String.format(Locale.getDefault(), "%02d:%02d", totalTimeSec / 60, totalTimeSec % 60));
        ((TextView) customView.findViewById(R.id.dialogPuzzleComplete_RewardTxt)).setText(String.valueOf(difficulty * 10));
        materialDialog.show();
    }

    public Bitmap getPuzzleImage(){
        return ((GlideBitmapDrawable)originalImage.getDrawable()).getBitmap();
    }

    @Override
    public void onResume(){
        super.onResume();
        soundSettings.toggleBGM();
        Utility.startImmersiveMode(this);
    }

    public void showSidePieces(View view) {
        PuzzlePieceRecyclerViewAdapter viewAdapter = (PuzzlePieceRecyclerViewAdapter) puzzlePieceRecycler.getAdapter();
        viewAdapter.showSidePieces();
        if (showSidePiecesBtn.getTag().equals("false")) {
            showSidePiecesBtn.setImageResource(R.drawable.ic_jigsaw24dp);
            showSidePiecesBtn.setTag("true");
        }else {
            showSidePiecesBtn.setImageResource(R.drawable.ic_jigsaw_corner24dp);

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
                .title(getString(R.string.background))
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
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        int defaultBackground = Constants.DEFAULT_BACKGROUND;
        return sharedPref.getInt(Constants.SHARED_PREF_BG, defaultBackground);
    }

    private void loadBackgroundImage(int bgId){
        Utility.setSharedPrefValues(this, Constants.SHARED_PREF_BG, bgId);

        //Display bg
        Glide.with(getApplicationContext())
                .load(bgId).asBitmap()
                .into(new SimpleTarget<Bitmap>(DisplayDimensions.getInstance().getWidth(), DisplayDimensions.getInstance().getHeight()) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        Drawable drawable = new BitmapDrawable(resource);
                        LinearLayout bg = (LinearLayout)findViewById(R.id.background);
                        bg.setBackground(drawable);
                    }
                });
    }

    public void openSettings(View view) {
        soundSettings.show();
    }

    public void goNavigationActivity(View view){
        new MaterialDialog.Builder(this)
                .content(getString(R.string.back_to_menu))
                .contentGravity(GravityEnum.CENTER)
                .positiveText(getString(R.string.yes))
                .negativeText(getString(R.string.no))
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
        contentValues.put(Constants.DB_DIFFICULTY, difficulty);
        contentValues.put(Constants.DB_SOLVETIME, totalTimeSec);
        contentValues.put(Constants.DB_PUZZLE, filePath);
        contentValues.put(Constants.DB_POSITIONS, puzzlepiecePositions.toString());

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

    public void playClickSoundEff() {
        soundSettings.playClick();
    }

    @Override
    public void onPause() {
        super.onPause();
        soundSettings.toggleBGM();
    }
}
