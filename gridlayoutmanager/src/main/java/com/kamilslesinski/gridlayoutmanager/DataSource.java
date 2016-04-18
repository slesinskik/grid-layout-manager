package com.kamilslesinski.gridlayoutmanager;

import android.support.annotation.NonNull;
import android.support.annotation.Size;

/**
 * This class uses {@link DataProvider} to provide data to {@link GridLayoutManager}.
 * It reads all the {@link Item Items} data from {@link DataProvider} (either {@link DataProvider1D} or {@link DataProvider2D})
 * and creates appropriate in-memory mappings.
 * This class has O(n+m) memory complexity, where n is the number of items in {@link DataProvider} and m is the number of strips.
 * This is required for fast (O(1)) mapping between 1D and 2D coordinates.
 *
 * @author Kamil Ślesiński (slesinskik@gmail.com)
 * @since 2016/04/12
 */
public class DataSource<I extends Item> implements DataProvider1D<I>, DataProvider2D<I> {
    /**
     * Defines index within a strip
     */
    static final int COORDINATE_POSITION = 0;
    /**
     * Defines strip index
     */
    static final int COORDINATE_STRIP = 1;

    private DataProvider mDataProvider;
    private DataProvider2D<I> mDataProvider2D;
    private DataProvider1D<I> mDataProvider1D;

    /**
     * This is a helper array to create 1D<->2D mapping. It's length is the same as the count of strips in {@link DataProvider}.
     * It stores index (in 1D space) of first {@link Item} in a specific strip.
     */
    private int[] mStripFirstItemIndex;
    /**
     * This is a helper array that helps to map 1D->2D coordinates in O(1).
     */
    private int[] mItemToStripMapping;
    /**
     * This is a helper array to store/process temporary coordinates (for performance reasons).
     */
    private int[] mCoordinates = new int[2];

    /**
     * Uses {@link DataProvider2D} as a data source.
     *
     * @param dataProvider2D
     */
    public void setDataProvider(DataProvider2D<I> dataProvider2D) {
        mDataProvider = mDataProvider2D = dataProvider2D;
        mDataProvider1D = null;
        buildCache();
    }

    /**
     * Uses {@link DataProvider1D} as a data source.
     *
     * @param dataProvider1D
     */
    public void setDataProvider(DataProvider1D<I> dataProvider1D) {
        mDataProvider = mDataProvider1D = dataProvider1D;
        mDataProvider2D = null;
        buildCache();
    }

    /**
     * This function initializes mapping helpers to allow O(1) mapping between 1D<->2D coordinates
     */
    private void buildCache() {
        int totalCount = mDataProvider.getItemCount();
        mItemToStripMapping = new int[totalCount];
        int stripsCount = mDataProvider.getStripsCount();
        mStripFirstItemIndex = new int[stripsCount];
        int index = 0;
        for (int stripNumber = 0; stripNumber < stripsCount; stripNumber++) {
            mStripFirstItemIndex[stripNumber] = index;
            int itemCount = mDataProvider.getItemCountForStrip(stripNumber);
            for (int j = 0; j < itemCount; j++) {
                mItemToStripMapping[index + j] = stripNumber;
            }
            index += itemCount;
        }
    }

    /**
     * Maps (z) coordinate into (x,y).
     *
     * @param position    position in 1D coordinates
     * @param destination destination array where to store result in 2D coordinates
     * @return coordinates, can be accessed using {@link #COORDINATE_POSITION} and {@link #COORDINATE_STRIP}
     */
    int[] map1DTo2D(int position, @NonNull @Size(2) int[] destination) {
        int stripIndex = mItemToStripMapping[position];
        destination[COORDINATE_STRIP] = stripIndex;
        destination[COORDINATE_POSITION] = position - mStripFirstItemIndex[stripIndex];
        return destination;
    }


    /**
     * Maps (x,y) coordinates into (z).
     *
     * @param strip    strip number
     * @param position position in strip
     * @return position in 1D coordinates
     */
    int map2Dto1D(int strip, int position) {
        return mStripFirstItemIndex[strip] + position;
    }

    @Override
    public int getItemCount() {
        return mDataProvider.getItemCount();
    }

    @Override
    public int getStripsCount() {
        return mDataProvider.getStripsCount();
    }

    @Override
    public int getItemCountForStrip(int strip) {
        return mDataProvider.getItemCountForStrip(strip);
    }


    @Override
    public I getItem(int position) {
        if (mDataProvider1D != null) {
            return mDataProvider1D.getItem(position);
        }
        map1DTo2D(position, mCoordinates);
        return mDataProvider2D.getItem(mCoordinates[COORDINATE_STRIP], mCoordinates[COORDINATE_POSITION]);
    }

    @Override
    public I getItem(int strip, int position) {
        if (mDataProvider2D != null) {
            return mDataProvider2D.getItem(strip, position);
        }
        return mDataProvider1D.getItem(map2Dto1D(strip, position));
    }
}
