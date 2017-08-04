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
    PuzzlePiece selectedPuzzlePiece;
    int soundVolume;
    int musicVolume;

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

    public int getSoundVolume(){
        return soundVolume;
    }

    public void setSoundVolume(int soundVolume){
        this.soundVolume = soundVolume;
    }

    public int getMusicVolume(){
        return musicVolume;
    }

    public void setMusicVolume(int musicVolume){
        this.musicVolume = musicVolume;
    }
}
