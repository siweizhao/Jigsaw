package com.szhao.jigsaw.global;

import com.szhao.jigsaw.activities.jigsawgame.jigsaw.PuzzlePiece;

/**
 * Created by Owner on 7/29/2017.
 */

public class GlobalGameData {
    private static final GlobalGameData ourInstance = new GlobalGameData();
    private PuzzlePiece selectedPuzzlePiece;

    private GlobalGameData() {
    }

    public static GlobalGameData getInstance() {
        return ourInstance;
    }

    public PuzzlePiece getSelectedPuzzlePiece() {
        return selectedPuzzlePiece;
    }

    public void setSelectedPuzzlePiece(PuzzlePiece selectedPuzzlePiece){
        this.selectedPuzzlePiece = selectedPuzzlePiece;
    }
}
