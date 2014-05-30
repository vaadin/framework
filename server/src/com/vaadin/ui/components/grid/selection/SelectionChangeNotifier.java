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
package com.vaadin.ui.components.grid.selection;

import java.io.Serializable;

/**
 * The interface for adding and removing listeners for
 * {@link SelectionChangeEvent SelectionChangeEvents}.
 * 
 * @since 7.4.0
 * @author Vaadin Ltd
 */
public interface SelectionChangeNotifier extends Serializable {
    /**
     * Registers a new selection change listener
     * 
     * @param listener
     *            the listener to register
     */
    void addSelectionChangeListener(SelectionChangeListener listener);

    /**
     * Removes a previously registered selection change listener
     * 
     * @param listener
     *            the listener to remove
     */
    void removeSelectionChangeListener(SelectionChangeListener listener);
}
