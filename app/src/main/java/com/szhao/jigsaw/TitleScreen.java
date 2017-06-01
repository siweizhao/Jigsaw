package com.szhao.jigsaw;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class TitleScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title_screen);

    }

    public void goToPuzzlesMenu(View view){
        Intent intent = new Intent (this, PuzzlesMenu.class);
        startActivity(intent);
    }

    public void goCompletedPuzzles(View view){
        Intent intent = new Intent (this, CompletedPuzzles.class);
        startActivity(intent);
    }

}
