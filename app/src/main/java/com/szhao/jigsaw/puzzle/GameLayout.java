package com.szhao.jigsaw.puzzle;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;
import android.util.Pair;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.szhao.jigsaw.R;
import com.szhao.jigsaw.db.Utility;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Owner on 7/28/2017.
 */

public class GameLayout {
    View layout;
    int rows, columns;
    Point[][] anchorPoints;
    ArrayList<PuzzlePiece> placedPuzzlePieces;
    MediaPlayer[] soundEffects;

    public GameLayout(View layout, int rows, int columns, Point[][] anchorPoints){
        this.layout = layout;
        this.rows = rows;
        this.columns = columns;
        this.anchorPoints = anchorPoints;
        placedPuzzlePieces = new ArrayList<>();
        initSnapToGrid();
        initSoundEffects();
    }

    public void setBoundary(FrameLayout parentLayout){
        parentLayout.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                switch (event.getAction()) {
                    case DragEvent.ACTION_DROP:
                        handleDropEvent(event);
                        break;
                }
                return true;
            }
        });
    }

    private void initSnapToGrid(){
        layout.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                switch (event.getAction()) {
                    case DragEvent.ACTION_DROP:
                        handleDropEvent(event);
                        break;
                }
                return true;
            }
        });
    }

    private void initSoundEffects(){
        soundEffects = new MediaPlayer[]{
                MediaPlayer.create(GlobalGameData.getInstance().getContext(), R.raw.pp_1),
                MediaPlayer.create(GlobalGameData.getInstance().getContext(), R.raw.pp_2),
                MediaPlayer.create(GlobalGameData.getInstance().getContext(), R.raw.pp_3),
                MediaPlayer.create(GlobalGameData.getInstance().getContext(), R.raw.pp_4),
                MediaPlayer.create(GlobalGameData.getInstance().getContext(), R.raw.pp_5),
                MediaPlayer.create(GlobalGameData.getInstance().getContext(), R.raw.pp_6)
        };
    }

    private void initTouchListener(final ImageView puzzlePiece){
        puzzlePiece.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    Bitmap puzzleImage = ((BitmapDrawable)puzzlePiece.getDrawable()).getBitmap();
                    PuzzlePiece selectedPiece = findPuzzlePiece(puzzleImage);
                    GlobalGameData.getInstance().setSelectedPuzzlePiece(selectedPiece);
                    PuzzlePieceDragShadowBuilder dragShadowBuilder = new PuzzlePieceDragShadowBuilder(GlobalGameData.getInstance().getContext(), puzzleImage);
                    v.startDrag(null,dragShadowBuilder,null,0);
                    placedPuzzlePieces.remove(selectedPiece);
                    ((RelativeLayout)layout).removeView((View)v.getParent());
                }
                return true;
            }
        });
    }

    public PuzzlePiece findPuzzlePiece(Bitmap image){
        int index = -1;
        for (int i = 0; i < placedPuzzlePieces.size(); i++){
            if (placedPuzzlePieces.get(i).getImage().sameAs(image)) {
                index = i;
                break;
            }
        }
        return placedPuzzlePieces.get(index);
    }


    private void handleDropEvent(DragEvent event){
        PuzzlePiece selectedPiece = GlobalGameData.getInstance().getSelectedPuzzlePiece();
        placedPuzzlePieces.add(selectedPiece);
        Point anchor = getClosestAnchorPoint(event.getX(), event.getY());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(selectedPiece.getImage().getWidth(),selectedPiece.getImage().getHeight());
        params.leftMargin = anchor.x;
        params.topMargin = anchor.y;
        Point currentPoint = new Point(Math.round(event.getX()),Math.round(event.getY()));

        ImageView puzzlePiece = new ImageView(GlobalGameData.getInstance().getContext());
        puzzlePiece.setImageBitmap(selectedPiece.getImage());
        puzzlePiece.setLayoutParams(params);
        initTouchListener(puzzlePiece);
        LinearLayout wrapper = new LinearLayout(GlobalGameData.getInstance().getContext());
        wrapper.addView(puzzlePiece);
        ((RelativeLayout)layout).addView(wrapper);

        animateDragToStart(puzzlePiece,currentPoint, anchor);
        playSoundEffect();
        selectedPiece.setCurrentPos(anchor);
        if (isPuzzleComplete()){
            Toast.makeText(GlobalGameData.getInstance().getContext(), "Puzzle Complete", Toast.LENGTH_LONG).show();
        }
    }

    private void playSoundEffect(){
        Random random = new Random();
        AudioManager audioManager = (AudioManager)GlobalGameData.getInstance().getContext().getSystemService(Context.AUDIO_SERVICE);
        int currentDeviceVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int maxDeviceVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int soundVolume = GlobalGameData.getInstance().getSoundVolume();

        float soundEffectVolume = ((float)currentDeviceVolume/maxDeviceVolume) * ((float)(1 - Math.log(Utility.MAX_VOLUME - soundVolume)/ Math.log(Utility.MAX_VOLUME)));
        Log.d("sound eff", currentDeviceVolume + " " + maxDeviceVolume + " " + soundEffectVolume + " global " + GlobalGameData.getInstance().getSoundVolume());
        int randomPosition = random.nextInt(soundEffects.length);

        soundEffects[randomPosition].setVolume(soundEffectVolume,soundEffectVolume);
        soundEffects[randomPosition].start();
    }

    private boolean isPuzzleComplete(){
        //Log.d("ispuzzlecomplete",placedPuzzlePieces.size() + " " + rows * columns);
        if (placedPuzzlePieces.size() < rows * columns)
            return false;
        for(int i = 0; i < placedPuzzlePieces.size(); i++){
            if (!placedPuzzlePieces.get(i).isCorrect()) {
                return false;
            }
        }
        return true;
    }

    private Point getClosestAnchorPoint(float x, float y){
        int intX = Math.round(x);
        int intY = Math.round(y);
        Point closestAnchor = anchorPoints[0][0];
        double minDistance = Double.MAX_VALUE;
        for (int j = 0; j < columns; j++){
            for (int i = 0; i < rows; i++){
                int centerX = anchorPoints[i][j].x + GlobalGameData.getInstance().getSelectedPuzzlePiece().getImage().getWidth()/2;
                int centerY = anchorPoints[i][j].y + GlobalGameData.getInstance().getSelectedPuzzlePiece().getImage().getHeight()/2;
                double distance = Math.sqrt(Math.pow((intX - centerX), 2) + (Math.pow((intY - centerY), 2)));
                if (distance < minDistance) {
                    closestAnchor = anchorPoints[i][j];
                    minDistance = distance;
                }
            }
        }
        return closestAnchor;
    }

    private void animateDragToStart(View view, Point from, Point to) {
        Bitmap piece = GlobalGameData.getInstance().getSelectedPuzzlePiece().getImage();
        Animation translateAnimation = new TranslateAnimation( from.x - to.x - piece.getWidth()/2, 0, from.y - to.y - piece.getHeight()/2, 0);
        translateAnimation.setDuration(300);
        translateAnimation.setFillAfter(true);
        translateAnimation.setInterpolator(new AccelerateInterpolator());
        view.startAnimation(translateAnimation);
    }
}
