/*
 * Copyright 2000-2014 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.data.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import com.vaadin.data.Container;
import com.vaadin.data.Container.Sortable;
import com.vaadin.data.Item;
import com.vaadin.data.Property;

/**
 * Provides a default implementation of an ItemSorter. The
 * <code>DefaultItemSorter</code> adheres to the
 * {@link Sortable#sort(Object[], boolean[])} rules and sorts the container
 * according to the properties given using
 * {@link #setSortProperties(Sortable, Object[], boolean[])}.
 * <p>
 * A Comparator is used for comparing the individual <code>Property</code>
 * values. The comparator can be set using the constructor. If no comparator is
 * provided a default comparator is used.
 * 
 */
public class DefaultItemSorter implements ItemSorter {

    private java.lang.Object[] sortPropertyIds;
    private boolean[] sortDirections;
    private Container container;
    private Comparator<Object> propertyValueComparator;

    /**
     * Constructs a DefaultItemSorter using the default <code>Comparator</code>
     * for comparing <code>Property</code>values.
     * 
     */
    public DefaultItemSorter() {
        this(new DefaultPropertyValueComparator());
    }

    /**
     * Constructs a DefaultItemSorter which uses the <code>Comparator</code>
     * indicated by the <code>propertyValueComparator</code> parameter for
     * comparing <code>Property</code>values.
     * 
     * @param propertyValueComparator
     *            The comparator to use when comparing individual
     *            <code>Property</code> values
     */
    public DefaultItemSorter(Comparator<Object> propertyValueComparator) {
        this.propertyValueComparator = propertyValueComparator;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.util.ItemSorter#compare(java.lang.Object,
     * java.lang.Object)
     */
    @Override
    public int compare(Object o1, Object o2) {
        Item item1 = container.getItem(o1);
        Item item2 = container.getItem(o2);

        /*
         * Items can be null if the container is filtered. Null is considered
         * "less" than not-null.
         */
        if (item1 == null) {
            if (item2 == null) {
                return 0;
            } else {
                return 1;
            }
        } else if (item2 == null) {
            return -1;
        }

        for (int i = 0; i < sortPropertyIds.length; i++) {

            int result = compareProperty(sortPropertyIds[i], sortDirections[i],
                    item1, item2);

            // If order can be decided
            if (result != 0) {
                return result;
            }

        }

        return 0;
    }

    /**
     * Compares the property indicated by <code>propertyId</code> in the items
     * indicated by <code>item1</code> and <code>item2</code> for order. Returns
     * a negative integer, zero, or a positive integer as the property value in
     * the first item is less than, equal to, or greater than the property value
     * in the second item. If the <code>sortDirection</code> is false the
     * returned value is negated.
     * <p>
     * The comparator set for this <code>DefaultItemSorter</code> is used for
     * comparing the two property values.
     * 
     * @param propertyId
     *            The property id for the property that is used for comparison.
     * @param sortDirection
     *            The direction of the sort. A false value negates the result.
     * @param item1
     *            The first item to compare.
     * @param item2
     *            The second item to compare.
     * @return a negative, zero, or positive integer if the property value in
     *         the first item is less than, equal to, or greater than the
     *         property value in the second item. Negated if
     *         {@code sortDirection} is false.
     */
    protected int compareProperty(Object propertyId, boolean sortDirection,
            Item item1, Item item2) {

        // Get the properties to compare
        final Property<?> property1 = item1.getItemProperty(propertyId);
        final Property<?> property2 = item2.getItemProperty(propertyId);

        // Get the values to compare
        final Object value1 = (property1 == null) ? null : property1.getValue();
        final Object value2 = (property2 == null) ? null : property2.getValue();

        // Result of the comparison
        int r = 0;
        if (sortDirection) {
            r = propertyValueComparator.compare(value1, value2);
        } else {
            r = propertyValueComparator.compare(value2, value1);
        }

        return r;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.data.util.ItemSorter#setSortProperties(com.vaadin.data.Container
     * .Sortable, java.lang.Object[], boolean[])
     */
    @Override
    public void setSortProperties(Container.Sortable container,
            Object[] propertyId, boolean[] ascending) {
        this.container = container;

        // Removes any non-sortable property ids
        final List<Object> ids = new ArrayList<Object>();
        final List<Boolean> orders = new ArrayList<Boolean>();
        final Collection<?> sortable = container
                .getSortableContainerPropertyIds();
        for (int i = 0; i < propertyId.length; i++) {
            if (sortable.contains(propertyId[i])) {
                ids.add(propertyId[i]);
                orders.add(Boolean.valueOf(i < ascending.length ? ascending[i]
                        : true));
            }
        }

        sortPropertyIds = ids.toArray();
        sortDirections = new boolean[orders.size()];
        for (int i = 0; i < sortDirections.length; i++) {
            sortDirections[i] = (orders.get(i)).booleanValue();
        }

    }

    /**
     * Provides a default comparator used for comparing {@link Property} values.
     * The <code>DefaultPropertyValueComparator</code> assumes all objects it
     * compares can be cast to Comparable.
     * 
     */
    public static class DefaultPropertyValueComparator implements
            Comparator<Object>, Serializable {

        @Override
        @SuppressWarnings("unchecked")
        public int compare(Object o1, Object o2) {
            int r = 0;
            // Normal non-null comparison
            if (o1 != null && o2 != null) {
                // Assume the objects can be cast to Comparable, throw
                // ClassCastException otherwise.
                r = ((Comparable<Object>) o1).compareTo(o2);
            } else if (o1 == o2) {
                // Objects are equal if both are null
                r = 0;
            } else {
                if (o1 == null) {
                    r = -1; // null is less than non-null
                } else {
                    r = 1; // non-null is greater than null
                }
            }

            return r;
        }
    }

}
