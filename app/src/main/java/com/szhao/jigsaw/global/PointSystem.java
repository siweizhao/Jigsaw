package com.szhao.jigsaw.global;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.szhao.jigsaw.activities.dashboard.vh.CategoryViewHolder;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Owner on 9/6/2017.
 */

public class PointSystem {
    private static final String SHARED_PREF_POINTS = "points";
    private static final String SHARED_PREF_AVAILABLE_PUZZLES = "available_puzzles";
    private static final int DEFAULT_POINTS = 500;
    private static final PointSystem ourInstance = new PointSystem();
    private CategoryViewHolder categoryViewHolder;
    private int points;
    private ArrayList<String> availablePuzzles;
    private PointChangeListener listener;

    private PointSystem() {
    }

    public static PointSystem getInstance() {
        return ourInstance;
    }

    public void loadAvailablePuzzles(Context context) {
        availablePuzzles = new ArrayList<>();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String defaultPuzzles = "";
        String[] puzzles = sharedPref.getString(SHARED_PREF_AVAILABLE_PUZZLES, defaultPuzzles).split(",");
        Collections.addAll(availablePuzzles, puzzles);
    }

    public void setCategoryViewHolder(CategoryViewHolder vh) {
        categoryViewHolder = vh;
    }

    public void increaseCountVH() {
        categoryViewHolder.increaseCount();
    }

    public boolean isPuzzleUnlocked(String filePath) {
        return availablePuzzles.contains(filePath);
    }

    public int getNumAvailablePuzzlesByCategory(String category) {
        int numPuzzles = 0;
        for (String p : availablePuzzles) {
            if (p.contains(category))
                numPuzzles++;
        }
        return numPuzzles;
    }

    public void savePuzzle(Context context, String filePath) {
        availablePuzzles.add(filePath);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        StringBuilder availablePuzzlesStr = new StringBuilder();
        for (String p : availablePuzzles) {
            availablePuzzlesStr.append(p).append(",");
        }
        if (availablePuzzlesStr.length() > 0)
            availablePuzzlesStr.deleteCharAt(availablePuzzlesStr.length() - 1);

        editor.putString(SHARED_PREF_AVAILABLE_PUZZLES, availablePuzzlesStr.toString());
        editor.commit();
    }

    public void setListener(PointChangeListener listener) {
        this.listener = listener;
    }

    public void loadPoints(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        points = sharedPref.getInt(SHARED_PREF_POINTS, DEFAULT_POINTS);
        listener.pointChanged(points);
    }

    public void addPoints(Context context, int points) {
        this.points += points;
        Utility.setSharedPrefValues(context, SHARED_PREF_POINTS, this.points);
    }

    public void spendPoints(Context context, int points) {
        this.points -= points;
        listener.pointChanged(this.points);
        Utility.setSharedPrefValues(context, SHARED_PREF_POINTS, this.points);
    }

    public int getPoints() {
        return this.points;
    }

    public interface PointChangeListener {
        void pointChanged(int points);
    }
}
