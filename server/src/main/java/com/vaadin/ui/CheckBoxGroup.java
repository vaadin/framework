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
import java.util.function.Predicate;

import com.vaadin.data.Listing;
import com.vaadin.server.data.DataSource;
import com.vaadin.shared.ui.optiongroup.CheckBoxGroupState;

/**
 * A group of Checkboxes. Individual checkboxes are made from items supplied by
 * a {@link DataSource}. Checkboxes may have captions and icons.
 *
 * @param <T>
 *            item type
 * @author Vaadin Ltd
 * @since 8.0
 */
public class CheckBoxGroup<T> extends AbstractMultiSelect<T> {

    /**
     * Constructs a new CheckBoxGroup with caption.
     *
     * @param caption
     *            caption text
     * @see Listing#setDataSource(DataSource)
     */
    public CheckBoxGroup(String caption) {
        this();
        setCaption(caption);
    }

    /**
     * Constructs a new CheckBoxGroup with caption and DataSource.
     *
     * @param caption
     *            the caption text
     * @param dataSource
     *            the data source, not null
     * @see Listing#setDataSource(DataSource)
     */
    public CheckBoxGroup(String caption, DataSource<T> dataSource) {
        this(caption);
        setDataSource(dataSource);
    }

    /**
     * Constructs a new CheckBoxGroup with caption and DataSource containing
     * given items.
     *
     * @param caption
     *            the caption text
     * @param items
     *            the data items to use, not null
     * @see Listing#setDataSource(DataSource)
     */
    public CheckBoxGroup(String caption, Collection<T> items) {
        this(caption, DataSource.create(items));
    }

    /**
     * Constructs a new CheckBoxGroup.
     *
     * @see Listing#setDataSource(DataSource)
     */
    public CheckBoxGroup() {
    }

    /**
     * Sets whether html is allowed in the item captions. If set to true, the
     * captions are passed to the browser as html and the developer is
     * responsible for ensuring no harmful html is used. If set to false, the
     * content is passed to the browser as plain text.
     *
     * @param htmlContentAllowed
     *            true if the captions are used as html, false if used as plain
     *            text
     */
    public void setHtmlContentAllowed(boolean htmlContentAllowed) {
        getState().htmlContentAllowed = htmlContentAllowed;
    }

    /**
     * Checks whether captions are interpreted as html or plain text.
     *
     * @return true if the captions are used as html, false if used as plain
     *         text
     * @see #setHtmlContentAllowed(boolean)
     */
    public boolean isHtmlContentAllowed() {
        return getState(false).htmlContentAllowed;
    }

    @Override
    protected CheckBoxGroupState getState() {
        return (CheckBoxGroupState) super.getState();
    }

    @Override
    protected CheckBoxGroupState getState(boolean markAsDirty) {
        return (CheckBoxGroupState) super.getState(markAsDirty);
    }

    @Override
    public IconGenerator<T> getItemIconGenerator() {
        return super.getItemIconGenerator();
    }

    @Override
    public void setItemIconGenerator(IconGenerator<T> itemIconGenerator) {
        super.setItemIconGenerator(itemIconGenerator);
    }

    @Override
    public Predicate<T> getItemEnabledProvider() {
        return super.getItemEnabledProvider();
    }

    @Override
    public void setItemEnabledProvider(Predicate<T> itemEnabledProvider) {
        super.setItemEnabledProvider(itemEnabledProvider);
    }
}
