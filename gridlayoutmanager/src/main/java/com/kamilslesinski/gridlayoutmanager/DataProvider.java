package com.kamilslesinski.gridlayoutmanager;

/**
 * {@link DataProvider} provides basic interface to get information about {@link Item Items}.
 *
 * @author Kamil Ślesiński (slesinskik@gmail.com)
 * @since 2016/04/12
 */
interface DataProvider {
    /**
     * @return total count of items in this data provider
     */
    int getItemCount();

    /**
     * Returns total count of strips (rows or columns, depending on {@link GridLayoutManager.Orientation}).
     * {@link GridLayoutManager} uses that to display appropriate amount of grid strips.
     *
     * @return count of strips this {@link DataProvider} contains
     */
    int getStripsCount();

    /**
     * Returns count of {@link Item items} for specific strip.
     *
     * @param strip strip number
     * @return count of {@link Item} in strip
     */
    int getItemCountForStrip(int strip);
}
