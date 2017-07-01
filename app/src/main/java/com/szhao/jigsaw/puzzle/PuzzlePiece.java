package com.szhao.jigsaw.puzzle;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.szhao.jigsaw.activities.JigsawGame;

/**
 * Created by Owner on 3/29/2017.
 */

public class PuzzlePiece extends android.support.v7.widget.AppCompatImageView implements View.OnTouchListener {
    private int row, col;
    private GameBoard gameBoard;
    float dX, dY;
    public PuzzlePiece(GameBoard gameBoard, int row, int col){
        super(gameBoard.getJigsawGame());
        this.gameBoard = gameBoard;
        this.row = row;
        this.col = col;
    }

    public boolean onTouch(View view, MotionEvent event) {
        if (gameBoard.getJigsawGame().isMenuOpen)
            return false;

        view.bringToFront();
        view.invalidate();
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
                if (withinBounds(piece, piece.row, piece.col)){
                    view.setVisibility(View.GONE);
                    gameBoard.getSolution()[piece.row][piece.col].setVisibility(View.VISIBLE);

                    //Check if solved;
                    if (isSolved()) {
                        gameBoard.getJigsawGame().stopTimer();
                        gameBoard.getJigsawGame().puzzleComplete();
                        Toast.makeText(gameBoard.getJigsawGame(), "Puzzle solved in " + (gameBoard.getJigsawGame().getTotalTimeSec() - 1) + " seconds", Toast.LENGTH_LONG).show();
                    }
                }

            default:
                return false;
        }
        return true;
    }

    private boolean withinBounds(PuzzlePiece piece, int row, int col){
        PuzzlePiece solution = gameBoard.getSolution()[row][col];
        int[] solutionLocation = new int[2];
        solution.getLocationOnScreen(solutionLocation);
        int x1 = solutionLocation[0];
        int y1 = solutionLocation[1];

        int[] pieceLocation = new int[2];
        piece.getLocationOnScreen(pieceLocation);
        int x2 = pieceLocation[0];
        int y2 = pieceLocation[1];

        int bounds = piece.getWidth()/6;
        return Math.abs(x1 - x2) <= bounds && Math.abs(y1-y2) <= bounds;
    }

    public boolean isSolved(){
        for (int i = 0; i < gameBoard.getDifficulty(); i++){
            for(int j = 0; j < gameBoard.getDifficulty(); j++) {
                if (gameBoard.getSolution()[i][j].getVisibility() == View.INVISIBLE)
                    return false;
            }
        }
        return true;
    }
}
