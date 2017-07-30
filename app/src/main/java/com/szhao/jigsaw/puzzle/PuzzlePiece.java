package com.szhao.jigsaw.puzzle;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

/**
 * Created by Owner on 3/29/2017.
 */

public class PuzzlePiece {
    Bitmap image;
    Point currentPos;
    Point correctPos;
    public static final Point UNPLACED_PIECE = new Point(-1,-1);

    public PuzzlePiece (Bitmap image, Point correctPos){
        this.image = image;
        this.correctPos = correctPos;
        this.currentPos = UNPLACED_PIECE;
    }

    public void setCurrentPos(Point currentPos){
        this.currentPos = currentPos;
    }

    public boolean isCorrect(){
        return currentPos.equals(correctPos);
    }
}
