package com.gvoltr.dragndrop.recyclerviewtest;

import android.app.Activity;
import android.app.Fragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gvoltr.dragndrop.IDragActivity;
import com.gvoltr.dragndrop.R;

/**
 * Created by stanislavgavrosh on 2/29/16.
 */
public class RecyclerViewFragment extends Fragment {

    private static final String TAG = RecyclerViewFragment.class.getSimpleName();

    private final String[] testData = new String[]{
            "Element 1",
            "Element 2",
            "Element 3",
            "Element 4",
            "Element 5",
            "Element 6",
            "Element 7",
            "Element 8",
            "Element 9",
            "Element 10",
            "Element 11",
            "Element 12",
            "Element 13",
            "Element 14",
            "Element 15",
            "Element 16",
            "Element 17",
            "Element 18",
    };

    private ViewGroup rootView;
    private IDragActivity dragActivity;
    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_recycler_view, null);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1, RecyclerView.HORIZONTAL, false));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2, RecyclerView.HORIZONTAL, false));
        }

        recyclerView.setAdapter(new TestAdapter(testData, dragActivity, TAG));
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
            recyclerView.addOnItemTouchListener(dragActivity.getViewTouchListenerForRecyclerView());
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (dragActivity != null) {
            recyclerView.removeOnItemTouchListener(dragActivity.getViewTouchListenerForRecyclerView());
            dragActivity.removeAllItemsForTag(TAG);
        }
    }
}
