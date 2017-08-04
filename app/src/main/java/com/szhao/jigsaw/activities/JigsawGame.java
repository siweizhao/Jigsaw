package com.szhao.jigsaw.activities;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.DragEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

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
import com.bumptech.glide.util.Util;
import com.szhao.jigsaw.R;
import com.szhao.jigsaw.adapters.BackgroundViewAdapter;
import com.szhao.jigsaw.adapters.PuzzlePieceRecyclerViewAdapter;
import com.szhao.jigsaw.db.PuzzleContentProvider;
import com.szhao.jigsaw.db.Utility;
import com.szhao.jigsaw.fragments.GameMenuDialog;
import com.szhao.jigsaw.puzzle.Game;
import com.szhao.jigsaw.puzzle.GlobalGameData;


public class JigsawGame extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private FrameLayout masterLayout;
    private RelativeLayout gameLayout;
    private int displayWidth, displayHeight;
    private int totalTimeSec = 0;
    private CountDownTimer timer;
    private Game game;
    private ImageView originalImage;
    ImageButton showSidePiecesBtn;
    private int difficulty;
    private GameMenuDialog gameMenuDialog;
    RecyclerView puzzlePieceRecycler;
    MediaPlayer bgmPlayer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GlobalGameData.getInstance().setContext(this);
        setContentView(R.layout.activity_jigsaw_game);
        masterLayout = (FrameLayout)findViewById(R.id.masterLayout);
        gameLayout = (RelativeLayout) findViewById(R.id.gameLayout);
        originalImage = (ImageView)findViewById(R.id.originalImage);
        showSidePiecesBtn = (ImageButton)findViewById(R.id.showSidePiecesBtn);
        gameMenuDialog = new GameMenuDialog();
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
        getLoaderManager().initLoader(Utility.TABLE_COMPLETED, null, this);

        Intent intent = getIntent();
        difficulty = intent.getExtras().getInt("difficulty");

        loadSharedPreferences();
        startTimer();
        startBGM();
        initGame(this);
    }

    private void startBGM(){
        bgmPlayer = MediaPlayer.create(this, R.raw.bg_music);
        bgmPlayer.setLooping(true);
        bgmPlayer.start();
    }

    private void initDisplayMetrics(){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        displayWidth = size.x;
        displayHeight = size.y;
    }

    private void loadSharedPreferences(){
        GlobalGameData.getInstance().setSoundVolume(getSavedSoundVol());
        GlobalGameData.getInstance().setMusicVolume(getSavedMusicVol());
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

    private void initGame(final JigsawGame jigsawGame){
        Glide.with(this)
            .load(this.getFilesDir() + "/" + Utility.IMAGE_FILENAME)
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .override(displayWidth - Utility.DISPLAY_WIDTH_OFFSET, displayHeight - Utility.DISPLAY_HEIGHT_OFFSET)
            .centerCrop()
            .into(new GlideDrawableImageViewTarget(originalImage) {
                @Override public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                    super.onResourceReady(resource, null); // ignores animation, but handles GIFs properly.
                    game = new Game(jigsawGame, difficulty, difficulty);
                    game.initGame();
                    initCondensedImage();
                }
            });
    }

    public void initCondensedImage(){
        ImageView condensedImage = (ImageView)findViewById(R.id.condensedImage);
        Glide.with(this)
                .load(this.getFilesDir() + "/" + Utility.IMAGE_FILENAME)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .override((displayWidth - Utility.DISPLAY_WIDTH_OFFSET)/2, (displayHeight - Utility.DISPLAY_HEIGHT_OFFSET)/2)
                .centerCrop()
                .into(condensedImage);
    }

    public void openGameMenu(View view){
        stopTimer();
        gameMenuDialog.show(getSupportFragmentManager(),"Game Menu");
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

    @Override
    public void onResume(){
        super.onResume();
        startImmersiveMode();
    }

    public void startImmersiveMode(){
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
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
                        startImmersiveMode();
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
                        startImmersiveMode();
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
        int savedBackground = sharedPref.getInt(getString(R.string.background), defaultBackground);
        return savedBackground;
    }

    public int getSavedMusicVol(){
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        int defaultVolume = Utility.MAX_VOLUME;
        int savedVolume = sharedPref.getInt(getString(R.string.music_volume), defaultVolume);
        return savedVolume;
    }

    public int getSavedSoundVol(){
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        int defaultVolume = Utility.MAX_VOLUME;
        int savedVolume = sharedPref.getInt(getString(R.string.sound_volume), defaultVolume);
        return savedVolume;
    }

    public void setSharedPrefValues(String key, int value){
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(key,value);
        editor.commit();
    }

    private void loadBackgroundImage(int bgId){
        setSharedPrefValues(getString(R.string.background), bgId);

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

    public void openSettings(View view){
        MaterialDialog materialDialog = new MaterialDialog.Builder(this)
                .customView(R.layout.dialog_settings, false)
                .titleGravity(GravityEnum.CENTER)
                .title("Settings")
                .dismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        startImmersiveMode();
                    }
                })
                .build();
        View v = materialDialog.getCustomView();

        SeekBar soundSeekbar = (SeekBar)v.findViewById(R.id.soundSeekBar);
        soundSeekbar.setMax(Utility.MAX_VOLUME);
        soundSeekbar.setProgress(getSavedSoundVol());
        soundSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                GlobalGameData.getInstance().setSoundVolume(progress);
                setSharedPrefValues(getString(R.string.sound_volume), progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        SeekBar musicSeekbar = (SeekBar)v.findViewById(R.id.musicSeekBar);
        musicSeekbar.setMax(Utility.MAX_VOLUME);
        musicSeekbar.setProgress(getSavedMusicVol());
        GlobalGameData.getInstance().setMusicVolume(getSavedMusicVol());
        musicSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                GlobalGameData.getInstance().setMusicVolume(progress);
                setSharedPrefValues(getString(R.string.music_volume), progress);
                AudioManager audioManager = (AudioManager)GlobalGameData.getInstance().getContext().getSystemService(Context.AUDIO_SERVICE);
                int currentDeviceVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                int maxDeviceVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                int musicVolume = GlobalGameData.getInstance().getMusicVolume();

                float bgmVolume = ((float)currentDeviceVolume/maxDeviceVolume) * ((float)(1 - Math.log(Utility.MAX_VOLUME - musicVolume)/ Math.log(Utility.MAX_VOLUME)));
                Log.d("music eff", currentDeviceVolume + " " + maxDeviceVolume + " " + bgmVolume + " global " + GlobalGameData.getInstance().getMusicVolume());
                bgmPlayer.setVolume(bgmVolume, bgmVolume);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });



        materialDialog.show();
    }
}
