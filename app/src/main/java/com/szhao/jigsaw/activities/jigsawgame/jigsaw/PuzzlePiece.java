package com.szhao.jigsaw.activities.jigsawgame.jigsaw;

import android.graphics.Bitmap;
import android.graphics.Point;

/**
 * Created by Owner on 3/29/2017.
 */

public class PuzzlePiece {
    Bitmap image;
    Point currentPos;
    Point correctPos;
    boolean isSidePiece;
    public static final Point UNPLACED_PIECE = new Point(-1,-1);

    public PuzzlePiece (Bitmap image, Point correctPos, boolean isSidePiece){
        this.image = image;
        this.correctPos = correctPos;
        this.currentPos = UNPLACED_PIECE;
        this.isSidePiece = isSidePiece;
    }

    public void setCurrentPos(Point currentPos){
        this.currentPos = currentPos;
    }

    public boolean isCorrect(){
        return currentPos.equals(correctPos);
    }

    public Bitmap getImage(){
        return image;
    }

    public boolean isSidePiece(){
        return isSidePiece;
    }

    public Point getCurrentPos(){
        return currentPos;
    }

    public Point getCorrectPos(){
        return correctPos;
    }
}
