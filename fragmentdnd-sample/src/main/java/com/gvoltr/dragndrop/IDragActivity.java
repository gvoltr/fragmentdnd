package com.gvoltr.dragndrop;

import android.view.View;

import com.gvoltr.fragmentdnd.DropTarget;

/**
 * Created by stanislavgavrosh on 12/14/15.
 */
public interface IDragActivity {

    void addDraggableView(View v, String tag, String placeTag, Object data);

    void addDropTarget(DropTarget target, String placeTag);

    void removeAllItemsForTag(String placeTag);

    void changeDataForDraggableView(View v, Object data);

}
