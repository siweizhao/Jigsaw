package com.szhao.jigsaw.Old.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.szhao.jigsaw.R;
import com.szhao.jigsaw.activities.jigsawgame.JigsawGameActivity;
import com.szhao.jigsaw.global.Utility;

/**
 * Created by Owner on 7/17/2017.
 */

public class DifficultyDialog extends AppCompatDialogFragment {
    private static final int DIFFICULTY_OFFSET = 2;
    static  SeekBar difficultySelector;
    public DifficultyDialog(){
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(AppCompatDialogFragment.STYLE_NO_TITLE, R.style.dialog_light);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        final View view = inflater.inflate(R.layout.dialog_difficulty, container, false);
        initPuzzleImage(view);
        initSeekBar(view);
        initStartGameBtn(view);
        return view;
    }

    private void initPuzzleImage(View view){
        ImageView puzzleImage = (ImageView) view.findViewById(R.id.dialog_puzzleSelected);
        Glide.with(this)
                .load(getActivity().getFilesDir() + "/" + Utility.IMAGE_FILENAME)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .override(Utility.IMAGE_DIMENSIONS, Utility.IMAGE_DIMENSIONS)
                .centerCrop()
                .into(puzzleImage);

    }

    private void initStartGameBtn(View view){
        Button startGameButton = (Button)view.findViewById(R.id.dialog_startGame);
        startGameButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent (getActivity(), JigsawGameActivity.class);
                intent.putExtra("difficulty", difficultySelector.getProgress() + DIFFICULTY_OFFSET);
                startActivity(intent);
            }
        });
    }

    private void initSeekBar(final View view){
        difficultySelector = (SeekBar)view.findViewById(R.id.dialog_seekBar);
        difficultySelector.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                TextView difficultyTxt = (TextView)view.findViewById(R.id.dialog_difficultyTxt);
                difficultyTxt.setText("Difficulty: "+ (seekBar.getProgress() + DIFFICULTY_OFFSET)
                        + " x " + (seekBar.getProgress() + DIFFICULTY_OFFSET));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }
}
