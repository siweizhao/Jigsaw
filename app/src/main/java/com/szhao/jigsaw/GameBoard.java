package com.szhao.jigsaw;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import java.util.Random;
/**
 * Created by Owner on 4/15/2017.
 */

public class GameBoard{
    private MainActivity mainActivity;
    private int difficulty;
    private Bitmap original;
    private JigsawConfig[][] puzzleConfig;
    private PuzzlePiece[][] solution;

    public GameBoard(MainActivity mainActivity, int difficulty, Bitmap original){
        this.mainActivity = mainActivity;
        this.difficulty = difficulty;
        this.original = original;
        this.puzzleConfig = JigsawConfig.getJigsawConfig(difficulty);
    }

    public void initGame(){
        this.solution = new PuzzlePiece[difficulty][difficulty];

        for (int j = 0; j < difficulty; j++) {
            for (int i = 0; i < difficulty; i++) {
                //Create puzzle piece from original image
                Bitmap puzzlePieceRaw = getPuzzlePieceRaw(i, j);
                double sideLength = original.getWidth()/difficulty;

                double width = 1.5 * sideLength;
                double height = 1.5 * sideLength;
                double y = j * sideLength - sideLength/4;
                double x = i * sideLength - sideLength/4;

                //Increase size of the puzzle piece so nothing gets cut off
                width += 7;
                height += 7;

                //Make sure x,y > 0 and boundaries constraint
                x = x < 0 ? 0 : x;
                y = y < 0 ? 0 : y;
                x = (int)Math.round(x + width) > original.getWidth() ? original.getWidth() - Math.round(width) : x;
                y = (int)Math.round(y + height) > original.getHeight() ? original.getHeight() - Math.round(height) : y;

                //Cuts out the puzzle piece from original image
                Bitmap puzzlePiece = Bitmap.createBitmap(puzzlePieceRaw, (int)Math.round(x),
                        (int)Math.round(y), (int)Math.round(width), (int)Math.round(height));

                //Position the solved piece on the game board
                PuzzlePiece solvedPiece = new PuzzlePiece(mainActivity,this,i,j);
                solution[i][j] = solvedPiece;
                solvedPiece.setImageBitmap(puzzlePiece);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int)Math.round(width), (int)Math.round(height));
                params.leftMargin = (int)Math.round(x);
                params.topMargin = (int)Math.round(y);
                solvedPiece.setLayoutParams(params);
                solvedPiece.setVisibility(View.INVISIBLE);
                mainActivity.getSolutionLayout().addView(solvedPiece);

                PuzzlePiece unsolvedPiece = new PuzzlePiece(mainActivity,this,i,j);
                unsolvedPiece.setImageBitmap(puzzlePiece);
                unsolvedPiece.setOnTouchListener(unsolvedPiece);
                unsolvedPiece.setVisibility(View.VISIBLE);
                mainActivity.getGameLayout().addView(unsolvedPiece);
            }
        }
    }



    //Generate the path for puzzle piece cutout
    private Path getPuzzlePath(int row, int column, JigsawConfig config){
        Path puzzlePath = new Path();
        float sideLength = (float)original.getWidth()/difficulty;
        float x1, x2, y1, y2;

        //Distance from edge to indent
        float segment = sideLength/3;
        float startX = sideLength * row;
        float startY = sideLength * column;

        //Offset to position the indent correctly
        float offset = segment/4;
        RectF indent;

        puzzlePath.moveTo(startX, startY);
        //top
        puzzlePath.lineTo(startX + segment, startY);
        if (config.getTop() != 0){
            x1 = startX + segment;
            x2 = startX + 2 * segment;
            if (config.getTop() == -1){
                y1 = startY - offset;
                y2 = startY + segment - offset;
            } else {
                y1 = startY - segment + offset;
                y2 = startY + offset;
            }
            indent = new RectF(x1, y1, x2, y2);
            puzzlePath.arcTo(indent, 180, config.getTop() * 180);
            puzzlePath.lineTo(x2, startY);
        }
        puzzlePath.lineTo(startX + sideLength, startY);
        startX = startX + sideLength;

        //right
        puzzlePath.lineTo(startX, startY + segment);
        if (config.getRight() != 0){
            y1 = startY + segment;
            y2 = startY + 2 * segment;
            if (config.getRight() == -1){
                x1 = startX - segment + offset;
                x2 = startX + offset;
            } else {
                x1 = startX - offset;
                x2 = startX + segment - offset;
            }
            indent = new RectF(x1, y1, x2, y2);
            puzzlePath.arcTo(indent, 270, config.getRight() * 180);
            puzzlePath.lineTo(startX, y2);
        }
        puzzlePath.lineTo(startX, startY + sideLength);
        startY = startY + sideLength;

        //bot
        puzzlePath.lineTo(startX - segment, startY);
        if (config.getBot() != 0){
            x1 = startX - 2 * segment;
            x2 = startX - segment;
            if (config.getBot() == -1){
                y1 = startY - segment + offset;
                y2 = startY + offset;
            } else {
                y1 = startY - offset;
                y2 = startY + segment - offset;
            }
            indent = new RectF(x1, y1, x2, y2);
            puzzlePath.arcTo(indent, 0, config.getBot() * 180);
            puzzlePath.lineTo(x1, startY);
        }
        puzzlePath.lineTo(startX - sideLength, startY);
        startX = startX - sideLength;

        //left
        puzzlePath.lineTo(startX, startY - segment);
        if (config.getLeft() != 0){
            y1 = startY - 2 * segment;
            y2 = startY - segment;
            if (config.getLeft() == -1){
                x1 = startX - offset;
                x2 = startX + segment - offset;
            } else {
                x1 = startX - segment + offset;
                x2 = startX + offset;
            }
            indent = new RectF(x1, y1, x2, y2);
            puzzlePath.arcTo(indent, 90, config.getLeft() * 180);
            puzzlePath.lineTo(startX, startY - 2 * segment);
        }
        puzzlePath.lineTo(startX, startY - sideLength);

        return puzzlePath;
    }

    //Creates puzzle piece from original picture
    private Bitmap getPuzzlePieceRaw(int i, int j){
        Path puzzlePath = getPuzzlePath(i, j, puzzleConfig[i][j]);
        Bitmap puzzlePiece = Bitmap.createBitmap(original.getWidth(), original.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(puzzlePiece);
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, original.getWidth(), original.getHeight());

        paint.setAntiAlias(true);
        canvas.drawPath(puzzlePath, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(original, rect, rect, paint);
        return puzzlePiece;
    }

    //Creates a new bitmap of just the puzzle piece
    private Bitmap trimPuzzlePiece(Bitmap puzzlePieceRaw, int i, int j){
        double sideLength = original.getWidth()/difficulty;
        double width = 1.5 * sideLength;
        double height = 1.5 * sideLength;
        double y = j * sideLength - sideLength/4;
        double x = i * sideLength - sideLength/4;

        //left, right edge
        if (i == 0 || i == difficulty - 1) {
            width = 1.25 * sideLength;
        }

        //top, bot edge
        if (j == 0 || j == difficulty - 1) {
            height = 1.25 * sideLength;
        }

        //Increase size of the puzzle piece so nothing gets cut off
        width += 7;
        height += 7;

        //Make sure x,y > 0 and boundaries constraint
        x = x < 0 ? 0 : x;
        y = y < 0 ? 0 : y;
        x = (int)Math.round(x + width) > original.getWidth() ? original.getWidth() - Math.round(width) : x;
        y = (int)Math.round(y + height) > original.getHeight() ? original.getHeight() - Math.round(height) : y;

        return Bitmap.createBitmap(puzzlePieceRaw, (int)Math.round(x),
                (int)Math.round(y), (int)Math.round(width), (int)Math.round(height));

    }

    public int getDifficulty(){
        return difficulty;
    }

    public PuzzlePiece[][] getSolution(){
        return solution;
    }
}
