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
package com.vaadin.ui.components.grid.sort;

import java.io.Serializable;

import com.vaadin.shared.ui.grid.SortDirection;

/**
 * Sort order descriptor. Links together a {@link SortDirection} value and a
 * Vaadin container property ID.
 *
 * @since 7.4
 * @author Vaadin Ltd
 */
public class SortOrder implements Serializable {

    private final Object propertyId;
    private final SortDirection direction;

    /**
     * Create a SortOrder object. Both arguments must be non-null.
     *
     * @param propertyId
     *            id of the data source property to sort by
     * @param direction
     *            value indicating whether the property id should be sorted in
     *            ascending or descending order
     */
    public SortOrder(Object propertyId, SortDirection direction) {
        if (propertyId == null) {
            throw new IllegalArgumentException("Property ID can not be null!");
        }
        if (direction == null) {
            throw new IllegalArgumentException(
                    "Direction value can not be null!");
        }
        this.propertyId = propertyId;
        this.direction = direction;
    }

    /**
     * Returns the property ID.
     *
     * @return a property ID
     */
    public Object getPropertyId() {
        return propertyId;
    }

    /**
     * Returns the {@link SortDirection} value.
     *
     * @return a sort direction value
     */
    public SortDirection getDirection() {
        return direction;
    }

}
