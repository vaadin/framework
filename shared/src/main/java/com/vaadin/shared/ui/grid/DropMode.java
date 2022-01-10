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
package com.vaadin.shared.ui.grid;

/**
 * Defines the locations within the Grid row where an element can be dropped.
 *
 * @author Vaadin Ltd.
 * @since 8.1
 */
public enum DropMode {

    /**
     * The drop event can happen between Grid rows. The drop is above a row when
     * the cursor is over the top 50% of a row, otherwise below the row.
     */
    BETWEEN,

    /**
     * The drop event can happen on top of Grid rows. The target of the drop is
     * the row under the cursor at the time of the drop event.
     */
    ON_TOP,

    /**
     * The drop event can happen either on top of or between Grid rows. The drop
     * is either
     * <ul>
     * <li><i>above</i> a row when the cursor is over a specified portion of the
     * top part of the row,</li>
     * <li><i>below</i> when the cursor is over a specified portion of the
     * bottom part of the row, or</li>
     * <li><i>on top</i> when the cursor is over the row but doesn't match the
     * above conditions.</li>
     * </ul>
     */
    ON_TOP_OR_BETWEEN,

    /**
     * The drop event will not happen on any specific row, it will show the drop
     * target outline around the whole grid. For this drop target, the drop
     * event will not contain any target row information. This is the drop
     * target used when the grid is completely empty. It can also be configured
     * to be used automatically when the user has sorted the grid.
     * <p>
     * When this mode is used, it also recommended to automatically scroll the
     * dropped data (new rows) to be visible for the user.
     *
     * @since 8.2
     */
    ON_GRID;
}
