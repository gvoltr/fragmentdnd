# fragmentdnd

Library for Android that makes drag and drop between fragments possible. 

## Getting started

In your `build.gradle`:

```gradle
 dependencies {
    compile 'com.gvoltr.fragmentdnd:fragment-dnd:1.0'
 }
```

In your `activity_layout` add DragLayer as last view :
```xml
<!-- All other views-->
...
<com.gvoltr.fragmentdnd.DragLayer
        android:id="@+id/drag_layer"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
```

Now you need to create custom `View` that implements `IDropTarget` interface, for example:

```java
public class DroppableRelativeLayout extends RelativeLayout implements IDropTarget {

    private OnDropListener dropListener;

    @Override
    public void setOnDropListener(OnDropListener listener) {
        dropListener = listener;
    }

    @Override
    public void onDrop(View v, String tag, String placeTag, Object dragInfo) {
        dropListener.onDrop(this, v, tag, placeTag, dragInfo);
    }

    @Override
    public void onDragEnter(View v, String tag, String placeTag, Object dragInfo) {

    }

    @Override
    public void onDragOver(View v, String tag, String placeTag, Object dragInfo) {

    }

    @Override
    public void onDragExit(View v, String tag, String placeTag, Object dragInfo) {

    }

    @Override
    public boolean acceptDrop(View v, int x, int y, String tag, String placeTag, Object dragInfo) {
        return getVisibility() == View.VISIBLE;
    }
```

Add this view to any `Fragment`, set `OnDropListener` and add it to `DragLayer`:
```java
    dragLayer.addDropTarget(dropTarget, placeTag);
```



