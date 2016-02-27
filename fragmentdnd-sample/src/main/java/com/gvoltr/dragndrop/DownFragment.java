package com.gvoltr.dragndrop;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by stanislavgavrosh on 12/11/15.
 */
public class DownFragment extends Fragment {

    private final static String TAG = DownFragment.class.getSimpleName();

    private static final String DRAG_TAG = "BOTTOM_FRAGMENT_TEST_COMPLEX_VIEW";

    private ViewGroup rootView;
    private IDragActivity dragActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_down, null);
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
        if (dragActivity != null){
            dragActivity.addDraggableView(rootView.findViewById(R.id.draggable_layout),
                    DRAG_TAG, TAG, "Test data for complex view");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (dragActivity != null){
            dragActivity.removeAllItemsForTag(TAG);
        }
    }
}
