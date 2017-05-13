package com.szhao.jigsaw;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;

public class DifficultySelector extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_difficulty_selector);

        Intent i = getIntent();
        int position = i.getExtras().getInt("id");
        ImageAdapter adapter = new ImageAdapter(this);

        ImageView puzzleSelected = (ImageView)findViewById(R.id.puzzleSelected);
        ImageLoader.getInstance().displayImage("drawable://" + adapter.images[position],puzzleSelected);
        //puzzleSelected.setImageResource(adapter.images[position]);

    }
}
