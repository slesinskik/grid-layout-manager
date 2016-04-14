package com.kamilslesinski.gridlayoutmanager.sampleapp;

import com.kamilslesinski.gridlayoutmanager.DataProvider1D;

import java.util.Random;

/**
 * @author Kamil Ślesiński (slesinskik@gmail.com)
 * @since 2016/04/08
 */
public class RandomDataProvider1D implements DataProvider1D<SimpleItem> {
    private int mItemCount;

    private int[] mStripItemCounts;
    private SimpleItem[] mGridItems;

    public RandomDataProvider1D() {
        Random random = new Random();

        mStripItemCounts = new int[random.nextInt(500) + 100];
        mItemCount = 0;
        for (int i = 0; i < mStripItemCounts.length; i++) {
            final int stripItemCount = (i % 7 == 0) ? 0 : random.nextInt(1000);
            mStripItemCounts[i] = stripItemCount;
            mItemCount += stripItemCount;
        }

        mGridItems = new SimpleItem[mItemCount];
        int itemIndex = 0;
        for (int stripNumber = 0; stripNumber < mStripItemCounts.length; stripNumber++) {
            int itemStart;
            int itemEnd = 0;
            for (int i = 0; i < mStripItemCounts[stripNumber]; i++) {
                itemStart = itemEnd + random.nextInt(30);
                itemEnd = itemStart + 60 + random.nextInt(100);
                SimpleItem gridItem = new SimpleItem(itemStart, itemEnd);
                mGridItems[itemIndex] = gridItem;
                itemIndex++;
            }
        }
    }

    @Override
    public int getItemCount() {
        return mItemCount;
    }

    @Override
    public int getStripsCount() {
        return mStripItemCounts.length;
    }

    @Override
    public int getItemCountForStrip(int strip) {
        return mStripItemCounts[strip];
    }

    @Override
    public SimpleItem getItem(int position) {
        return mGridItems[position];
    }
}
