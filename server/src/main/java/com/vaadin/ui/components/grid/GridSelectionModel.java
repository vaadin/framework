/*
 * Copyright 2000-2016 Vaadin Ltd.
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

import com.vaadin.data.SelectionModel;
import com.vaadin.server.Extension;
import com.vaadin.ui.Grid.AbstractGridExtension;

/**
 * The server-side interface that controls Grid's selection state.
 * SelectionModel should extend {@link AbstractGridExtension}.
 *
 * @author Vaadin Ltd
 * @since 8.0
 *
 * @param <T>
 *            the grid bean type
 * @see AbstractSelectionModel
 * @see SingleSelectionModel
 * @see MultiSelectionModel
 */
public interface GridSelectionModel<T> extends SelectionModel<T>, Extension {

    /**
     * Removes this selection model from the grid.
     * <p>
     * Must call super {@link Extension#remove()} to detach the extension, and
     * fire an selection change event for the selection model (with an empty
     * selection).
     */
    @Override
    public void remove();

    /**
     * Sets whether the user is allowed to change the selection.
     * <p>
     * The check is done only for the client side actions. It doesn't affect
     * selection requests sent from the server side.
     *
     * @param allowed
     *            <code>true</code> if the user is allowed to change the
     *            selection, <code>false</code> otherwise
     */
    public void setUserSelectionAllowed(boolean allowed);

    /**
     * Checks if the user is allowed to change the selection.
     * <p>
     * The check is done only for the client side actions. It doesn't affect
     * selection requests sent from the server side.
     *
     * @return <code>true</code> if the user is allowed to change the selection,
     *         <code>false</code> otherwise
     */
    public boolean isUserSelectionAllowed();
}
