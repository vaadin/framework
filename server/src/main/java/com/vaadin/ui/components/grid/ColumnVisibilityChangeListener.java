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

import java.io.Serializable;

import com.vaadin.ui.Grid;

/**
 * An event listener for column visibility change events in the Grid.
 *
 * @author Vaadin Ltd
 * @since 8.0
 */
@FunctionalInterface
public interface ColumnVisibilityChangeListener extends Serializable {

    /**
     * Called when a column has become hidden or unhidden.
     *
     * @param event
     *            the event
     */
    void columnVisibilityChanged(Grid.ColumnVisibilityChangeEvent event);
}
