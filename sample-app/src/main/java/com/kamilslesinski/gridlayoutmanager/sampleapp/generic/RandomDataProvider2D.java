package com.kamilslesinski.gridlayoutmanager.sampleapp.generic;

import com.kamilslesinski.gridlayoutmanager.DataProvider2D;

import java.util.Random;

/**
 * @author Kamil Ślesiński (slesinskik@gmail.com)
 * @since 2016/04/08
 */
public class RandomDataProvider2D implements DataProvider2D<SimpleItem> {
    private int mItemCount;

    private SimpleItem[][] mGridItems;

    public RandomDataProvider2D() {
        Random random = new Random();
        int stripCount = random.nextInt(500) + 100;
        mGridItems = new SimpleItem[stripCount][];
        mItemCount = 0;
        for (int i = 0; i < mGridItems.length; i++) {
            final int stripItemCount = (i % 7 == 0) ? 0 : random.nextInt(1000);
            mItemCount += stripItemCount;
            mGridItems[i] = new SimpleItem[stripItemCount];

            int itemStart = 0;
            int itemEnd = 0;

            for (int j = 0; j < mGridItems[i].length; j++) {
                itemStart = itemEnd + random.nextInt(30);
                itemEnd = itemStart + 60 + random.nextInt(100);
                SimpleItem gridItem = new SimpleItem(itemStart, itemEnd);
                mGridItems[i][j] = gridItem;
            }
        }
    }

    @Override
    public int getItemCount() {
        return mItemCount;
    }

    @Override
    public int getStripsCount() {
        return mGridItems.length;
    }

    @Override
    public int getItemCountForStrip(int strip) {
        return mGridItems[strip].length;
    }

    @Override
    public SimpleItem getItem(int strip, int position) {
        return mGridItems[strip][position];
    }
}
