/*
@ITMillApache2LicenseForJavaFiles@
 */
package com.vaadin.data.util.filter;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;

/**
 * A compound {@link Filter} that accepts an item if all of its filters accept
 * the item.
 * 
 * If no filters are given, the filter should accept all items.
 * 
 * This filter also directly supports in-memory filtering when all sub-filters
 * do so.
 * 
 * @see Or
 * 
 * @since 6.6
 */
public final class And extends AbstractJunctionFilter {

    /**
     * 
     * @param filters
     *            filters of which the And filter will be composed
     */
    public And(Filter... filters) {
        super(filters);
    }

    public boolean passesFilter(Object itemId, Item item)
            throws UnsupportedFilterException {
        for (Filter filter : getFilters()) {
            if (!filter.passesFilter(itemId, item)) {
                return false;
            }
        }
        return true;
    }

}
