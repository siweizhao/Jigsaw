package com.szhao.jigsaw;

import android.app.Activity;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by Owner on 3/29/2017.
 */

public class PuzzlePiece extends android.support.v7.widget.AppCompatImageView implements View.OnTouchListener {
    private int row, col;
    Context context;
    GameBoard gameBoard;
    float dX, dY;

    public PuzzlePiece(Context context, GameBoard gameBoard, int row, int col){
        super(context);
        this.context = context;
        this.gameBoard = gameBoard;
        this.row = row;
        this.col = col;

    }

    public int getRow(){
        return row;
    }
    public int getCol() { return col; }
    public boolean onTouch(View view, MotionEvent event) {
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                dX = view.getX() - event.getRawX();
                dY = view.getY() - event.getRawY();
                return true;
            case MotionEvent.ACTION_MOVE:
                view.animate()
                        .x(event.getRawX() + dX)
                        .y(event.getRawY() + dY)
                        .setDuration(0)
                        .start();
                break;

            case MotionEvent.ACTION_UP:
                //Check if correct placement
                PuzzlePiece piece = (PuzzlePiece)view;
                if (withinBounds(piece, piece.getRow(), piece.getCol())){
                    view.setVisibility(View.GONE);
                    gameBoard.solvedPuzzle[piece.getRow()][piece.getCol()].setVisibility(View.VISIBLE);

                    //Check if solved;
                    if (isSolved())
                        Toast.makeText(context, "Puzzle solved", Toast.LENGTH_LONG).show();
                }

            default:
                return false;
        }
        return true;
    }

    private boolean withinBounds(PuzzlePiece piece, int row, int col){
        PuzzlePiece solution = gameBoard.solvedPuzzle[row][col];
        int[] solutionLocation = new int[2];
        solution.getLocationOnScreen(solutionLocation);
        int x1 = solutionLocation[0];
        int y1 = solutionLocation[1];

        int[] pieceLocation = new int[2];
        piece.getLocationOnScreen(pieceLocation);
        //Use center location of piece as reference
        int x2 = pieceLocation[0] + piece.getWidth()/2;
        int y2 = pieceLocation[1] + piece.getHeight()/2;

        if ((x2 >= x1 && x2 <= (x1 + solution.getWidth()) &&
                (y2 >= y1 && y2 <= (y1 + solution.getHeight()))))
            return true;
        else
            return false;
    }

    public boolean isSolved(){
        for (int i = 0; i < gameBoard.difficulty; i++){
            for(int j = 0; j < gameBoard.difficulty; j++) {
                if (gameBoard.solvedPuzzle[i][j].getVisibility() == View.INVISIBLE)
                    return false;
            }
        }
        return true;
    }
}
