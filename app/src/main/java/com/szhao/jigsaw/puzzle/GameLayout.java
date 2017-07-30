package com.szhao.jigsaw.puzzle;

import android.content.ClipData;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.util.Pair;
import android.view.DragEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Owner on 7/28/2017.
 */

public class GameLayout {
    View layout;
    int rows, columns;
    Point[][] anchorPoints;
    ArrayList<PuzzlePiece> placedPuzzlePieces;

    public GameLayout(View layout, int rows, int columns, Point[][] anchorPoints){
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
                }
                return true;
            }
        });
    }

    public void initSnapToGrid(){
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

    private void initLongClickListener(final ImageView puzzlePiece){
        puzzlePiece.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                PuzzlePiece selectedPiece = findPuzzlePiece(((BitmapDrawable)puzzlePiece.getDrawable()).getBitmap());
                GlobalGameData.getInstance().setSelectedPuzzlePiece(selectedPiece);
                ClipData dragData = ClipData.newPlainText("","");
                View.DragShadowBuilder dragShadowBuilder = new View.DragShadowBuilder(v);
                v.startDrag(dragData,dragShadowBuilder,v,0);
                placedPuzzlePieces.remove(selectedPiece);
                ((RelativeLayout)layout).removeView(v);
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
        ((RelativeLayout)layout).addView(puzzlePiece);
        initLongClickListener(puzzlePiece);

        animateDragToStart(puzzlePiece,currentPoint, anchor);
        selectedPiece.setCurrentPos(anchor);
        if (isPuzzleComplete()){
            Toast.makeText(GlobalGameData.getInstance().getContext(), "Puzzle Complete", Toast.LENGTH_LONG).show();
        }
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
        translateAnimation.setDuration(500);
        translateAnimation.setFillAfter(true);
        translateAnimation.setInterpolator(new AccelerateInterpolator());
        view.startAnimation(translateAnimation);
    }
}
