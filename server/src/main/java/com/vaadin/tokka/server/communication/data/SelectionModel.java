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
package com.vaadin.tokka.server.communication.data;

import java.util.Collection;

import com.vaadin.tokka.server.ListingExtension;
import com.vaadin.tokka.ui.components.HasValue;
import com.vaadin.tokka.ui.components.HasValue.ValueChange;
import com.vaadin.tokka.ui.components.Listing;
import com.vaadin.ui.Component;

/**
 * Generic selection model interface.
 * 
 * @since
 * @param <T>
 *            type of selected values
 */
public interface SelectionModel<T> extends ListingExtension<T> {

    public class SelectionChange<T> extends ValueChange<T> {
        public SelectionChange(SelectionModel<T> source, T value,
                boolean userOriginated) {
            super(source, value, userOriginated);
        }
    }

    /**
     * Selection model for selection a single value.
     *
     * @param <T>
     *            type of selected values
     */
    public interface Single<T> extends SelectionModel<T>, HasValue<T> {
    }

    /**
     * Selection model for selection multiple values.
     *
     * @param <T>
     *            type of selected values
     */
    public interface Multi<T>
            extends SelectionModel<T>, HasValue<Collection<T>> {
    }

    /**
     * Get current selection.
     * 
     * @return selection
     */
    Collection<T> getSelected();

    /**
     * Add this extension to the target listing component. SelectionModel can
     * only extend {@link Component}s that implement {@link Listing} interface.
     * This method should only be called from a listing component when changing
     * the selection model.
     *
     * @param target
     *            listing component to extend.
     *
     * @throws IllegalArgumentException
     */
    void setParentListing(Listing<T> target);

    /**
     * Selects the given object.
     * 
     * @param value
     *            selected value
     */
    void select(T value);

    /**
     * Deselects the given object.
     * 
     * @param value
     *            deselected value
     */
    void deselect(T value);
}
