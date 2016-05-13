package com.kamilslesinski.gridlayoutmanager.sampleapp.tvguide;

import com.kamilslesinski.gridlayoutmanager.DataProvider2D;

import java.util.ArrayList;
import java.util.Random;

/**
 * @author Kamil Ślesiński (slesinskik@gmail.com)
 * @since 2016/04/08
 */
public class RandomTVGuideDataProvider2D implements DataProvider2D<TVGuideItem> {
    private static final int MINUTES_IN_DAY = 24 * 60;

    private int mItemCount;

    private TVGuideItem[][] mGridItems;

    public RandomTVGuideDataProvider2D() {
        Random random = new Random();
        int stripCount = random.nextInt(100) + 10;
        mGridItems = new TVGuideItem[stripCount][];
        mItemCount = 0;
        for (int i = 0; i < mGridItems.length; i++) {
            int itemStart = random.nextInt(3) * 50;

            ArrayList<TVGuideItem> items = new ArrayList<>();
            do {
                int duration = random.nextInt(6) * 30;
                items.add(new TVGuideItem(itemStart, itemStart + duration));
                itemStart += duration + random.nextInt(3) * 5;
            } while (itemStart < MINUTES_IN_DAY);
            mItemCount += items.size();
            mGridItems[i] = items.toArray(new TVGuideItem[items.size()]);
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
    public TVGuideItem getItem(int strip, int position) {
        return mGridItems[strip][position];
    }
}
