/*
 * Copyright 2000-2021 Vaadin Ltd.
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.vaadin.data.Binder;
import com.vaadin.data.HasHierarchicalDataProvider;
import com.vaadin.data.SelectionModel;
import com.vaadin.data.TreeData;
import com.vaadin.data.provider.DataGenerator;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.HierarchicalDataProvider;
import com.vaadin.data.provider.HierarchicalQuery;
import com.vaadin.data.provider.TreeDataProvider;
import com.vaadin.event.CollapseEvent;
import com.vaadin.event.CollapseEvent.CollapseListener;
import com.vaadin.event.ConnectorEvent;
import com.vaadin.event.ContextClickEvent;
import com.vaadin.event.ExpandEvent;
import com.vaadin.event.ExpandEvent.ExpandListener;
import com.vaadin.event.SerializableEventListener;
import com.vaadin.event.selection.SelectionListener;
import com.vaadin.server.ErrorMessage;
import com.vaadin.server.Resource;
import com.vaadin.shared.EventId;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.Registration;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.shared.ui.grid.ScrollDestination;
import com.vaadin.shared.ui.tree.TreeMultiSelectionModelState;
import com.vaadin.shared.ui.tree.TreeRendererState;
import com.vaadin.ui.Component.Focusable;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.components.grid.MultiSelectionModelImpl;
import com.vaadin.ui.components.grid.NoSelectionModel;
import com.vaadin.ui.components.grid.SingleSelectionModelImpl;
import com.vaadin.ui.declarative.DesignAttributeHandler;
import com.vaadin.ui.declarative.DesignContext;
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
        implements HasHierarchicalDataProvider<T>, Focusable {

    @Deprecated
    private static final Method ITEM_CLICK_METHOD = ReflectTools
            .findMethod(ItemClickListener.class, "itemClick", ItemClick.class);
    private Registration contextClickRegistration = null;

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
        private final MouseEventDetails mouseEventDetails;

        /**
         * Constructs a new item click.
         *
         * @param source
         *            the tree component
         * @param item
         *            the clicked item
         * @param mouseEventDetails
         *            information about the original mouse event (mouse button
         *            clicked, coordinates if available etc.)
         */
        protected ItemClick(Tree<T> source, T item,
                MouseEventDetails mouseEventDetails) {
            super(source);
            this.item = item;
            this.mouseEventDetails = mouseEventDetails;
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

        /**
         * Returns the mouse event details.
         *
         * @return the mouse event details
         */
        public MouseEventDetails getMouseEventDetails() {
            return mouseEventDetails;
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
    public static final class TreeMultiSelectionModel<T>
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

    private TreeGrid<T> treeGrid = createTreeGrid();

    /**
     * Create inner {@link TreeGrid} object. May be overridden in subclasses.
     *
     * @return new {@link TreeGrid}
     */
    protected TreeGrid<T> createTreeGrid() {
        return new TreeGrid<>();
    }

    private ItemCaptionGenerator<T> captionGenerator = String::valueOf;
    private IconGenerator<T> iconProvider = t -> null;
    private final TreeRenderer renderer;
    private boolean autoRecalculateWidth = true;

    /**
     * Constructs a new Tree Component.
     */
    public Tree() {
        setCompositionRoot(treeGrid);
        renderer = new TreeRenderer();
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

        treeGrid.addExpandListener(event -> {
            fireExpandEvent(event.getExpandedItem(), event.isUserOriginated());
            if (autoRecalculateWidth) {
                treeGrid.recalculateColumnWidths();
            }
        });
        treeGrid.addCollapseListener(event -> {
            fireCollapseEvent(event.getCollapsedItem(),
                    event.isUserOriginated());
            if (autoRecalculateWidth) {
                treeGrid.recalculateColumnWidths();
            }
        });
        treeGrid.addItemClickListener(event -> fireEvent(new ItemClick<>(this,
                event.getItem(), event.getMouseEventDetails())));
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
     * Expands the given items and their children recursively until the given
     * depth.
     * <p>
     * {@code depth} describes the maximum distance between a given item and its
     * descendant, meaning that {@code expandRecursively(items, 0)} expands only
     * the given items while {@code expandRecursively(items, 2)} expands the
     * given items as well as their children and grandchildren.
     *
     * @param items
     *            the items to expand recursively
     * @param depth
     *            the maximum depth of recursion
     * @since 8.4
     */
    public void expandRecursively(Collection<T> items, int depth) {
        treeGrid.expandRecursively(items, depth);
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
     * Collapse the given items and their children recursively until the given
     * depth.
     * <p>
     * {@code depth} describes the maximum distance between a given item and its
     * descendant, meaning that {@code collapseRecursively(items, 0)} collapses
     * only the given items while {@code collapseRecursively(items, 2)}
     * collapses the given items as well as their children and grandchildren.
     *
     * @param items
     *            the items to expand recursively
     * @param depth
     *            the maximum depth of recursion
     * @since 8.4
     */
    public void collapseRecursively(Collection<T> items, int depth) {
        treeGrid.collapseRecursively(items, depth);
    }

    /**
     * Returns whether a given item is expanded or collapsed.
     *
     * @param item
     *            the item to check
     * @return true if the item is expanded, false if collapsed
     */
    public boolean isExpanded(T item) {
        return treeGrid.isExpanded(item);
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
     * {@link #setSelectionMode(SelectionMode)}, then this listener is not
     * triggered anymore when selection changes!
     *
     * @param listener
     *            the listener to add
     * @return a registration handle to remove the listener
     *
     * @throws UnsupportedOperationException
     *             if selection has been disabled with
     *             {@link SelectionMode#NONE}
     */
    public Registration addSelectionListener(SelectionListener<T> listener) {
        return treeGrid.addSelectionListener(listener);
    }
    
    /**
     * Use this tree as a multi select in {@link Binder}. Throws
     * {@link IllegalStateException} if the tree is not using
     * {@link SelectionMode#MULTI}.
     *
     * @return the multi select wrapper that can be used in binder
     * @since 8.11
     */
    public MultiSelect<T> asMultiSelect() {
        return treeGrid.asMultiSelect();
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
     * Sets the description generator that is used for generating tooltip
     * descriptions for items.
     *
     * @since 8.2
     * @param descriptionGenerator
     *            the item description generator to set, or <code>null</code> to
     *            remove a previously set generator
     */
    public void setItemDescriptionGenerator(
            DescriptionGenerator<T> descriptionGenerator) {
        treeGrid.setDescriptionGenerator(descriptionGenerator);
    }

    /**
     * Sets the description generator that is used for generating HTML tooltip
     * descriptions for items.
     *
     * @param descriptionGenerator
     *            the item description generator to set, or <code>null</code> to
     *            remove a previously set generator
     * @param contentMode
     *            how client should interpret textual values
     *
     * @since 8.4
     */
    public void setItemDescriptionGenerator(
            DescriptionGenerator<T> descriptionGenerator,
            ContentMode contentMode) {
        treeGrid.setDescriptionGenerator(descriptionGenerator, contentMode);
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
     * Gets the item description generator.
     *
     * @since 8.2
     * @return the item description generator
     */
    public DescriptionGenerator<T> getItemDescriptionGenerator() {
        return treeGrid.getDescriptionGenerator();
    }

    /**
     * Adds an item click listener. The listener is called when an item of this
     * {@code Tree} is clicked.
     *
     * @param listener
     *            the item click listener, not null
     * @return a registration for the listener
     * @see #addContextClickListener
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

    private SelectionMode getSelectionMode() {
        SelectionModel<T> selectionModel = getSelectionModel();
        SelectionMode mode = null;
        if (selectionModel.getClass().equals(SingleSelectionModelImpl.class)) {
            mode = SelectionMode.SINGLE;
        } else if (selectionModel.getClass()
                .equals(TreeMultiSelectionModel.class)) {
            mode = SelectionMode.MULTI;
        } else if (selectionModel.getClass().equals(NoSelectionModel.class)) {
            mode = SelectionMode.NONE;
        }
        return mode;
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

    /**
     * Sets the height of a row. If -1 (default), the row height is calculated
     * based on the theme for an empty row before the Tree is displayed.
     *
     * @param rowHeight
     *            The height of a row in pixels or -1 for automatic calculation
     */
    public void setRowHeight(double rowHeight) {
        treeGrid.setRowHeight(rowHeight);
    }

    /**
     * Gets the currently set content mode of the item captions of this Tree.
     *
     * @since 8.1.3
     * @see ContentMode
     * @return the content mode of the item captions of this Tree
     */
    public ContentMode getContentMode() {
        return renderer.getState(false).mode;
    }

    /**
     * Sets the content mode of the item caption.
     *
     * @see ContentMode
     * @param contentMode
     *            the content mode
     */
    public void setContentMode(ContentMode contentMode) {
        renderer.getState().mode = contentMode;
    }

    /**
     * Returns the current state of automatic width recalculation.
     *
     * @return {@code true} if enabled; {@code false} if disabled
     *
     * @since 8.1.1
     */
    public boolean isAutoRecalculateWidth() {
        return autoRecalculateWidth;
    }

    /**
     * Sets the automatic width recalculation on or off. This feature is on by
     * default.
     *
     * @param autoRecalculateWidth
     *            {@code true} to enable recalculation; {@code false} to turn it
     *            off
     *
     * @since 8.1.1
     */
    public void setAutoRecalculateWidth(boolean autoRecalculateWidth) {
        this.autoRecalculateWidth = autoRecalculateWidth;

        treeGrid.getColumns().get(0)
                .setMinimumWidthFromContent(autoRecalculateWidth);
        treeGrid.recalculateColumnWidths();
    }

    /**
     * Adds a context click listener that gets notified when a context click
     * happens.
     *
     * @param listener
     *            the context click listener to add, not null actual event
     *            provided to the listener is {@link TreeContextClickEvent}
     * @return a registration object for removing the listener
     *
     * @since 8.1
     * @see #addItemClickListener
     * @see Registration
     */
    @Override
    public Registration addContextClickListener(
            ContextClickEvent.ContextClickListener listener) {
        Registration registration = addListener(EventId.CONTEXT_CLICK,
                ContextClickEvent.class, listener,
                ContextClickEvent.CONTEXT_CLICK_METHOD);
        setupContextClickListener();
        return () -> {
            registration.remove();
            setupContextClickListener();
        };
    }

    @Override
    @Deprecated
    public void removeContextClickListener(
            ContextClickEvent.ContextClickListener listener) {
        super.removeContextClickListener(listener);
        setupContextClickListener();
    }

    @Override
    public void writeDesign(Element design, DesignContext designContext) {
        super.writeDesign(design, designContext);
        Attributes attrs = design.attributes();

        SelectionMode mode = getSelectionMode();
        if (mode != null) {
            DesignAttributeHandler.writeAttribute("selection-mode", attrs, mode,
                    SelectionMode.SINGLE, SelectionMode.class, designContext);
        }
        DesignAttributeHandler.writeAttribute("content-mode", attrs,
                getContentMode(), ContentMode.TEXT, ContentMode.class,
                designContext);

        if (designContext.shouldWriteData(this)) {
            writeItems(design, designContext);
        }
    }

    private void writeItems(Element design, DesignContext designContext) {
        getDataProvider().fetch(new HierarchicalQuery<>(null, null))
                .forEach(item -> writeItem(design, designContext, item, null));
    }

    private void writeItem(Element design, DesignContext designContext, T item,
            T parent) {

        Element itemElement = design.appendElement("node");
        itemElement.attr("item", serializeDeclarativeRepresentation(item));

        if (parent != null) {
            itemElement.attr("parent",
                    serializeDeclarativeRepresentation(parent));
        }

        if (getSelectionModel().isSelected(item)) {
            itemElement.attr("selected", true);
        }

        Resource icon = getItemIconGenerator().apply(item);
        DesignAttributeHandler.writeAttribute("icon", itemElement.attributes(),
                icon, null, Resource.class, designContext);

        String text = getItemCaptionGenerator().apply(item);
        itemElement.html(
                Optional.ofNullable(text).map(Object::toString).orElse(""));

        getDataProvider().fetch(new HierarchicalQuery<>(null, item)).forEach(
                childItem -> writeItem(design, designContext, childItem, item));
    }

    @Override
    public void readDesign(Element design, DesignContext designContext) {
        super.readDesign(design, designContext);
        Attributes attrs = design.attributes();
        if (attrs.hasKey("selection-mode")) {
            setSelectionMode(DesignAttributeHandler.readAttribute(
                    "selection-mode", attrs, SelectionMode.class));
        }
        if (attrs.hasKey("content-mode")) {
            setContentMode(DesignAttributeHandler.readAttribute("content-mode",
                    attrs, ContentMode.class));
        }
        readItems(design.children());
    }

    private void readItems(Elements bodyItems) {
        if (bodyItems.isEmpty()) {
            return;
        }

        DeclarativeValueProvider<T> valueProvider = new DeclarativeValueProvider<>();
        setItemCaptionGenerator(item -> valueProvider.apply(item));

        DeclarativeIconGenerator<T> iconGenerator = new DeclarativeIconGenerator<>(
                item -> null);
        setItemIconGenerator(iconGenerator);

        getSelectionModel().deselectAll();
        List<T> selectedItems = new ArrayList<>();
        TreeData<T> data = new TreeData<T>();

        for (Element row : bodyItems) {
            T item = deserializeDeclarativeRepresentation(row.attr("item"));
            T parent = null;
            if (row.hasAttr("parent")) {
                parent = deserializeDeclarativeRepresentation(
                        row.attr("parent"));
            }
            data.addItem(parent, item);
            if (row.hasAttr("selected")) {
                selectedItems.add(item);
            }

            valueProvider.addValue(item, row.html());
            iconGenerator.setIcon(item, DesignAttributeHandler
                    .readAttribute("icon", row.attributes(), Resource.class));
        }

        setDataProvider(new TreeDataProvider<>(data));
        selectedItems.forEach(getSelectionModel()::select);
    }

    /**
     * Deserializes a string to a data item. Used when reading from the
     * declarative format of this Tree.
     * <p>
     * Default implementation is able to handle only {@link String} as an item
     * type. There will be a {@link ClassCastException} if {@code T } is not a
     * {@link String}.
     *
     * @since 8.1.3
     *
     * @see #serializeDeclarativeRepresentation(Object)
     *
     * @param item
     *            string to deserialize
     * @throws ClassCastException
     *             if type {@code T} is not a {@link String}
     * @return deserialized item
     */
    @SuppressWarnings("unchecked")
    protected T deserializeDeclarativeRepresentation(String item) {
        if (item == null) {
            return (T) new String(UUID.randomUUID().toString());
        }
        return (T) new String(item);
    }

    /**
     * Serializes an {@code item} to a string. Used when saving this Tree to its
     * declarative format.
     * <p>
     * Default implementation delegates a call to {@code item.toString()}.
     *
     * @since 8.1.3
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

    private void setupContextClickListener() {
        if (hasListeners(ContextClickEvent.class)) {
            if (contextClickRegistration == null) {
                contextClickRegistration = treeGrid
                        .addContextClickListener(event -> {
                            T item = null;
                            if (event instanceof Grid.GridContextClickEvent) {
                                item = ((Grid.GridContextClickEvent<T>) event)
                                        .getItem();
                            }
                            fireEvent(new TreeContextClickEvent<>(this,
                                    event.getMouseEventDetails(), item));
                        });
            }
        } else if (contextClickRegistration != null) {
            contextClickRegistration.remove();
            contextClickRegistration = null;
        }
    }

    /**
     * ContextClickEvent for the Tree Component.
     * <p>
     * Usage:
     *
     * <pre>
     * tree.addContextClickListener(event -&gt; Notification.show(
     *         ((TreeContextClickEvent&lt;Person&gt;) event).getItem() + " Clicked"));
     * </pre>
     *
     * @param <T>
     *            the tree bean type
     * @since 8.1
     */
    public static class TreeContextClickEvent<T> extends ContextClickEvent {

        private final T item;

        /**
         * Creates a new context click event.
         *
         * @param source
         *            the tree where the context click occurred
         * @param mouseEventDetails
         *            details about mouse position
         * @param item
         *            the item which was clicked or {@code null} if the click
         *            happened outside any item
         */
        public TreeContextClickEvent(Tree<T> source,
                MouseEventDetails mouseEventDetails, T item) {
            super(source, mouseEventDetails);
            this.item = item;
        }

        /**
         * Returns the item of context clicked row.
         *
         * @return clicked item; {@code null} the click happened outside any
         *         item
         */
        public T getItem() {
            return item;
        }

        @Override
        public Tree<T> getComponent() {
            return (Tree<T>) super.getComponent();
        }
    }

    /**
     * Scrolls to a certain item, using {@link ScrollDestination#ANY}.
     * <p>
     * If the item has an open details row, its size will also be taken into
     * account.
     *
     * @param row
     *            zero based index of the item to scroll to in the current view.
     * @throws IllegalArgumentException
     *             if the provided row is outside the item range
     * @since 8.2
     */
    public void scrollTo(int row) throws IllegalArgumentException {
        treeGrid.scrollTo(row, ScrollDestination.ANY);
    }

    /**
     * Scrolls to a certain item, using user-specified scroll destination.
     * <p>
     * If the item has an open details row, its size will also be taken into
     * account.
     *
     * @param row
     *            zero based index of the item to scroll to in the current view.
     * @param destination
     *            value specifying desired position of scrolled-to row, not
     *            {@code null}
     * @throws IllegalArgumentException
     *             if the provided row is outside the item range
     * @since 8.2
     */
    public void scrollTo(int row, ScrollDestination destination) {
        treeGrid.scrollTo(row, destination);
    }

    /**
     * Scrolls to the beginning of the first data row.
     *
     * @since 8.2
     */
    public void scrollToStart() {
        treeGrid.scrollToStart();
    }

    /**
     * Scrolls to the end of the last data row.
     *
     * @since 8.2
     */
    public void scrollToEnd() {
        treeGrid.scrollToEnd();
    }

    @Override
    public int getTabIndex() {
        return treeGrid.getTabIndex();
    }

    @Override
    public void setTabIndex(int tabIndex) {
        treeGrid.setTabIndex(tabIndex);
    }

    @Override
    public void focus() {
        treeGrid.focus();
    }
}
