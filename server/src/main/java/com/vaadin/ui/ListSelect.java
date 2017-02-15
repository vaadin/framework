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
import com.vaadin.shared.ui.listselect.ListSelectState;

/**
 * This is a simple list select without, for instance, support for new items,
 * lazyloading, and other advanced features.
 *
 * @author Vaadin Ltd
 *
 * @param <T>
 *            item type
 */
public class ListSelect<T> extends AbstractMultiSelect<T>
        implements HasDataProvider<T> {

    /** Default number of rows visible for select. */
    // protected to allow javadoc linking
    protected static final int DEFAULT_ROWS = 10;

    /**
     * Constructs a new ListSelect.
     */
    public ListSelect() {
        setRows(DEFAULT_ROWS);
    }

    /**
     * Constructs a new ListSelect with the given caption.
     *
     * @param caption
     *            the caption to set, can be {@code null}
     */
    public ListSelect(String caption) {
        this();
        setCaption(caption);
    }

    /**
     * Constructs a new ListSelect with caption and data provider for options.
     *
     * @param caption
     *            the caption to set, can be {@code null}
     * @param dataProvider
     *            the data provider, not {@code null}
     * @since 8.0
     */
    public ListSelect(String caption, DataProvider<T, ?> dataProvider) {
        this(caption);
        setDataProvider(dataProvider);
    }

    /**
     * Constructs a new ListSelect with caption and the given options.
     *
     * @param caption
     *            the caption to set, can be {@code null}
     * @param options
     *            the options, cannot be {@code null}
     */
    public ListSelect(String caption, Collection<T> options) {
        this(caption, DataProvider.ofCollection(options));
    }

    /**
     * Returns the number of rows in the select.
     * <p>
     * Default value is {@link #DEFAULT_ROWS}
     *
     * @return the number of rows visible
     */
    public int getRows() {
        return getState(false).rows;
    }

    /**
     * Sets the number of rows in the select. If the number of rows is set to 0,
     * the actual number of displayed rows is determined implicitly by the
     * select.
     * <p>
     * If a height if set (using {@link #setHeight(String)} or
     * {@link #setHeight(float, Unit)}) it overrides the number of rows. Leave
     * the height undefined to use this method.
     * <p>
     * Default value is {@link #DEFAULT_ROWS}
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

    @Override
    protected ListSelectState getState() {
        return (ListSelectState) super.getState();
    }

    @Override
    protected ListSelectState getState(boolean markAsDirty) {
        return (ListSelectState) super.getState(markAsDirty);
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
