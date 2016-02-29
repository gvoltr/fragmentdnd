package com.gvoltr.dragndrop.draganddroptest;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.gvoltr.dragndrop.IDragActivity;
import com.gvoltr.dragndrop.R;
import com.gvoltr.fragmentdnd.DropTarget;
import com.gvoltr.fragmentdnd.OnDropListener;

/**
 * Created by stanislavgavrosh on 12/11/15.
 */
public class TopFragment extends Fragment {

    private static final String TAG = TopFragment.class.getSimpleName();

    private static final String DRAG_TAG = "TOP_FRAGMENT_TEST_DRAGGABLE_VIEW";

    private ViewGroup rootView;
    private IDragActivity dragActivity;

    private OnDropListener dropListener = new OnDropListener() {
        @Override
        public void onDrop(View dropTarget, View draggedView, String tag, String placeTag, Object data) {
            Toast.makeText(getActivity(), "Dropped from \"" + placeTag + "\" with tag \"" + tag +
                    "\" with data \"" + data + "\"", Toast.LENGTH_LONG).show();
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_top, null);
        ((DropTarget) rootView.findViewById(R.id.top_drop_area)).setOnDropListener(dropListener);
        rootView.findViewById(R.id.top_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Click still work", Toast.LENGTH_SHORT).show();
            }
        });
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof IDragActivity) {
            dragActivity = (IDragActivity) activity;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (dragActivity != null) {
            dragActivity.addDraggableView(rootView.findViewById(R.id.top_button), DRAG_TAG, TAG, "Test data");
            dragActivity.addDropTarget((DropTarget) rootView.findViewById(R.id.top_drop_area), TAG);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (dragActivity != null) {
            dragActivity.removeAllItemsForTag(TAG);
        }
    }
}
