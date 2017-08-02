package com.szhao.jigsaw.puzzle;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.constraint.solver.widgets.Rectangle;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.szhao.jigsaw.R;
import com.szhao.jigsaw.activities.JigsawGame;
import com.szhao.jigsaw.adapters.PuzzlePieceRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Owner on INDENT_RATIO/15/2017.
 */

public class Game {
    public final static int SEGMENT_RATIO = 3;
    public final static int INDENT_RATIO = 4;
    public final static int PUZZLE_SIZE_OFFSET = 7;
    
    private JigsawGame jigsawGame;
    private int rows, columns;
    private Bitmap original;
    private JigsawConfig[][] puzzleConfig;
    int pieceLengthX, pieceLengthY, indentSizeX, indentSizeY;


    public Game(JigsawGame jigsawGame, int rows, int columns){
        this.jigsawGame = jigsawGame;
        this.rows = rows;
        this.columns = columns;
        this.original = jigsawGame.getPuzzleImage();
        initMeasurements();
    }

    private void initMeasurements(){
        pieceLengthX = original.getWidth()/rows;
        indentSizeX = pieceLengthX/ Game.INDENT_RATIO;
        pieceLengthY = original.getHeight()/columns;
        indentSizeY = pieceLengthX/ Game.INDENT_RATIO;
    }

    public void initGame(){
        this.puzzleConfig = JigsawConfig.generateJigsawConfig(rows, columns);
        ArrayList<PuzzlePiece>puzzlePieceArrayList = new ArrayList<>();
        Point[][] anchorPoints = new Point[rows][columns];
        for (int j = 0; j < columns; j++) {
            for (int i = 0; i < rows; i++) {
                //Create puzzle piece from original image, the rest of the image will be blank
                Bitmap puzzlePieceFromOriginal = getPuzzlePieceFromOriginal(i, j);

                //Get position of the puzzle piece
                Rectangle dimension = getPuzzlePieceDimension(i,j);

                //Cuts out the puzzle piece from original image
                Bitmap puzzlePiece = Bitmap.createBitmap(puzzlePieceFromOriginal, dimension.x, dimension.y, dimension.width, dimension.height);

                boolean isSidePiece = (i == 0 || i == rows - 1 || j == 0 || j == columns -1) ? true : false;
                Point solutionCoords = new Point(dimension.x - indentSizeX,dimension.y - indentSizeY);
                puzzlePieceArrayList.add(new PuzzlePiece(puzzlePiece, solutionCoords, isSidePiece));
                anchorPoints[i][j]= solutionCoords;
            }
        }
        PuzzlePieceRecyclerViewAdapter viewAdapter = new PuzzlePieceRecyclerViewAdapter(jigsawGame,puzzlePieceArrayList);
        RecyclerView recycler = jigsawGame.getPuzzlePieceRecycler();
        recycler.setAdapter(viewAdapter);
        GameLayout gameLayout = new GameLayout(jigsawGame.getGameLayout(), rows, columns, anchorPoints);
        gameLayout.setBoundary(jigsawGame.getCompleteLayout());
    }

    //Generate the path for puzzle piece cutout
    private Path getPuzzlePath(int i, int j, JigsawConfig config){
        Path puzzlePath = new Path();
        float sideLengthX = (float)pieceLengthX;
        float sideLengthY = (float)pieceLengthY;

        float x1, x2, y1, y2;

        //Distance from edge to indent
        float segmentX = sideLengthX/SEGMENT_RATIO;
        float segmentY = sideLengthY/SEGMENT_RATIO;
        float startX = sideLengthX * i;
        float startY = sideLengthY * j;

        //Offset to position the indent correctly
        float offsetX = segmentX/INDENT_RATIO;
        float offsetY = segmentY/INDENT_RATIO;

        RectF indent;
        puzzlePath.moveTo(startX, startY);
        //top
        puzzlePath.lineTo(startX + segmentX, startY);
        if (config.getTop() != 0){
            x1 = startX + segmentX;
            x2 = startX + 2 * segmentX;
            if (config.getTop() == -1){
                y1 = startY - offsetY;
                y2 = startY + segmentY - offsetY;
            } else {
                y1 = startY - segmentY + offsetY;
                y2 = startY + offsetY;
            }
            indent = new RectF(x1, y1, x2, y2);
            puzzlePath.arcTo(indent, 180, config.getTop() * 180);
            puzzlePath.lineTo(x2, startY);
        }
        puzzlePath.lineTo(startX + sideLengthX, startY);
        startX = startX + sideLengthX;

        //right
        puzzlePath.lineTo(startX, startY + segmentY);
        if (config.getRight() != 0){
            y1 = startY + segmentY;
            y2 = startY + 2 * segmentY;
            if (config.getRight() == -1){
                x1 = startX - segmentX + offsetX;
                x2 = startX + offsetX;
            } else {
                x1 = startX - offsetX;
                x2 = startX + segmentX - offsetX;
            }
            indent = new RectF(x1, y1, x2, y2);
            puzzlePath.arcTo(indent, 270, config.getRight() * 180);
            puzzlePath.lineTo(startX, y2);
        }
        puzzlePath.lineTo(startX, startY + sideLengthY);
        startY = startY + sideLengthY;

        //bot
        puzzlePath.lineTo(startX - segmentX, startY);
        if (config.getBot() != 0){
            x1 = startX - 2 * segmentX;
            x2 = startX - segmentX;
            if (config.getBot() == -1){
                y1 = startY - segmentY + offsetY;
                y2 = startY + offsetY;
            } else {
                y1 = startY - offsetY;
                y2 = startY + segmentY - offsetY;
            }
            indent = new RectF(x1, y1, x2, y2);
            puzzlePath.arcTo(indent, 0, config.getBot() * 180);
            puzzlePath.lineTo(x1, startY);
        }
        puzzlePath.lineTo(startX - sideLengthX, startY);
        startX = startX - sideLengthX;

        //left
        puzzlePath.lineTo(startX, startY - segmentY);
        if (config.getLeft() != 0){
            y1 = startY - 2 * segmentY;
            y2 = startY - segmentY;
            if (config.getLeft() == -1){
                x1 = startX - offsetX;
                x2 = startX + segmentX - offsetX;
            } else {
                x1 = startX - segmentX + offsetX;
                x2 = startX + offsetX;
            }
            indent = new RectF(x1, y1, x2, y2);
            puzzlePath.arcTo(indent, 90, config.getLeft() * 180);
            puzzlePath.lineTo(startX, startY - 2 * segmentY);
        }
        puzzlePath.lineTo(startX, startY - sideLengthY);

        return puzzlePath;
    }

    //Creates puzzle piece from original picture
    private Bitmap getPuzzlePieceFromOriginal(int i, int j){
        Path puzzlePath = getPuzzlePath(i, j, puzzleConfig[i][j]);
        Bitmap puzzlePiece = Bitmap.createBitmap(original.getWidth() + 2 * indentSizeX, original.getHeight() + 2 * indentSizeY, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(puzzlePiece);
        canvas.translate((float)indentSizeX, (float)indentSizeY);
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, original.getWidth() + 2 * indentSizeX, original.getHeight() + 2 * indentSizeY);

        paint.setAntiAlias(true);
        canvas.drawPath(puzzlePath, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(original, rect, rect, paint);

        Paint puzzleOutline = new Paint();
        puzzleOutline.setStrokeWidth(3);
        puzzleOutline.setColor(Color.BLACK);
        puzzleOutline.setStyle(Paint.Style.STROKE);
        puzzleOutline.setAntiAlias(true);
        canvas.drawPath(puzzlePath, puzzleOutline);

        return puzzlePiece;
    }

    private Rectangle getPuzzlePieceDimension(int i, int j){
        int width = pieceLengthX + 2 * indentSizeX;
        int x = i * pieceLengthX;

        int height = pieceLengthY + 2 *indentSizeY;
        int y = j * pieceLengthY;

        Rectangle position = new Rectangle();
        position.setBounds(x,y,width,height);
        return position;
    }
}
