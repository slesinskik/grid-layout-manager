package com.kamilslesinski.gridlayoutmanager;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;

import hugo.weaving.DebugLog;

/**
 * A layout manager that allows to display views in a grid layout.
 * This layout can be scrolled both horizontally and vertically and the position is defined by {@link Item}, not by the layout manager.
 * Views ({@link Item}) belong to strips and can be displayed in two orientations: {@link GridLayoutManager#ORIENTATION_HORIZONTAL} or {@link GridLayoutManager#ORIENTATION_VERTICAL}.
 * This layout manager, for performance reasons, assumes that {@link Item GridItems} provided by {@link DataSource} are ordered and do not overlap.
 *
 * An example use of this layout is a TV Guide.
 *
 * @author Kamil Ślesiński (slesinskik@gmail.com)
 * @since 2016/04/07
 */
// TODO: add smooth scroller support
public class GridLayoutManager extends RecyclerView.LayoutManager {

    public static final int ORIENTATION_HORIZONTAL = 0;
    public static final int ORIENTATION_VERTICAL = 1;

    public static final int STRIP_LENGTH_DEFAULT = 2000;
    public static final int STRIP_LENGTH_AUTO = -1;
    public static final int DEFAULT_STRIP_SIZE = 72;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({ORIENTATION_HORIZONTAL, ORIENTATION_VERTICAL})
    public @interface Orientation {}

    private static final String TAG = GridLayoutManager.class.getSimpleName();

    private static final boolean DEBUG = false;

    /* Flag to force current scroll offsets to be ignored on re-layout. Set it to true in, i.e. scrollToPosition, etc */
    private boolean mForceClearOffsets;

    private boolean mIsHorizontal = true;
    private boolean mDynamicLength = false;
    /**
     * Maximum strip length in pixels.
     */
    private int mStripLength = STRIP_LENGTH_DEFAULT;
    /**
     * Strip size (either height or width) in pixels.
     */
    private int mStripSize = DEFAULT_STRIP_SIZE;
    /**
     * Screen density. Used to translate dp to pixels.
     */
    private final float mDensity;

    /**
     * Shows how many strips can fit on the view
     */
    int mVisibleStripCount;
    int mTotalStripSize;
    private int mOffsetX = 0;
    private int mOffsetY = 0;

    private DataSource<?> mDataSource = null;
    private int[] coordinates = new int[2];

    private ArrayDeque<Strip> mStrips = new ArrayDeque<>();
    private SparseIntArray mDebugStripsCount = new SparseIntArray();
    private Queue<Strip> mStripsQueue = new ArrayDeque<>();

    public void setResetListener(OnResetListener listener) {
        this.mListener = listener;
    }

    private OnResetListener mListener;

    public GridLayoutManager(DisplayMetrics dm) {
        this.mDensity = dm.density;
    }

    public void setDataSource(DataSource<?> dataSource) {
        mDataSource = dataSource;
    }

    /**
     * Sets strip length (either width or height of the view depending on {@link Orientation}). Use {@link #STRIP_LENGTH_AUTO} to calculate length based on items from {@link DataSource}.
     * Uses {@link #STRIP_LENGTH_DEFAULT} by default.
     *
     * @param stripLength length of strips in dp, or {@link #STRIP_LENGTH_AUTO} to calculate strip length dynamically
     */
    public void setStripLength(int stripLength) {
        mDynamicLength = stripLength == STRIP_LENGTH_AUTO;
        mStripLength = (int) (stripLength * mDensity + 0.5f);
    }

    public void setOrientation(@Orientation int orientation) {
        mIsHorizontal = orientation == ORIENTATION_HORIZONTAL;
    }

    @DebugLog
    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (getItemCount() == 0 || mDataSource == null) {
            removeAndRecycleAllViews(recycler);
            mStrips.clear();
            if (DEBUG) {
                Log.d(TAG, "item count: 0");
                mDebugStripsCount.clear();
            }
            return;
        }

        //Always update the visible strips/column counts
        updateWindowSizing();

        /*
         * Keep the existing initial position, and save off
         * the current scrolled offset.
         */
        if (mForceClearOffsets) {
            // reset layout
            mOffsetX = mOffsetY = 0;
            mForceClearOffsets = false;
        }

        /*
         * When data set is too small to scroll vertically, adjust vertical offset
         * and shift position to the first strip, preserving current column
         */
        int verticalSize = mIsHorizontal ? mTotalStripSize : mStripLength;
        int horizontalSize = mIsHorizontal ? mStripLength : mTotalStripSize;

        mOffsetY = Math.max(Math.min(mOffsetY, verticalSize - getVerticalSpace()), 0);
        mOffsetX = Math.max(Math.min(mOffsetX, horizontalSize - getHorizontalSpace()), 0);

        removeAndRecycleAllViews(recycler);

        mStrips.clear();
        if (DEBUG) {
            mDebugStripsCount.clear();
        }

        fill(recycler, state);

        if (mListener != null) {
            mListener.onReset(mOffsetX, mOffsetY);
        }

        if (DEBUG) {
            validate();
        }
    }

    @DebugLog
    private void validate() {
        int count = 0;
        for (Strip r : mStrips) {
            count += r.mViews.size();
        }
        int child = getChildCount();
        if (count != child) {
            Log.e(TAG, "ERROR! count: " + count + ", child: " + child);
        } else {
            Log.d(TAG, "item count: " + count);
        }
    }

    @DebugLog
    private void fill(RecyclerView.Recycler recycler, RecyclerView.State state) {
        fillStripsEnd();
        fillStripsStart();

        for (Strip strip : mStrips) {
            strip.fillItemsEnd(recycler, state);
            strip.fillItemsStart(recycler, state);
        }
    }

    @DebugLog
    private void removeNonVisibleStrips(RecyclerView.Recycler recycler) {
        int offset = mIsHorizontal ? mOffsetY : mOffsetX;
        int space = mIsHorizontal ? getVerticalSpace() : getHorizontalSpace();
        Strip strip = mStrips.peekFirst();
        while (strip != null && strip.end < offset) {
            if (DEBUG) {
                validateRemove(strip);
            }
            mStrips.removeFirst();
            strip.removeAllItems(recycler);
            mStripsQueue.add(strip);
            strip = mStrips.peekFirst();
        }

        strip = mStrips.peekLast();
        while (strip != null && strip.start > space + offset) {
            if (DEBUG) {
                validateRemove(strip);
            }
            mStrips.removeLast();
            strip.removeAllItems(recycler);
            mStripsQueue.add(strip);
            strip = mStrips.peekLast();
        }
    }

    @DebugLog
    private void validateRemove(Strip strip) {
        int stripId = strip.stripId;
        int currentCount = mDebugStripsCount.get(stripId);
        if (currentCount != 1) {
            throw new IllegalStateException("WTF! Consistency check fail. Removing stripId: " + stripId + ", strip count: " + currentCount);
        }
        mDebugStripsCount.delete(stripId);
        Log.d(TAG, "Removing " + stripId);
    }

    private void fillStripsEnd() {
        int availableSpace = mIsHorizontal ? getVerticalSpace() : getHorizontalSpace();
        int offset = mIsHorizontal ? mOffsetY : mOffsetX;
        int stripCount = mDataSource.getStripsCount();
        do {
            Strip lastStrip = mStrips.peekLast();
            if (lastStrip != null && lastStrip.end >= availableSpace + offset) {
                break;
            }

            int bottomViewIndex = getStripToInsertEnd();
            if (bottomViewIndex >= stripCount) { // no more strips to add
                break;
            }

            if (DEBUG) {
                validateStripAdd(bottomViewIndex);
            }

            Strip newStrip = mStripsQueue.poll();
            if (newStrip == null) {
                newStrip = new Strip(bottomViewIndex);
            } else {
                newStrip.setStripId(bottomViewIndex);
            }
            mStrips.addLast(newStrip);
        } while (true);
    }

    @DebugLog
    private void validateStripAdd(int bottomViewIndex) {
        if (mDebugStripsCount.get(bottomViewIndex) != 0) {
            throw new IllegalStateException("WTF! Consistency check fail. Adding stripId: " + bottomViewIndex + ", strip already exists!");
        }
        mDebugStripsCount.put(bottomViewIndex, 1);
    }

    private void fillStripsStart() {
        int offset = mIsHorizontal ? mOffsetY : mOffsetX;
        do {
            Strip firstStrip = mStrips.peekFirst();
            if (firstStrip != null && firstStrip.start <= offset) {
                break;
            }
            int topStripIndex = getStripToInsertStart();
            if (topStripIndex < 0) {
                break;
            }

            if (DEBUG) {
                validateStripAdd(topStripIndex);
            }

            Strip newStrip = new Strip(topStripIndex);
            mStrips.addFirst(newStrip);
        } while (true);
    }

    @DebugLog
    private int getStripToInsertStart() {
        if (mStrips.size() == 0) {
            return -1; // don't insert, we'll handle this situation in 'getStripToInsertEnd'
        }
        int topStripIdx = mStrips.getFirst().stripId;
        return topStripIdx - 1;
    }

    @DebugLog
    private int getStripToInsertEnd() {
        int offset = mIsHorizontal ? mOffsetY : mOffsetX;
        if (mStrips.size() == 0) {
            // if there are no strips to insert, we need to know what the current scroll is and calculate the strip to insert
            int hiddenStrips = Math.abs(offset) / mStripSize; // strips that are "offscreen" and should not be shown
            return hiddenStrips; // same as index of the strip to insert
        }
        int bottomStripIdx = mStrips.getLast().stripId;
        return bottomStripIdx + 1;
    }

    private int getPixels(int size) {
        return (int) (size * mDensity + 0.5f);
    }

    /**
     * Holds reference to views in the same strip
     */
    private class Strip {
        private int stripId;
        private int start, end;
        ArrayDeque<View> mViews = new ArrayDeque<>();

        public Strip(int stripId) {
            setStripId(stripId);
        }

        public void setStripId(int stripId) {
            this.stripId = stripId;
            start = this.stripId * mStripSize;
            end = start + mStripSize;
        }

        public View getView(int position) {
            View v = mViews.peekFirst();
            if (v == null) {
                return null;
            }
            LayoutParams lp = (LayoutParams) v.getLayoutParams();
            int currPosition = lp.position;
            int idx = position - currPosition;
            if (idx < getChildCount()) {
                Iterator<View> iterator = mViews.iterator();
                for (int i = 0; i <= idx; i++) {
                    View view = iterator.next();
                    if (i == idx) {
                        return view;
                    }
                }
            }
            return null;
        }

        public int getChildCount() {
            return mViews.size();
        }

        private void fillItemsEnd(RecyclerView.Recycler recycler, RecyclerView.State state) {
            View rightCell = mViews.peekLast();
            int newItemIndex = 0;

            int availableSpace = mIsHorizontal ? getHorizontalSpace() : getVerticalSpace();
            int offset = mIsHorizontal ? mOffsetX : mOffsetY;

            if (rightCell != null) {
                Item rightMostEvent = (Item) rightCell.getTag();
                int rightmostEventEndTime = getPixels(rightMostEvent.getEnd());
                if (rightmostEventEndTime > availableSpace +
                        offset) { // current start-most event spans beyond the screen to the start, no need to check previous event
                    return;
                }

                LayoutParams lp = (LayoutParams) rightCell.getLayoutParams();
                if (lp.strip != stripId) {
                    throw new IllegalArgumentException("Cell data inconsistent with current strip");
                }
                newItemIndex = lp.position + 1; // next event from the same strip, going right
            } else {
                newItemIndex = 0; // start from first event (which can be offscreen to the left)
            }

            int itemCount = mDataSource.getItemCountForStrip(stripId);

            for (int i = newItemIndex; i < itemCount; i++) {
                Item event = mDataSource.getItem(stripId, i);
                int rightEdge = getPixels(event.getEnd());
                if (rightEdge < offset) {
                    // event left-offscreen
                    continue;
                }
                int leftEdge = getPixels(event.getStart());
                if (leftEdge > availableSpace + offset) {
                    // event right-offscreen
                    break;
                }
                int adapterPosition = mDataSource.map2Dto1D(stripId, i);
                View item = recycler.getViewForPosition(adapterPosition);
                addAndMeasureItem(item, event, stripId, i, -1); // end of list
            }
        }

        private void fillItemsStart(RecyclerView.Recycler recycler, RecyclerView.State state) {
            View leftCell = mViews.peekFirst();
            if (leftCell == null) {
                return;
            }
            int newEventIndex = 0;

            int availableSpace = mIsHorizontal ? getHorizontalSpace() : getVerticalSpace();
            int offset = mIsHorizontal ? mOffsetX : mOffsetY;

            Item leftMostEvent = (Item) leftCell.getTag();
            int leftmostEventStartTime = getPixels(leftMostEvent.getStart());
            if (leftmostEventStartTime < offset) { // current leftmost event spans beyond the screen to the left, no need to check previous event
                return;
            }

            LayoutParams lp = (LayoutParams) leftCell.getLayoutParams();
            if (lp.strip != stripId) {
                throw new IllegalArgumentException("Cell data inconsistent with current strip");
            }
            newEventIndex = lp.position - 1; // previous event from the same strip, going left

            for (int i = newEventIndex; i >= 0; i--) {
                Item event = mDataSource.getItem(stripId, i);

                int rightEdge = getPixels(event.getEnd());
                if (rightEdge < offset) {
                    break; // event left-offscreen, we can stop now
                }

                int adapterPosition = mDataSource.map2Dto1D(stripId, i);
                View item = recycler.getViewForPosition(adapterPosition);
                addAndMeasureItem(item, event, stripId, i, 0); // beginning of list
            }
        }

        @DebugLog
        public void removeNonVisibleItems(RecyclerView.Recycler recycler, RecyclerView.State state) {
            int availableSpace = mIsHorizontal ? getHorizontalSpace() : getVerticalSpace();
            int offset = mIsHorizontal ? mOffsetX : mOffsetY;

            View child = mViews.peekFirst();
            while (child != null) {
                Item event = (Item) child.getTag();
                if (getPixels(event.getEnd()) < offset) {
                    removeAndRecycleView(child, recycler);
                    mViews.removeFirst();
                    child = mViews.peekFirst();
                } else {
                    break;
                }
            }
            child = mViews.peekLast();
            while (child != null) {
                Item event = (Item) child.getTag();
                if (getPixels(event.getStart()) > availableSpace + offset) {
                    removeAndRecycleView(child, recycler);
                    mViews.removeLast();
                    child = mViews.peekLast();
                } else {
                    break;
                }
            }
        }

        @DebugLog
        private void addAndMeasureItem(final View view, Item item, int strip, int itemPos, int viewPos) {
            int size = getPixels(item.getSize());
            LayoutParams params = (LayoutParams) view.getLayoutParams();
            params.width = mIsHorizontal ? size : mStripSize;
            params.height = mIsHorizontal ? mStripSize : size;
            params.position = itemPos;
            params.strip = strip;

            addView(view);

            if (viewPos == 0) {
                mViews.addFirst(view);
            } else { // -1
                mViews.addLast(view);
            }

            int offsetA = mIsHorizontal ? mOffsetX : mOffsetY;
            int offsetB = mIsHorizontal ? mOffsetY : mOffsetX;
            int left = getPixels(item.getStart()) - offsetA;
            int topPos = start - offsetB;
            int right = left + size;
            int bottom = topPos + mStripSize;

            measureChildWithMargins(view, 0, 0);
            layoutDecorated(view,
                    mIsHorizontal ? left : topPos,
                    mIsHorizontal ? topPos : left,
                    mIsHorizontal ? right : bottom,
                    mIsHorizontal ? bottom : right);
        }

        @DebugLog
        public void removeAllItems(RecyclerView.Recycler recycler) {
            for (View v : mViews) {
                removeAndRecycleView(v, recycler);
            }
            mViews.clear();
        }
    }

    @DebugLog
    @Override
    public void onAdapterChanged(RecyclerView.Adapter oldAdapter, RecyclerView.Adapter newAdapter) {
        //Completely scrap the existing layout
        removeAllViews();
        mStrips.clear();
        mDebugStripsCount.clear();
    }

    @DebugLog
    private void updateWindowSizing() {
        int availableSpaceForStrips = mIsHorizontal ? getVerticalSpace() : getHorizontalSpace();
        mVisibleStripCount = (availableSpaceForStrips / mStripSize) + 1;
        if (availableSpaceForStrips % mStripSize > 0) {
            mVisibleStripCount++;
        }

        int stripCount = mDataSource.getStripsCount();
        if (mVisibleStripCount > stripCount) {
            mVisibleStripCount = stripCount;
        }
        mTotalStripSize = stripCount * mStripSize;
        if (mDynamicLength) {
            mStripLength = 0;
            for (int i = 0; i < stripCount; i++) {
                int stripItemCount = mDataSource.getItemCountForStrip(i);
                if (stripItemCount == 0) {
                    continue;
                }
                Item lastItem = mDataSource.getItem(i, stripItemCount - 1);
                mStripLength = Math.max(mStripLength, getPixels(lastItem.getEnd()));
            }
        }
    }

    @Override
    public boolean canScrollVertically() {
        return true;
    }

    @Override
    public boolean canScrollHorizontally() {
        return true;
    }

    @DebugLog
    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int horizontalSize = mIsHorizontal ? mStripLength : mTotalStripSize;
        int delta;
        if (dx > 0) { // Contents are scrolling left
            int remainingOffset = horizontalSize - getHorizontalSpace() - mOffsetX;
            delta = Math.min(dx, Math.max(remainingOffset, 0));
        } else { // Contents are scrolling right
            delta = Math.max(dx, -mOffsetX);
        }

        mOffsetX += delta;
        if (mIsHorizontal) {
            for (Strip rv : mStrips) {
                rv.removeNonVisibleItems(recycler, state);
            }
        } else {
            removeNonVisibleStrips(recycler);
        }

        offsetChildrenHorizontal(-delta);

        if (mIsHorizontal) {
            for (Strip rv : mStrips) {
                if (dx > 0) {
                    rv.fillItemsEnd(recycler, state);
                } else {
                    if (rv.mViews.size() != 0) {
                        rv.fillItemsStart(recycler, state);
                    } else {
                        rv.fillItemsEnd(recycler, state);
                    }
                }
            }
        } else {
            fill(recycler, state);
        }

        if (DEBUG) {
            validate();
        }
        return delta;
    }

    @DebugLog
    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int verticalSize = mIsHorizontal ? mTotalStripSize : mStripLength;
        int delta;
        if (dy > 0) { // Contents are scrolling up
            int remainingOffset = verticalSize - (getVerticalSpace() + mOffsetY);
            delta = Math.min(dy, Math.max(remainingOffset, 0));
        } else { // Contents are scrolling down
            delta = Math.max(dy, -mOffsetY);
        }

        mOffsetY += delta;
        if (mIsHorizontal) {
            removeNonVisibleStrips(recycler);
        } else {
            for (Strip rv : mStrips) {
                rv.removeNonVisibleItems(recycler, state);
            }
        }

        offsetChildrenVertical(-delta);

        if (mIsHorizontal) {
            fill(recycler, state);
        } else {
            for (Strip rv : mStrips) {
                if (dy > 0) {
                    rv.fillItemsEnd(recycler, state);
                } else {
                    if (rv.mViews.size() != 0) {
                        rv.fillItemsStart(recycler, state);
                    } else {
                        rv.fillItemsEnd(recycler, state);
                    }
                }
            }
        }

        if (DEBUG) {
            validate();
        }

        return delta;
    }

    /*
     * This is a helper method used by RecyclerView to determine
     * if a specific child view can be returned.
     * It is used mostly for smooth scrolling. However, since default LinearSmoothScroller scrolls only in one direction, we need a custom scroller.
     * TODO: implement custom SmoothScroller
     */
    @DebugLog
    @Override
    public View findViewByPosition(int position) {
        coordinates = mDataSource.map1DTo2D(position, coordinates);
        int positionInStrip = coordinates[DataSource.COORDINATE_POSITION];
        int strip = coordinates[DataSource.COORDINATE_STRIP];
        Strip rv = getStrip(strip);
        if (rv == null) {
            return null;
        }
        return rv.getView(positionInStrip);
    }

    private Strip getStrip(int y) {
        if (mStrips == null) {
            return null;
        }
        Strip rv = mStrips.peekFirst();
        if (rv == null) {
            return null;
        }
        int topY = rv.stripId;
        int idx = y - topY;
        if (idx < mStrips.size()) {
            Iterator<Strip> iterator = mStrips.iterator();
            for (int i = 0; i <= idx; i++) {
                Strip strip = iterator.next();
                if (i == idx) {
                    return strip;
                }
            }
        }
        return null;
    }

    /*
     * Even without extending LayoutParams, we must override this method
     * to provide the default layout parameters that each child view
     * will receive when added.
     */
    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(0, 0);
    }

    @Override
    public RecyclerView.LayoutParams generateLayoutParams(Context c, AttributeSet attrs) {
        return new LayoutParams(c, attrs);
    }

    @Override
    public RecyclerView.LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
        if (lp instanceof ViewGroup.MarginLayoutParams) {
            return new LayoutParams((ViewGroup.MarginLayoutParams) lp);
        } else {
            return new LayoutParams(lp);
        }
    }

    @Override
    public boolean checkLayoutParams(RecyclerView.LayoutParams lp) {
        return lp instanceof LayoutParams;
    }

    /**
     * Sets strip size in dp. Depending on the current orientation, it can either mean height (for {@link #ORIENTATION_HORIZONTAL}) or width (for {@link #ORIENTATION_VERTICAL}).
     *
     * @param stripSize - height in DP
     */
    public void setStripSize(int stripSize) {
        mStripSize = getPixels(stripSize);
    }

    public static class LayoutParams extends RecyclerView.LayoutParams {

        //Current strip in the grid
        public int strip;
        //Current position in strip
        public int position;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(RecyclerView.LayoutParams source) {
            super(source);
        }
    }

    private int getHorizontalSpace() {
        return getWidth() - getPaddingRight() - getPaddingLeft();
    }

    private int getVerticalSpace() {
        return getHeight() - getPaddingBottom() - getPaddingTop();
    }

    public interface OnResetListener {
        void onReset(int x, int y);
    }

    @Override
    public int computeHorizontalScrollExtent(RecyclerView.State state) {
        return getHorizontalSpace();
    }

    @Override
    public int computeHorizontalScrollOffset(RecyclerView.State state) {
        return mOffsetX;
    }

    @Override
    public int computeHorizontalScrollRange(RecyclerView.State state) {
        return mIsHorizontal ? mStripLength : mTotalStripSize;
    }

    @Override
    public int computeVerticalScrollExtent(RecyclerView.State state) {
        return getVerticalSpace();
    }

    @Override
    public int computeVerticalScrollOffset(RecyclerView.State state) {
        return mOffsetY;
    }

    @Override
    public int computeVerticalScrollRange(RecyclerView.State state) {
        return mIsHorizontal ? mTotalStripSize : mStripLength;
    }

    /**
     * Tries to scroll to position x, y. Will be clamped to available data
     *
     * @param x - logical x position (not the same as pixels; LayoutManager will translate it to pixels)
     */
    public void scrollTo(int x) {
        mOffsetX = (int) (x * mDensity + 0.5f);
        requestLayout();
    }
}
