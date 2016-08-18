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
import com.vaadin.shared.ui.twincolselect.TwinColSelectState;

/**
 * Multiselect component with two lists: left side for available items and right
 * side for selected items.
 */
@SuppressWarnings("serial")
public class TwinColSelect extends AbstractSelect {

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
        // Adds the number of columns
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

    @Override
    protected TwinColSelectState getState() {
        return (TwinColSelectState) super.getState();
    }

}
