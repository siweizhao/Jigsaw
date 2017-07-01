package com.szhao.jigsaw.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.szhao.jigsaw.R;

public class TitleScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title_screen);
        globalInit();
    }

    public void goToPuzzlesMenu(View view){
        Intent intent = new Intent (this, PuzzleSelector.class);
        startActivity(intent);
    }

    public void goCompletedPuzzles(View view){
        Intent intent = new Intent (this, CompletedPuzzles.class);
        startActivity(intent);
    }

    private void globalInit(){
    }
}
