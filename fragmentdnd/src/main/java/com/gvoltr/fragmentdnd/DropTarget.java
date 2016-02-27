package com.gvoltr.fragmentdnd;

import android.view.View;

/**
 * Created by stanislavgavrosh on 12/14/15.
 */
public interface DropTarget {

    void setOnDropListener(OnDropListener listener);

    /**
     * Handle an object being dropped on the DropTarget
     *
     * @param v View where the drag started
     * @param dragInfo Data associated with the object being dragged
     *
     */
    void onDrop(View v, String tag, String placeTag, Object dragInfo);

    void onDragEnter(View v, String tag, String placeTag, Object dragInfo);
    void onDragOver(View v, String tag, String placeTag, Object dragInfo);
    void onDragExit(View v, String tag, String placeTag, Object dragInfo);

    /**
     * Check if a drop action can occur at, or near, the requested location.
     * This may be called repeatedly during a drag, so any calls should return
     * quickly.
     *
     * @param v View where the drag started
     * @param x X coordinate of the drop location
     * @param y Y coordinate of the drop location
     * @param dragInfo Data associated with the object being dragged
     * @return True if the drop will be accepted, false otherwise.
     */
    boolean acceptDrop(View v, int x, int y, String tag, String placeTag, Object dragInfo);

}
