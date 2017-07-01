package com.szhao.jigsaw.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.szhao.jigsaw.R;
import com.szhao.jigsaw.db.Utility;

public class DifficultySelector extends AppCompatActivity {

    private SeekBar difficultySelector;
    private static final int DIFFICULTY_OFFSET = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_difficulty_selector);

        Bitmap bitmap = Utility.getStoredImage(this);

        ImageView puzzleSelected = (ImageView)findViewById(R.id.puzzleSelected);
        puzzleSelected.setImageBitmap(bitmap);

        difficultySelector = (SeekBar)findViewById(R.id.seekBar);

        difficultySelector.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                TextView difficultyTxt = (TextView)findViewById(R.id.difficultyTxt);
                difficultyTxt.setText("Difficulty: "+ (seekBar.getProgress() + DIFFICULTY_OFFSET)
                        + " x " + (seekBar.getProgress() + 2));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void startGame(View view){
        Intent intent = new Intent (this, JigsawGame.class);
        intent.putExtra("difficulty", difficultySelector.getProgress() + DIFFICULTY_OFFSET);
        startActivity(intent);
    }
}
