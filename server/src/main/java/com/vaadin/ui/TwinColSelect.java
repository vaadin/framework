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

package com.vaadin.ui;

import java.util.Collection;

import com.vaadin.data.HasDataProvider;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.shared.ui.twincolselect.TwinColSelectState;

/**
 * Multiselect component with two lists: left side for available items and right
 * side for selected items.
 *
 * @author Vaadin Ltd
 *
 * @param <T>
 *            item type
 */
public class TwinColSelect<T> extends AbstractMultiSelect<T>
        implements HasDataProvider<T> {

    /**
     * Constructs a new TwinColSelect.
     */
    public TwinColSelect() {
    }

    /**
     * Constructs a new TwinColSelect with the given caption.
     *
     * @param caption
     *            the caption to set, can be {@code null}
     */
    public TwinColSelect(String caption) {
        this();
        setCaption(caption);
    }

    /**
     * Constructs a new TwinColSelect with caption and data provider for
     * options.
     *
     * @param caption
     *            the caption to set, can be {@code null}
     * @param dataProvider
     *            the data provider, not {@code null}
     * @since 8.0
     */
    public TwinColSelect(String caption, DataProvider<T, ?> dataProvider) {
        this(caption);
        setDataProvider(dataProvider);
    }

    /**
     * Constructs a new TwinColSelect with caption and the given options.
     *
     * @param caption
     *            the caption to set, can be {@code null}
     * @param options
     *            the options, cannot be {@code null}
     */
    public TwinColSelect(String caption, Collection<T> options) {
        this(caption, DataProvider.ofCollection(options));
    }

    /**
     * Returns the number of rows in the selects.
     *
     * @return the number of rows visible
     */
    public int getRows() {
        return getState(false).rows;
    }

    /**
     * Sets the number of rows in the selects. If the number of rows is set to 0
     * or less, the actual number of displayed rows is determined implicitly by
     * the selects.
     * <p>
     * If a height if set (using {@link #setHeight(String)} or
     * {@link #setHeight(float, Unit)}) it overrides the number of rows. Leave
     * the height undefined to use this method.
     *
     * @param rows
     *            the number of rows to set.
     */
    public void setRows(int rows) {
        if (rows < 0) {
            rows = 0;
        }
        if (getState(false).rows != rows) {
            getState().rows = rows;
        }
    }

    /**
     * Sets the text shown above the right column. {@code null} clears the
     * caption.
     *
     * @param rightColumnCaption
     *            The text to show, {@code null} to clear
     */
    public void setRightColumnCaption(String rightColumnCaption) {
        getState().rightColumnCaption = rightColumnCaption;
    }

    /**
     * Returns the text shown above the right column.
     *
     * @return The text shown or {@code null} if not set.
     */
    public String getRightColumnCaption() {
        return getState(false).rightColumnCaption;
    }

    /**
     * Sets the text shown above the left column. {@code null} clears the
     * caption.
     *
     * @param leftColumnCaption
     *            The text to show, {@code null} to clear
     */
    public void setLeftColumnCaption(String leftColumnCaption) {
        getState().leftColumnCaption = leftColumnCaption;
        markAsDirty();
    }

    /**
     * Returns the text shown above the left column.
     *
     * @return The text shown or {@code null} if not set.
     */
    public String getLeftColumnCaption() {
        return getState(false).leftColumnCaption;
    }

    @Override
    protected TwinColSelectState getState() {
        return (TwinColSelectState) super.getState();
    }

    @Override
    protected TwinColSelectState getState(boolean markAsDirty) {
        return (TwinColSelectState) super.getState(markAsDirty);
    }

    @Override
    public DataProvider<T, ?> getDataProvider() {
        return internalGetDataProvider();
    }

    @Override
    public void setDataProvider(DataProvider<T, ?> dataProvider) {
        internalSetDataProvider(dataProvider);
    }

}
