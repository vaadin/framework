/*
 * Copyright 2000-2018 Vaadin Ltd.
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
package com.vaadin.v7.data.util;

import java.io.Serializable;

import com.vaadin.data.Binder;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.v7.data.Container.Filter;
import com.vaadin.v7.data.sort.SortOrder;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.util.filter.UnsupportedFilterException;

/**
 * PropertyValueGenerator for GeneratedPropertyContainer.
 *
 * @param <T>
 *            Property data type
 * @since 7.4
 * @author Vaadin Ltd
 *
 * @deprecated As of 8.0, no direct replacement available. You can use any bean directly as an item for {@link Binder}
 * or {@link DataProvider} and access item properties with lambdas like {@code binder.forField(component).bind(...)} or
 * {@code new Grid<Bean>(dataProvider).addColumn(bean->bean.getSomething())}.
 */
@Deprecated
public abstract class PropertyValueGenerator<T> implements Serializable {

    /**
     * Returns value for given Item. Used by GeneratedPropertyContainer when
     * generating new properties.
     *
     * @param item
     *            currently handled item
     * @param itemId
     *            item id for currently handled item
     * @param propertyId
     *            id for this property
     * @return generated value
     */
    public abstract T getValue(Item item, Object itemId, Object propertyId);

    /**
     * Return Property type for this generator. This function is called when
     * {@link Property#getType()} is called for generated property.
     *
     * @return type of generated property
     */
    public abstract Class<T> getType();

    /**
     * Translates sorting of the generated property in a specific direction to a
     * set of property ids and directions in the underlying container.
     * <p>
     * SortOrder is similar to (or the same as) the SortOrder already defined
     * for Grid.
     * <p>
     * The default implementation of this method returns an empty array, which
     * means that the property will not be included in
     * getSortableContainerPropertyIds(). Attempting to sort by that column
     * throws UnsupportedOperationException.
     *
     * Returning null is not allowed.
     *
     * @param order
     *            a sort order for this property
     * @return an array of sort orders describing how this property is sorted
     */
    public SortOrder[] getSortProperties(SortOrder order) {
        return new SortOrder[] {};
    }

    /**
     * Return an updated filter that should be compatible with the underlying
     * container.
     * <p>
     * This function is called when setting a filter for this generated
     * property. Returning null from this function causes
     * GeneratedPropertyContainer to discard the filter and not use it.
     * <p>
     * By default this function throws UnsupportedFilterException.
     *
     * @param filter
     *            original filter for this property
     * @return modified filter that is compatible with the underlying container
     * @throws UnsupportedFilterException
     *             if the implementation doesn't support modifying the provided
     *             filter
     */
    public Filter modifyFilter(Filter filter)
            throws UnsupportedFilterException {
        throw new UnsupportedFilterException(
                "Filter" + filter + " is not supported");
    }

}
