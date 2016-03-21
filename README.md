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

Now you need to create custom `View` that implements `DropTarget` interface, for example:

```java
public class DroppableRelativeLayout extends RelativeLayout implements DropTarget {

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

Add any `View` from `Fragment` as draggable `View` to `DragLayer`:
```java
    dragLayer.addDraggableView(v, tag, placeTag, data);
```

To avoid memory leaks you need to remove all views from `Fragment` onDestroy():
```java
    dragLayer.removeAllItemsForTag(placeTag);
```

You can change data object for `View` at any time:
```java
    dragLayer.changeDataForDraggableView(v, data);
```

## Enable RecyclerView 

Dragging elements from `RecyclerView` is possible, all you need is to add/remove `OnItemTouchListener` provided by `DragLayer` depends on lifecycle events:
```java
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
        }
    }
```

License
-------

    Copyright 2016 Stanislav Havrosh

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
