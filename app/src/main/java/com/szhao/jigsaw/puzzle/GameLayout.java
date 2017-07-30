package com.szhao.jigsaw.puzzle;

import android.content.ClipData;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.util.Pair;
import android.view.DragEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Created by Owner on 7/28/2017.
 */

public class GameLayout {
    View layout;
    int rows, columns;
    Point[][] anchorPoints;
    Bitmap selectedPiece;
    int correctPos;
    int currentPos;

    public GameLayout(View layout, int rows, int columns, Point[][] anchorPoints){
        this.layout = layout;
        this.rows = rows;
        this.columns = columns;
        this.anchorPoints = anchorPoints;
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
                GlobalGameData.getInstance().setSelectedPieceData(new Pair(((BitmapDrawable)puzzlePiece.getDrawable()).getBitmap(),correctPos));
                ClipData dragData = ClipData.newPlainText("","");
                View.DragShadowBuilder dragShadowBuilder = new View.DragShadowBuilder(v);
                v.startDrag(dragData,dragShadowBuilder,v,0);
                if(currentPos == correctPos)
                    GlobalGameData.getInstance().numCorrectPiecesDecrease();
                ((RelativeLayout)layout).removeView(v);
                return true;
            }
        });
    }

    private void handleDropEvent(DragEvent event){
        selectedPiece = GlobalGameData.getInstance().getSelectedPieceData().first;
        correctPos = GlobalGameData.getInstance().getSelectedPieceData().second;
        Point anchor = getClosestAnchorPoint(event.getX(), event.getY());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(selectedPiece.getWidth(),selectedPiece.getHeight());
        params.leftMargin = anchor.x;
        params.topMargin = anchor.y;
        Point currentPoint = new Point(Math.round(event.getX()),Math.round(event.getY()));

        ImageView puzzlePiece = new ImageView(GlobalGameData.getInstance().getContext());
        puzzlePiece.setImageBitmap(selectedPiece);
        puzzlePiece.setLayoutParams(params);
        ((RelativeLayout)layout).addView(puzzlePiece);
        initLongClickListener(puzzlePiece);

        animateDragToStart(puzzlePiece,currentPoint, anchor);
        checkCorrectPosition();
    }

    public void checkCorrectPosition(){
        if (currentPos == correctPos)
            GlobalGameData.getInstance().numCorrectPiecesIncrease();
    }

    private Point getClosestAnchorPoint(float x, float y){
        GlobalGameData globalGameData = GlobalGameData.getInstance();
        int intX = Math.round(x);
        int intY = Math.round(y);
        Point closestAnchor = anchorPoints[0][0];
        currentPos = 0;
        double minDistance = Double.MAX_VALUE;
        for (int j = 0; j < columns; j++){
            for (int i = 0; i < rows; i++){
                int centerX = anchorPoints[i][j].x + globalGameData.getSelectedPieceData().first.getWidth()/2;
                int centerY = anchorPoints[i][j].y + globalGameData.getSelectedPieceData().first.getHeight()/2;
                double distance = Math.sqrt(Math.pow((intX - centerX), 2) + (Math.pow((intY - centerY), 2)));
                if (distance < minDistance) {
                    closestAnchor = anchorPoints[i][j];
                    minDistance = distance;
                    currentPos = i + j * columns;
                }
            }
        }
        return closestAnchor;
    }

    private void animateDragToStart(View view, Point from, Point to) {
        Bitmap piece = GlobalGameData.getInstance().getSelectedPieceData().first;
        Animation translateAnimation = new TranslateAnimation( from.x - to.x - piece.getWidth()/2, 0, from.y - to.y - piece.getHeight()/2, 0);
        translateAnimation.setDuration(500);
        translateAnimation.setFillAfter(true);
        translateAnimation.setInterpolator(new AccelerateInterpolator());
        view.startAnimation(translateAnimation);
    }
}
