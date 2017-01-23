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

import java.util.Objects;

import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;

import com.vaadin.data.HasDataProvider;
import com.vaadin.data.HasFilterableDataProvider;
import com.vaadin.data.HasItems;
import com.vaadin.data.provider.DataCommunicator;
import com.vaadin.data.provider.DataGenerator;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.Query;
import com.vaadin.server.AbstractExtension;
import com.vaadin.server.Resource;
import com.vaadin.server.SerializableConsumer;
import com.vaadin.shared.extension.abstractlisting.AbstractListingExtensionState;
import com.vaadin.shared.ui.abstractlisting.AbstractListingState;
import com.vaadin.ui.Component.Focusable;
import com.vaadin.ui.declarative.DesignAttributeHandler;
import com.vaadin.ui.declarative.DesignContext;
import com.vaadin.ui.declarative.DesignException;
import com.vaadin.ui.declarative.DesignFormatter;

/**
 * A base class for listing components. Provides common handling for fetching
 * backend data items, selection logic, and server-client communication.
 * <p>
 * <strong>Note: </strong> concrete component implementations should implement
 * the {@link HasDataProvider} or {@link HasFilterableDataProvider} interface.
 *
 * @author Vaadin Ltd.
 * @since 8.0
 *
 * @param <T>
 *            the item data type
 *
 */
public abstract class AbstractListing<T> extends AbstractComponent
        implements Focusable, HasItems<T> {
    /**
     * The item icon caption provider.
     */
    private ItemCaptionGenerator<T> itemCaptionGenerator = String::valueOf;

    /**
     * The item icon provider. It is up to the implementing class to support
     * this or not.
     */
    private IconGenerator<T> itemIconGenerator = item -> null;

    /**
     * A helper base class for creating extensions for Listing components. This
     * class provides helpers for accessing the underlying parts of the
     * component and its communication mechanism.
     *
     * @param <T>
     *            the listing item type
     */
    public abstract static class AbstractListingExtension<T>
            extends AbstractExtension implements DataGenerator<T> {

        /**
         * Adds this extension to the given parent listing.
         *
         * @param listing
         *            the parent component to add to
         */
        public void extend(AbstractListing<T> listing) {
            super.extend(listing);
            listing.addDataGenerator(this);
        }

        @Override
        public void remove() {
            getParent().removeDataGenerator(this);

            super.remove();
        }

        /**
         * Gets a data object based on its client-side identifier key.
         *
         * @param key
         *            key for data object
         * @return the data object
         */
        protected T getData(String key) {
            return getParent().getDataCommunicator().getKeyMapper().get(key);
        }

        @Override
        @SuppressWarnings("unchecked")
        public AbstractListing<T> getParent() {
            return (AbstractListing<T>) super.getParent();
        }

        /**
         * A helper method for refreshing the client-side representation of a
         * single data item.
         *
         * @param item
         *            the item to refresh
         */
        protected void refresh(T item) {
            getParent().getDataCommunicator().refresh(item);
        }

        @Override
        protected AbstractListingExtensionState getState() {
            return (AbstractListingExtensionState) super.getState();
        }

        @Override
        protected AbstractListingExtensionState getState(boolean markAsDirty) {
            return (AbstractListingExtensionState) super.getState(markAsDirty);
        }
    }

    private final DataCommunicator<T> dataCommunicator;

    /**
     * Creates a new {@code AbstractListing} with a default data communicator.
     * <p>
     */
    protected AbstractListing() {
        this(new DataCommunicator<>());
    }

    /**
     * Creates a new {@code AbstractListing} with the given custom data
     * communicator.
     * <p>
     * <strong>Note:</strong> This method is for creating an
     * {@code AbstractListing} with a custom communicator. In the common case
     * {@link AbstractListing#AbstractListing()} should be used.
     * <p>
     *
     * @param dataCommunicator
     *            the data communicator to use, not null
     */
    protected AbstractListing(DataCommunicator<T> dataCommunicator) {
        Objects.requireNonNull(dataCommunicator,
                "dataCommunicator cannot be null");

        this.dataCommunicator = dataCommunicator;
        addExtension(dataCommunicator);
    }

    protected void internalSetDataProvider(DataProvider<T, ?> dataProvider) {
        internalSetDataProvider(dataProvider, null);
    }

    protected <F> SerializableConsumer<F> internalSetDataProvider(
            DataProvider<T, F> dataProvider, F initialFilter) {
        return getDataCommunicator().setDataProvider(dataProvider,
                initialFilter);
    }

    protected DataProvider<T, ?> internalGetDataProvider() {
        return getDataCommunicator().getDataProvider();
    }

    /**
     * Gets the item caption generator that is used to produce the strings shown
     * in the combo box for each item.
     *
     * @return the item caption generator used, not null
     */
    protected ItemCaptionGenerator<T> getItemCaptionGenerator() {
        return itemCaptionGenerator;
    }

    /**
     * Sets the item caption generator that is used to produce the strings shown
     * in the combo box for each item. By default,
     * {@link String#valueOf(Object)} is used.
     *
     * @param itemCaptionGenerator
     *            the item caption provider to use, not null
     */
    protected void setItemCaptionGenerator(
            ItemCaptionGenerator<T> itemCaptionGenerator) {
        Objects.requireNonNull(itemCaptionGenerator,
                "Item caption generators must not be null");
        this.itemCaptionGenerator = itemCaptionGenerator;
        getDataCommunicator().reset();
    }

    /**
     * Sets the item icon generator that is used to produce custom icons for
     * showing items in the popup. The generator can return null for items with
     * no icon.
     *
     * @see IconGenerator
     *
     * @param itemIconGenerator
     *            the item icon generator to set, not null
     * @throws NullPointerException
     *             if {@code itemIconGenerator} is {@code null}
     */
    protected void setItemIconGenerator(IconGenerator<T> itemIconGenerator) {
        Objects.requireNonNull(itemIconGenerator,
                "Item icon generator must not be null");
        this.itemIconGenerator = itemIconGenerator;
        getDataCommunicator().reset();
    }

    /**
     * Gets the currently used item icon generator. The default item icon
     * provider returns null for all items, resulting in no icons being used.
     *
     * @see IconGenerator
     * @see #setItemIconGenerator(IconGenerator)
     *
     * @return the currently used item icon generator, not null
     */
    protected IconGenerator<T> getItemIconGenerator() {
        return itemIconGenerator;
    }

    /**
     * Adds the given data generator to this listing. If the generator was
     * already added, does nothing.
     *
     * @param generator
     *            the data generator to add, not null
     */
    protected void addDataGenerator(DataGenerator<T> generator) {
        getDataCommunicator().addDataGenerator(generator);
    }

    /**
     * Removes the given data generator from this listing. If this listing does
     * not have the generator, does nothing.
     *
     * @param generator
     *            the data generator to remove, not null
     */
    protected void removeDataGenerator(DataGenerator<T> generator) {
        getDataCommunicator().removeDataGenerator(generator);
    }

    /**
     * Returns the data communicator of this listing.
     *
     * @return the data communicator, not null
     */
    public DataCommunicator<T> getDataCommunicator() {
        return dataCommunicator;
    }

    @Override
    public void writeDesign(Element design, DesignContext designContext) {
        super.writeDesign(design, designContext);
        doWriteDesign(design, designContext);
    }

    /**
     * Writes listing specific state into the given design.
     * <p>
     * This method is separated from
     * {@link #writeDesign(Element, DesignContext)} to be overridable in
     * subclasses that need to replace this, but still must be able to call
     * {@code super.writeDesign(...)}.
     *
     * @see #doReadDesign(Element, DesignContext)
     *
     * @param design
     *            The element to write the component state to. Any previous
     *            attributes or child nodes are <i>not</i> cleared.
     * @param designContext
     *            The DesignContext instance used for writing the design
     *
     */
    protected void doWriteDesign(Element design, DesignContext designContext) {
        // Write options if warranted
        if (designContext.shouldWriteData(this)) {
            writeItems(design, designContext);
        }

        AbstractListing<T> select = designContext.getDefaultInstance(this);
        Attributes attr = design.attributes();
        DesignAttributeHandler.writeAttribute("readonly", attr, isReadOnly(),
                select.isReadOnly(), Boolean.class, designContext);
    }

    /**
     * Writes the data source items to a design. Hierarchical select components
     * should override this method to only write the root items.
     *
     * @param design
     *            the element into which to insert the items
     * @param context
     *            the DesignContext instance used in writing
     */
    protected void writeItems(Element design, DesignContext context) {
        internalGetDataProvider().fetch(new Query<>())
                .forEach(item -> writeItem(design, item, context));
    }

    /**
     * Writes a data source Item to a design. Hierarchical select components
     * should override this method to recursively write any child items as well.
     *
     * @param design
     *            the element into which to insert the item
     * @param item
     *            the item to write
     * @param context
     *            the DesignContext instance used in writing
     * @return a JSOUP element representing the {@code item}
     */
    protected Element writeItem(Element design, T item, DesignContext context) {
        Element element = design.appendElement("option");

        String caption = getItemCaptionGenerator().apply(item);
        if (caption != null) {
            element.html(DesignFormatter.encodeForTextNode(caption));
        } else {
            element.html(DesignFormatter.encodeForTextNode(item.toString()));
        }
        element.attr("item", serializeDeclarativeRepresentation(item));

        Resource icon = getItemIconGenerator().apply(item);
        if (icon != null) {
            DesignAttributeHandler.writeAttribute("icon", element.attributes(),
                    icon, null, Resource.class, context);
        }

        return element;
    }

    @Override
    public void readDesign(Element design, DesignContext context) {
        super.readDesign(design, context);
        doReadDesign(design, context);
    }

    /**
     * Reads the listing specific state from the given design.
     * <p>
     * This method is separated from {@link #readDesign(Element, DesignContext)}
     * to be overridable in subclasses that need to replace this, but still must
     * be able to call {@code super.readDesign(...)}.
     *
     * @see #doWriteDesign(Element, DesignContext)
     *
     * @param design
     *            The element to obtain the state from
     * @param context
     *            The DesignContext instance used for parsing the design
     */
    protected void doReadDesign(Element design, DesignContext context) {
        Attributes attr = design.attributes();
        if (attr.hasKey("readonly")) {
            setReadOnly(DesignAttributeHandler.readAttribute("readonly", attr,
                    Boolean.class));
        }

        setItemCaptionGenerator(
                new DeclarativeCaptionGenerator<>(getItemCaptionGenerator()));
        setItemIconGenerator(
                new DeclarativeIconGenerator<>(getItemIconGenerator()));

        readItems(design, context);
    }

    /**
     * Reads the data source items from the {@code design}.
     *
     * @param design
     *            The element to obtain the state from
     * @param context
     *            The DesignContext instance used for parsing the design
     */
    protected abstract void readItems(Element design, DesignContext context);

    /**
     * Reads an Item from a design and inserts it into the data source.
     * <p>
     * Doesn't care about selection/value (if any).
     *
     * @param child
     *            a child element representing the item
     * @param context
     *            the DesignContext instance used in parsing
     * @return the item id of the new item
     *
     * @throws DesignException
     *             if the tag name of the {@code child} element is not
     *             {@code option}.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected T readItem(Element child, DesignContext context) {
        if (!"option".equals(child.tagName())) {
            throw new DesignException("Unrecognized child element in "
                    + getClass().getSimpleName() + ": " + child.tagName());
        }

        String serializedItem = "";
        String caption = DesignFormatter.decodeFromTextNode(child.html());
        if (child.hasAttr("item")) {
            serializedItem = child.attr("item");
        }

        T item = deserializeDeclarativeRepresentation(serializedItem);

        ItemCaptionGenerator<T> captionGenerator = getItemCaptionGenerator();
        if (captionGenerator instanceof DeclarativeCaptionGenerator) {
            ((DeclarativeCaptionGenerator) captionGenerator).setCaption(item,
                    caption);
        } else {
            throw new IllegalStateException(String.format(
                    "Don't know how "
                            + "to set caption using current caption generator '%s'",
                    captionGenerator.getClass().getName()));
        }

        IconGenerator<T> iconGenerator = getItemIconGenerator();
        if (child.hasAttr("icon")) {
            if (iconGenerator instanceof DeclarativeIconGenerator) {
                ((DeclarativeIconGenerator) iconGenerator).setIcon(item,
                        DesignAttributeHandler.readAttribute("icon",
                                child.attributes(), Resource.class));
            } else {
                throw new IllegalStateException(String.format(
                        "Don't know how "
                                + "to set icon using current caption generator '%s'",
                        iconGenerator.getClass().getName()));
            }
        }

        return item;
    }

    /**
     * Deserializes a string to a data item.
     * <p>
     * Default implementation is able to handle only {@link String} as an item
     * type. There will be a {@link ClassCastException} if {@code T } is not a
     * {@link String}.
     *
     * @see #serializeDeclarativeRepresentation(Object)
     *
     * @param item
     *            string to deserialize
     * @throws ClassCastException
     *             if type {@code T} is not a {@link String}
     * @return deserialized item
     */
    protected T deserializeDeclarativeRepresentation(String item) {
        return (T) item;
    }

    /**
     * Serializes an {@code item} to a string for saving declarative format.
     * <p>
     * Default implementation delegates a call to {@code item.toString()}.
     *
     * @see #deserializeDeclarativeRepresentation(String)
     *
     * @param item
     *            a data item
     * @return string representation of the {@code item}.
     */
    protected String serializeDeclarativeRepresentation(T item) {
        return item.toString();
    }

    @Override
    protected AbstractListingState getState() {
        return (AbstractListingState) super.getState();
    }

    @Override
    protected AbstractListingState getState(boolean markAsDirty) {
        return (AbstractListingState) super.getState(markAsDirty);
    }

    @Override
    public void focus() {
        super.focus();
    }

    @Override
    public int getTabIndex() {
        return getState(false).tabIndex;
    }

    @Override
    public void setTabIndex(int tabIndex) {
        getState().tabIndex = tabIndex;
    }
}
