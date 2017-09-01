package com.szhao.jigsaw.activities.dashboard.fragment;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.szhao.jigsaw.R;
import com.szhao.jigsaw.activities.jigsawgame.JigsawGameActivity;
import com.szhao.jigsaw.db.PuzzleContentProvider;

import org.w3c.dom.Text;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DifficultyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DifficultyFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "item";
    private static final String ARG_PARAM2 = "difficulty";
    private static final String ARG_PARAM3 = "positions";

    // TODO: Rename and change types of parameters
    private String item;
    private int difficulty;
    View masterLayout;
    LoaderManager loaderManager;
    ImageView puzzleImage;
    SeekBar difficultySelector;
    int[] bestSolveTimes;
    String[] startedPuzzlesPositions;
    int[] currentTime;
    public static final int DIFFICULTY_MIN_VALUE = 2;

    public static final int TABLE_COMPLETED = 1;
    public static final int TABLE_STARTED = 2;
    private int loadFinished;

    public DifficultyFragment() {
        // Required empty public constructor
    }

    public static DifficultyFragment newInstance(String filePath, int difficulty, String positions) {
        DifficultyFragment fragment = new DifficultyFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, filePath);
        args.putInt(ARG_PARAM2, difficulty);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            item = getArguments().getString(ARG_PARAM1);
            difficulty = getArguments().getInt(ARG_PARAM2);
            loadFinished = 0;
            loaderManager = getLoaderManager();
            loaderManager.initLoader(TABLE_COMPLETED, null, this);
            loaderManager.initLoader(TABLE_STARTED, null, this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        masterLayout = inflater.inflate(R.layout.fragment_difficulty, container, false);
        puzzleImage = (ImageView)masterLayout.findViewById(R.id.difficultyFragmentPuzzleImage);
        difficultySelector = (SeekBar)masterLayout.findViewById(R.id.difficultyFragmentSeekBar);
        loadImage();
        initStartGameBtn();
        return masterLayout;
    }

    private void loadImage(){
        Glide.with(getContext())
                .load(new File(item))
                .override(700,550)
                .centerCrop()
                .into(puzzleImage);
    }

    private void initStartGameBtn(){
        Button startGameBtn = (Button)masterLayout.findViewById(R.id.difficultyFragmentStartGameBtn);
        startGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (getActivity(), JigsawGameActivity.class);
                intent.putExtra("filePath", item);
                intent.putExtra("difficulty", difficultySelector.getProgress() + DIFFICULTY_MIN_VALUE);
                intent.putExtra("positions", startedPuzzlesPositions[difficultySelector.getProgress() + DIFFICULTY_MIN_VALUE]);
                intent.putExtra("currTime", currentTime[difficultySelector.getProgress() + DIFFICULTY_MIN_VALUE]);
                startActivity(intent);
            }
        });
    }

    private void initSeekBar(int difficulty){
        difficultySelector.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                TextView difficultyTxt = (TextView)masterLayout.findViewById(R.id.difficultyFragmentDifficultyTxt);
                difficultyTxt.setText("Difficulty: "+ (seekBar.getProgress() + DIFFICULTY_MIN_VALUE)
                        + " x " + (seekBar.getProgress() + DIFFICULTY_MIN_VALUE));
                setBestTime(seekBar.getProgress());
                Button startGameBtn = (Button)masterLayout.findViewById(R.id.difficultyFragmentStartGameBtn);
                if (startedPuzzlesPositions[seekBar.getProgress() + DIFFICULTY_MIN_VALUE] == null){
                    startGameBtn.setText("Start Game");
                } else {
                    startGameBtn.setText("Continue");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        //Trigger on progress change
        difficultySelector.setProgress(difficultySelector.getMax());
        difficultySelector.setProgress(difficulty - DIFFICULTY_MIN_VALUE);
    }

    private void setBestTime(int difficulty){
        int bestTimeSec = bestSolveTimes[difficulty + DIFFICULTY_MIN_VALUE] % 60;
        int bestTimeMin = bestSolveTimes[difficulty + DIFFICULTY_MIN_VALUE] / 60;
        ((TextView)masterLayout.findViewById(R.id.difficultyFragmentBestTime))
                .setText("Best Time: " + bestTimeMin + ":" + bestTimeSec);
    }

    @Override
    public void onResume() {
        super.onResume();
        //loaderManager.restartLoader(TABLE_COMPLETED, null, this);
        //loaderManager.restartLoader(TABLE_STARTED, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String whereClause = "PUZZLE = '" + item + "'";
        CursorLoader cursorLoader = null;
        switch(id){
            case TABLE_COMPLETED:
                cursorLoader = new CursorLoader(getContext(),PuzzleContentProvider.CONTENT_URI_COMPLETED, null, whereClause, null, null);
                break;
            case TABLE_STARTED:
                cursorLoader = new CursorLoader(getContext(),PuzzleContentProvider.CONTENT_URI_STARTED, null, whereClause, null, null);
                break;
        }
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()){
            case TABLE_COMPLETED:
                bestSolveTimes = new int[11];
                while (data.moveToNext()) {
                    int difficulty = data.getInt(data.getColumnIndex("DIFFICULTY"));
                    int solveTime = data.getInt(data.getColumnIndex("SOLVETIME"));
                    bestSolveTimes[difficulty] = solveTime;
                }
                loadFinished++;
                break;
            case TABLE_STARTED:
                Log.d("startedtable", data.getCount() + "");
                startedPuzzlesPositions = new String[11];
                currentTime = new int[11];
                while (data.moveToNext()) {
                    int difficulty = data.getInt(data.getColumnIndex("DIFFICULTY"));
                    String positions = data.getString(data.getColumnIndex("POSITIONS"));
                    int time = data.getInt(data.getColumnIndex("SOLVETIME"));
                    startedPuzzlesPositions[difficulty] = positions;
                    currentTime[difficulty] = time;
                }
                loadFinished++;
                break;
        }
        //Both tables finished loading
        if (loadFinished > 1){
            initSeekBar(difficulty);
            loadFinished = 0;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
