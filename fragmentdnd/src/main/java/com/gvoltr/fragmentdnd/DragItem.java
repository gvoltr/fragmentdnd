package com.gvoltr.fragmentdnd;

import android.view.View;

/**
 * Created by stanislavgavrosh on 12/15/15.
 */
public class DragItem {

    private View view;
    /**
     * tag for recognizing element context
     */
    private String tag;
    /**
     * tag with view place description
     */
    private String placeTag;
    private Object data;

    public DragItem(View view, String tag, String placeTag, Object data) {
        this.view = view;
        this.tag = tag;
        this.placeTag = placeTag;
        this.data = data;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getPlaceTag() {
        return placeTag;
    }

    public void setPlaceTag(String placeTag) {
        this.placeTag = placeTag;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
