package com.szhao.jigsaw.puzzle;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Owner on 7/29/2017.
 */

public class GlobalGameData {
    private static final GlobalGameData ourInstance = new GlobalGameData();

    Context context;
    Pair<Bitmap,Integer> selectedPieceData;
    int currentPos;
    int numCorrectPieces = 0;
    int totalNumPieces;

    public static GlobalGameData getInstance() {
        return ourInstance;
    }

    private GlobalGameData() {
    }

    public void setTotalNumPieces(int totalNumPieces){
        this.totalNumPieces = totalNumPieces;
    }

    public Pair<Bitmap,Integer> getSelectedPieceData() {
        return selectedPieceData;
    }

    public void setSelectedPieceData(Pair<Bitmap,Integer> selectedPieceData){
        this.selectedPieceData = selectedPieceData;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public int getCurrentPos(){
        return currentPos;
    }

    public void setCurrentPos(int currentPos){
        this.currentPos = currentPos;
    }

    public void numCorrectPiecesIncrease(){
        numCorrectPieces ++;
        Log.d("correct puzzlePiece inc", " "+ numCorrectPieces);
        if (numCorrectPieces == totalNumPieces)
            Toast.makeText(context, "Puzzle Complete", Toast.LENGTH_LONG).show();
    }

    public void numCorrectPiecesDecrease(){
        if (numCorrectPieces > 0) {
            numCorrectPieces--;
            Log.d("correct puzzlePiece dec", " "+ numCorrectPieces);
        }
    }

}
