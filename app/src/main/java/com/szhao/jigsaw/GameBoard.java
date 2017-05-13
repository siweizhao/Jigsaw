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

    public GameBoard(Context context, int difficulty, Bitmap original,RelativeLayout puzzleArea){
        this.context =context;
        this.difficulty = difficulty;
        this.original = original;
        this.puzzleArea = puzzleArea;
        this.puzzleConfig = getJigsawConfig();
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

                Bitmap puzzlePiece = Bitmap.createBitmap(output, (int)x, (int) y, (int)width, (int)height);
                puzzlePieces[i][j] = puzzlePiece;

                PuzzlePiece solvedPiece = new PuzzlePiece(context,i,j);
                solvedPiece.setImageBitmap(puzzlePiece);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int)width, (int)height);
                params.leftMargin = (int)x;
                params.topMargin = (int)y;
                solvedPiece.setLayoutParams(params);
                puzzleArea.addView(solvedPiece);


            }
        }
        solved = puzzlePieces;
    }


    //Generate the path for template
    public Path getPuzzlePath(int row, int column, JigsawConfig config){
        Path puzzlePath = new Path();
        float sideLength = (float)original.getWidth()/difficulty;

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
        if (config.getTop() == -1){
            indent = new RectF(startX + segment, startY - offset, startX + 2 * segment, startY + segment - offset);
            puzzlePath.arcTo(indent, 180, -180);
        } else if (config.getTop() == 1){
            indent = new RectF(startX + segment, startY - segment + offset, startX + 2 * segment, startY + offset);
            puzzlePath.arcTo(indent, 180, 180);
        }
        puzzlePath.lineTo(startX + 2 * segment, startY);
        puzzlePath.lineTo(startX + sideLength, startY);
        startX = startX + sideLength;

        //right
        puzzlePath.lineTo(startX, startY + segment);
        if (config.getRight() == -1){
            indent = new RectF(startX - segment + offset , startY + segment, startX + offset, startY + 2 * segment);
            puzzlePath.arcTo(indent, 270, -180);
        } else if (config.getRight() == 1){
            indent = new RectF(startX - offset, startY + segment, startX + segment - offset, startY + 2 * segment);
            puzzlePath.arcTo(indent,270,180);
        }
        puzzlePath.lineTo(startX, startY + 2 * segment);
        puzzlePath.lineTo(startX, startY + sideLength);
        startY = startY + sideLength;

        //bot
        puzzlePath.lineTo(startX - segment, startY);
        if (config.getBot() == -1){
            indent = new RectF(startX - 2 * segment, startY - segment + offset, startX - segment, startY + offset);
            puzzlePath.arcTo(indent, 0, -180);
        } else if (config.getBot() == 1){
            indent = new RectF(startX - 2 * segment, startY - offset, startX - segment,  startY + segment - offset);
            puzzlePath.arcTo(indent,0,180);
        }
        puzzlePath.lineTo(startX - 2 * segment, startY);
        puzzlePath.lineTo(startX - sideLength, startY);
        startX = startX - sideLength;

        //left
        puzzlePath.lineTo(startX, startY - segment);
        if (config.getLeft() == -1){
            indent = new RectF(startX - offset , startY - 2 * segment, startX + segment - offset, startY - segment);
            puzzlePath.arcTo(indent, 90, -180);
        } else if (config.getLeft() == 1){
            indent = new RectF(startX - segment + offset, startY - 2 * segment, startX + offset, startY - segment);
            puzzlePath.arcTo(indent,90,180);
        }
        puzzlePath.lineTo(startX, startY - 2 * segment);
        puzzlePath.lineTo(startX, startY - sideLength);

        return puzzlePath;
    }

    // -1 represents indent, 1 represents outdent, 0 represents a flat surface
    // the sides of each jigsaw piece will be represented by this
    public JigsawConfig[][] getJigsawConfig(){
        JigsawConfig[][] config = new JigsawConfig[difficulty][difficulty];
        Random random = new Random();
        int top, bot, left, right;

        for(int j = 0; j < difficulty; j ++) {
            for (int i = 0; i < difficulty; i++){
                top = random.nextInt(2) * 2 - 1;
                right = random.nextInt(2) * 2 - 1;
                bot = random.nextInt(2) * 2 - 1;
                left = random.nextInt(2) * 2 - 1;

                //Check adjacent pieces
                if (i > 0)
                    left = -config[i - 1][j].getRight();
                if (j > 0)
                    top = -config[i][j - 1].getBot();

                if (i == 0)
                    left = 0;
                else if (i == difficulty - 1)
                    right = 0;

                if (j == 0)
                    top = 0;
                else if (j == difficulty - 1)
                    bot = 0;
                config[i][j] = new JigsawConfig(top,bot,left,right);
            }
        }
        return config;
    }
}
