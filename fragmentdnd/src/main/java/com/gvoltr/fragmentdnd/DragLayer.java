package com.gvoltr.fragmentdnd;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by stanislavgavrosh on 12/11/15.
 */
public class DragLayer extends FrameLayout {

    public static final double ANIMATION_SPEED_BASE = 1.2d;
    public static final double ANIMATION_SPEED_SLOW = 0.8d;
    public static final double ANIMATION_SPEED_FAST = 1.6d;

    private float[] dragCenter = new float[2];
    private final int[] dropCoordinates = new int[2];

    private InputMethodManager inputMethodManager;

    /**
     * The bitmap that is currently being dragged
     */
    private Bitmap dragBitmap = null;
    private View originator;
    private int bitmapOffsetX;
    private int bitmapOffsetY;

    /**
     * Utility rectangle
     */
    private Rect dragRect = new Rect();

    private final Rect rect = new Rect();

    private float lastMotionX;
    private float lastMotionY;
    private boolean shouldDrop;
    private boolean dropAssepted;
    private boolean dragging = false;
    private boolean animating = false;
    private Paint dragPaint;
    private boolean animatedReturnDragViewEnabled;
    private double animationSpeed = ANIMATION_SPEED_BASE;

    private DragItem currentDragItem;
    ArrayList<DragItem> draggableItems = new ArrayList<>();
    ArrayList<DropItem> dropTargetItems = new ArrayList<>();

    private DropTarget lastDropTarget;
    private ImageView viewForAnimations;

    private RecyclerView.OnItemTouchListener recyclerViewTouchListener;

    private OnTouchListener draggableItemTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (dragging) {
                dispatchTouchEvent(event);
            } else if (shouldStartDrag(event)) {
                startDrag(v);
            }
            return dragging;
        }
    };

    public DragLayer(Context context) {
        super(context);
    }

    public DragLayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DragLayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Add new draggable view, from now it can be dragged
     *
     * @param v        view that we want to drag
     * @param tag      tag for describe this view or content, if needed
     * @param placeTag Tag for associating view with place, e.g. Fragment name.
     *                 This tag will be used for {@link #removeAllItemsForTag(String)} later
     * @param data     any data that will be received on drop
     */
    public void addDraggableView(View v, String tag, String placeTag, Object data) {
        v.setOnTouchListener(draggableItemTouchListener);
        draggableItems.add(new DragItem(v, tag, placeTag, data));
    }

    /**
     * This listener used for proper work of drag from RecyclerView
     *
     * @return recyclerViewTouchListener instance;
     */
    public RecyclerView.OnItemTouchListener getItemTouchListenerForRecyclerView() {
        if (recyclerViewTouchListener == null) {
            recyclerViewTouchListener = createItemTouchListenerForRecyclerView();
        }
        return recyclerViewTouchListener;
    }

    private RecyclerView.OnItemTouchListener createItemTouchListenerForRecyclerView() {
        return new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                return dragging;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
                if (dragging) {
                    dispatchTouchEvent(e);
                }
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            }
        };
    }

    /**
     * Add new drop target, from now it will accept drops
     *
     * @param target   Any View, that implement DropTarget interface
     * @param placeTag Tag for associating view with place, e.g. Fragment name.
     *                 This tag will be used for {@link #removeAllItemsForTag(String)} later
     */
    public void addDropTarget(DropTarget target, String placeTag) {
        if (!(target instanceof View)) {
            throw new IllegalArgumentException("DropTarget must be a View");
        }

        dropTargetItems.add(new DropItem(target, placeTag));
    }

    /**
     * Remove all Views and data which have been added with the specified tag
     *
     * @param placeTag Tag for associating view with place, e.g. Fragment name
     */
    public void removeAllItemsForTag(String placeTag) {
        removeDraggableViewsForTag(placeTag);
        removeDropTargetsForTag(placeTag);
    }

    private void removeDraggableViewsForTag(String placeTag) {
        ArrayList<DragItem> itemsToDelete = new ArrayList<>();
        for (DragItem item : draggableItems) {
            if (item.getPlaceTag().equals(placeTag)) itemsToDelete.add(item);
        }
        draggableItems.removeAll(itemsToDelete);
    }

    private void removeDropTargetsForTag(String placeTag) {
        ArrayList<DropItem> itemsToDelete = new ArrayList<>();
        for (DropItem item : dropTargetItems) {
            if (item.getPlaceTag().equals(placeTag)) itemsToDelete.add(item);
        }
        dropTargetItems.removeAll(itemsToDelete);
    }

    /**
     * Sometimes data for reusable views can be changed, so it can be updated with this method
     *
     * @param v    View that now displays some new data
     * @param data for update
     */
    public void changeDataForDraggableView(View v, Object data) {
        getDragItemForView(v).setData(data);
    }

    /**
     * Enable or disable option, that will animate dragged view back to start position in case of
     * unsuccessful drop
     *
     * @param enabled
     */
    public void setAnimatedReturnDragViewEnabled(boolean enabled) {
        animatedReturnDragViewEnabled = enabled;
        if (enabled && viewForAnimations == null) {
            viewForAnimations = new ImageView(getContext());
            viewForAnimations.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            viewForAnimations.setVisibility(GONE);
            addView(viewForAnimations);
        }
    }

    /**
     * Set animation speed for animation {@link #setAnimatedReturnDragViewEnabled(boolean)}
     * the duration of the animation is calculated as (range between start and end / animationSpeed)
     *
     * @param animationSpeed
     */
    public void setAnimationSpeed(double animationSpeed) {
        this.animationSpeed = animationSpeed;
    }

    private void startDrag(View v) {
        // Hide soft keyboard, if visible
        if (inputMethodManager == null) {
            inputMethodManager = (InputMethodManager)
                    getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        }
        inputMethodManager.hideSoftInputFromWindow(getWindowToken(), 0);

        v.clearFocus();
        v.setPressed(false);

        boolean willNotCache = v.willNotCacheDrawing();
        v.setWillNotCacheDrawing(false);

        // Reset the drawing cache background color to fully transparent
        // for the duration of this operation
        int color = v.getDrawingCacheBackgroundColor();
        v.setDrawingCacheBackgroundColor(0);
        if (color != 0) {
            v.destroyDrawingCache();
        }

        v.buildDrawingCache();

        Bitmap viewBitmap = v.getDrawingCache();
        int width = viewBitmap.getWidth();
        int height = viewBitmap.getHeight();

        dragBitmap = Bitmap.createBitmap(viewBitmap, 0, 0, width, height, null, true);
        v.destroyDrawingCache();
        v.setWillNotCacheDrawing(willNotCache);
        v.setDrawingCacheBackgroundColor(color);

        final Bitmap dragBitmap = this.dragBitmap;

        final int[] locationOnScreen = new int[2];
        getLocationOnScreen(locationOnScreen);
        bitmapOffsetX = (dragBitmap.getWidth()) / 2 + locationOnScreen[0];
        bitmapOffsetY = (dragBitmap.getHeight()) / 2 + locationOnScreen[1];

        v.setVisibility(GONE);

        dragPaint = null;
        dragging = true;
        shouldDrop = true;
        dropAssepted = false;
        originator = v;
        currentDragItem = getDragItemForView(v);
        invalidate();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return dragging || super.dispatchKeyEvent(event);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (dragging && !animating && dragBitmap != null) {
            // Draw actual icon being dragged
            canvas.drawBitmap(dragBitmap,
                    lastMotionX - bitmapOffsetX,
                    lastMotionY - bitmapOffsetY, dragPaint);
        }
    }

    private void endDrag() {
        if (dragging) {
            restoreOriginatorVisibility();
            invalidate();
        }
    }

    private void restoreOriginatorVisibility() {
        if (originator != null) {
            if (animatedReturnDragViewEnabled && !dropAssepted) {
                moveOriginatorToStartPositionWithAnimation();
            } else {
                originator.setVisibility(VISIBLE);
                endDragClear();
            }
        }
    }

    private void moveOriginatorToStartPositionWithAnimation() {
        animating = true;

        int[] originatorLocation = new int[2];
        int[] dragLayerLocation = new int[2];
        originator.getLocationOnScreen(originatorLocation);
        getLocationOnScreen(dragLayerLocation);

        viewForAnimations.setImageBitmap(dragBitmap);

        Point dragViewEndCoordinates = new Point((int)(lastMotionX - dragLayerLocation[0] - dragBitmap.getWidth() / 2),
                (int)(lastMotionY - dragLayerLocation[1] - dragBitmap.getHeight() / 2));
        Point dragViewStartCoordinates = new Point(originatorLocation[0] - dragLayerLocation[0],
                originatorLocation[1] - dragLayerLocation[1]);

        final double rangeFromEndToStart = getTwoPointsRange(dragViewEndCoordinates, dragViewStartCoordinates);

        viewForAnimations.setX(dragViewEndCoordinates.x);
        viewForAnimations.setY(dragViewEndCoordinates.y);

        ObjectAnimator translateX = ObjectAnimator.ofFloat(viewForAnimations, "translationX",
                dragViewStartCoordinates.x);
        ObjectAnimator translateY = ObjectAnimator.ofFloat(viewForAnimations, "translationY",
                dragViewStartCoordinates.y);

        AnimatorSet set = new AnimatorSet();
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                viewForAnimations.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                animating = false;
                viewForAnimations.setVisibility(GONE);
                originator.setVisibility(VISIBLE);
                endDragClear();
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
        set.playTogether(translateX, translateY);
        set.setDuration((long)(rangeFromEndToStart / animationSpeed));
        set.start();
    }

    private void endDragClear() {
        dragging = false;
        currentDragItem = null;
        originator = null;
        lastDropTarget = null;
        if (dragBitmap != null) {
            dragBitmap.recycle();
        }
    }

    private float startX;
    private float startY;
    private final float START_DRAG_DELTA = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
            10, getResources().getDisplayMetrics());

    private boolean shouldStartDrag(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getRawX();
                startY = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                return getTwoPointsRange(new Point((int) startX, (int) startY),
                        new Point((int) event.getRawX(), (int) event.getRawY())) > START_DRAG_DELTA;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                startX = 0;
                startY = 0;
                break;
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        final float x = ev.getRawX();
        final float y = ev.getRawY();
        switch (action) {
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_DOWN:
                // stop dragging if new touch
                if (dragging) {
                    endDrag();
                    return false;
                }
                // Remember location of down touch
                lastMotionX = x;
                lastMotionY = y;
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (shouldDrop) {
                    dropAssepted = drop(x, y);
                    shouldDrop = false;
                }
                endDrag();
                break;
        }
        return dragging;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!dragging || animating) {
            return false;
        }

        final int action = ev.getAction();
        final float x = ev.getRawX();
        final float y = ev.getRawY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                // stop dragging if new touch
                if (dragging) {
                    endDrag();
                    return false;
                }
                // Remember where the motion event started
                lastMotionX = x;
                lastMotionY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                final int offsetX = bitmapOffsetX;
                final int offsetY = bitmapOffsetY;

                int left = (int) (lastMotionX - offsetX);
                int top = (int) (lastMotionY - offsetY);

                final Bitmap dragBitmap = this.dragBitmap;
                final int width = dragBitmap.getWidth();
                final int height = dragBitmap.getHeight();

                final Rect rect = this.rect;
                rect.set(left - 1, top - 1, left + width + 1, top + height + 1);

                lastMotionX = x;
                lastMotionY = y;

                left = (int) (x - offsetX);
                top = (int) (y - offsetY);

                // Invalidate current icon position
                rect.union(left - 1, top - 1, left + width + 1, top + height + 1);

                dragCenter[0] = rect.centerX();
                dragCenter[1] = rect.centerY();

                final int[] coordinates = dropCoordinates;
                DropTarget dropTarget = findDropTarget((int) x, (int) y, coordinates);
                if (dropTarget != null) {
                    if (lastDropTarget == dropTarget) {
                        dropTarget.onDragOver(originator, currentDragItem.getTag(),
                                currentDragItem.getPlaceTag(), currentDragItem.getData());
                    } else {
                        if (lastDropTarget != null) {
                            lastDropTarget.onDragExit(originator, currentDragItem.getTag(),
                                    currentDragItem.getPlaceTag(), currentDragItem.getData());
                        }
                        dropTarget.onDragEnter(originator, currentDragItem.getTag(),
                                currentDragItem.getPlaceTag(), currentDragItem.getData());
                    }
                } else {
                    if (lastDropTarget != null) {
                        lastDropTarget.onDragExit(originator, currentDragItem.getTag(),
                                currentDragItem.getPlaceTag(), currentDragItem.getData());
                    }
                }

                invalidate(rect);

                lastDropTarget = dropTarget;
                break;

            case MotionEvent.ACTION_UP:
                if (shouldDrop) {
                    dropAssepted = drop(x, y);
                    shouldDrop = false;
                }
                endDrag();
                break;
        }
        return true;
    }

    private boolean drop(float x, float y) {
        invalidate();
        final int[] coordinates = dropCoordinates;
        DropTarget dropTarget = findDropTarget((int) x, (int) y, coordinates);
        if (dropTarget != null) {
            dropTarget.onDragExit(originator, currentDragItem.getTag(),
                    currentDragItem.getPlaceTag(), currentDragItem.getData());
            if (dropTarget.acceptDrop(originator, coordinates[0], coordinates[1],
                    currentDragItem.getTag(), currentDragItem.getPlaceTag(), currentDragItem.getData())) {
                dropTarget.onDrop(originator, currentDragItem.getTag(),
                        currentDragItem.getPlaceTag(), currentDragItem.getData());
                return true;
            }
        }
        return false;
    }

    private DropTarget findDropTarget(int x, int y, int[] dropCoordinates) {
        final Rect r = dragRect;
        int[] targetViewCoordinates = new int[2];

        for (DropItem dropItem : dropTargetItems) {
            final View targetView = (View) dropItem.getTarget();
            if (targetView.getVisibility() == VISIBLE) {
                targetView.getLocationOnScreen(targetViewCoordinates);
                r.set(targetViewCoordinates[0], targetViewCoordinates[1],
                        targetViewCoordinates[0] + targetView.getWidth(),
                        targetViewCoordinates[1] + targetView.getHeight());
                if (r.contains(x, y)) {
                    dropCoordinates[0] = x;
                    dropCoordinates[1] = y;
                    return dropItem.getTarget();
                }
            }
        }
        return null;
    }

    private DragItem getDragItemForView(View v) {
        for (DragItem item : draggableItems) {
            if (item.getView() == v) return item;
        }
        return null;
    }

    private double getTwoPointsRange(Point point1, Point point2) {
        return Math.sqrt(Math.pow((point2.x - point1.x), 2) + Math.pow((point2.y - point1.y), 2));
    }
}
