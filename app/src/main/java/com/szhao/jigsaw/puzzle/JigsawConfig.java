package com.szhao.jigsaw.puzzle;

import android.util.Log;
import java.util.Random;

/**
 * Created by Owner on 4/10/2017.
 */

public class JigsawConfig{
    private int top;
    private int bot;
    private int left;
    private int right;

    private JigsawConfig(int top, int bot, int left, int right){
        this.top = top;
        this.bot = bot;
        this.left = left;
        this.right = right;
    }

    public int getTop(){
        return top;
    }

    public int getBot(){
        return bot;
    }

    public int getLeft(){
        return left;
    }

    public int getRight(){
        return right;
    }

    // -1 represents indent, 1 represents outdent, 0 represents a flat surface
    // the sides of each jigsaw piece will be represented by this
    public static JigsawConfig[][] generateJigsawConfig(int rows, int columns){
        JigsawConfig[][] config = new JigsawConfig[rows][columns];
        Random random = new Random();
        int top, bot, left, right;

        for(int j = 0; j < columns; j ++) {
            for (int i = 0; i < rows; i++){
                top = random.nextInt(2) * 2 - 1;
                right = random.nextInt(2) * 2 - 1;
                bot = random.nextInt(2) * 2 - 1;
                left = random.nextInt(2) * 2 - 1;

                //Check adjacent pieces
                if (i > 0)
                    left = -config[i - 1][j].getRight();
                if (j > 0)
                    top = -config[i][j - 1].getBot();

                if (i == 0)
                    left = 0;
                else if (i == rows - 1)
                    right = 0;

                if (j == 0)
                    top = 0;
                else if (j == columns - 1)
                    bot = 0;
                config[i][j] = new JigsawConfig(top,bot,left,right);
            }
        }
        return config;
    }

    @Override
    public String toString(){
        return ("top:" + top + " bot:" + bot + " left:" + left + " right:" + right + "\n");
    }

    public static void printConfig(JigsawConfig[][] config){
        for (int j = 0; j < config.length; j++){
            for (int i = 0; i < config[0].length; i++){
                Log.d("jigsaw config", config[i][j] + "         row:" + i + " col:" + j);
            }
        }


    }

}
