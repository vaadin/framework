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
package com.vaadin.data.sort;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.vaadin.shared.data.sort.SortDirection;

/**
 * Fluid Sort API. Provides a convenient, human-readable way of specifying
 * multi-column sort order.
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
public class Sort implements Serializable {

    private final Sort previous;
    private final SortOrder order;

    /**
     * Initial constructor, called by the static by() methods.
     * 
     * @param propertyId
     *            a property ID, corresponding to a property in the data source
     * @param direction
     *            a sort direction value
     */
    private Sort(Object propertyId, SortDirection direction) {
        previous = null;
        order = new SortOrder(propertyId, direction);
    }

    /**
     * Chaining constructor, called by the non-static then() methods. This
     * constructor links to the previous Sort object.
     * 
     * @param previous
     *            the sort marker that comes before this one
     * @param propertyId
     *            a property ID, corresponding to a property in the data source
     * @param direction
     *            a sort direction value
     */
    private Sort(Sort previous, Object propertyId, SortDirection direction) {
        this.previous = previous;
        order = new SortOrder(propertyId, direction);

        Sort s = previous;
        while (s != null) {
            if (s.order.getPropertyId() == propertyId) {
                throw new IllegalStateException(
                        "Can not sort along the same property (" + propertyId
                                + ") twice!");
            }
            s = s.previous;
        }

    }

    /**
     * Start building a Sort order by sorting a provided column in ascending
     * order.
     * 
     * @param propertyId
     *            a property id, corresponding to a data source property
     * @return a sort object
     */
    public static Sort by(Object propertyId) {
        return by(propertyId, SortDirection.ASCENDING);
    }

    /**
     * Start building a Sort order by sorting a provided column.
     * 
     * @param propertyId
     *            a property id, corresponding to a data source property
     * @param direction
     *            a sort direction value
     * @return a sort object
     */
    public static Sort by(Object propertyId, SortDirection direction) {
        return new Sort(propertyId, direction);
    }

    /**
     * Continue building a Sort order. The provided property is sorted in
     * ascending order if the previously added properties have been evaluated as
     * equals.
     * 
     * @param propertyId
     *            a property id, corresponding to a data source property
     * @return a sort object
     */
    public Sort then(Object propertyId) {
        return then(propertyId, SortDirection.ASCENDING);
    }

    /**
     * Continue building a Sort order. The provided property is sorted in
     * specified order if the previously added properties have been evaluated as
     * equals.
     * 
     * @param propertyId
     *            a property id, corresponding to a data source property
     * @param direction
     *            a sort direction value
     * @return a sort object
     */
    public Sort then(Object propertyId, SortDirection direction) {
        return new Sort(this, propertyId, direction);
    }

    /**
     * Build a sort order list, ready to be passed to Grid
     * 
     * @return a sort order list.
     */
    public List<SortOrder> build() {

        int count = 1;
        Sort s = this;
        while (s.previous != null) {
            s = s.previous;
            ++count;
        }

        List<SortOrder> order = new ArrayList<SortOrder>(count);

        s = this;
        do {
            order.add(0, s.order);
            s = s.previous;
        } while (s != null);

        return order;
    }
}
