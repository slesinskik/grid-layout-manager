package com.kamilslesinski.gridlayoutmanager.sampleapp.tvguide;

import com.kamilslesinski.gridlayoutmanager.sampleapp.generic.SimpleItem;

/**
 * @author Kamil Ślesiński (slesinskik@gmail.com)
 * @since 2016/04/24
 */
public class TVGuideItem extends SimpleItem {
    private static final int DP_PER_MINUTE = 4;

    public TVGuideItem(int minutesStart, int minutesEnd) {
        super(minutesStart * DP_PER_MINUTE, minutesEnd * DP_PER_MINUTE);
    }

    public int getStartInMinutes() {
        return getStart() / DP_PER_MINUTE;
    }

    public int getEndInMinutes() {
        return getEnd() / DP_PER_MINUTE;
    }
}
