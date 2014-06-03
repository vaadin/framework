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

package com.vaadin.ui;

import java.util.Collection;

import com.vaadin.data.Container;
import com.vaadin.server.PaintException;
import com.vaadin.server.PaintTarget;
import com.vaadin.shared.ui.twincolselect.TwinColSelectConstants;

/**
 * Multiselect component with two lists: left side for available items and right
 * side for selected items.
 */
@SuppressWarnings("serial")
public class TwinColSelect extends AbstractSelect {

    private int columns = 0;
    private int rows = 0;

    private String leftColumnCaption;
    private String rightColumnCaption;

    /**
     * 
     */
    public TwinColSelect() {
        super();
        setMultiSelect(true);
    }

    /**
     * @param caption
     */
    public TwinColSelect(String caption) {
        super(caption);
        setMultiSelect(true);
    }

    /**
     * @param caption
     * @param dataSource
     */
    public TwinColSelect(String caption, Container dataSource) {
        super(caption, dataSource);
        setMultiSelect(true);
    }

    /**
     * Sets the width of the component so that it displays approximately the
     * given number of letters in each of the two selects.
     * <p>
     * Calling {@code setColumns(10);} is roughly equivalent to calling
     * {@code setWidth((10*2+4)+"10em");}
     * </p>
     * 
     * @deprecated As of 7.0. "Columns" does not reflect the exact number of
     *             characters that will be displayed. It is better to use
     *             setWidth together with "em" to control the width of the
     *             field.
     * @param columns
     *            the number of columns to set.
     */
    @Deprecated
    public void setColumns(int columns) {
        if (columns < 0) {
            columns = 0;
        }
        if (this.columns != columns) {
            this.columns = columns;
            markAsDirty();
        }
    }

    /**
     * Gets the number of columns for the component.
     * 
     * @see #setColumns(int)
     * @deprecated As of 7.0. "Columns" does not reflect the exact number of
     *             characters that will be displayed. It is better to use
     *             setWidth together with "em" to control the width of the
     *             field.
     */
    @Deprecated
    public int getColumns() {
        return columns;
    }

    public int getRows() {
        return rows;
    }

    /**
     * Sets the number of rows in the editor. If the number of rows is set to 0,
     * the actual number of displayed rows is determined implicitly by the
     * adapter.
     * <p>
     * If a height if set (using {@link #setHeight(String)} or
     * {@link #setHeight(float, int)}) it overrides the number of rows. Leave
     * the height undefined to use this method. This is the opposite of how
     * {@link #setColumns(int)} work.
     * 
     * 
     * @param rows
     *            the number of rows to set.
     */
    public void setRows(int rows) {
        if (rows < 0) {
            rows = 0;
        }
        if (this.rows != rows) {
            this.rows = rows;
            markAsDirty();
        }
    }

    /**
     * @param caption
     * @param options
     */
    public TwinColSelect(String caption, Collection<?> options) {
        super(caption, options);
        setMultiSelect(true);
    }

    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        target.addAttribute("type", "twincol");
        // Adds the number of columns
        if (columns != 0) {
            target.addAttribute("cols", columns);
        }
        // Adds the number of rows
        if (rows != 0) {
            target.addAttribute("rows", rows);
        }

        // Right and left column captions and/or icons (if set)
        String lc = getLeftColumnCaption();
        String rc = getRightColumnCaption();
        if (lc != null) {
            target.addAttribute(TwinColSelectConstants.ATTRIBUTE_LEFT_CAPTION,
                    lc);
        }
        if (rc != null) {
            target.addAttribute(TwinColSelectConstants.ATTRIBUTE_RIGHT_CAPTION,
                    rc);
        }

        super.paintContent(target);
    }

    /**
     * Sets the text shown above the right column.
     * 
     * @param caption
     *            The text to show
     */
    public void setRightColumnCaption(String rightColumnCaption) {
        this.rightColumnCaption = rightColumnCaption;
        markAsDirty();
    }

    /**
     * Returns the text shown above the right column.
     * 
     * @return The text shown or null if not set.
     */
    public String getRightColumnCaption() {
        return rightColumnCaption;
    }

    /**
     * Sets the text shown above the left column.
     * 
     * @param caption
     *            The text to show
     */
    public void setLeftColumnCaption(String leftColumnCaption) {
        this.leftColumnCaption = leftColumnCaption;
        markAsDirty();
    }

    /**
     * Returns the text shown above the left column.
     * 
     * @return The text shown or null if not set.
     */
    public String getLeftColumnCaption() {
        return leftColumnCaption;
    }

}
