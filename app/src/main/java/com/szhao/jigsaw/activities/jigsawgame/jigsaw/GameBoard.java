package com.szhao.jigsaw.activities.jigsawgame.jigsaw;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
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

import com.szhao.jigsaw.activities.jigsawgame.JigsawGameActivity;
import com.szhao.jigsaw.global.GlobalGameData;

import java.util.ArrayList;

/**
 * Created by Owner on 7/28/2017.
 */

public class GameBoard {
    private View layout;
    private int rows, columns;
    private Point[][] anchorPoints;
    private ArrayList<PuzzlePiece> placedPuzzlePieces;
    private JigsawGameActivity jigsawGameActivity;

    public GameBoard(JigsawGameActivity jigsawGameActivity, View layout, int rows, int columns, Point[][] anchorPoints){
        this.jigsawGameActivity = jigsawGameActivity;
        this.layout = layout;
        this.rows = rows;
        this.columns = columns;
        this.anchorPoints = anchorPoints;
        placedPuzzlePieces = new ArrayList<>();
        initSnapToGrid();
    }

    public void setBoundary(FrameLayout parentLayout){
        parentLayout.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                switch (event.getAction()) {
                    case DragEvent.ACTION_DROP:
                        handleDropEvent(event);
                        break;
                    default:
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
                    default:
                        break;
                }
                return true;
            }
        });
    }

    private void initTouchListener(final ImageView puzzlePiece){
        puzzlePiece.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    Bitmap puzzleImage = ((BitmapDrawable)puzzlePiece.getDrawable()).getBitmap();
                    PuzzlePiece selectedPiece = findPuzzlePiece(puzzleImage);
                    GlobalGameData.getInstance().setSelectedPuzzlePiece(selectedPiece);
                    PuzzlePieceDragShadowBuilder dragShadowBuilder = new PuzzlePieceDragShadowBuilder(jigsawGameActivity, puzzleImage);
                    v.startDrag(null,dragShadowBuilder,null,0);
                    placedPuzzlePieces.remove(selectedPiece);
                    ((RelativeLayout)layout).removeView((View)v.getParent());
                }
                return true;
            }
        });
    }

    private PuzzlePiece findPuzzlePiece(Bitmap image) {
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
        ImageView puzzlePiece = setPuzzlePiece(selectedPiece, anchor);
        Point currentPoint = new Point(Math.round(event.getX()),Math.round(event.getY()));
        animateDragToStart(puzzlePiece,currentPoint, anchor);
        jigsawGameActivity.playClickSoundEff();
        selectedPiece.setCurrentPos(anchor);
        if (isPuzzleComplete()){
            placedPuzzlePieces.clear();
            ((RelativeLayout) layout).removeAllViews();
            jigsawGameActivity.puzzleComplete();
        }
    }

    private boolean isPuzzleComplete(){
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

    public ArrayList<PuzzlePiece> getPlacedPuzzlePieces(){
        return placedPuzzlePieces;
    }

    public void loadPuzzlePieces(ArrayList<PuzzlePiece> savedPuzzlePieces){
        placedPuzzlePieces = savedPuzzlePieces;
        for (PuzzlePiece p : placedPuzzlePieces){
            setPuzzlePiece(p, p.getCurrentPos());
        }
    }

    private ImageView setPuzzlePiece(PuzzlePiece p, Point currentPos){
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(p.getImage().getWidth(),p.getImage().getHeight());
        params.leftMargin = currentPos.x;
        params.topMargin = currentPos.y;

        ImageView puzzlePiece = new ImageView(jigsawGameActivity);
        puzzlePiece.setImageBitmap(p.getImage());
        puzzlePiece.setLayoutParams(params);
        initTouchListener(puzzlePiece);
        LinearLayout wrapper = new LinearLayout(jigsawGameActivity);
        wrapper.addView(puzzlePiece);
        ((RelativeLayout)layout).addView(wrapper);
        return puzzlePiece;
    }
}
