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
package com.vaadin.event.selection;

import java.util.Optional;
import java.util.Set;

import com.vaadin.event.HasUserOriginated;

/**
 * A selection event that unifies the way to access to selection event for multi
 * selection and single selection components (in case when only one selected
 * item is required).
 *
 * @since 8.0
 * @author Vaadin Ltd
 * @param <T>
 *            the data type of the selection model
 */
public interface SelectionEvent<T> extends HasUserOriginated {

    /**
     * Get first selected data item.
     * <p>
     * This is the same as {@link SingleSelectionEvent#getSelectedItem()} in
     * case of single selection and the first selected item from
     * {@link MultiSelectionEvent#getNewSelection()} in case of multi selection.
     *
     * @return the first selected item.
     */
    Optional<T> getFirstSelectedItem();

    /**
     * Gets all the currently selected items.
     * <p>
     * This method applies more to multiselection - for single select it returns
     * either an empty set or a set containing the only selected item.
     *
     * @return return all the selected items, if any, never {@code null}
     */
    Set<T> getAllSelectedItems();
}
