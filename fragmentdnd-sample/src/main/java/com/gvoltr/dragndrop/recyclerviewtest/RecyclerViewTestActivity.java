package com.gvoltr.dragndrop.recyclerviewtest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.gvoltr.dragndrop.IDragActivity;
import com.gvoltr.dragndrop.R;
import com.gvoltr.dragndrop.draganddroptest.MainActivity;
import com.gvoltr.fragmentdnd.DragLayer;
import com.gvoltr.fragmentdnd.DropTarget;

/**
 * Created by stanislavgavrosh on 2/29/16.
 */
public class RecyclerViewTestActivity extends Activity implements IDragActivity{

    private DragLayer dragLayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recyclerview_test);
        dragLayer = (DragLayer) findViewById(R.id.drag_layer);
        dragLayer.setAnimatedReturnDragViewEnabled(true);

        findViewById(R.id.open_base_test_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RecyclerViewTestActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    @Override
    public void addDraggableView(View v, String tag, String placeTag, Object data) {
        dragLayer.addDraggableView(v, tag, placeTag, data);
    }

    @Override
    public void addDropTarget(DropTarget target, String placeTag) {
        dragLayer.addDropTarget(target, placeTag);
    }

    @Override
    public void removeAllItemsForTag(String placeTag) {
        dragLayer.removeAllItemsForTag(placeTag);
    }

    @Override
    public void changeDataForDraggableView(View v, Object data) {
        dragLayer.changeDataForDraggableView(v, data);
    }

    @Override
    public RecyclerView.OnItemTouchListener getViewTouchListenerForRecyclerView() {
        return dragLayer.getItemTouchListenerForRecyclerView();
    }
}
