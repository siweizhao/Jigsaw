package com.szhao.jigsaw.puzzle;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.constraint.solver.widgets.Rectangle;
import android.view.View;
import android.widget.RelativeLayout;

import com.szhao.jigsaw.activities.JigsawGame;

import java.util.Random;

/**
 * Created by Owner on INDENT_RATIO/15/2017.
 */

public class GameBoard{
    public final static int INDENT_RATIO = 4;
    public final static int PUZZLE_SIZE_OFFSET = 7;
    
    private JigsawGame jigsawGame;
    private int difficulty;
    private Bitmap original;
    private JigsawConfig[][] puzzleConfig;
    private PuzzlePiece[][] solution;

    public GameBoard(JigsawGame jigsawGame, int difficulty, Bitmap original){
        this.jigsawGame = jigsawGame;
        this.difficulty = difficulty;
        this.original = original;
    }

    public void initGame(){
        this.puzzleConfig = JigsawConfig.generateJigsawConfig(difficulty);
        this.solution = new PuzzlePiece[difficulty][difficulty];
        for (int j = 0; j < difficulty; j++) {
            for (int i = 0; i < difficulty; i++) {
                //Create puzzle piece from original image, the rest of the image will be blank
                Bitmap puzzlePieceRaw = getPuzzlePieceRaw(i, j);

                //Get position of the puzzle piece
                Rectangle position = getPuzzlePiecePosition(i,j);

                //Cuts out the puzzle piece from original image
                Bitmap puzzlePiece = Bitmap.createBitmap(puzzlePieceRaw, position.x, position.y, position.width, position.height);

                //Position the solved piece on the game board
                placePuzzlePiece(i,j,puzzlePiece,position);

                //Place the unsolved puzzle piece in a random location
                scramblePuzzlePiece(puzzlePiece,i,j);
            }
        }
    }

    //Position each puzzle piece randomly
    private void scramblePuzzlePiece(Bitmap puzzlePiece,int i, int j){
        Random random = new Random();
        RelativeLayout.LayoutParams params =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        PuzzlePiece unsolvedPiece = new PuzzlePiece(this,i,j);
        params.leftMargin = random.nextInt(jigsawGame.getDisplayWidth() - puzzlePiece.getWidth());
        params.topMargin = random.nextInt(jigsawGame.getDisplayHeight()  - puzzlePiece.getHeight());
        unsolvedPiece.setLayoutParams(params);
        unsolvedPiece.setImageBitmap(puzzlePiece);
        unsolvedPiece.setOnTouchListener(unsolvedPiece);
        unsolvedPiece.setVisibility(View.VISIBLE);
        jigsawGame.getGameLayout().addView(unsolvedPiece);
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
        float offset = segment/INDENT_RATIO;
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

    private Rectangle getPuzzlePiecePosition(int i, int j){
        JigsawConfig jigsawConfig = puzzleConfig[i][j];
        int pieceLength = original.getWidth()/difficulty;
        int indentSize = pieceLength/GameBoard.INDENT_RATIO;

        int width = pieceLength;
        int x = i * pieceLength;
        //Determine size and position of new bitmap for each puzzle piece
        if (jigsawConfig.getLeft() == 1){
            width += indentSize;
            x -= indentSize;
        }
        if (jigsawConfig.getRight() == 1){
            width += indentSize;
        }

        int height = pieceLength;
        int y = j * pieceLength;
        if (jigsawConfig.getTop() == 1){
            height += indentSize;
            y -= indentSize;
        }
        if (jigsawConfig.getBot() == 1){
            height += indentSize;
        }

        //Increase size of the puzzle piece so nothing gets cut off
        width += PUZZLE_SIZE_OFFSET;
        height += PUZZLE_SIZE_OFFSET;

        //Checking boundaries
        x = x < 0 ? 0 : x;
        y = y < 0 ? 0 : y;
        x = x + width > original.getWidth() ? original.getWidth() - width : x;
        y = y + height > original.getHeight() ? original.getHeight() - height : y;

        Rectangle position = new Rectangle();
        position.setBounds(x,y,width,height);
        return position;
    }

    private void placePuzzlePiece(int i, int j, Bitmap puzzlePiece, Rectangle position){
        PuzzlePiece solvedPiece = new PuzzlePiece(this,i,j);
        solution[i][j] = solvedPiece;
        solvedPiece.setImageBitmap(puzzlePiece);
        RelativeLayout.LayoutParams params =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.leftMargin = position.x;
        params.topMargin = position.y;
        solvedPiece.setLayoutParams(params);
        solvedPiece.setVisibility(View.INVISIBLE);
        jigsawGame.getSolutionLayout().addView(solvedPiece);
    }


    public int getDifficulty(){
        return difficulty;
    }

    public PuzzlePiece[][] getSolution(){
        return solution;
    }

    public JigsawGame getJigsawGame(){
        return jigsawGame;
    }
}
