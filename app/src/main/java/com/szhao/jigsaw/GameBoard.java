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
import android.widget.RelativeLayout;
import java.util.Random;
/**
 * Created by Owner on 4/15/2017.
 */

public class GameBoard{
    Context context;
    int difficulty;
    Bitmap original;
    JigsawConfig[][] puzzleConfig;
    Bitmap[][] solved;
    RelativeLayout puzzleArea;
    public PuzzlePiece[][] solvedPuzzle;

    public GameBoard(Context context, int difficulty, Bitmap original,RelativeLayout puzzleArea){
        this.context = context;
        this.difficulty = difficulty;
        this.original = original;
        this.puzzleArea = puzzleArea;
        this.puzzleConfig = JigsawConfig.getJigsawConfig(difficulty);
        this.solvedPuzzle = new PuzzlePiece[difficulty][difficulty];
    }

    public void initGame(){
        Bitmap[][] puzzlePieces = new Bitmap[difficulty][difficulty];
        Bitmap bitmap = Bitmap.createBitmap(original);

        for (int j = 0; j < difficulty; j++) {
            for (int i = 0; i < difficulty; i++) {
                Path puzzlePath = getPuzzlePath(i, j, puzzleConfig[i][j]);
                Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(output);
                final Paint paint = new Paint();
                final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

                paint.setAntiAlias(true);
                canvas.drawPath(puzzlePath, paint);
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
                canvas.drawBitmap(bitmap, rect, rect, paint);

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

                x = x < 0 ? 0 : x;
                y = y < 0 ? 0 : y;

                //Increase size of the puzzle piece so nothing gets cut off
                width += 7;
                height += 7;
                if ((int)Math.round(x + width) > original.getWidth())
                    x = original.getWidth() - Math.round(width);
                if ((int)Math.round(y + height) > original.getHeight())
                    y = original.getHeight() - Math.round(height);

                Bitmap puzzlePiece = Bitmap.createBitmap(output, (int)Math.round(x),
                        (int)Math.round(y), (int)Math.round(width), (int)Math.round(height));

                //Bitmap puzzlePiece = Bitmap.createBitmap(output, (int)x, (int)y, (int)width, (int)height);

                puzzlePieces[i][j] = puzzlePiece;

                PuzzlePiece solvedPiece = new PuzzlePiece(context,this,i,j);
                solvedPuzzle[i][j] = solvedPiece;
                solvedPiece.setImageBitmap(puzzlePiece);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int)Math.round(width), (int)Math.round(height));
                params.leftMargin = (int)Math.round(x);
                params.topMargin = (int)Math.round(y);
                solvedPiece.setLayoutParams(params);
                solvedPiece.setVisibility(View.INVISIBLE);
                puzzleArea.addView(solvedPiece);

                PuzzlePiece unsolvedPiece = new PuzzlePiece(context,this,i,j);
                unsolvedPiece.setImageBitmap(puzzlePiece);
                unsolvedPiece.setOnTouchListener(unsolvedPiece);
                unsolvedPiece.setVisibility(View.VISIBLE);
                puzzleArea.addView(unsolvedPiece);
            }
        }
        solved = puzzlePieces;
    }


    //Generate the path for template
    public Path getPuzzlePath(int row, int column, JigsawConfig config){
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
        x1 = startX + segment;
        x2 = startX + 2 * segment;
        if (config.getTop() == -1){
            y1 = startY - offset;
            y2 = startY + segment - offset;
            indent = new RectF(x1, y1, x2, y2);
            puzzlePath.arcTo(indent, 180, -180);
        } else if (config.getTop() == 1){
            y1 = startY - segment + offset;
            y2 = startY + offset;
            indent = new RectF(x1, y1, x2, y2);
            puzzlePath.arcTo(indent, 180, 180);
        }
        puzzlePath.lineTo(x2, startY);
        puzzlePath.lineTo(startX + sideLength, startY);
        startX = startX + sideLength;

        //right
        puzzlePath.lineTo(startX, startY + segment);
        y1 = startY + segment;
        y2 = startY + 2 * segment;
        if (config.getRight() == -1){
            x1 = startX - segment + offset;
            x2 = startX + offset;
            //Log.d("right - 1", " " + x1 + " " + x2);
            indent = new RectF(x1 , y1, x2, y2);
            puzzlePath.arcTo(indent, 270, -180);
        } else if (config.getRight() == 1){
            x1 = startX - offset;
            x2 = startX + segment - offset;
            //Log.d("right + 1", " " + x1 + " " + x2);
            indent = new RectF(x1, y1, x2, y2);
            puzzlePath.arcTo(indent,270,180);
        }
        puzzlePath.lineTo(startX, y2);
        puzzlePath.lineTo(startX, startY + sideLength);
        startY = startY + sideLength;

        //bot
        puzzlePath.lineTo(startX - segment, startY);
        x1 = startX - 2 * segment;
        x2 = startX - segment;
        if (config.getBot() == -1){
            y1 = startY - segment + offset;
            y2 = startY + offset;
            indent = new RectF(x1, y1, x2, y2);
            puzzlePath.arcTo(indent, 0, -180);
        } else if (config.getBot() == 1){
            y1 = startY - offset;
            y2 = startY + segment - offset;
            indent = new RectF(x1, y1, x2, y2);
            puzzlePath.arcTo(indent,0,180);
        }
        puzzlePath.lineTo(x1, startY);
        puzzlePath.lineTo(startX - sideLength, startY);
        startX = startX - sideLength;

        //left
        puzzlePath.lineTo(startX, startY - segment);
        y1 = startY - 2 * segment;
        y2 = startY - segment;
        if (config.getLeft() == -1){
            x1 = startX - offset;
            x2 = startX + segment - offset;
            //Log.d("left - 1", " " + x1 + " " + x2);
            indent = new RectF(x1, y1, x2, y2);
            puzzlePath.arcTo(indent, 90, -180);
        } else if (config.getLeft() == 1){
            x1 = startX - segment + offset;
            x2 = startX + offset;
            //Log.d("left + 1", " " + x1 + " " + x2);
            indent = new RectF(x1, y1, x2, y2);
            puzzlePath.arcTo(indent,90,180);
        }
        puzzlePath.lineTo(startX, startY - 2 * segment);
        puzzlePath.lineTo(startX, startY - sideLength);

        return puzzlePath;
    }
}
