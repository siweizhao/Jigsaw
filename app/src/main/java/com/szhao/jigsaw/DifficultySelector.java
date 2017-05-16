package com.szhao.jigsaw;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

public class DifficultySelector extends AppCompatActivity {

    private int puzzleId;
    private SeekBar difficultySelector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_difficulty_selector);

        Intent intent = getIntent();
        puzzleId = intent.getExtras().getInt("id");
        ImageAdapter adapter = new ImageAdapter(this);

        ImageView puzzleSelected = (ImageView)findViewById(R.id.puzzleSelected);
        ImageLoader.getInstance().displayImage("drawable://" + adapter.images[puzzleId],puzzleSelected);

        difficultySelector = (SeekBar)findViewById(R.id.seekBar);

        difficultySelector.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                TextView difficultyTxt = (TextView)findViewById(R.id.difficultyTxt);
                difficultyTxt.setText("Difficulty: "+ String.valueOf(seekBar.getProgress() + 2)
                        + " x " + String.valueOf(seekBar.getProgress() + 2));
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
        Intent intent = new Intent (this, MainActivity.class);
        intent.putExtra("id", puzzleId);
        intent.putExtra("difficulty", difficultySelector.getProgress() + 2);
        startActivity(intent);
    }
}
