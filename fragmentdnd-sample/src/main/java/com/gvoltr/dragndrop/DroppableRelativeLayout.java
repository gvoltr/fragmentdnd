package com.gvoltr.dragndrop;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.gvoltr.fragmentdnd.DropTarget;
import com.gvoltr.fragmentdnd.OnDropListener;

/**
 * Created by stanislavgavrosh on 12/15/15.
 */
public class DroppableRelativeLayout extends RelativeLayout  implements DropTarget {

    private OnDropListener mDropListener;

    public DroppableRelativeLayout(Context context) {
        super(context);
    }

    public DroppableRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DroppableRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setOnDropListener(OnDropListener listener) {
        mDropListener = listener;
    }

    @Override
    public void onDrop(View v, String tag, String placeTag, Object dragInfo) {
        mDropListener.onDrop(this, v, tag, placeTag, dragInfo);
    }

    @Override
    public void onDragEnter(View v, String tag, String placeTag, Object dragInfo) {
        Animations.sizeUpAnimation(this);
    }

    @Override
    public void onDragOver(View v, String tag, String placeTag, Object dragInfo) {

    }

    @Override
    public void onDragExit(View v, String tag, String placeTag, Object dragInfo) {
        Animations.sizeToNormalAnimation(this);
    }

    @Override
    public boolean acceptDrop(View v, int x, int y, String tag, String placeTag, Object dragInfo) {
        return getVisibility() == View.VISIBLE;
    }

}
