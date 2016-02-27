package com.gvoltr.fragmentdnd;

/**
 * Created by stanislavgavrosh on 12/15/15.
 */
public class DropItem {

    private DropTarget target;
    /**
     * tag with view place description
     */
    private String placeTag;

    public DropItem(DropTarget target, String placeTag) {
        this.target = target;
        this.placeTag = placeTag;
    }

    public DropTarget getTarget() {
        return target;
    }

    public void setTarget(DropTarget target) {
        this.target = target;
    }

    public String getPlaceTag() {
        return placeTag;
    }

    public void setPlaceTag(String placeTag) {
        this.placeTag = placeTag;
    }
}
