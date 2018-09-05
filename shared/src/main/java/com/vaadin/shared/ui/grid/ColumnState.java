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

import com.vaadin.shared.Connector;
import com.vaadin.shared.ui.ContentMode;

/**
 * Shared state for a Grid column.
 *
 * @author Vaadin Ltd
 * @since 8.0
 */
public class ColumnState extends AbstractGridExtensionState {

    public String caption;
    public String internalId;
    public boolean sortable = false;
    public boolean editable = false;

    /**
     * Sets whether Grid should handle events from Widgets from this column or
     * not.
     *
     * @since 8.3
     */
    public boolean handleWidgetEvents = false;

    /** The assistive device caption for the column. */
    public String assistiveCaption;

    /** The caption for the column hiding toggle. */
    public String hidingToggleCaption;

    /** Whether this column is currently hidden. */
    public boolean hidden = false;

    /** Whether the column can be hidden by the user. */
    public boolean hidable = false;

    /**
     * Column width in pixels. Default column width is
     * {@value GridConstants#DEFAULT_COLUMN_WIDTH_PX}.
     */
    public double width = GridConstants.DEFAULT_COLUMN_WIDTH_PX;

    /** How much of the remaining space this column will reserve. */
    public int expandRatio = GridConstants.DEFAULT_EXPAND_RATIO;

    /**
     * The maximum expansion width of this column. -1 for "no maximum". If
     * maxWidth is less than the calculated width, maxWidth is ignored.
     */
    public double maxWidth = GridConstants.DEFAULT_MAX_WIDTH;

    /**
     * The minimum expansion width of this column. -1 for "no minimum". If
     * minWidth is less than the calculated width, minWidth will win.
     */
    public double minWidth = GridConstants.DEFAULT_MIN_WIDTH;

    /** Whether this column is resizable by the user. */
    public boolean resizable = true;

    public Connector renderer;
    /**
     * Whether the contents define the minimum width for this column.
     *
     * @since 8.1
     */
    public boolean minimumWidthFromContent = true;

    /**
     * The content mode for tooltips.
     *
     * @since 8.2
     */
    public ContentMode tooltipContentMode;
}
