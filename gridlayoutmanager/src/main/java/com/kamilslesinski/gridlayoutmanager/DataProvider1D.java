package com.kamilslesinski.gridlayoutmanager;

/**
 * {@inheritDoc} This {@link DataProvider1D} provides linear (1D) access to {@link Item Items}.
 *
 * @author Kamil Ślesiński (slesinskik@gmail.com)
 * @since 2016/04/07
 */
public interface DataProvider1D<T extends Item> extends DataProvider {

    /**
     * Gets {@link Item} for given index.
     *
     * @param index index of item in this {@link DataProvider1D}
     * @return {@link Item} for given index
     */
    T getItem(int index);

}
