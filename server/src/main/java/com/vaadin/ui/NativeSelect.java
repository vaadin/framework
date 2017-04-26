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

import com.vaadin.data.HasDataProvider;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.BlurNotifier;
import com.vaadin.event.FieldEvents.FocusAndBlurServerRpcDecorator;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.event.FieldEvents.FocusNotifier;
import com.vaadin.shared.Registration;
import com.vaadin.shared.data.DataCommunicatorConstants;
import com.vaadin.shared.ui.nativeselect.NativeSelectState;

/**
 * A simple drop-down select component. Represented on the client side by a
 * "native" HTML {@code <select>} element. Lacks advanced features such as lazy
 * loading, filtering, and adding new items.
 *
 * @author Vaadin Ltd.
 *
 * @param <T>
 *            the data item type
 *
 * @see com.vaadin.ui.ComboBox
 */
public class NativeSelect<T> extends AbstractSingleSelect<T>
        implements FocusNotifier, BlurNotifier, HasDataProvider<T> {

    /**
     * Creates a new {@code NativeSelect} with an empty caption and no items.
     */
    public NativeSelect() {
        registerRpc(new FocusAndBlurServerRpcDecorator(this, this::fireEvent));
        addDataGenerator((item, json) -> {
            String caption = getItemCaptionGenerator().apply(item);
            if (caption == null) {
                caption = "";
            }
            json.put(DataCommunicatorConstants.DATA, caption);
        });

        setItemCaptionGenerator(String::valueOf);
    }

    /**
     * Creates a new {@code NativeSelect} with the given caption and no items.
     *
     * @param caption
     *            the component caption to set, null for no caption
     */
    public NativeSelect(String caption) {
        this();
        setCaption(caption);
    }

    /**
     * Creates a new {@code NativeSelect} with the given caption, containing the
     * data items in the given collection.
     *
     * @param caption
     *            the component caption to set, null for no caption
     * @param items
     *            the data items to use, not null
     */
    public NativeSelect(String caption, Collection<T> items) {
        this(caption);
        setItems(items);
    }

    /**
     * Creates a new {@code NativeSelect} with the given caption, using the
     * given {@code DataProvider} as the source of data items.
     *
     * @param caption
     *            the component caption to set, null for no caption
     * @param dataProvider
     *            the source of data items to use, not null
     */
    public NativeSelect(String caption, DataProvider<T, ?> dataProvider) {
        this(caption);
        setDataProvider(dataProvider);
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
    protected NativeSelectState getState() {
        return getState(true);
    }

    @Override
    protected NativeSelectState getState(boolean markAsDirty) {
        return (NativeSelectState) super.getState(markAsDirty);
    }

    @Override
    public DataProvider<T, ?> getDataProvider() {
        return internalGetDataProvider();
    }

    @Override
    public void setDataProvider(DataProvider<T, ?> dataProvider) {
        internalSetDataProvider(dataProvider);
    }

    @Override
    public void setItemCaptionGenerator(
            ItemCaptionGenerator<T> itemCaptionGenerator) {
        super.setItemCaptionGenerator(itemCaptionGenerator);
    }

    @Override
    public ItemCaptionGenerator<T> getItemCaptionGenerator() {
        return super.getItemCaptionGenerator();
    }

    /**
     * Returns whether the user is allowed to select nothing in the combo box.
     *
     * @return true if empty selection is allowed, false otherwise
     * @since 8.0
     */
    public boolean isEmptySelectionAllowed() {
        return getState(false).emptySelectionAllowed;
    }

    /**
     * Sets whether the user is allowed to select nothing in the combo box. When
     * true, a special empty item is shown to the user.
     *
     * @param emptySelectionAllowed
     *            true to allow not selecting anything, false to require
     *            selection
     * @since 8.0
     */
    public void setEmptySelectionAllowed(boolean emptySelectionAllowed) {
        getState().emptySelectionAllowed = emptySelectionAllowed;
    }

    /**
     * Returns the empty selection caption.
     * <p>
     * The empty string {@code ""} is the default empty selection caption.
     *
     * @see #setEmptySelectionAllowed(boolean)
     * @see #isEmptySelectionAllowed()
     * @see #setEmptySelectionCaption(String)
     * @see #isSelected(Object)
     *
     * @return the empty selection caption, not {@code null}
     * @since 8.0
     */
    public String getEmptySelectionCaption() {
        return getState(false).emptySelectionCaption;
    }

    /**
     * Sets the empty selection caption.
     * <p>
     * The empty string {@code ""} is the default empty selection caption.
     * <p>
     * If empty selection is allowed via the
     * {@link #setEmptySelectionAllowed(boolean)} method (it is by default) then
     * the empty item will be shown with the given caption.
     *
     * @param caption
     *            the caption to set, not {@code null}
     * @see #isSelected(Object)
     * @since 8.0
     */
    public void setEmptySelectionCaption(String caption) {
        Objects.nonNull(caption);
        getState().emptySelectionCaption = caption;
    }

    /**
     * Sets the number of items that are visible. If only one item is visible,
     * then the box will be displayed as a drop-down list (the default).
     *
     * @since 8.1
     * @param visibleItemCount
     *            the visible item count
     * @throws IllegalArgumentException
     *             if the value is smaller than one
     */
    public void setVisibleItemCount(int visibleItemCount) {
        if (visibleItemCount < 1) {
            throw new IllegalArgumentException(
                    "There must be at least one item visible");
        }
        getState().visibleItemCount = visibleItemCount;
    }

    /**
     * Gets the number of items that are visible. If only one item is visible,
     * then the box will be displayed as a drop-down list.
     *
     * @since 8.1
     * @return the visible item count
     */
    public int getVisibleItemCount() {
        return getState(false).visibleItemCount;
    }
}
