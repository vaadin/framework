/*
 * Copyright 2000-2022 Vaadin Ltd.
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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import com.vaadin.data.provider.CallbackDataProvider;
import com.vaadin.data.provider.DataChangeEvent;
import com.vaadin.data.provider.DataCommunicator;
import com.vaadin.data.provider.DataGenerator;
import com.vaadin.data.provider.DataKeyMapper;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.InMemoryDataProvider;
import com.vaadin.data.provider.ListDataProvider;
import org.jsoup.nodes.Element;

import com.vaadin.data.HasFilterableDataProvider;
import com.vaadin.data.HasValue;
import com.vaadin.data.ValueProvider;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusAndBlurServerRpcDecorator;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.server.ConnectorResource;
import com.vaadin.server.KeyMapper;
import com.vaadin.server.Resource;
import com.vaadin.server.ResourceReference;
import com.vaadin.server.SerializableBiPredicate;
import com.vaadin.server.SerializableConsumer;
import com.vaadin.server.SerializableFunction;
import com.vaadin.server.SerializableToIntFunction;
import com.vaadin.shared.Registration;
import com.vaadin.shared.data.DataCommunicatorConstants;
import com.vaadin.shared.ui.combobox.ComboBoxClientRpc;
import com.vaadin.shared.ui.combobox.ComboBoxConstants;
import com.vaadin.shared.ui.combobox.ComboBoxServerRpc;
import com.vaadin.shared.ui.combobox.ComboBoxState;
import com.vaadin.ui.declarative.DesignAttributeHandler;
import com.vaadin.ui.declarative.DesignContext;
import com.vaadin.ui.declarative.DesignFormatter;

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
public class ComboBox<T> extends AbstractSingleSelect<T>
        implements FieldEvents.BlurNotifier, FieldEvents.FocusNotifier,
        HasFilterableDataProvider<T, String> {

    /**
     * A callback method for fetching items. The callback is provided with a
     * non-null string filter, offset index and limit.
     *
     * @param <T>
     *            item (bean) type in ComboBox
     * @since 8.0
     */
    @FunctionalInterface
    public interface FetchItemsCallback<T> extends Serializable {

        /**
         * Returns a stream of items that match the given filter, limiting the
         * results with given offset and limit.
         * <p>
         * This method is called after the size of the data set is asked from a
         * related size callback. The offset and limit are promised to be within
         * the size of the data set.
         *
         * @param filter
         *            a non-null filter string
         * @param offset
         *            the first index to fetch
         * @param limit
         *            the fetched item count
         * @return stream of items
         */
        public Stream<T> fetchItems(String filter, int offset, int limit);
    }

    /**
     * Handler that adds a new item based on user input when the new items
     * allowed mode is active.
     * <p>
     * NOTE 1: If the new item is rejected the client must be notified of the
     * fact via ComboBoxClientRpc or selection handling won't complete.
     * </p>
     * <p>
     * NOTE 2: Selection handling must be completed separately if filtering the
     * data source with the same value won't include the new item in the initial
     * list of suggestions. Failing to do so will lead to selection handling
     * never completing and previous selection remaining on the server.
     * </p>
     *
     * @since 8.0
     * @deprecated Since 8.4 replaced by {@link NewItemProvider}.
     */
    @Deprecated
    @FunctionalInterface
    public interface NewItemHandler extends SerializableConsumer<String> {
    }

    /**
     * Provider function that adds a new item based on user input when the new
     * items allowed mode is active. After the new item handling is complete,
     * this function should return {@code Optional.of(text)} for the completion
     * of automatic selection handling. If automatic selection is not wished
     * for, always return {@code Optional.isEmpty()}.
     *
     * @since 8.4
     */
    @FunctionalInterface
    public interface NewItemProvider<T>
            extends SerializableFunction<String, Optional<T>> {
    }

    /**
     * Item style generator class for declarative support.
     * <p>
     * Provides a straightforward mapping between an item and its style.
     *
     * @param <T>
     *            item type
     * @since 8.0
     */
    protected static class DeclarativeStyleGenerator<T>
            implements StyleGenerator<T> {

        private StyleGenerator<T> fallback;
        private Map<T, String> styles = new HashMap<>();

        public DeclarativeStyleGenerator(StyleGenerator<T> fallback) {
            this.fallback = fallback;
        }

        @Override
        public String apply(T item) {
            return styles.containsKey(item) ? styles.get(item)
                    : fallback.apply(item);
        }

        /**
         * Sets a {@code style} for the {@code item}.
         *
         * @param item
         *            a data item
         * @param style
         *            a style for the {@code item}
         */
        protected void setStyle(T item, String style) {
            styles.put(item, style);
        }
    }

    private ComboBoxServerRpc rpc = new ComboBoxServerRpc() {
        @Override
        public void createNewItem(String itemValue) {
            // New option entered
            boolean added = false;
            if (itemValue != null && !itemValue.isEmpty()) {
                if (getNewItemProvider() != null) {
                    Optional<T> item = getNewItemProvider().apply(itemValue);
                    added = item.isPresent();
                    // Fixes issue
                    // https://github.com/vaadin/framework/issues/11343
                    // Update the internal selection state immediately to avoid
                    // client side hanging. This is needed for cases that user
                    // interaction fires multi events (like adding and deleting)
                    // on a new item during the same round trip.
                    item.ifPresent(value -> {
                        setSelectedItem(value, true);
                        getDataCommunicator().reset();
                    });
                } else if (getNewItemHandler() != null) {
                    getNewItemHandler().accept(itemValue);
                    // Up to the user to tell if no item was added.
                    added = true;
                }
            }

            if (!added) {
                // New item was not handled.
                getRpcProxy(ComboBoxClientRpc.class).newItemNotAdded(itemValue);
            }
        }

        @Override
        public void setFilter(String filterText) {
            getState().currentFilterText = filterText;
            filterSlot.accept(filterText);
        }

        @Override
        public void resetForceDataSourceUpdate() {
            getState().forceDataSourceUpdate = false;
        }
    };

    /**
     * Handler for new items entered by the user.
     */
    @Deprecated
    private NewItemHandler newItemHandler;

    /**
     * Provider function for new items entered by the user.
     */
    private NewItemProvider<T> newItemProvider;

    private StyleGenerator<T> itemStyleGenerator = item -> null;

    private SerializableConsumer<String> filterSlot = filter -> {
        // Just ignore when neither setDataProvider nor setItems has been called
    };

    private Registration dataProviderListener = null;

    /**
     * Constructs an empty combo box without a caption. The content of the combo
     * box can be set with {@link #setDataProvider(DataProvider)} or
     * {@link #setItems(Collection)}
     */
    public ComboBox() {
        this(new DataCommunicator<T>() {
            @Override
            protected DataKeyMapper<T> createKeyMapper(
                    ValueProvider<T, Object> identifierGetter) {
                return new KeyMapper<T>(identifierGetter) {
                    @Override
                    public void remove(T removeobj) {
                        // never remove keys from ComboBox to support selection
                        // of items that are not currently visible
                    }
                };
            }
        });
    }

    /**
     * Constructs an empty combo box, whose content can be set with
     * {@link #setDataProvider(DataProvider)} or {@link #setItems(Collection)}.
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
     * Constructs a combo box with a static in-memory data provider with the
     * given options.
     *
     * @param caption
     *            the caption to show in the containing layout, null for no
     *            caption
     * @param options
     *            collection of options, not null
     */
    public ComboBox(String caption, Collection<T> options) {
        this(caption);

        setItems(options);
    }

    /**
     * Constructs and initializes an empty combo box.
     *
     * @param dataCommunicator
     *            the data comnunicator to use with this ComboBox
     * @since 8.5
     */
    protected ComboBox(DataCommunicator<T> dataCommunicator) {
        super(dataCommunicator);
        init();
    }

    /**
     * Initialize the ComboBox with default settings and register client to
     * server RPC implementation.
     */
    private void init() {
        registerRpc(rpc);
        registerRpc(new FocusAndBlurServerRpcDecorator(this, this::fireEvent));

        addDataGenerator(new DataGenerator<T>() {

            /**
             * Map for storing names for icons.
             */
            private Map<Object, String> resourceKeyMap = new HashMap<>();
            private int counter = 0;

            @Override
            public void generateData(T item, JsonObject jsonObject) {
                String caption = getItemCaptionGenerator().apply(item);
                if (caption == null) {
                    caption = "";
                }
                jsonObject.put(DataCommunicatorConstants.NAME, caption);
                String style = itemStyleGenerator.apply(item);
                if (style != null) {
                    jsonObject.put(ComboBoxConstants.STYLE, style);
                }
                Resource icon = getItemIcon(item);
                if (icon != null) {
                    String iconKey = resourceKeyMap
                            .get(getDataProvider().getId(item));
                    String iconUrl = ResourceReference
                            .create(icon, ComboBox.this, iconKey).getURL();
                    jsonObject.put(ComboBoxConstants.ICON, iconUrl);
                }
            }

            @Override
            public void destroyData(T item) {
                Object itemId = getDataProvider().getId(item);
                if (resourceKeyMap.containsKey(itemId)) {
                    setResource(resourceKeyMap.get(itemId), null);
                    resourceKeyMap.remove(itemId);
                }
            }

            private Resource getItemIcon(T item) {
                Resource icon = getItemIconGenerator().apply(item);
                if (icon == null || !(icon instanceof ConnectorResource)) {
                    return icon;
                }

                Object itemId = getDataProvider().getId(item);
                if (!resourceKeyMap.containsKey(itemId)) {
                    resourceKeyMap.put(itemId, "icon" + (counter++));
                }
                setResource(resourceKeyMap.get(itemId), icon);
                return icon;
            }
        });
    }

    /**
     * {@inheritDoc}
     * <p>
     * Filtering will use a case insensitive match to show all items where the
     * filter text is a substring of the caption displayed for that item.
     */
    @Override
    public void setItems(Collection<T> items) {
        ListDataProvider<T> listDataProvider = DataProvider.ofCollection(items);

        setDataProvider(listDataProvider);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Filtering will use a case insensitive match to show all items where the
     * filter text is a substring of the caption displayed for that item.
     */
    @Override
    public void setItems(Stream<T> streamOfItems) {
        // Overridden only to add clarification to javadocs
        super.setItems(streamOfItems);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Filtering will use a case insensitive match to show all items where the
     * filter text is a substring of the caption displayed for that item.
     */
    @Override
    public void setItems(T... items) {
        // Overridden only to add clarification to javadocs
        super.setItems(items);
    }

    /**
     * Sets a list data provider as the data provider of this combo box.
     * Filtering will use a case insensitive match to show all items where the
     * filter text is a substring of the caption displayed for that item.
     * <p>
     * Note that this is a shorthand that calls
     * {@link #setDataProvider(DataProvider)} with a wrapper of the provided
     * list data provider. This means that {@link #getDataProvider()} will
     * return the wrapper instead of the original list data provider.
     *
     * @param listDataProvider
     *            the list data provider to use, not <code>null</code>
     * @since 8.0
     */
    public void setDataProvider(ListDataProvider<T> listDataProvider) {
        // Cannot use the case insensitive contains shorthand from
        // ListDataProvider since it wouldn't react to locale changes
        CaptionFilter defaultCaptionFilter = (itemText, filterText) -> itemText
                .toLowerCase(getLocale())
                .contains(filterText.toLowerCase(getLocale()));

        setDataProvider(defaultCaptionFilter, listDataProvider);
    }

    /**
     * Sets the data items of this listing and a simple string filter with which
     * the item string and the text the user has input are compared.
     * <p>
     * Note that unlike {@link #setItems(Collection)}, no automatic case
     * conversion is performed before the comparison.
     *
     * @param captionFilter
     *            filter to check if an item is shown when user typed some text
     *            into the ComboBox
     * @param items
     *            the data items to display
     * @since 8.0
     */
    public void setItems(CaptionFilter captionFilter, Collection<T> items) {
        ListDataProvider<T> listDataProvider = DataProvider.ofCollection(items);

        setDataProvider(captionFilter, listDataProvider);
    }

    /**
     * Sets a list data provider with an item caption filter as the data
     * provider of this combo box. The caption filter is used to compare the
     * displayed caption of each item to the filter text entered by the user.
     *
     * @param captionFilter
     *            filter to check if an item is shown when user typed some text
     *            into the ComboBox
     * @param listDataProvider
     *            the list data provider to use, not <code>null</code>
     * @since 8.0
     */
    public void setDataProvider(CaptionFilter captionFilter,
            ListDataProvider<T> listDataProvider) {
        Objects.requireNonNull(listDataProvider,
                "List data provider cannot be null");

        // Must do getItemCaptionGenerator() for each operation since it might
        // not be the same as when this method was invoked
        setDataProvider(listDataProvider, filterText -> item -> captionFilter
                .test(getItemCaptionOfItem(item), filterText));
    }

    // Helper method for the above to make lambda more readable
    private String getItemCaptionOfItem(T item) {
        String caption = getItemCaptionGenerator().apply(item);
        if (caption == null) {
            caption = "";
        }
        return caption;
    }

    /**
     * Sets the data items of this listing and a simple string filter with which
     * the item string and the text the user has input are compared.
     * <p>
     * Note that unlike {@link #setItems(Collection)}, no automatic case
     * conversion is performed before the comparison.
     *
     * @param captionFilter
     *            filter to check if an item is shown when user typed some text
     *            into the ComboBox
     * @param items
     *            the data items to display
     * @since 8.0
     */
    public void setItems(CaptionFilter captionFilter,
            @SuppressWarnings("unchecked") T... items) {
        setItems(captionFilter, Arrays.asList(items));
    }

    /**
     * Gets the current placeholder text shown when the combo box would be
     * empty.
     *
     * @see #setPlaceholder(String)
     * @return the current placeholder string, or null if not enabled
     * @since 8.0
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
     * @since 8.0
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
     * the selections or enter a new value if new item provider or handler is
     * set (see {@link #setNewItemProvider(NewItemProvider)} (recommended) and
     * {@link #setNewItemHandler(NewItemHandler)} (deprecated)). If text input
     * is disabled, the comboBox will work in the same way as a
     * {@link NativeSelect}
     *
     * @return true if text input is allowed
     */
    public boolean isTextInputAllowed() {
        return getState(false).textInputAllowed;
    }

    @Override
    public Registration addBlurListener(BlurListener listener) {
        return addListener(BlurEvent.EVENT_ID, BlurEvent.class, listener,
                BlurListener.blurMethod);
    }

    @Override
    public Registration addFocusListener(FocusListener listener) {
        return addListener(FocusEvent.EVENT_ID, FocusEvent.class, listener,
                FocusListener.focusMethod);
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
     * Returns the suggestion pop-up's width as a CSS string. By default this
     * width is set to "100%".
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
     * @return the empty selection caption, not {@code null}
     * @see #setEmptySelectionAllowed(boolean)
     * @see #isEmptySelectionAllowed()
     * @see #setEmptySelectionCaption(String)
     * @see #isSelected(Object)
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
     * Sets the suggestion pop-up's width as a CSS string. By using relative
     * units (e.g. "50%") it's possible to set the popup's width relative to the
     * ComboBox itself.
     * <p>
     * By default this width is set to "100%" so that the pop-up's width is
     * equal to the width of the combobox. By setting width to null the pop-up's
     * width will automatically expand beyond 100% relative width to fit the
     * content of all displayed items.
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
     * <p>
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
     * Sets the style generator that is used to produce custom class names for
     * items visible in the popup. The CSS class name that will be added to the
     * item is <tt>v-filterselect-item-[style name]</tt>. Returning null from
     * the generator results in no custom style name being set.
     *
     * @see StyleGenerator
     *
     * @param itemStyleGenerator
     *            the item style generator to set, not null
     * @throws NullPointerException
     *             if {@code itemStyleGenerator} is {@code null}
     * @since 8.0
     */
    public void setStyleGenerator(StyleGenerator<T> itemStyleGenerator) {
        Objects.requireNonNull(itemStyleGenerator,
                "Item style generator must not be null");
        this.itemStyleGenerator = itemStyleGenerator;
        getDataCommunicator().reset();
    }

    /**
     * Gets the currently used style generator that is used to generate CSS
     * class names for items. The default item style provider returns null for
     * all items, resulting in no custom item class names being set.
     *
     * @see StyleGenerator
     * @see #setStyleGenerator(StyleGenerator)
     *
     * @return the currently used item style generator, not null
     * @since 8.0
     */
    public StyleGenerator<T> getStyleGenerator() {
        return itemStyleGenerator;
    }

    @Override
    public void setItemIconGenerator(IconGenerator<T> itemIconGenerator) {
        super.setItemIconGenerator(itemIconGenerator);
    }

    @Override
    public IconGenerator<T> getItemIconGenerator() {
        return super.getItemIconGenerator();
    }

    /**
     * Sets the handler that is called when user types a new item. The creation
     * of new items is allowed when a new item handler has been set. If new item
     * provider is also set, the new item handler is ignored.
     *
     * @param newItemHandler
     *            handler called for new items, null to only permit the
     *            selection of existing items, all options ignored if new item
     *            provider is set
     * @since 8.0
     * @deprecated Since 8.4 use {@link #setNewItemProvider(NewItemProvider)}
     *             instead.
     */
    @Deprecated
    public void setNewItemHandler(NewItemHandler newItemHandler) {
        getLogger().log(Level.WARNING,
                "NewItemHandler is deprecated. Please use NewItemProvider instead.");
        this.newItemHandler = newItemHandler;
        getState(true).allowNewItems = newItemProvider != null
                || newItemHandler != null;
    }

    /**
     * Sets the provider function that is called when user types a new item. The
     * creation of new items is allowed when a new item provider has been set.
     * If a deprecated new item handler is also set it is ignored in favor of
     * new item provider.
     *
     * @param newItemProvider
     *            provider function that is called for new items, null to only
     *            permit the selection of existing items or to use a deprecated
     *            new item handler if set
     * @since 8.4
     */
    public void setNewItemProvider(NewItemProvider<T> newItemProvider) {
        this.newItemProvider = newItemProvider;
        getState(true).allowNewItems = newItemProvider != null
                || newItemHandler != null;
    }

    /**
     * Returns the handler called when the user enters a new item (not present
     * in the data provider).
     *
     * @return new item handler or null if none specified
     * @deprecated Since 8.4 use {@link #getNewItemProvider()} instead.
     */
    @Deprecated
    public NewItemHandler getNewItemHandler() {
        return newItemHandler;
    }

    /**
     * Returns the provider function that is called when the user enters a new
     * item (not present in the data provider).
     *
     * @since 8.4
     * @return new item provider or null if none specified
     */
    public NewItemProvider<T> getNewItemProvider() {
        return newItemProvider;
    }

    // HasValue methods delegated to the selection model

    @Override
    public Registration addValueChangeListener(
            HasValue.ValueChangeListener<T> listener) {
        return addSelectionListener(event -> listener
                .valueChange(new ValueChangeEvent<>(event.getComponent(), this,
                        event.getOldValue(), event.isUserOriginated())));
    }

    @Override
    protected ComboBoxState getState() {
        return (ComboBoxState) super.getState();
    }

    @Override
    protected ComboBoxState getState(boolean markAsDirty) {
        return (ComboBoxState) super.getState(markAsDirty);
    }

    @Override
    protected void updateSelectedItemState(T value) {
        super.updateSelectedItemState(value);

        updateSelectedItemCaption(value);
        updateSelectedItemIcon(value);
    }

    private void updateSelectedItemCaption(T value) {
        String selectedCaption = null;
        if (value != null) {
            selectedCaption = getItemCaptionGenerator().apply(value);
        }
        getState().selectedItemCaption = selectedCaption;
    }

    private void updateSelectedItemIcon(T value) {
        String selectedItemIcon = null;
        if (value != null) {
            Resource icon = getItemIconGenerator().apply(value);
            if (icon != null) {
                if (icon instanceof ConnectorResource) {
                    if (!isAttached()) {
                        // Deferred resource generation.
                        return;
                    }
                    setResource("selected", icon);
                }
                selectedItemIcon = ResourceReference
                        .create(icon, ComboBox.this, "selected").getURL();
            }
        }
        getState().selectedItemIcon = selectedItemIcon;
    }

    @Override
    public void attach() {
        super.attach();

        // Update icon for ConnectorResource
        updateSelectedItemIcon(getValue());

        DataProvider<T, ?> dataProvider = getDataProvider();
        if (dataProvider != null && dataProviderListener == null) {
            setupDataProviderListener(dataProvider);
        }
    }

    @Override
    public void detach() {
        if (dataProviderListener != null) {
            dataProviderListener.remove();
            dataProviderListener = null;
        }
        super.detach();
    }

    @Override
    protected Element writeItem(Element design, T item, DesignContext context) {
        Element element = design.appendElement("option");

        String caption = getItemCaptionGenerator().apply(item);
        if (caption != null) {
            element.html(DesignFormatter.encodeForTextNode(caption));
        } else {
            element.html(DesignFormatter.encodeForTextNode(item.toString()));
        }
        element.attr("item", item.toString());

        Resource icon = getItemIconGenerator().apply(item);
        if (icon != null) {
            DesignAttributeHandler.writeAttribute("icon", element.attributes(),
                    icon, null, Resource.class, context);
        }

        String style = getStyleGenerator().apply(item);
        if (style != null) {
            element.attr("style", style);
        }

        if (isSelected(item)) {
            element.attr("selected", true);
        }

        return element;
    }

    @Override
    protected void readItems(Element design, DesignContext context) {
        setStyleGenerator(new DeclarativeStyleGenerator<>(getStyleGenerator()));
        super.readItems(design, context);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    protected T readItem(Element child, Set<T> selected,
            DesignContext context) {
        T item = super.readItem(child, selected, context);

        if (child.hasAttr("style")) {
            StyleGenerator<T> styleGenerator = getStyleGenerator();
            if (styleGenerator instanceof DeclarativeStyleGenerator) {
                ((DeclarativeStyleGenerator) styleGenerator).setStyle(item,
                        child.attr("style"));
            } else {
                throw new IllegalStateException(String.format("Don't know how "
                        + "to set style using current style generator '%s'",
                        styleGenerator.getClass().getName()));
            }
        }
        return item;
    }

    @Override
    public DataProvider<T, ?> getDataProvider() {
        if (this.getDataCommunicator() != null) {
            return internalGetDataProvider();
        }
        return null;
    }

    @Override
    public <C> void setDataProvider(DataProvider<T, C> dataProvider,
            SerializableFunction<String, C> filterConverter) {
        Objects.requireNonNull(dataProvider, "dataProvider cannot be null");
        Objects.requireNonNull(filterConverter,
                "filterConverter cannot be null");

        SerializableFunction<String, C> convertOrNull = filterText -> {
            if (filterText == null || filterText.isEmpty()) {
                return null;
            }

            return filterConverter.apply(filterText);
        };

        SerializableConsumer<C> providerFilterSlot = internalSetDataProvider(
                dataProvider,
                convertOrNull.apply(getState(false).currentFilterText));

        filterSlot = filter -> providerFilterSlot
                .accept(convertOrNull.apply(filter));

        setupDataProviderListener(dataProvider);
    }

    private <C> void setupDataProviderListener(
            DataProvider<T, C> dataProvider) {
        // This workaround is done to fix issue #11642 for unpaged comboboxes.
        // Data sources for on the client need to be updated after data provider
        // refreshAll so that serverside selection works even before the
        // dropdown
        // is opened. Only done for in-memory data providers for performance
        // reasons.
        if (dataProvider instanceof InMemoryDataProvider) {
            if (dataProviderListener != null) {
                dataProviderListener.remove();
            }
            dataProviderListener = dataProvider
                    .addDataProviderListener(event -> {
                        if ((!(event instanceof DataChangeEvent.DataRefreshEvent))
                                && (getPageLength() == 0)) {
                            getState().forceDataSourceUpdate = true;
                        }
                    });
        }
    }

    /**
     * Sets a CallbackDataProvider using the given fetch items callback and a
     * size callback.
     * <p>
     * This method is a shorthand for making a {@link CallbackDataProvider} that
     * handles a partial {@link com.vaadin.data.provider.Query Query} object.
     *
     * @param fetchItems
     *            a callback for fetching items
     * @param sizeCallback
     *            a callback for getting the count of items
     *
     * @see CallbackDataProvider
     * @see #setDataProvider(DataProvider)
     */
    public void setDataProvider(FetchItemsCallback<T> fetchItems,
            SerializableToIntFunction<String> sizeCallback) {
        setDataProvider(new CallbackDataProvider<>(
                q -> fetchItems.fetchItems(q.getFilter().orElse(""),
                        q.getOffset(), q.getLimit()),
                q -> sizeCallback.applyAsInt(q.getFilter().orElse(""))));
    }

    /**
     * Predicate to check {@link ComboBox} item captions against user typed
     * strings.
     *
     * @see ComboBox#setItems(CaptionFilter, Collection)
     * @see ComboBox#setItems(CaptionFilter, Object[])
     * @since 8.0
     */
    @FunctionalInterface
    public interface CaptionFilter
            extends SerializableBiPredicate<String, String> {

        /**
         * Check item caption against entered text.
         *
         * @param itemCaption
         *            the caption of the item to filter, not {@code null}
         * @param filterText
         *            user entered filter, not {@code null}
         * @return {@code true} if item passes the filter and should be listed,
         *         {@code false} otherwise
         */
        @Override
        public boolean test(String itemCaption, String filterText);
    }

    private static Logger getLogger() {
        return Logger.getLogger(ComboBox.class.getName());
    }
}
