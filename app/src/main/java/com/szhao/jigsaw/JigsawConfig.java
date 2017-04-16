package com.szhao.jigsaw;

/**
 * Created by Owner on 4/10/2017.
 */

public class JigsawConfig{
    private int top;
    private int bot;
    private int left;
    private int right;

    public JigsawConfig(int top, int bot, int left, int right){
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

    @Override
    public String toString(){
        return ("top:" + top + " bot:" + bot + " left:" + left + " right:" + right);
    }
}
