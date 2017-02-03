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
import java.util.Set;

import org.jsoup.nodes.Element;

import com.vaadin.data.HasDataProvider;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.BlurNotifier;
import com.vaadin.event.FieldEvents.FocusAndBlurServerRpcDecorator;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.event.FieldEvents.FocusNotifier;
import com.vaadin.server.SerializablePredicate;
import com.vaadin.shared.Registration;
import com.vaadin.shared.ui.optiongroup.CheckBoxGroupState;
import com.vaadin.ui.declarative.DesignContext;
import com.vaadin.ui.declarative.DesignFormatter;

/**
 * A group of Checkboxes. Individual checkboxes are made from items supplied by
 * a {@link DataProvider}. Checkboxes may have captions and icons.
 *
 * @param <T>
 *            item type
 * @author Vaadin Ltd
 * @since 8.0
 */
public class CheckBoxGroup<T> extends AbstractMultiSelect<T>
        implements FocusNotifier, BlurNotifier, HasDataProvider<T> {

    /**
     * Constructs a new CheckBoxGroup with caption.
     *
     * @param caption
     *            caption text
     */
    public CheckBoxGroup(String caption) {
        this();
        setCaption(caption);
    }

    /**
     * Constructs a new CheckBoxGroup with caption and DataProvider.
     *
     * @param caption
     *            the caption text
     * @param dataProvider
     *            the data provider, not null
     * @see HasDataProvider#setDataProvider(DataProvider)
     */
    public CheckBoxGroup(String caption, DataProvider<T, ?> dataProvider) {
        this(caption);
        setDataProvider(dataProvider);
    }

    /**
     * Constructs a new CheckBoxGroup with caption and DataProvider containing
     * given items.
     *
     * @param caption
     *            the caption text
     * @param items
     *            the data items to use, not null
     * @see #setItems(Collection)
     */
    public CheckBoxGroup(String caption, Collection<T> items) {
        this(caption, DataProvider.ofCollection(items));
    }

    /**
     * Constructs a new CheckBoxGroup.
     */
    public CheckBoxGroup() {
        registerRpc(new FocusAndBlurServerRpcDecorator(this, this::fireEvent));
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
    public SerializablePredicate<T> getItemEnabledProvider() {
        return super.getItemEnabledProvider();
    }

    @Override
    public void setItemEnabledProvider(
            SerializablePredicate<T> itemEnabledProvider) {
        super.setItemEnabledProvider(itemEnabledProvider);
    }

    @Override
    public Registration addFocusListener(FocusListener listener) {
        return addListener(FocusEvent.EVENT_ID, FocusEvent.class, listener,
                FocusListener.focusMethod);
    }

    @Override
    public Registration addBlurListener(BlurListener listener) {
        return addListener(BlurEvent.EVENT_ID, BlurEvent.class, listener,
                BlurListener.blurMethod);
    }

    @Override
    protected void readItems(Element design, DesignContext context) {
        setItemEnabledProvider(new DeclarativeItemEnabledProvider<>());
        super.readItems(design, context);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    protected T readItem(Element child, Set<T> selected,
            DesignContext context) {
        T item = super.readItem(child, selected, context);

        SerializablePredicate<T> provider = getItemEnabledProvider();
        if (provider instanceof DeclarativeItemEnabledProvider) {
            if (child.hasAttr("disabled")) {
                ((DeclarativeItemEnabledProvider) provider).addDisabled(item);
            }
        } else {
            throw new IllegalStateException(String.format(
                    "Don't know how "
                            + "to disable item using current item enabled provider '%s'",
                    provider.getClass().getName()));
        }
        return item;
    }

    @Override
    protected Element writeItem(Element design, T item, DesignContext context) {
        Element elem = super.writeItem(design, item, context);

        if (!getItemEnabledProvider().test(item)) {
            elem.attr("disabled", "");
        }

        if (isHtmlContentAllowed()) {
            // need to unencode HTML entities. AbstractMultiSelect.writeDesign
            // can't check if HTML content is allowed, so it always encodes
            // entities like '>', '<' and '&'; in case HTML content is allowed
            // this is undesirable so we need to unencode entities. Entities
            // other than '<' and '>' will be taken care by Jsoup.
            elem.html(DesignFormatter.decodeFromTextNode(elem.html()));
        }

        return elem;
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
