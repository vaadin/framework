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
package com.vaadin.shared.ui.grid;

import java.io.Serializable;

import com.vaadin.shared.Connector;

/**
 * Column state DTO for transferring column properties from the server to the
 * client
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
public class GridColumnState implements Serializable {

    /**
     * Id used by grid connector to map server side column with client side
     * column
     */
    public String id;

    /**
     * Column width in pixels. Default column width is
     * {@value GridConstants#DEFAULT_COLUMN_WIDTH_PX}.
     */
    public double width = GridConstants.DEFAULT_COLUMN_WIDTH_PX;

    /**
     * The connector for the renderer used to render the cells in this column.
     */
    public Connector rendererConnector;

    /**
     * Whether the values in this column are editable when the editor interface
     * is active.
     */
    public boolean editable = true;

    /**
     * The connector for the field used to edit cells in this column when the
     * editor interface is active.
     */
    public Connector editorConnector;

    /**
     * Are sorting indicators shown for a column. Default is false.
     */
    public boolean sortable = false;

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

    /** Is the column currently hidden. */
    public boolean hidden = false;

    /** Can the column be hidden by the UI. */
    public boolean hidable = false;

    /** The caption for the column hiding toggle. */
    public String hidingToggleCaption;

    /** Column header caption */
    public String headerCaption;
}
