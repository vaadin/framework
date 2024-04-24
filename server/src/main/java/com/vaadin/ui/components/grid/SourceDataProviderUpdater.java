/*
 * Copyright 2000-2022 Vaadin Ltd.
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
package com.vaadin.ui.components.grid;

import java.io.Serializable;
import java.util.Collection;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.shared.ui.dnd.DropEffect;

/**
 * A handler for source grid data provider updater for {@link GridRowDragger}.
 *
 * Used to handle updates to the source grid's {@link DataProvider} after a
 * drop.
 *
 * @author Vaadin Ltd
 * @since 8.2
 *
 * @param <T>
 *            the bean type
 */
@FunctionalInterface
public interface SourceDataProviderUpdater<T> extends Serializable {

    /**
     * A NOOP updater that does not do anything for the source data provider.
     */
    static SourceDataProviderUpdater NOOP = (e, dp, i) -> {
    };

    /**
     * Called when Items have been dragged.
     *
     * @param dropEffect
     *            the reported drop effect from the drop event
     * @param dataProvider
     *            the data provider for the source grid
     * @param items
     *            dragged items.
     */
    public void removeItems(DropEffect dropEffect,
            DataProvider<T, ?> dataProvider, Collection<T> items);
}
