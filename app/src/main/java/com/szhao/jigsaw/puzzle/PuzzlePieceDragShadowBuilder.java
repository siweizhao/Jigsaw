package com.szhao.jigsaw.puzzle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * Created by Owner on 7/30/2017.
 */

public class PuzzlePieceDragShadowBuilder extends View.DragShadowBuilder {
    private Drawable shadow;

    public PuzzlePieceDragShadowBuilder(Context context, Bitmap bitmap){
        super();
        shadow = new BitmapDrawable(context.getResources(), bitmap);
        shadow.setBounds(0, 0, shadow.getMinimumWidth(), shadow.getMinimumHeight());
    }

    @Override
    public void onDrawShadow(Canvas canvas) {
        shadow.draw(canvas);
    }

    @Override
    public void onProvideShadowMetrics(Point shadowSize, Point shadowTouchPoint) {
        shadowSize.x = shadow.getMinimumWidth();
        shadowSize.y = shadow.getMinimumHeight();

        shadowTouchPoint.x = shadowSize.x / 2;
        shadowTouchPoint.y = shadowSize.y / 2;
    }


}
