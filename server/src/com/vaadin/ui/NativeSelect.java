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

/**
 * This is a simple drop-down select without, for instance, support for
 * multiselect, new items, lazyloading, and other advanced features. Sometimes
 * "native" select without all the bells-and-whistles of the ComboBox is a
 * better choice.
 */
@SuppressWarnings("serial")
public class NativeSelect extends AbstractSelect {

    // width in characters, mimics TextField
    private int columns = 0;

    public NativeSelect() {
        super();
    }

    public NativeSelect(String caption, Collection<?> options) {
        super(caption, options);
    }

    public NativeSelect(String caption, Container dataSource) {
        super(caption, dataSource);
    }

    public NativeSelect(String caption) {
        super(caption);
    }

    /**
     * Sets the width of the component so that it can display approximately the
     * given number of letters.
     * <p>
     * Calling {@code setColumns(10);} is equivalent to calling
     * {@code setWidth("10em");}
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

    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        target.addAttribute("type", "native");
        // Adds the number of columns
        if (columns != 0) {
            target.addAttribute("cols", columns);
        }

        super.paintContent(target);
    }

    @Override
    public void setMultiSelect(boolean multiSelect)
            throws UnsupportedOperationException {
        if (multiSelect == true) {
            throw new UnsupportedOperationException("Multiselect not supported");
        }
    }

    @Override
    public void setNewItemsAllowed(boolean allowNewOptions)
            throws UnsupportedOperationException {
        if (allowNewOptions == true) {
            throw new UnsupportedOperationException(
                    "newItemsAllowed not supported");
        }
    }

}
