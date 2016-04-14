package com.kamilslesinski.gridlayoutmanager;

/**
 * Used in {@link GridLayoutManager}. Defines item position and size in a strip. {@link Item} can belong to one strip only.
 * {@link Item} height/width (depending on {@link GridLayoutManager.Orientation} is controlled by {@link GridLayoutManager}.
 *
 * @author Kamil Ślesiński (slesinskik@gmail.com)
 * @since 2016/04/07
 */
public interface Item {
    /**
     * @return start position in dp.
     */
    int getStart();

    /**
     * @return end position in strip in dp.
     */
    int getEnd();

    /**
     * @return size position in strip in dp. Should be same as {@link #getEnd()} - {@link #getStart()}.
     */
    int getSize();
}
