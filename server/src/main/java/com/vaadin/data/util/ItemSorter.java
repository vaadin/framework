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
import java.util.Comparator;

import com.vaadin.data.Container;
import com.vaadin.data.Container.Sortable;

/**
 * An item comparator which is compatible with the {@link Sortable} interface.
 * The <code>ItemSorter</code> interface can be used in <code>Sortable</code>
 * implementations to provide a custom sorting method.
 */
public interface ItemSorter extends Comparator<Object>, Cloneable, Serializable {

    /**
     * Sets the parameters for an upcoming sort operation. The parameters
     * determine what container to sort and how the <code>ItemSorter</code>
     * sorts the container.
     * 
     * @param container
     *            The container that will be sorted. The container must contain
     *            the propertyIds given in the <code>propertyId</code>
     *            parameter.
     * @param propertyId
     *            The property ids used for sorting. The property ids must exist
     *            in the container and should only be used if they are also
     *            sortable, i.e include in the collection returned by
     *            <code>container.getSortableContainerPropertyIds()</code>. See
     *            {@link Sortable#sort(Object[], boolean[])} for more
     *            information.
     * @param ascending
     *            Sorting order flags for each property id. See
     *            {@link Sortable#sort(Object[], boolean[])} for more
     *            information.
     */
    void setSortProperties(Container.Sortable container, Object[] propertyId,
            boolean[] ascending);

    /**
     * Compares its two arguments for order. Returns a negative integer, zero,
     * or a positive integer as the first argument is less than, equal to, or
     * greater than the second.
     * <p>
     * The parameters for the <code>ItemSorter</code> <code>compare()</code>
     * method must always be item ids which exist in the container set using
     * {@link #setSortProperties(Sortable, Object[], boolean[])}.
     * 
     * @see Comparator#compare(Object, Object)
     */
    @Override
    int compare(Object itemId1, Object itemId2);

}
