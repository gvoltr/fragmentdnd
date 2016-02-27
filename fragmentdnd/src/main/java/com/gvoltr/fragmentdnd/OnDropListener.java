package com.gvoltr.fragmentdnd;

import android.view.View;

/**
 * Created by stanislavgavrosh on 12/15/15.
 */
public interface OnDropListener {

    void onDrop(View dropTarget, View draggedView, String tag, String placeTag, Object data);

}
