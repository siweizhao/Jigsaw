package com.szhao.jigsaw.activities;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.transition.Explode;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.szhao.jigsaw.R;

public class TitleScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title_screen);
    }

    public void goToPuzzlesMenu(View view){
        Intent intent = new Intent (this, PuzzleSelector.class);
        startActivity(intent);
    }

    public void goCompletedPuzzles(View view){
        Intent intent = new Intent (this, CompletedPuzzles.class);
        startActivity(intent);
    }
}
