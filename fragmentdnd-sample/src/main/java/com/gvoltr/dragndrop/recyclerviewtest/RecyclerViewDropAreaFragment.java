package com.gvoltr.dragndrop.recyclerviewtest;

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
 * Created by stanislavgavrosh on 2/29/16.
 */
public class RecyclerViewDropAreaFragment extends Fragment {

    private static final String TAG = RecyclerViewDropAreaFragment.class.getSimpleName();

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
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_drop_area, null);
        ((DropTarget) rootView.findViewById(R.id.top_drop_area)).setOnDropListener(dropListener);
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
