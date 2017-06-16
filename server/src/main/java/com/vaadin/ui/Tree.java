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

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.vaadin.data.Binder;
import com.vaadin.data.HasHierarchicalDataProvider;
import com.vaadin.data.SelectionModel;
import com.vaadin.data.TreeData;
import com.vaadin.data.provider.DataGenerator;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.HierarchicalDataProvider;
import com.vaadin.data.provider.TreeDataProvider;
import com.vaadin.event.CollapseEvent;
import com.vaadin.event.CollapseEvent.CollapseListener;
import com.vaadin.event.ConnectorEvent;
import com.vaadin.event.ExpandEvent;
import com.vaadin.event.ExpandEvent.ExpandListener;
import com.vaadin.event.SerializableEventListener;
import com.vaadin.event.selection.SelectionListener;
import com.vaadin.server.ErrorMessage;
import com.vaadin.server.Resource;
import com.vaadin.shared.Registration;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.shared.ui.tree.TreeMultiSelectionModelState;
import com.vaadin.shared.ui.tree.TreeRendererState;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.components.grid.MultiSelectionModelImpl;
import com.vaadin.ui.renderers.AbstractRenderer;
import com.vaadin.util.ReflectTools;

import elemental.json.JsonObject;

/**
 * Tree component. A Tree can be used to select an item from a hierarchical set
 * of items.
 *
 * @author Vaadin Ltd
 * @since 8.1
 *
 * @param <T>
 *            the data type
 */
public class Tree<T> extends Composite
        implements HasHierarchicalDataProvider<T> {

    @Deprecated
    private static final Method ITEM_CLICK_METHOD = ReflectTools
            .findMethod(ItemClickListener.class, "itemClick", ItemClick.class);

    /**
     * A listener for item click events.
     *
     * @param <T>
     *            the tree item type
     *
     * @see ItemClick
     * @see Registration
     * @since 8.1
     */
    @FunctionalInterface
    public interface ItemClickListener<T> extends SerializableEventListener {
        /**
         * Invoked when this listener receives a item click event from a Tree to
         * which it has been added.
         *
         * @param event
         *            the received event, not {@code null}
         */
        public void itemClick(Tree.ItemClick<T> event);
    }

    /**
     * Tree item click event.
     *
     * @param <T>
     *            the data type of tree
     * @since 8.1
     */
    public static class ItemClick<T> extends ConnectorEvent {

        private final T item;

        /**
         * Constructs a new item click.
         *
         * @param source
         *            the tree component
         * @param item
         *            the clicked item
         */
        protected ItemClick(Tree<T> source, T item) {
            super(source);
            this.item = item;
        }

        /**
         * Returns the clicked item.
         *
         * @return the clicked item
         */
        public T getItem() {
            return item;
        }

        @SuppressWarnings("unchecked")
        @Override
        public Tree<T> getSource() {
            return (Tree<T>) super.getSource();
        }
    }

    /**
     * String renderer that handles icon resources and stores their identifiers
     * into data objects.
     *
     * @since 8.1
     */
    public final class TreeRenderer extends AbstractRenderer<T, String>
            implements DataGenerator<T> {

        /**
         * Constructs a new TreeRenderer.
         */
        protected TreeRenderer() {
            super(String.class);
        }

        private Map<T, String> resourceKeyMap = new HashMap<>();
        private int counter = 0;

        @Override
        public void generateData(T item, JsonObject jsonObject) {
            Resource resource = iconProvider.apply(item);
            if (resource == null) {
                destroyData(item);
                return;
            }

            if (!resourceKeyMap.containsKey(item)) {
                resourceKeyMap.put(item, "icon" + (counter++));
            }
            setResource(resourceKeyMap.get(item), resource);
            jsonObject.put("itemIcon", resourceKeyMap.get(item));
        }

        @Override
        public void destroyData(T item) {
            if (resourceKeyMap.containsKey(item)) {
                setResource(resourceKeyMap.get(item), null);
                resourceKeyMap.remove(item);
            }
        }

        @Override
        public void destroyAllData() {
            Set<T> keys = new HashSet<>(resourceKeyMap.keySet());
            for (T key : keys) {
                destroyData(key);
            }
        }

        @Override
        protected TreeRendererState getState() {
            return (TreeRendererState) super.getState();
        }

        @Override
        protected TreeRendererState getState(boolean markAsDirty) {
            return (TreeRendererState) super.getState(markAsDirty);
        }
    }

    /**
     * Custom MultiSelectionModel for Tree. TreeMultiSelectionModel does not
     * show selection column.
     *
     * @param <T>
     *            the tree item type
     *
     * @since 8.1
     */
    public final static class TreeMultiSelectionModel<T>
            extends MultiSelectionModelImpl<T> {

        @Override
        protected TreeMultiSelectionModelState getState() {
            return (TreeMultiSelectionModelState) super.getState();
        }

        @Override
        protected TreeMultiSelectionModelState getState(boolean markAsDirty) {
            return (TreeMultiSelectionModelState) super.getState(markAsDirty);
        }
    }

    private TreeGrid<T> treeGrid = new TreeGrid<>();
    private ItemCaptionGenerator<T> captionGenerator = String::valueOf;
    private IconGenerator<T> iconProvider = t -> null;

    /**
     * Constructs a new Tree Component.
     */
    public Tree() {
        setCompositionRoot(treeGrid);
        TreeRenderer renderer = new TreeRenderer();
        treeGrid.getDataCommunicator().addDataGenerator(renderer);
        treeGrid.addColumn(i -> captionGenerator.apply(i), renderer)
                .setId("column");
        treeGrid.setHierarchyColumn("column");
        while (treeGrid.getHeaderRowCount() > 0) {
            treeGrid.removeHeaderRow(0);
        }
        treeGrid.setPrimaryStyleName("v-tree8");
        treeGrid.setRowHeight(28);

        setWidth("100%");
        treeGrid.setHeightUndefined();
        treeGrid.setHeightMode(HeightMode.UNDEFINED);

        treeGrid.addExpandListener(e -> fireExpandEvent(e.getExpandedItem(),
                e.isUserOriginated()));
        treeGrid.addCollapseListener(e -> fireCollapseEvent(
                e.getCollapsedItem(), e.isUserOriginated()));
        treeGrid.addItemClickListener(
                e -> fireEvent(new ItemClick<>(this, e.getItem())));
    }

    /**
     * Constructs a new Tree Component with given caption.
     *
     * @param caption
     *            the caption for component
     */
    public Tree(String caption) {
        this();

        setCaption(caption);
    }

    /**
     * Constructs a new Tree Component with given caption and {@code TreeData}.
     *
     * @param caption
     *            the caption for component
     * @param treeData
     *            the tree data for component
     */
    public Tree(String caption, TreeData<T> treeData) {
        this(caption, new TreeDataProvider<>(treeData));
    }

    /**
     * Constructs a new Tree Component with given caption and
     * {@code HierarchicalDataProvider}.
     *
     * @param caption
     *            the caption for component
     * @param dataProvider
     *            the hierarchical data provider for component
     */
    public Tree(String caption, HierarchicalDataProvider<T, ?> dataProvider) {
        this(caption);

        treeGrid.setDataProvider(dataProvider);
    }

    /**
     * Constructs a new Tree Component with given
     * {@code HierarchicalDataProvider}.
     *
     * @param dataProvider
     *            the hierarchical data provider for component
     */
    public Tree(HierarchicalDataProvider<T, ?> dataProvider) {
        this(null, dataProvider);
    }

    @Override
    public HierarchicalDataProvider<T, ?> getDataProvider() {
        return treeGrid.getDataProvider();
    }

    @Override
    public void setDataProvider(DataProvider<T, ?> dataProvider) {
        treeGrid.setDataProvider(dataProvider);
    }

    /**
     * Adds an ExpandListener to this Tree.
     *
     * @see ExpandEvent
     *
     * @param listener
     *            the listener to add
     * @return a registration for the listener
     */
    public Registration addExpandListener(ExpandListener<T> listener) {
        return addListener(ExpandEvent.class, listener,
                ExpandListener.EXPAND_METHOD);
    }

    /**
     * Adds a CollapseListener to this Tree.
     *
     * @see CollapseEvent
     *
     * @param listener
     *            the listener to add
     * @return a registration for the listener
     */
    public Registration addCollapseListener(CollapseListener<T> listener) {
        return addListener(CollapseEvent.class, listener,
                CollapseListener.COLLAPSE_METHOD);
    }

    /**
     * Fires an expand event with given item.
     *
     * @param item
     *            the expanded item
     * @param userOriginated
     *            whether the expand was triggered by a user interaction or the
     *            server
     */
    protected void fireExpandEvent(T item, boolean userOriginated) {
        fireEvent(new ExpandEvent<>(this, item, userOriginated));
    }

    /**
     * Fires a collapse event with given item.
     *
     * @param item
     *            the collapsed item
     * @param userOriginated
     *            whether the collapse was triggered by a user interaction or
     *            the server
     */
    protected void fireCollapseEvent(T item, boolean userOriginated) {
        fireEvent(new CollapseEvent<>(this, item, userOriginated));
    }

    /**
     * Expands the given items.
     * <p>
     * If an item is currently expanded, does nothing. If an item does not have
     * any children, does nothing.
     *
     * @param items
     *            the items to expand
     */
    public void expand(T... items) {
        treeGrid.expand(items);
    }

    /**
     * Expands the given items.
     * <p>
     * If an item is currently expanded, does nothing. If an item does not have
     * any children, does nothing.
     *
     * @param items
     *            the items to expand
     */
    public void expand(Collection<T> items) {
        treeGrid.expand(items);
    }

    /**
     * Collapse the given items.
     * <p>
     * For items that are already collapsed, does nothing.
     *
     * @param items
     *            the collection of items to collapse
     */
    public void collapse(T... items) {
        treeGrid.collapse(items);
    }

    /**
     * Collapse the given items.
     * <p>
     * For items that are already collapsed, does nothing.
     *
     * @param items
     *            the collection of items to collapse
     */
    public void collapse(Collection<T> items) {
        treeGrid.collapse(items);
    }

    /**
     * This method is a shorthand that delegates to the currently set selection
     * model.
     *
     * @see #getSelectionModel()
     *
     * @return set of selected items
     */
    public Set<T> getSelectedItems() {
        return treeGrid.getSelectedItems();
    }

    /**
     * This method is a shorthand that delegates to the currently set selection
     * model.
     *
     * @param item
     *            item to select
     *
     * @see SelectionModel#select(Object)
     * @see #getSelectionModel()
     */
    public void select(T item) {
        treeGrid.select(item);
    }

    /**
     * This method is a shorthand that delegates to the currently set selection
     * model.
     *
     * @param item
     *            item to deselect
     *
     * @see SelectionModel#deselect(Object)
     * @see #getSelectionModel()
     */
    public void deselect(T item) {
        treeGrid.deselect(item);
    }

    /**
     * Adds a selection listener to the current selection model.
     * <p>
     * <strong>NOTE:</strong> If selection mode is switched with
     * {@link setSelectionMode(SelectionMode)}, then this listener is not
     * triggered anymore when selection changes!
     *
     * @param listener
     *            the listener to add
     * @return a registration handle to remove the listener
     *
     * @throws UnsupportedOperationException
     *             if selection has been disabled with
     *             {@link SelectionMode.NONE}
     */
    public Registration addSelectionListener(SelectionListener<T> listener) {
        return treeGrid.addSelectionListener(listener);
    }

    /**
     * Use this tree as a single select in {@link Binder}. Throws
     * {@link IllegalStateException} if the tree is not using
     * {@link SelectionMode#SINGLE}.
     *
     * @return the single select wrapper that can be used in binder
     */
    public SingleSelect<T> asSingleSelect() {
        return treeGrid.asSingleSelect();
    }

    /**
     * Returns the selection model for this Tree.
     *
     * @return the selection model, not <code>null</code>
     */
    public SelectionModel<T> getSelectionModel() {
        return treeGrid.getSelectionModel();
    }

    /**
     * Sets the item caption generator that is used to produce the strings shown
     * as the text for each item. By default, {@link String#valueOf(Object)} is
     * used.
     *
     * @param captionGenerator
     *            the item caption provider to use, not <code>null</code>
     */
    public void setItemCaptionGenerator(
            ItemCaptionGenerator<T> captionGenerator) {
        Objects.requireNonNull(captionGenerator,
                "Caption generator must not be null");
        this.captionGenerator = captionGenerator;
        treeGrid.getDataCommunicator().reset();
    }

    /**
     * Sets the item icon generator that is used to produce custom icons for
     * items. The generator can return <code>null</code> for items with no icon.
     *
     * @see IconGenerator
     *
     * @param iconGenerator
     *            the item icon generator to set, not <code>null</code>
     * @throws NullPointerException
     *             if {@code itemIconGenerator} is {@code null}
     */
    public void setItemIconGenerator(IconGenerator<T> iconGenerator) {
        Objects.requireNonNull(iconGenerator,
                "Item icon generator must not be null");
        this.iconProvider = iconGenerator;
        treeGrid.getDataCommunicator().reset();
    }

    /**
     * Sets the item collapse allowed provider for this Tree. The provider
     * should return {@code true} for any item that the user can collapse.
     * <p>
     * <strong>Note:</strong> This callback will be accessed often when sending
     * data to the client. The callback should not do any costly operations.
     *
     * @param provider
     *            the item collapse allowed provider, not {@code null}
     */
    public void setItemCollapseAllowedProvider(
            ItemCollapseAllowedProvider<T> provider) {
        treeGrid.setItemCollapseAllowedProvider(provider);
    }

    /**
     * Sets the style generator that is used for generating class names for
     * items in this tree. Returning null from the generator results in no
     * custom style name being set.
     *
     * @see StyleGenerator
     *
     * @param styleGenerator
     *            the item style generator to set, not {@code null}
     * @throws NullPointerException
     *             if {@code styleGenerator} is {@code null}
     */
    public void setStyleGenerator(StyleGenerator<T> styleGenerator) {
        treeGrid.setStyleGenerator(styleGenerator);
    }

    /**
     * Gets the item caption generator.
     *
     * @return the item caption generator
     */
    public ItemCaptionGenerator<T> getItemCaptionGenerator() {
        return captionGenerator;
    }

    /**
     * Gets the item icon generator.
     *
     * @see IconGenerator
     *
     * @return the item icon generator
     */
    public IconGenerator<T> getItemIconGenerator() {
        return iconProvider;
    }

    /**
     * Gets the item collapse allowed provider.
     *
     * @return the item collapse allowed provider
     */
    public ItemCollapseAllowedProvider<T> getItemCollapseAllowedProvider() {
        return treeGrid.getItemCollapseAllowedProvider();
    }

    /**
     * Gets the style generator.
     *
     * @see StyleGenerator
     *
     * @return the item style generator
     */
    public StyleGenerator<T> getStyleGenerator() {
        return treeGrid.getStyleGenerator();
    }

    /**
     * Adds an item click listener. The listener is called when an item of this
     * {@code Tree} is clicked.
     *
     * @param listener
     *            the item click listener, not null
     * @return a registration for the listener
     */
    public Registration addItemClickListener(ItemClickListener<T> listener) {
        return addListener(ItemClick.class, listener, ITEM_CLICK_METHOD);
    }

    /**
     * Sets the tree's selection mode.
     * <p>
     * The built-in selection modes are:
     * <ul>
     * <li>{@link SelectionMode#SINGLE} <b>the default model</b></li>
     * <li>{@link SelectionMode#MULTI}</li>
     * <li>{@link SelectionMode#NONE} preventing selection</li>
     * </ul>
     *
     * @param selectionMode
     *            the selection mode to switch to, not {@code null}
     * @return the used selection model
     *
     * @see SelectionMode
     */
    public SelectionModel<T> setSelectionMode(SelectionMode selectionMode) {
        Objects.requireNonNull(selectionMode,
                "Can not set selection mode to null");
        switch (selectionMode) {
        case MULTI:
            TreeMultiSelectionModel<T> model = new TreeMultiSelectionModel<>();
            treeGrid.setSelectionModel(model);
            return model;
        default:
            return treeGrid.setSelectionMode(selectionMode);
        }
    }

    /**
     * @deprecated This component's height is always set to be undefined.
     *             Calling this method will have no effect.
     */
    @Override
    @Deprecated
    public void setHeight(String height) {
    }

    /**
     * @deprecated This component's height is always set to be undefined.
     *             Calling this method will have no effect.
     */
    @Override
    @Deprecated
    public void setHeight(float height, Unit unit) {
    }

    /**
     * @deprecated This component's height is always set to be undefined.
     *             Calling this method will have no effect.
     */
    @Override
    @Deprecated
    public void setHeightUndefined() {
    }

    @Override
    public void setCaption(String caption) {
        treeGrid.setCaption(caption);
    }

    @Override
    public String getCaption() {
        return treeGrid.getCaption();
    }

    @Override
    public void setIcon(Resource icon) {
        treeGrid.setIcon(icon);
    }

    @Override
    public Resource getIcon() {
        return treeGrid.getIcon();
    }

    @Override
    public String getStyleName() {
        return treeGrid.getStyleName();
    }

    @Override
    public void setStyleName(String style) {
        treeGrid.setStyleName(style);
    }

    @Override
    public void setStyleName(String style, boolean add) {
        treeGrid.setStyleName(style, add);
    }

    @Override
    public void addStyleName(String style) {
        treeGrid.addStyleName(style);
    }

    @Override
    public void removeStyleName(String style) {
        treeGrid.removeStyleName(style);
    }

    @Override
    public String getPrimaryStyleName() {
        return treeGrid.getPrimaryStyleName();
    }

    @Override
    public void setPrimaryStyleName(String style) {
        treeGrid.setPrimaryStyleName(style);
    }

    @Override
    public void setId(String id) {
        treeGrid.setId(id);
    }

    @Override
    public String getId() {
        return treeGrid.getId();
    }

    @Override
    public void setCaptionAsHtml(boolean captionAsHtml) {
        treeGrid.setCaptionAsHtml(captionAsHtml);
    }

    @Override
    public boolean isCaptionAsHtml() {
        return treeGrid.isCaptionAsHtml();
    }

    @Override
    public void setDescription(String description) {
        treeGrid.setDescription(description);
    }

    @Override
    public void setDescription(String description, ContentMode mode) {
        treeGrid.setDescription(description, mode);
    }

    @Override
    public ErrorMessage getErrorMessage() {
        return treeGrid.getErrorMessage();
    }

    @Override
    public ErrorMessage getComponentError() {
        return treeGrid.getComponentError();
    }

    @Override
    public void setComponentError(ErrorMessage componentError) {
        treeGrid.setComponentError(componentError);
    }
}
