package com.szhao.jigsaw.Global;

import android.content.Context;

import com.szhao.jigsaw.activities.JigsawGame.Jigsaw.PuzzlePiece;

/**
 * Created by Owner on 7/29/2017.
 */

public class GlobalGameData {
    private static final GlobalGameData ourInstance = new GlobalGameData();

    Context context;
    PuzzlePiece selectedPuzzlePiece;

    public static GlobalGameData getInstance() {
        return ourInstance;
    }

    private GlobalGameData() {
    }

    public PuzzlePiece getSelectedPuzzlePiece() {
        return selectedPuzzlePiece;
    }

    public void setSelectedPuzzlePiece(PuzzlePiece selectedPuzzlePiece){
        this.selectedPuzzlePiece = selectedPuzzlePiece;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

}
