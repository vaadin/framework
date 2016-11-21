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

import com.vaadin.data.Listing;
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
import com.vaadin.server.data.DataGenerator;
import com.vaadin.server.data.DataProvider;
import com.vaadin.shared.Registration;
import com.vaadin.shared.ui.ListingJsonConstants;
import com.vaadin.shared.ui.optiongroup.RadioButtonGroupState;

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
        implements FocusNotifier, BlurNotifier {

    private IconGenerator<T> itemIconGenerator = item -> null;

    private ItemCaptionGenerator<T> itemCaptionGenerator = String::valueOf;

    private SerializablePredicate<T> itemEnabledProvider = item -> true;

    /**
     * Constructs a new RadioButtonGroup with caption.
     *
     * @param caption
     *            caption text
     * @see Listing#setDataProvider(DataProvider)
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
     * @see Listing#setDataProvider(DataProvider)
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
     * @see Listing#setDataProvider(DataProvider)
     */
    public RadioButtonGroup(String caption, Collection<T> items) {
        this(caption, DataProvider.create(items));
    }

    /**
     * Constructs a new RadioButtonGroup.
     *
     * @see Listing#setDataProvider(DataProvider)
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

    /**
     * Returns the item icon generator.
     *
     * @return the currently set icon generator
     * @see #setItemIconGenerator
     * @see IconGenerator
     */
    public IconGenerator<T> getItemIconGenerator() {
        return itemIconGenerator;
    }

    /**
     * Sets the item icon generator for this radiobutton group. The icon
     * generator is queried for each item to optionally display an icon next to
     * the item caption. If the generator returns null for an item, no icon is
     * displayed. The default generator always returns null (no icons).
     *
     * @param itemIconGenerator
     *            the icon generator, not null
     * @see IconGenerator
     */
    public void setItemIconGenerator(IconGenerator<T> itemIconGenerator) {
        Objects.requireNonNull(itemIconGenerator,
                "Item icon generator cannot be null.");
        this.itemIconGenerator = itemIconGenerator;
    }

    /**
     * Returns the currently set item caption generator.
     *
     * @return the currently set caption generator
     * @see #setItemCaptionGenerator
     * @see ItemCaptionGenerator
     */
    public ItemCaptionGenerator<T> getItemCaptionGenerator() {
        return itemCaptionGenerator;
    }

    /**
     * Sets the item caption generator for this radiobutton group. The caption
     * generator is queried for each item to optionally display an item textual
     * representation. The default generator returns
     * {@code String.valueOf(item)}.
     *
     * @param itemCaptionGenerator
     *            the item caption generator, not null
     * @see ItemCaptionGenerator
     */
    public void setItemCaptionGenerator(
            ItemCaptionGenerator<T> itemCaptionGenerator) {
        Objects.requireNonNull(itemCaptionGenerator,
                "Item caption generator cannot be null.");
        this.itemCaptionGenerator = itemCaptionGenerator;
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
        addListener(FocusEvent.EVENT_ID, FocusEvent.class, listener,
                FocusListener.focusMethod);
        return () -> removeListener(FocusEvent.EVENT_ID, FocusEvent.class,
                listener);
    }

    @Override
    @Deprecated
    public void removeFocusListener(FocusListener listener) {
        removeListener(FocusEvent.EVENT_ID, FocusEvent.class, listener);
    }

    @Override
    public Registration addBlurListener(BlurListener listener) {
        addListener(BlurEvent.EVENT_ID, BlurEvent.class, listener,
                BlurListener.blurMethod);
        return () -> removeListener(BlurEvent.EVENT_ID, BlurEvent.class,
                listener);
    }

    @Override
    @Deprecated
    public void removeBlurListener(BlurListener listener) {
        removeListener(BlurEvent.EVENT_ID, BlurEvent.class, listener);
    }
}
