package com.kamilslesinski.gridlayoutmanager;

/**
 * {@inheritDoc} This {@link DataProvider2D} gives 2D access to {@link Item Items}.
 *
 * @author Kamil Ślesiński (slesinskik@gmail.com)
 * @since 2016/04/07
 */
public interface DataProvider2D<T extends Item> extends DataProvider {
    /**
     * Returns {@link Item} for given coordinates.
     *
     * @param strip strip number
     * @param index index in given strip
     * @return {@link Item} for given (strip, index)
     */
    T getItem(int strip, int index);
}
