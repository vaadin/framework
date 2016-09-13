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

import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import com.vaadin.data.HasValue;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusAndBlurServerRpcImpl;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.server.KeyMapper;
import com.vaadin.server.Resource;
import com.vaadin.server.ResourceReference;
import com.vaadin.server.data.DataCommunicator;
import com.vaadin.server.data.DataKeyMapper;
import com.vaadin.server.data.DataSource;
import com.vaadin.shared.Registration;
import com.vaadin.shared.data.DataCommunicatorConstants;
import com.vaadin.shared.ui.combobox.ComboBoxClientRpc;
import com.vaadin.shared.ui.combobox.ComboBoxConstants;
import com.vaadin.shared.ui.combobox.ComboBoxServerRpc;
import com.vaadin.shared.ui.combobox.ComboBoxState;

import elemental.json.JsonObject;

/**
 * A filtering dropdown single-select. Items are filtered based on user input.
 * Supports the creation of new items when a handler is set by the user.
 *
 * @param <T>
 *            item (bean) type in ComboBox
 * @author Vaadin Ltd
 */
@SuppressWarnings("serial")
public class ComboBox<T> extends AbstractSingleSelect<T> implements HasValue<T>,
        FieldEvents.BlurNotifier, FieldEvents.FocusNotifier {

    /**
     * Custom single selection model for ComboBox.
     */
    protected class ComboBoxSelectionModel extends SimpleSingleSelection {
        @Override
        protected void doSetSelectedKey(String key) {
            super.doSetSelectedKey(key);

            String selectedCaption = null;
            T value = getDataCommunicator().getKeyMapper().get(key);
            if (value != null) {
                selectedCaption = getItemCaptionProvider().apply(value);
            }
            // FIXME now overlap between state and RPC
            getRpcProxy(ComboBoxClientRpc.class).setSelectedItem(key,
                    selectedCaption);
        }

    }

    /**
     * Handler that adds a new item based on user input when the new items
     * allowed mode is active.
     */
    @FunctionalInterface
    public interface NewItemHandler extends Consumer<String>, Serializable {
    }

    /**
     * ItemCaptionProvider can be used to customize the string shown to the user
     * for an item.
     *
     * @see ComboBox#setItemCaptionProvider(ItemCaptionProvider)
     * @param <T>
     *            item type in the combo box
     */
    @FunctionalInterface
    public interface ItemCaptionProvider<T>
            extends Function<T, String>, Serializable {
    }

    /**
     * ItemStyleProvider can be used to add custom styles to combo box items
     * shown in the popup. The CSS class name that will be added to the item
     * style names is <tt>v-filterselect-item-[style name]</tt>.
     *
     * @see ComboBox#setItemStyleProvider(ItemStyleProvider)
     * @param <T>
     *            item type in the combo box
     */
    @FunctionalInterface
    public interface ItemStyleProvider<T>
            extends Function<T, String>, Serializable {
    }

    /**
     * ItemIconProvider can be used to add custom icons to combo box items shown
     * in the popup.
     *
     * @see ComboBox#setItemIconProvider(ItemIconProvider)
     * @param <T>
     *            item type in the combo box
     */
    @FunctionalInterface
    public interface ItemIconProvider<T>
            extends Function<T, Resource>, Serializable {
    }

    /**
     * Filter can be used to customize the filtering of items based on user
     * input.
     *
     * @see ComboBox#setFilter(ItemFilter)
     * @param <T>
     *            item type in the combo box
     */
    @FunctionalInterface
    public interface ItemFilter<T>
            extends BiFunction<String, T, Boolean>, Serializable {
    }

    private ComboBoxServerRpc rpc = new ComboBoxServerRpc() {
        @Override
        public void createNewItem(String itemValue) {
            // New option entered
            if (getNewItemHandler() != null && itemValue != null
                    && itemValue.length() > 0) {
                getNewItemHandler().accept(itemValue);
                // rebuild list
                filterstring = null;
            }
        }

        @Override
        public void setSelectedItem(String key) {
            // it seems both of these happen, and mean empty selection...
            if (key == null || "".equals(key)) {
                getSelectionModel().setSelectedFromClient(null);
            } else {
                getSelectionModel().setSelectedFromClient(key);
            }
        }

        @Override
        public void setFilter(String filterText) {
            filterstring = filterText;
            if (filterText != null) {
                getDataCommunicator().setInMemoryFilter(
                        item -> filter.apply(filterstring, item));
            } else {
                getDataCommunicator().setInMemoryFilter(null);
            }
        }
    };

    private FocusAndBlurServerRpcImpl focusBlurRpc = new FocusAndBlurServerRpcImpl(
            this) {
        @Override
        protected void fireEvent(Component.Event event) {
            ComboBox.this.fireEvent(event);
        }
    };

    private String filterstring;

    /**
     * Handler for new items entered by the user.
     */
    private NewItemHandler newItemHandler;

    private ItemCaptionProvider<T> itemCaptionProvider = String::valueOf;

    private ItemStyleProvider<T> itemStyleProvider = item -> null;
    private ItemIconProvider<T> itemIconProvider = item -> null;

    private ItemFilter<T> filter = (filterText, item) -> {
        if (filterText == null) {
            return true;
        } else {
            return getItemCaptionProvider().apply(item).toLowerCase(getLocale())
                    .contains(filterText.toLowerCase(getLocale()));
        }
    };

    /**
     * Constructs an empty combo box without a caption. The content of the combo
     * box can be set with {@link #setDataSource(DataSource)} or
     * {@link #setItems(Collection)}
     */
    public ComboBox() {
        super(new DataCommunicator<T>() {
            @Override
            protected DataKeyMapper<T> createKeyMapper() {
                return new KeyMapper<T>() {
                    @Override
                    public void remove(T removeobj) {
                        // never remove keys from ComboBox to support selection
                        // of items that are not currently visible
                    }
                };
            }
        });
        setSelectionModel(new ComboBoxSelectionModel());

        init();
    }

    /**
     * Constructs an empty combo box, whose content can be set with
     * {@link #setDataSource(DataSource)} or {@link #setItems(Collection)}.
     *
     * @param caption
     *            the caption to show in the containing layout, null for no
     *            caption
     */
    public ComboBox(String caption) {
        this();
        setCaption(caption);
    }

    /**
     * Constructs a combo box with a static in-memory data source with the given
     * options.
     *
     * @param caption
     *            the caption to show in the containing layout, null for no
     *            caption
     * @param options
     *            collection of options, not null
     */
    public ComboBox(String caption, Collection<T> options) {
        this(caption, DataSource.create(options));
    }

    /**
     * Constructs a combo box with the given data source.
     *
     * @param caption
     *            the caption to show in the containing layout, null for no
     *            caption
     * @param dataSource
     *            the data source to use, not null
     */
    public ComboBox(String caption, DataSource<T> dataSource) {
        this(caption);
        setDataSource(dataSource);
    }

    /**
     * Initialize the ComboBox with default settings and register client to
     * server RPC implementation.
     */
    private void init() {
        registerRpc(rpc);
        registerRpc(focusBlurRpc);

        addDataGenerator((T data, JsonObject jsonObject) -> {
            jsonObject.put(DataCommunicatorConstants.NAME,
                    getItemCaptionProvider().apply(data));
            String style = itemStyleProvider.apply(data);
            if (style != null) {
                jsonObject.put(ComboBoxConstants.STYLE, style);
            }
            Resource icon = itemIconProvider.apply(data);
            if (icon != null) {
                String iconUrl = ResourceReference
                        .create(icon, ComboBox.this, null).getURL();
                jsonObject.put(ComboBoxConstants.ICON, iconUrl);
            }
        });
    }

    /**
     * Gets the current placeholder text shown when the combo box would be
     * empty.
     *
     * @see #setPlaceholder(String)
     * @return the current placeholder string, or null if not enabled
     */
    public String getPlaceholder() {
        return getState(false).placeholder;
    }

    /**
     * Sets the placeholder string - a textual prompt that is displayed when the
     * select would otherwise be empty, to prompt the user for input.
     *
     * @param placeholder
     *            the desired placeholder, or null to disable
     */
    public void setPlaceholder(String placeholder) {
        getState().placeholder = placeholder;
    }

    /**
     * Sets whether it is possible to input text into the field or whether the
     * field area of the component is just used to show what is selected. By
     * disabling text input, the comboBox will work in the same way as a
     * {@link NativeSelect}
     *
     * @see #isTextInputAllowed()
     *
     * @param textInputAllowed
     *            true to allow entering text, false to just show the current
     *            selection
     */
    public void setTextInputAllowed(boolean textInputAllowed) {
        getState().textInputAllowed = textInputAllowed;
    }

    /**
     * Returns true if the user can enter text into the field to either filter
     * the selections or enter a new value if {@link #isNewItemsAllowed()}
     * returns true. If text input is disabled, the comboBox will work in the
     * same way as a {@link NativeSelect}
     *
     * @return true if text input is allowed
     */
    public boolean isTextInputAllowed() {
        return getState(false).textInputAllowed;
    }

    @Override
    public void addBlurListener(BlurListener listener) {
        addListener(BlurEvent.EVENT_ID, BlurEvent.class, listener,
                BlurListener.blurMethod);
    }

    @Override
    public void removeBlurListener(BlurListener listener) {
        removeListener(BlurEvent.EVENT_ID, BlurEvent.class, listener);
    }

    @Override
    public void addFocusListener(FocusListener listener) {
        addListener(FocusEvent.EVENT_ID, FocusEvent.class, listener,
                FocusListener.focusMethod);
    }

    @Override
    public void removeFocusListener(FocusListener listener) {
        removeListener(FocusEvent.EVENT_ID, FocusEvent.class, listener);
    }

    /**
     * Returns the page length of the suggestion popup.
     *
     * @return the pageLength
     */
    public int getPageLength() {
        return getState(false).pageLength;
    }

    /**
     * Returns the suggestion pop-up's width as a CSS string.
     *
     * @see #setPopupWidth
     * @since 7.7
     * @return explicitly set popup width as CSS size string or null if not set
     */
    public String getPopupWidth() {
        return getState(false).suggestionPopupWidth;
    }

    /**
     * Sets the page length for the suggestion popup. Setting the page length to
     * 0 will disable suggestion popup paging (all items visible).
     *
     * @param pageLength
     *            the pageLength to set
     */
    public void setPageLength(int pageLength) {
        getState().pageLength = pageLength;
    }

    /**
     * Returns whether the user is allowed to select nothing in the combo box.
     *
     * @return true if empty selection is allowed, false otherwise
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
     */
    public void setEmptySelectionAllowed(boolean emptySelectionAllowed) {
        getState().emptySelectionAllowed = emptySelectionAllowed;
    }

    /**
     * Sets the suggestion pop-up's width as a CSS string. By using relative
     * units (e.g. "50%") it's possible to set the popup's width relative to the
     * ComboBox itself.
     *
     * @see #getPopupWidth()
     * @since 7.7
     * @param width
     *            the width
     */
    public void setPopupWidth(String width) {
        getState().suggestionPopupWidth = width;
    }

    /**
     * Sets whether to scroll the selected item visible (directly open the page
     * on which it is) when opening the combo box popup or not.
     *
     * This requires finding the index of the item, which can be expensive in
     * many large lazy loading containers.
     *
     * @param scrollToSelectedItem
     *            true to find the page with the selected item when opening the
     *            selection popup
     */
    public void setScrollToSelectedItem(boolean scrollToSelectedItem) {
        getState().scrollToSelectedItem = scrollToSelectedItem;
    }

    /**
     * Returns true if the select should find the page with the selected item
     * when opening the popup.
     *
     * @see #setScrollToSelectedItem(boolean)
     *
     * @return true if the page with the selected item will be shown when
     *         opening the popup
     */
    public boolean isScrollToSelectedItem() {
        return getState(false).scrollToSelectedItem;
    }

    /**
     * Gets the item caption provider that is used to produce the strings shown
     * in the combo box for each item.
     *
     * @return the item caption provider used, not null
     */
    public ItemCaptionProvider<T> getItemCaptionProvider() {
        return itemCaptionProvider;
    }

    /**
     * Sets the item caption provider that is used to produce the strings shown
     * in the combo box for each item. By default,
     * {@link String#valueOf(Object)} is used.
     *
     * @param itemCaptionProvider
     *            the item caption provider to use, not null
     */
    public void setItemCaptionProvider(
            ItemCaptionProvider<T> itemCaptionProvider) {
        Objects.requireNonNull(itemCaptionProvider,
                "Item caption providers must not be null");
        this.itemCaptionProvider = itemCaptionProvider;
        getDataCommunicator().reset();
    }

    /**
     * Sets the item style provider that is used to produce custom styles for
     * showing items in the popup. The CSS class name that will be added to the
     * item style names is <tt>v-filterselect-item-[style name]</tt>. Returning
     * null from the provider results in no custom style name being set.
     *
     * @param itemStyleProvider
     *            the item style provider to set, not null
     */
    public void setItemStyleProvider(ItemStyleProvider<T> itemStyleProvider) {
        Objects.requireNonNull(itemStyleProvider,
                "Item style providers must not be null");
        this.itemStyleProvider = itemStyleProvider;
        getDataCommunicator().reset();
    }

    /**
     * Gets the currently used item style provider that is used to generate CSS
     * class names for items. The default item style provider returns null for
     * all items, resulting in no custom item class names being set.
     *
     * @see #setItemStyleProvider(ItemStyleProvider)
     *
     * @return the currently used item style provider, not null
     */
    public ItemStyleProvider<T> getItemStyleProvider() {
        return itemStyleProvider;
    }

    /**
     * Sets the item icon provider that is used to produce custom icons for
     * showing items in the popup. The provider can return null for items with
     * no icon.
     *
     * @param itemIconProvider
     *            the item icon provider to set, not null
     */
    public void setItemIconProvider(ItemIconProvider<T> itemIconProvider) {
        Objects.requireNonNull(itemIconProvider,
                "Item icon providers must not be null");
        this.itemIconProvider = itemIconProvider;
        getDataCommunicator().reset();
    }

    /**
     * Gets the currently used item icon provider. The default item icon
     * provider returns null for all items, resulting in no icons being used.
     *
     * @see #setItemIconProvider(ItemIconProvider)
     *
     * @return the currently used item icon provider, not null
     */
    public ItemIconProvider<T> getItemIconProvider() {
        return itemIconProvider;
    }

    /**
     * Sets the handler that is called when user types a new item. The creation
     * of new items is allowed when a new item handler has been set.
     *
     * @param newItemHandler
     *            handler called for new items, null to only permit the
     *            selection of existing items
     */
    public void setNewItemHandler(NewItemHandler newItemHandler) {
        this.newItemHandler = newItemHandler;
        getState().allowNewItems = (newItemHandler != null);
        markAsDirty();
    }

    /**
     * Returns the handler called when the user enters a new item (not present
     * in the data source).
     *
     * @return new item handler or null if none specified
     */
    public NewItemHandler getNewItemHandler() {
        return newItemHandler;
    }

    // HasValue methods delegated to the selection model

    /**
     * Returns the filter used to customize the list based on user input.
     *
     * @return the current filter, not null
     */
    public ItemFilter<T> getFilter() {
        return filter;
    }

    /**
     * Sets the filter used to customize the list based on user input. The
     * default filter checks case-insensitively that the input string is
     * contained in the item caption.
     *
     * @param filter
     *            the filter function to use, not null
     */
    public void setFilter(ItemFilter<T> filter) {
        Objects.requireNonNull(filter, "Item filter must not be null");
        this.filter = filter;
    }

    @Override
    public void setValue(T value) {
        getSelectionModel().setSelectedFromServer(value);

    }

    @Override
    public T getValue() {
        return getSelectionModel().getSelectedItem().orElse(null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Registration addValueChangeListener(
            HasValue.ValueChangeListener<? super T> listener) {
        return addSelectionListener(event -> {
            ((ValueChangeListener<T>) listener)
                    .accept(new ValueChange<T>(event.getConnector(),
                            event.getValue(), event.isUserOriginated()));
        });
    }

    @Override
    protected ComboBoxState getState() {
        return (ComboBoxState) super.getState();
    }

    @Override
    protected ComboBoxState getState(boolean markAsDirty) {
        return (ComboBoxState) super.getState(markAsDirty);
    }

}
