package com.szhao.jigsaw.global;

import com.szhao.jigsaw.R;

/**
 * Created by Owner on 9/15/2017.
 */

public class Constants {
    public static final String DOWNLOADED_PUZZLES_DIR = "DL";
    public static final String CUSTOM_PUZZLES_DIR = "custom_puzzles";
    public static final String ALL_PUZZLES_DIR = "Puzzles";
    public static final String DB_PUZZLE = "PUZZLE";
    public static final String DB_DIFFICULTY = "DIFFICULTY";
    public static final String DB_SOLVETIME = "SOLVETIME";
    public static final String DB_POSITIONS = "POSITIONS";
    public static final String INTENT_FILE_PATH = "filePath";
    public static final String INTENT_DIFFICULTY = "difficulty";
    public static final String INTENT_POSITIONS = "positions";
    public static final String INTENT_CURR_TIME = "currTime";
    public static final String SHARED_PREF_BG = "bg";

    public static final float GOLDEN_RATIO = 1.618f;
    public static final float CATEGORY_RECYCLER_HEIGHT = 0.3f;

    public static final int UNLOCKED_ALPHA = 255;
    public static final int LOCKED_ALPHA = 155;
    public static final int CATEGORY_VH_MARGIN = 100;
    public static final int CONTENT_VH_MARGIN = 50;

    public static final int DIFFICULTY_MIN_VALUE = 2;
    public static final int TABLE_COMPLETED = 1;
    public static final int TABLE_STARTED = 2;
    public static final int UNLOCK_COST = 100;
    public static final int PICK_IMAGE = 1;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 2;
    public static final int PUZZLE_PIECE_ANIMATION_DURATION = 300;
    public static final int DEFAULT_BACKGROUND = R.drawable.bg_1;
    public static int[] backgroundIds = new int[]{
            R.drawable.bg_1,
            R.drawable.bg_2,
            R.drawable.bg_3,
            R.drawable.bg_4,
            R.drawable.bg_5,
            R.drawable.bg_6,
            R.drawable.bg_7,
            R.drawable.bg_8,
            R.drawable.bg_9,
            R.drawable.bg_10,
            R.drawable.bg_11,
            R.drawable.bg_12,
            R.drawable.bg_13,
            R.drawable.bg_14,
            R.drawable.bg_15,
            R.drawable.bg_16,
            R.drawable.bg_17,
            R.drawable.bg_18,
            R.drawable.bg_19,
            R.drawable.bg_20
    };
}
