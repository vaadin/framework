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
package com.vaadin.shared.ui.grid;

/**
 * Defines drop locations within a Grid row.
 *
 * @author Vaadin Ltd.
 * @since 8.1
 */
public enum DropLocation {

    /**
     * Drop on top of the row.
     */
    ON_TOP,

    /**
     * Drop above or before the row.
     */
    ABOVE,

    /**
     * Drop below or after the row.
     */
    BELOW,

    /**
     * Dropping into an empty grid, to a sorted grid, when
     * {@link DropMode#ON_GRID} is used, or to the empty area below the grid
     * rows when {@link DropMode#ON_TOP} is used.
     */
    EMPTY;
}
