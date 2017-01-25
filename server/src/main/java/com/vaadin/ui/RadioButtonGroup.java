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
import java.util.Objects;
import java.util.Set;

import org.jsoup.nodes.Element;

import com.vaadin.data.HasDataProvider;
import com.vaadin.data.provider.DataGenerator;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.BlurNotifier;
import com.vaadin.event.FieldEvents.FocusAndBlurServerRpcDecorator;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.event.FieldEvents.FocusNotifier;
import com.vaadin.server.Resource;
import com.vaadin.server.ResourceReference;
import com.vaadin.server.SerializablePredicate;
import com.vaadin.shared.Registration;
import com.vaadin.shared.ui.ListingJsonConstants;
import com.vaadin.shared.ui.optiongroup.RadioButtonGroupState;
import com.vaadin.ui.declarative.DesignContext;
import com.vaadin.ui.declarative.DesignFormatter;

import elemental.json.JsonObject;

/**
 * A group of RadioButtons. Individual radiobuttons are made from items supplied
 * by a {@link DataProvider}. RadioButtons may have captions and icons.
 *
 * @param <T>
 *            item type
 * @author Vaadin Ltd
 * @since 8.0
 */
public class RadioButtonGroup<T> extends AbstractSingleSelect<T>
        implements FocusNotifier, BlurNotifier, HasDataProvider<T> {

    private SerializablePredicate<T> itemEnabledProvider = item -> true;

    /**
     * Constructs a new RadioButtonGroup with caption.
     *
     * @param caption
     *            caption text
     */
    public RadioButtonGroup(String caption) {
        this();
        setCaption(caption);
    }

    /**
     * Constructs a new RadioButtonGroup with caption and DataProvider.
     *
     * @param caption
     *            the caption text
     * @param dataProvider
     *            the data provider, not null
     * @see HasDataProvider#setDataProvider(DataProvider)
     */
    public RadioButtonGroup(String caption, DataProvider<T, ?> dataProvider) {
        this(caption);
        setDataProvider(dataProvider);
    }

    /**
     * Constructs a new RadioButtonGroup with caption and DataProvider
     * containing given items.
     *
     * @param caption
     *            the caption text
     * @param items
     *            the data items to use, not null
     * @see #setItems(Collection)
     */
    public RadioButtonGroup(String caption, Collection<T> items) {
        this(caption, DataProvider.ofCollection(items));
    }

    /**
     * Constructs a new RadioButtonGroup.
     */
    public RadioButtonGroup() {
        registerRpc(new FocusAndBlurServerRpcDecorator(this, this::fireEvent));

        addDataGenerator(new DataGenerator<T>() {
            @Override
            public void generateData(T data, JsonObject jsonObject) {
                String caption = getItemCaptionGenerator().apply(data);
                if (caption != null) {
                    jsonObject.put(ListingJsonConstants.JSONKEY_ITEM_VALUE,
                            caption);
                } else {
                    jsonObject.put(ListingJsonConstants.JSONKEY_ITEM_VALUE, "");
                }
                Resource icon = getItemIconGenerator().apply(data);
                if (icon != null) {
                    String iconUrl = ResourceReference
                            .create(icon, RadioButtonGroup.this, null).getURL();
                    jsonObject.put(ListingJsonConstants.JSONKEY_ITEM_ICON,
                            iconUrl);
                }
                if (!itemEnabledProvider.test(data)) {
                    jsonObject.put(ListingJsonConstants.JSONKEY_ITEM_DISABLED,
                            true);
                }

                if (isSelected(data)) {
                    jsonObject.put(ListingJsonConstants.JSONKEY_ITEM_SELECTED,
                            true);
                }
            }

            @Override
            public void destroyData(T data) {
            }
        });

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
    protected RadioButtonGroupState getState() {
        return (RadioButtonGroupState) super.getState();
    }

    @Override
    protected RadioButtonGroupState getState(boolean markAsDirty) {
        return (RadioButtonGroupState) super.getState(markAsDirty);
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
    public ItemCaptionGenerator<T> getItemCaptionGenerator() {
        return super.getItemCaptionGenerator();
    }

    @Override
    public void setItemCaptionGenerator(
            ItemCaptionGenerator<T> itemCaptionGenerator) {
        super.setItemCaptionGenerator(itemCaptionGenerator);
    }

    /**
     * Returns the item enabled predicate.
     *
     * @return the item enabled predicate
     * @see #setItemEnabledProvider
     */
    public SerializablePredicate<T> getItemEnabledProvider() {
        return itemEnabledProvider;
    }

    /**
     * Sets the item enabled predicate for this radiobutton group. The predicate
     * is applied to each item to determine whether the item should be enabled
     * (true) or disabled (false). Disabled items are displayed as grayed out
     * and the user cannot select them. The default predicate always returns
     * true (all the items are enabled).
     *
     * @param itemEnabledProvider
     *            the item enable predicate, not null
     */
    public void setItemEnabledProvider(
            SerializablePredicate<T> itemEnabledProvider) {
        Objects.requireNonNull(itemEnabledProvider);
        this.itemEnabledProvider = itemEnabledProvider;
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
