package com.szhao.jigsaw.activities.dashboard.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.crash.FirebaseCrash;
import com.szhao.jigsaw.R;
import com.szhao.jigsaw.activities.dashboard.vh.ContentViewHolder;
import com.szhao.jigsaw.global.Constants;
import com.szhao.jigsaw.global.PointSystem;
import com.szhao.jigsaw.global.Utility;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Owner on 8/24/2017.
 */

public class ContentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<String> puzzles;
    private ItemSelectListener listener;
    private Cursor startedPuzzlesCursor;
    private ArrayList<Integer> startedDifficulties;

    public ContentRecyclerViewAdapter(Context context, Cursor cursor){
        this.context = context;
        this.startedPuzzlesCursor = cursor;
        puzzles = new ArrayList<>();
        startedDifficulties = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.cv_content_image, parent, false);
        final ContentViewHolder vh = new ContentViewHolder(context, v);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null){
                    if (vh.getLockStatus()) {
                        //Need to unlock puzzle
                        showUnlockDialog(vh);
                    } else {
                        int currAdapterPosition = vh.getAdapterPosition();
                        int difficulty = startedDifficulties.size() == 0 ? 2 : startedDifficulties.get(currAdapterPosition);
                        listener.onClick(puzzles.get(currAdapterPosition), difficulty);
                    }
                }
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ContentViewHolder vh = (ContentViewHolder)holder;
        vh.setPuzzleImage(puzzles.get(position));
        //If the puzzle was downloaded it needs to be unlocked by paying coins
        File downloadedDir = context.getDir(Constants.DOWNLOADED_PUZZLES_DIR, Context.MODE_PRIVATE);
        if (puzzles.get(position).contains(downloadedDir.getName()) && !PointSystem.getInstance().isPuzzleUnlocked(puzzles.get(position))) {
            vh.setLock();
        } else {
            vh.setUnlock();
        }
    }

    public void setPuzzles(String category){
        puzzles.clear();
        startedDifficulties.clear();
        if (category.equals(context.getString(R.string.started))){
            setStartPuzzles();
        } else {
            try {
                String[] puzzleFilePaths = context.getAssets().list(category);
                for (String path : puzzleFilePaths) {
                    puzzles.add("android_asset/" + category + "/" + path);
                }

                //DLed puzzle
                if (puzzleFilePaths.length == 0) {
                    setDLPuzzles(category);
                }
            } catch (IOException e) {
                FirebaseCrash.logcat(Log.ERROR, "Load Puzzles", "Error loading puzzles from assets");
                FirebaseCrash.report(e);
            }
        }
        notifyDataSetChanged();
    }

    public void setCustomPuzzles(){
        puzzles.clear();
        File dir = context.getDir(Constants.CUSTOM_PUZZLES_DIR, Context.MODE_PRIVATE);
        File[] puzzleFilePaths = dir.listFiles();
        for (File filePath : puzzleFilePaths) {
            puzzles.add(filePath.getAbsolutePath());
        }
        Collections.reverse(puzzles);
        notifyDataSetChanged();
    }

    private void setDLPuzzles(String category) {
        puzzles.clear();
        File dir = context.getDir(Constants.DOWNLOADED_PUZZLES_DIR, Context.MODE_PRIVATE);
        File categoryDir = new File(dir, category);
        File[] puzzleFilePaths = categoryDir.listFiles();
        for (File filePath : puzzleFilePaths){
            puzzles.add(filePath.getAbsolutePath());
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return puzzles.size();
    }

    public void setListener(ItemSelectListener listener){
        this.listener = listener;
    }

    private void showUnlockDialog(final ContentViewHolder vh) {
        MaterialDialog materialDialog = new MaterialDialog.Builder(context)
                .title(R.string.unlock)
                .titleGravity(GravityEnum.CENTER)
                .customView(R.layout.dialog_unlock, true)
                .positiveText(R.string.accept)
                .negativeText(R.string.decline)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (PointSystem.getInstance().getPoints() >= Constants.UNLOCK_COST) {
                            PointSystem.getInstance().spendPoints(context, Constants.UNLOCK_COST);
                            PointSystem.getInstance().savePuzzle(context, puzzles.get(vh.getAdapterPosition()));
                            vh.setUnlock();
                            PointSystem.getInstance().increaseCountVH();
                        } else {
                            Toast.makeText(context, context.getString(R.string.insufficent_funds), Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .dismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        Utility.startImmersiveMode(context);
                    }
                })
                .build();
        View customView = materialDialog.getCustomView();
        ((TextView)customView.findViewById(R.id.dialogUnlockCostTxt)).setText(String.valueOf(Constants.UNLOCK_COST));
        materialDialog.show();
    }

    private void setStartPuzzles() {
        startedPuzzlesCursor.moveToPosition(-1);
        while (startedPuzzlesCursor.moveToNext()) {
            puzzles.add(startedPuzzlesCursor.getString(startedPuzzlesCursor.getColumnIndex(Constants.DB_PUZZLE)));
            startedDifficulties.add(startedPuzzlesCursor.getInt(startedPuzzlesCursor.getColumnIndex(Constants.DB_DIFFICULTY)));
        }
        Collections.reverse(puzzles);
        Collections.reverse(startedDifficulties);
    }
}
