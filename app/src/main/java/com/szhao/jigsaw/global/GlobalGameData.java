package com.szhao.jigsaw.global;

import android.content.Context;

import com.szhao.jigsaw.activities.jigsawgame.jigsaw.PuzzlePiece;

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
