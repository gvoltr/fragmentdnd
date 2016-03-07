package com.gvoltr.dragndrop.recyclerviewtest;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gvoltr.dragndrop.IDragActivity;
import com.gvoltr.dragndrop.R;

/**
 * Created by stanislavgavrosh on 3/8/16.
 */
public class TestAdapter extends RecyclerView.Adapter<TestAdapter.ViewHolder> {

    private String[] values;
    private IDragActivity dragActivity;
    private String placeTag;

    public TestAdapter(String[] values, IDragActivity dragActivity, String placeTag){
        this.values = values;
        this.dragActivity = dragActivity;
        this.placeTag = placeTag;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View testView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.test_list_element, parent, false);

        ViewHolder holder = new ViewHolder(testView);
        dragActivity.addDraggableView(testView, "RecyclerView test element", placeTag, "");
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.elementText.setText(values[position]);

        dragActivity.changeDataForDraggableView(holder.itemView, values[position]);
    }

    @Override
    public int getItemCount() {
        return values.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView elementText;

        public ViewHolder(View itemView) {
            super(itemView);
            elementText = (TextView) itemView.findViewById(R.id.list_element_text);
        }
    }
}
