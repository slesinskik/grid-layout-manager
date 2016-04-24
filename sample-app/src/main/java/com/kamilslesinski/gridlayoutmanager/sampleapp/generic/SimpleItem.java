package com.kamilslesinski.gridlayoutmanager.sampleapp.generic;

import com.kamilslesinski.gridlayoutmanager.Item;

/**
 * @author Kamil Ślesiński (slesinskik@gmail.com)
 * @since 2016/04/08
 */
public class SimpleItem implements Item {

    private int mLeft = 0;
    private int mRight = 0;

    public SimpleItem(int left, int right) {
        mLeft = left;
        mRight = right;
    }

    @Override
    public int getStart() {
        return mLeft;
    }

    @Override
    public int getEnd() {
        return mRight;
    }

    @Override
    public int getSize() {
        return mRight - mLeft;
    }
}
