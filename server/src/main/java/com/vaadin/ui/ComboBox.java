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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import org.jsoup.nodes.Element;

import com.vaadin.data.HasValue;
import com.vaadin.data.Listing;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusAndBlurServerRpcDecorator;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.server.KeyMapper;
import com.vaadin.server.Resource;
import com.vaadin.server.ResourceReference;
import com.vaadin.server.SerializableBiPredicate;
import com.vaadin.server.data.DataCommunicator;
import com.vaadin.server.data.DataKeyMapper;
import com.vaadin.server.data.DataProvider;
import com.vaadin.shared.Registration;
import com.vaadin.shared.data.DataCommunicatorConstants;
import com.vaadin.shared.ui.combobox.ComboBoxConstants;
import com.vaadin.shared.ui.combobox.ComboBoxServerRpc;
import com.vaadin.shared.ui.combobox.ComboBoxState;
import com.vaadin.ui.declarative.DesignAttributeHandler;
import com.vaadin.ui.declarative.DesignContext;
import com.vaadin.ui.declarative.DesignFormatter;

import elemental.json.Json;
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
        implements HasValue<T>, FieldEvents.BlurNotifier,
        FieldEvents.FocusNotifier, Listing<T, DataProvider<T, String>> {

    /**
     * Handler that adds a new item based on user input when the new items
     * allowed mode is active.
     */
    @FunctionalInterface
    public interface NewItemHandler extends Consumer<String>, Serializable {
    }

    /**
     * Item style generator class for declarative support.
     * <p>
     * Provides a straightforward mapping between an item and its style.
     *
     * @param <T>
     *            item type
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
            if (getNewItemHandler() != null && itemValue != null
                    && itemValue.length() > 0) {
                getNewItemHandler().accept(itemValue);
            }
        }

        @Override
        public void setFilter(String filterText) {
            getDataCommunicator().setFilter(filterText);
        }
    };

    /**
     * Handler for new items entered by the user.
     */
    private NewItemHandler newItemHandler;

    private StyleGenerator<T> itemStyleGenerator = item -> null;

    private final SerializableBiPredicate<String, T> defaultFilterMethod = (
            text, item) -> getItemCaptionGenerator().apply(item)
                    .toLowerCase(getLocale())
                    .contains(text.toLowerCase(getLocale()));

    /**
     * Constructs an empty combo box without a caption. The content of the combo
     * box can be set with {@link #setDataProvider(DataProvider)} or
     * {@link #setItems(Collection)}
     */
    public ComboBox() {
        super(new DataCommunicator<T, String>() {
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

        init();
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
     * Initialize the ComboBox with default settings and register client to
     * server RPC implementation.
     */
    private void init() {
        registerRpc(rpc);
        registerRpc(new FocusAndBlurServerRpcDecorator(this, this::fireEvent));

        addDataGenerator((T data, JsonObject jsonObject) -> {
            jsonObject.put(DataCommunicatorConstants.NAME,
                    getItemCaptionGenerator().apply(data));
            String style = itemStyleGenerator.apply(data);
            if (style != null) {
                jsonObject.put(ComboBoxConstants.STYLE, style);
            }
            Resource icon = getItemIconGenerator().apply(data);
            if (icon != null) {
                String iconUrl = ResourceReference
                        .create(icon, ComboBox.this, null).getURL();
                jsonObject.put(ComboBoxConstants.ICON, iconUrl);
            }
        });
    }

    @Override
    public void setItems(Collection<T> items) {
        DataProvider<T, String> provider = DataProvider.create(items)
                .convertFilter(filterText -> item -> defaultFilterMethod
                        .test(filterText, item));
        setDataProvider(provider);
    }

    @Override
    public void setItems(@SuppressWarnings("unchecked") T... items) {
        DataProvider<T, String> provider = DataProvider.create(items)
                .convertFilter(filterText -> item -> defaultFilterMethod
                        .test(filterText, item));
        setDataProvider(provider);
    }

    /**
     * Sets the data items of this listing and a simple string filter with which
     * the item string and the text the user has input are compared.
     * <p>
     * Note that unlike {@link #setItems(Collection)}, no automatic case
     * conversion is performed before the comparison.
     *
     * @param filterPredicate
     *            predicate for comparing the item string (first parameter) and
     *            the filter string (second parameter)
     * @param items
     *            the data items to display
     */
    public void setItems(
            SerializableBiPredicate<String, String> filterPredicate,
            Collection<T> items) {
        DataProvider<T, String> provider = DataProvider.create(items)
                .convertFilter(filterText -> item -> filterPredicate.test(
                        getItemCaptionGenerator().apply(item), filterText));
        setDataProvider(provider);
    }

    /**
     * Sets the data items of this listing and a simple string filter with which
     * the item string and the text the user has input are compared.
     * <p>
     * Note that unlike {@link #setItems(Collection)}, no automatic case
     * conversion is performed before the comparison.
     *
     * @param filterPredicate
     *            predicate for comparing the item string (first parameter) and
     *            the filter string (second parameter)
     * @param items
     *            the data items to display
     */
    public void setItems(
            SerializableBiPredicate<String, String> filterPredicate,
            @SuppressWarnings("unchecked") T... items) {
        DataProvider<T, String> provider = DataProvider.create(items)
                .convertFilter(filterText -> item -> filterPredicate.test(
                        getItemCaptionGenerator().apply(item), filterText));
        setDataProvider(provider);
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
     * Returns the empty selection caption.
     * <p>
     * The empty string {@code ""} is the default empty selection caption.
     *
     * @see #setEmptySelectionAllowed(boolean)
     * @see #isEmptySelectionAllowed()
     * @see #setEmptySelectionCaption(String)
     * @see #isSelected(Object)
     * @see #select(Object)
     *
     * @return the empty selection caption, not {@code null}
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
     * @see #getNullSelectionItemId()
     * @see #isSelected(Object)
     * @see #select(Object)
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
     * in the data provider).
     *
     * @return new item handler or null if none specified
     */
    public NewItemHandler getNewItemHandler() {
        return newItemHandler;
    }

    // HasValue methods delegated to the selection model

    @Override
    public Registration addValueChangeListener(
            com.vaadin.event.Listener<ValueChangeEvent<T>> listener) {
        return addSelectionListener(event -> {
            listener.onEvent(new ValueChangeEvent<>(event.getComponent(), this,
                    event.isUserOriginated()));
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

    @Override
    protected void doSetSelectedKey(String key) {
        super.doSetSelectedKey(key);

        String selectedCaption = null;
        T value = getDataCommunicator().getKeyMapper().get(key);
        if (value != null) {
            selectedCaption = getItemCaptionGenerator().apply(value);
        }
        getState().selectedItemCaption = selectedCaption;
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
            element.attr("selected", "");
        }

        return element;
    }

    @Override
    protected List<T> readItems(Element design, DesignContext context) {
        setStyleGenerator(new DeclarativeStyleGenerator<>(getStyleGenerator()));
        return super.readItems(design, context);
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
                throw new IllegalStateException(String.format(
                        "Don't know how "
                                + "to set style using current style generator '%s'",
                        styleGenerator.getClass().getName()));
            }
        }
        return item;
    }

    @Override
    @SuppressWarnings("unchecked")
    public DataProvider<T, String> getDataProvider() {
        return (DataProvider<T, String>) internalGetDataProvider();
    }

    @Override
    public void setDataProvider(DataProvider<T, String> dataProvider) {
        internalSetDataProvider(dataProvider);
    }

    @Override
    @SuppressWarnings("unchecked")
    public DataCommunicator<T, String> getDataCommunicator() {
        // Not actually an unsafe cast. DataCommunicator is final and set by
        // ComboBox.
        return (DataCommunicator<T, String>) super.getDataCommunicator();
    }

    @Override
    protected void setSelectedFromClient(String key) {
        super.setSelectedFromClient(key);

        /*
         * The client side for combo box always expects a state change for
         * selectedItemKey after it has sent a selection change. This means that
         * we must store a value in the diffstate that guarantees that a new
         * value will be sent, regardless of what the value actually is at the
         * time when changes are sent.
         *
         * Keys are always strings (or null), so using a non-string type will
         * always trigger a diff mismatch and a resend.
         */
        updateDiffstate("selectedItemKey", Json.create(0));
    }
}
