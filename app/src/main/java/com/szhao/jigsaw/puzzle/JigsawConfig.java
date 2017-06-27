package com.szhao.jigsaw.puzzle;

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
    public static JigsawConfig[][] generateJigsawConfig(int difficulty){
        JigsawConfig[][] config = new JigsawConfig[difficulty][difficulty];
        Random random = new Random();
        int top, bot, left, right;

        for(int j = 0; j < difficulty; j ++) {
            for (int i = 0; i < difficulty; i++){
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
                else if (i == difficulty - 1)
                    right = 0;

                if (j == 0)
                    top = 0;
                else if (j == difficulty - 1)
                    bot = 0;
                config[i][j] = new JigsawConfig(top,bot,left,right);
            }
        }
        return config;
    }

    @Override
    public String toString(){
        return ("top:" + top + " bot:" + bot + " left:" + left + " right:" + right);
    }
}
