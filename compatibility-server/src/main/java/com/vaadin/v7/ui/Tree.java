/*
 * Copyright 2000-2018 Vaadin Ltd.
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

package com.vaadin.v7.ui;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.StringTokenizer;

import org.jsoup.nodes.Element;

import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.event.ContextClickEvent;
import com.vaadin.event.Transferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DragSource;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.DropTarget;
import com.vaadin.event.dd.TargetDetails;
import com.vaadin.event.dd.acceptcriteria.ClientSideCriterion;
import com.vaadin.event.dd.acceptcriteria.ServerSideCriterion;
import com.vaadin.event.dd.acceptcriteria.TargetDetailIs;
import com.vaadin.server.KeyMapper;
import com.vaadin.server.PaintException;
import com.vaadin.server.PaintTarget;
import com.vaadin.server.Resource;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.ui.MultiSelectMode;
import com.vaadin.shared.ui.dd.VerticalDropLocation;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.declarative.DesignAttributeHandler;
import com.vaadin.ui.declarative.DesignContext;
import com.vaadin.ui.declarative.DesignException;
import com.vaadin.util.ReflectTools;
import com.vaadin.v7.data.Container;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.ContainerHierarchicalWrapper;
import com.vaadin.v7.data.util.HierarchicalContainer;
import com.vaadin.v7.event.DataBoundTransferable;
import com.vaadin.v7.event.ItemClickEvent;
import com.vaadin.v7.event.ItemClickEvent.ItemClickListener;
import com.vaadin.v7.event.ItemClickEvent.ItemClickNotifier;
import com.vaadin.v7.shared.ui.tree.TreeConstants;
import com.vaadin.v7.shared.ui.tree.TreeServerRpc;
import com.vaadin.v7.shared.ui.tree.TreeState;

/**
 * Tree component. A Tree can be used to select an item (or multiple items) from
 * a hierarchical set of items.
 *
 * @author Vaadin Ltd.
 * @since 3.0
 *
 * @deprecated See {@code com.vaadin.ui.Tree}.
 */
@SuppressWarnings({ "serial", "deprecation" })
@Deprecated
public class Tree extends AbstractSelect implements Container.Hierarchical,
        Action.Container, ItemClickNotifier, DragSource, DropTarget {

    /**
     * ContextClickEvent for the Tree Component.
     *
     * @since 7.6
     */
    @Deprecated
    public static class TreeContextClickEvent extends ContextClickEvent {

        private final Object itemId;

        public TreeContextClickEvent(Tree source, Object itemId,
                MouseEventDetails mouseEventDetails) {
            super(source, mouseEventDetails);
            this.itemId = itemId;
        }

        @Override
        public Tree getComponent() {
            return (Tree) super.getComponent();
        }

        /**
         * Returns the item id of context clicked row.
         *
         * @return item id of clicked row; <code>null</code> if no row is
         *         present at the location
         */
        public Object getItemId() {
            return itemId;
        }
    }

    /* Private members */

    private static final String NULL_ALT_EXCEPTION_MESSAGE = "Parameter 'altText' needs to be non null";

    /**
     * Item icons alt texts.
     */
    private final Map<Object, String> itemIconAlts = new HashMap<Object, String>();

    /**
     * Set of expanded nodes.
     */
    private HashSet<Object> expanded = new HashSet<Object>();

    /**
     * List of action handlers.
     */
    private LinkedList<Action.Handler> actionHandlers = null;

    /**
     * Action mapper.
     */
    private KeyMapper<Action> actionMapper = null;

    /**
     * Is the tree selectable on the client side.
     */
    private boolean selectable = true;

    /**
     * Flag to indicate sub-tree loading
     */
    private boolean partialUpdate = false;

    /**
     * Holds a itemId which was recently expanded
     */
    private Object expandedItemId;

    /**
     * a flag which indicates initial paint. After this flag set true partial
     * updates are allowed.
     */
    private boolean initialPaint = true;

    /**
     * Item tooltip generator
     */
    private ItemDescriptionGenerator itemDescriptionGenerator;

    /**
     * Supported drag modes for Tree.
     */
    @Deprecated
    public enum TreeDragMode {
        /**
         * When drag mode is NONE, dragging from Tree is not supported. Browsers
         * may still support selecting text/icons from Tree which can initiate
         * HTML 5 style drag and drop operation.
         */
        NONE,
        /**
         * When drag mode is NODE, users can initiate drag from Tree nodes that
         * represent {@link Item}s in from the backed {@link Container}.
         */
        NODE
        // , SUBTREE
    }

    private TreeDragMode dragMode = TreeDragMode.NONE;

    private MultiSelectMode multiSelectMode = MultiSelectMode.DEFAULT;

    /* Tree constructors */

    /**
     * Creates a new empty tree.
     */
    public Tree() {
        this(null);

        registerRpc(new TreeServerRpc() {
            @Override
            public void contextClick(String rowKey, MouseEventDetails details) {
                fireEvent(new TreeContextClickEvent(Tree.this,
                        itemIdMapper.get(rowKey), details));
            }
        });
    }

    /**
     * Creates a new empty tree with caption.
     *
     * @param caption
     */
    public Tree(String caption) {
        this(caption, new HierarchicalContainer());
    }

    /**
     * Creates a new tree with caption and connect it to a Container.
     *
     * @param caption
     * @param dataSource
     */
    public Tree(String caption, Container dataSource) {
        super(caption, dataSource);
    }

    @Override
    public void setItemIcon(Object itemId, Resource icon) {
        setItemIcon(itemId, icon, "");
    }

    /**
     * Sets the icon for an item.
     *
     * @param itemId
     *            the id of the item to be assigned an icon.
     * @param icon
     *            the icon to use or null.
     *
     * @param altText
     *            the alternative text for the icon
     */
    public void setItemIcon(Object itemId, Resource icon, String altText) {
        if (itemId != null) {
            super.setItemIcon(itemId, icon);

            if (icon == null) {
                itemIconAlts.remove(itemId);
            } else if (altText == null) {
                throw new IllegalArgumentException(NULL_ALT_EXCEPTION_MESSAGE);
            } else {
                itemIconAlts.put(itemId, altText);
            }
            markAsDirty();
        }
    }

    /**
     * Set the alternate text for an item.
     *
     * Used when the item has an icon.
     *
     * @param itemId
     *            the id of the item to be assigned an icon.
     * @param altText
     *            the alternative text for the icon
     */
    public void setItemIconAlternateText(Object itemId, String altText) {
        if (itemId != null) {
            if (altText == null) {
                throw new IllegalArgumentException(NULL_ALT_EXCEPTION_MESSAGE);
            } else {
                itemIconAlts.put(itemId, altText);
            }
        }
    }

    /**
     * Return the alternate text of an icon in a tree item.
     *
     * @param itemId
     *            Object with the ID of the item
     * @return String with the alternate text of the icon, or null when no icon
     *         was set
     */
    public String getItemIconAlternateText(Object itemId) {
        String storedAlt = itemIconAlts.get(itemId);
        return storedAlt == null ? "" : storedAlt;
    }

    /* Expanding and collapsing */

    /**
     * Check is an item is expanded.
     *
     * @param itemId
     *            the item id.
     * @return true if the item is expanded.
     */
    public boolean isExpanded(Object itemId) {
        return expanded.contains(itemId);
    }

    /**
     * Expands an item.
     *
     * @param itemId
     *            the item id.
     * @return True if the expand operation succeeded
     */
    public boolean expandItem(Object itemId) {
        boolean success = expandItem(itemId, true);
        markAsDirty();
        return success;
    }

    /**
     * Expands an item.
     *
     * @param itemId
     *            the item id.
     * @param sendChildTree
     *            flag to indicate if client needs subtree or not (may be
     *            cached)
     * @return True if the expand operation succeeded
     */
    private boolean expandItem(Object itemId, boolean sendChildTree) {

        // Succeeds if the node is already expanded
        if (isExpanded(itemId)) {
            return true;
        }

        // Nodes that can not have children are not expandable
        if (!areChildrenAllowed(itemId)) {
            return false;
        }

        // Expands
        expanded.add(itemId);

        expandedItemId = itemId;
        if (initialPaint) {
            markAsDirty();
        } else if (sendChildTree) {
            requestPartialRepaint();
        }
        fireExpandEvent(itemId);

        return true;
    }

    @Override
    public void markAsDirty() {
        super.markAsDirty();
        partialUpdate = false;
    }

    private void requestPartialRepaint() {
        super.markAsDirty();
        partialUpdate = true;
    }

    /**
     * Expands the items recursively
     *
     * Expands all the children recursively starting from an item. Operation
     * succeeds only if all expandable items are expanded.
     *
     * @param startItemId
     * @return True if the expand operation succeeded
     */
    public boolean expandItemsRecursively(Object startItemId) {

        boolean result = true;

        // Initial stack
        final Stack<Object> todo = new Stack<Object>();
        todo.add(startItemId);

        // Expands recursively
        while (!todo.isEmpty()) {
            final Object id = todo.pop();
            if (areChildrenAllowed(id) && !expandItem(id, false)) {
                result = false;
            }
            if (hasChildren(id)) {
                todo.addAll(getChildren(id));
            }
        }
        markAsDirty();
        return result;
    }

    /**
     * Collapses an item.
     *
     * @param itemId
     *            the item id.
     * @return True if the collapse operation succeeded
     */
    public boolean collapseItem(Object itemId) {

        // Succeeds if the node is already collapsed
        if (!isExpanded(itemId)) {
            return true;
        }

        // Collapse
        expanded.remove(itemId);
        markAsDirty();
        fireCollapseEvent(itemId);

        return true;
    }

    /**
     * Collapses the items recursively.
     *
     * Collapse all the children recursively starting from an item. Operation
     * succeeds only if all expandable items are collapsed.
     *
     * @param startItemId
     * @return True if the collapse operation succeeded
     */
    public boolean collapseItemsRecursively(Object startItemId) {

        boolean result = true;

        // Initial stack
        final Stack<Object> todo = new Stack<Object>();
        todo.add(startItemId);

        // Collapse recursively
        while (!todo.isEmpty()) {
            final Object id = todo.pop();
            if (areChildrenAllowed(id) && !collapseItem(id)) {
                result = false;
            }
            if (hasChildren(id)) {
                todo.addAll(getChildren(id));
            }
        }

        return result;
    }

    /**
     * Returns the current selectable state. Selectable determines if the a node
     * can be selected on the client side. Selectable does not affect
     * {@link #setValue(Object)} or {@link #select(Object)}.
     *
     * <p>
     * The tree is selectable by default.
     * </p>
     *
     * @return the current selectable state.
     */
    public boolean isSelectable() {
        return selectable;
    }

    /**
     * Sets the selectable state. Selectable determines if the a node can be
     * selected on the client side. Selectable does not affect
     * {@link #setValue(Object)} or {@link #select(Object)}.
     *
     * <p>
     * The tree is selectable by default.
     * </p>
     *
     * @param selectable
     *            The new selectable state.
     */
    public void setSelectable(boolean selectable) {
        if (this.selectable != selectable) {
            this.selectable = selectable;
            markAsDirty();
        }
    }

    /**
     * Sets the behavior of the multiselect mode.
     *
     * @param mode
     *            The mode to set
     */
    public void setMultiselectMode(MultiSelectMode mode) {
        if (multiSelectMode != mode && mode != null) {
            multiSelectMode = mode;
            markAsDirty();
        }
    }

    /**
     * Returns the mode the multiselect is in. The mode controls how
     * multiselection can be done.
     *
     * @return The mode
     */
    public MultiSelectMode getMultiselectMode() {
        return multiSelectMode;
    }

    /* Component API */

    @Override
    public void changeVariables(Object source, Map<String, Object> variables) {

        if (variables.containsKey("clickedKey")) {
            String key = (String) variables.get("clickedKey");

            Object id = itemIdMapper.get(key);
            MouseEventDetails details = MouseEventDetails
                    .deSerialize((String) variables.get("clickEvent"));
            Item item = getItem(id);
            if (item != null) {
                fireEvent(new ItemClickEvent(this, item, id, null, details));
            }
        }

        if (!isSelectable() && variables.containsKey("selected")) {
            // Not-selectable is a special case, AbstractSelect does not support
            // TODO could be optimized.
            variables = new HashMap<String, Object>(variables);
            variables.remove("selected");
        }

        // Collapses the nodes
        if (variables.containsKey("collapse")) {
            for (String key : (String[]) variables.get("collapse")) {
                final Object id = itemIdMapper.get(key);
                if (id != null && isExpanded(id)) {
                    expanded.remove(id);
                    if (expandedItemId == id) {
                        expandedItemId = null;
                    }
                    fireCollapseEvent(id);
                }
            }
        }

        // Expands the nodes
        if (variables.containsKey("expand")) {
            boolean sendChildTree = false;
            if (variables.containsKey("requestChildTree")) {
                sendChildTree = true;
            }
            for (String key : (String[]) variables.get("expand")) {
                final Object id = itemIdMapper.get(key);
                if (id != null) {
                    expandItem(id, sendChildTree);
                }
            }
        }

        // AbstractSelect cannot handle multiselection so we handle
        // it ourself
        if (variables.containsKey("selected") && isMultiSelect()
                && multiSelectMode == MultiSelectMode.DEFAULT) {
            handleSelectedItems(variables);
            variables = new HashMap<String, Object>(variables);
            variables.remove("selected");
        }

        // Selections are handled by the select component
        super.changeVariables(source, variables);

        // Actions
        if (variables.containsKey("action")) {
            final StringTokenizer st = new StringTokenizer(
                    (String) variables.get("action"), ",");
            if (st.countTokens() == 2) {
                final Object itemId = itemIdMapper.get(st.nextToken());
                final Action action = actionMapper.get(st.nextToken());
                if (action != null && (itemId == null || containsId(itemId))
                        && actionHandlers != null) {
                    for (Handler ah : actionHandlers) {
                        ah.handleAction(action, this, itemId);
                    }
                }
            }
        }
    }

    /**
     * Handles the selection
     *
     * @param variables
     *            The variables sent to the server from the client
     */
    private void handleSelectedItems(Map<String, Object> variables) {

        // Converts the key-array to id-set
        final LinkedList<Object> s = new LinkedList<Object>();
        for (String key : (String[]) variables.get("selected")) {
            final Object id = itemIdMapper.get(key);
            if (!isNullSelectionAllowed()
                    && (id == null || id == getNullSelectionItemId())) {
                // skip empty selection if nullselection is not allowed
                markAsDirty();
            } else if (id != null && containsId(id)) {
                s.add(id);
            }
        }

        if (!isNullSelectionAllowed() && s.isEmpty()) {
            // empty selection not allowed, keep old value
            markAsDirty();
            return;
        }

        setValue(s, true);
    }

    /**
     * Paints any needed component-specific things to the given UIDL stream.
     *
     * @see AbstractComponent#paintContent(PaintTarget)
     */
    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        initialPaint = false;

        if (partialUpdate) {
            target.addAttribute("partialUpdate", true);
            target.addAttribute("rootKey", itemIdMapper.key(expandedItemId));
        } else {
            getCaptionChangeListener().clear();

            // The tab ordering number
            if (getTabIndex() > 0) {
                target.addAttribute("tabindex", getTabIndex());
            }

            // Paint tree attributes
            if (isSelectable()) {
                target.addAttribute("selectmode",
                        (isMultiSelect() ? "multi" : "single"));
                if (isMultiSelect()) {
                    target.addAttribute("multiselectmode",
                            multiSelectMode.toString());
                }
            } else {
                target.addAttribute("selectmode", "none");
            }
            if (isNewItemsAllowed()) {
                target.addAttribute("allownewitem", true);
            }

            if (isNullSelectionAllowed()) {
                target.addAttribute("nullselect", true);
            }

            if (dragMode != TreeDragMode.NONE) {
                target.addAttribute("dragMode", dragMode.ordinal());
            }

            if (isHtmlContentAllowed()) {
                target.addAttribute(TreeConstants.ATTRIBUTE_HTML_ALLOWED, true);
            }

        }

        // Initialize variables
        final Set<Action> actionSet = new LinkedHashSet<Action>();

        // rendered selectedKeys
        LinkedList<String> selectedKeys = new LinkedList<String>();

        final LinkedList<String> expandedKeys = new LinkedList<String>();

        // Iterates through hierarchical tree using a stack of iterators
        final Stack<Iterator<?>> iteratorStack = new Stack<Iterator<?>>();
        Collection<?> ids;
        if (partialUpdate) {
            ids = getChildren(expandedItemId);
        } else {
            ids = rootItemIds();
        }

        if (ids != null) {
            iteratorStack.push(ids.iterator());
        }

        /*
         * Body actions - Actions which has the target null and can be invoked
         * by right clicking on the Tree body
         */
        if (actionHandlers != null) {
            final List<String> keys = new ArrayList<String>();
            for (Handler ah : actionHandlers) {

                // Getting action for the null item, which in this case
                // means the body item
                final Action[] aa = ah.getActions(null, this);
                if (aa != null) {
                    for (int ai = 0; ai < aa.length; ai++) {
                        final String akey = actionMapper.key(aa[ai]);
                        actionSet.add(aa[ai]);
                        keys.add(akey);
                    }
                }
            }
            target.addAttribute("alb", keys.toArray());
        }

        while (!iteratorStack.isEmpty()) {

            // Gets the iterator for current tree level
            final Iterator<?> i = iteratorStack.peek();

            // If the level is finished, back to previous tree level
            if (!i.hasNext()) {

                // Removes used iterator from the stack
                iteratorStack.pop();

                // Closes node
                if (!iteratorStack.isEmpty()) {
                    target.endTag("node");
                }
            } else {
                // Adds the item on current level
                final Object itemId = i.next();

                // Starts the item / node
                final boolean isNode = areChildrenAllowed(itemId);
                if (isNode) {
                    target.startTag("node");
                } else {
                    target.startTag("leaf");
                }

                if (itemStyleGenerator != null) {
                    String stylename = itemStyleGenerator.getStyle(this,
                            itemId);
                    if (stylename != null) {
                        target.addAttribute(TreeConstants.ATTRIBUTE_NODE_STYLE,
                                stylename);
                    }
                }

                if (itemDescriptionGenerator != null) {
                    String description = itemDescriptionGenerator
                            .generateDescription(this, itemId, null);
                    if (description != null && !description.equals("")) {
                        target.addAttribute("descr", description);
                    }
                }

                // Adds the attributes
                target.addAttribute(TreeConstants.ATTRIBUTE_NODE_CAPTION,
                        getItemCaption(itemId));
                final Resource icon = getItemIcon(itemId);
                if (icon != null) {
                    target.addAttribute(TreeConstants.ATTRIBUTE_NODE_ICON,
                            getItemIcon(itemId));
                    target.addAttribute(TreeConstants.ATTRIBUTE_NODE_ICON_ALT,
                            getItemIconAlternateText(itemId));
                }
                final String key = itemIdMapper.key(itemId);
                target.addAttribute("key", key);
                if (isSelected(itemId)) {
                    target.addAttribute("selected", true);
                    selectedKeys.add(key);
                }
                if (areChildrenAllowed(itemId) && isExpanded(itemId)) {
                    target.addAttribute("expanded", true);
                    expandedKeys.add(key);
                }

                // Add caption change listener
                getCaptionChangeListener().addNotifierForItem(itemId);

                // Actions
                if (actionHandlers != null) {
                    final List<String> keys = new ArrayList<String>();
                    for (Action.Handler ah : actionHandlers) {
                        final Action[] aa = ah.getActions(itemId, this);
                        if (aa != null) {
                            for (int ai = 0; ai < aa.length; ai++) {
                                final String akey = actionMapper.key(aa[ai]);
                                actionSet.add(aa[ai]);
                                keys.add(akey);
                            }
                        }
                    }
                    target.addAttribute("al", keys.toArray());
                }

                // Adds the children if expanded, or close the tag
                if (isExpanded(itemId) && hasChildren(itemId)
                        && areChildrenAllowed(itemId)) {
                    iteratorStack.push(getChildren(itemId).iterator());
                } else {
                    if (isNode) {
                        target.endTag("node");
                    } else {
                        target.endTag("leaf");
                    }
                }
            }
        }

        // Actions
        if (!actionSet.isEmpty()) {
            target.addVariable(this, "action", "");
            target.startTag("actions");
            final Iterator<Action> i = actionSet.iterator();
            while (i.hasNext()) {
                final Action a = i.next();
                target.startTag("action");
                if (a.getCaption() != null) {
                    target.addAttribute(TreeConstants.ATTRIBUTE_ACTION_CAPTION,
                            a.getCaption());
                }
                if (a.getIcon() != null) {
                    target.addAttribute(TreeConstants.ATTRIBUTE_ACTION_ICON,
                            a.getIcon());
                }
                target.addAttribute("key", actionMapper.key(a));
                target.endTag("action");
            }
            target.endTag("actions");
        }

        if (partialUpdate) {
            partialUpdate = false;
        } else {
            // Selected
            target.addVariable(this, "selected",
                    selectedKeys.toArray(new String[selectedKeys.size()]));

            // Expand and collapse
            target.addVariable(this, "expand", new String[] {});
            target.addVariable(this, "collapse", new String[] {});

            // New items
            target.addVariable(this, "newitem", new String[] {});

            if (dropHandler != null) {
                dropHandler.getAcceptCriterion().paint(target);
            }

        }
    }

    /* Container.Hierarchical API */

    /**
     * Tests if the Item with given ID can have any children.
     *
     * @see Container.Hierarchical#areChildrenAllowed(Object)
     */
    @Override
    public boolean areChildrenAllowed(Object itemId) {
        return ((Container.Hierarchical) items).areChildrenAllowed(itemId);
    }

    /**
     * Gets the IDs of all Items that are children of the specified Item.
     *
     * @see Container.Hierarchical#getChildren(Object)
     */
    @Override
    public Collection<?> getChildren(Object itemId) {
        return ((Container.Hierarchical) items).getChildren(itemId);
    }

    /**
     * Gets the ID of the parent Item of the specified Item.
     *
     * @see Container.Hierarchical#getParent(Object)
     */
    @Override
    public Object getParent(Object itemId) {
        return ((Container.Hierarchical) items).getParent(itemId);
    }

    /**
     * Tests if the Item specified with <code>itemId</code> has child Items.
     *
     * @see Container.Hierarchical#hasChildren(Object)
     */
    @Override
    public boolean hasChildren(Object itemId) {
        return ((Container.Hierarchical) items).hasChildren(itemId);
    }

    /**
     * Tests if the Item specified with <code>itemId</code> is a root Item.
     *
     * @see Container.Hierarchical#isRoot(Object)
     */
    @Override
    public boolean isRoot(Object itemId) {
        return ((Container.Hierarchical) items).isRoot(itemId);
    }

    /**
     * Gets the IDs of all Items in the container that don't have a parent.
     *
     * @see Container.Hierarchical#rootItemIds()
     */
    @Override
    public Collection<?> rootItemIds() {
        return ((Container.Hierarchical) items).rootItemIds();
    }

    /**
     * Sets the given Item's capability to have children.
     *
     * @see Container.Hierarchical#setChildrenAllowed(Object, boolean)
     */
    @Override
    public boolean setChildrenAllowed(Object itemId,
            boolean areChildrenAllowed) {
        final boolean success = ((Container.Hierarchical) items)
                .setChildrenAllowed(itemId, areChildrenAllowed);
        if (success) {
            markAsDirty();
        }
        return success;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.data.Container.Hierarchical#setParent(java.lang.Object ,
     * java.lang.Object)
     */
    @Override
    public boolean setParent(Object itemId, Object newParentId) {
        final boolean success = ((Container.Hierarchical) items)
                .setParent(itemId, newParentId);
        if (success) {
            markAsDirty();
        }
        return success;
    }

    /* Overriding select behavior */

    /**
     * Sets the Container that serves as the data source of the viewer.
     *
     * @see Container.Viewer#setContainerDataSource(Container)
     */
    @Override
    public void setContainerDataSource(Container newDataSource) {
        if (newDataSource == null) {
            newDataSource = new HierarchicalContainer();
        }

        // Assure that the data source is ordered by making unordered
        // containers ordered by wrapping them
        if (Container.Hierarchical.class
                .isAssignableFrom(newDataSource.getClass())) {
            super.setContainerDataSource(newDataSource);
        } else {
            super.setContainerDataSource(
                    new ContainerHierarchicalWrapper(newDataSource));
        }

        /*
         * Ensure previous expanded items are cleaned up if they don't exist in
         * the new container
         */
        if (expanded != null) {
            /*
             * We need to check that the expanded-field is not null since
             * setContainerDataSource() is called from the parent constructor
             * (AbstractSelect()) and at that time the expanded field is not yet
             * initialized.
             */
            cleanupExpandedItems();
        }

    }

    @Override
    public void containerItemSetChange(Container.ItemSetChangeEvent event) {
        super.containerItemSetChange(event);
        if (getContainerDataSource() instanceof Filterable) {
            boolean hasFilters = !((Filterable) getContainerDataSource())
                    .getContainerFilters().isEmpty();
            if (!hasFilters) {
                /*
                 * If Container is not filtered then the itemsetchange is caused
                 * by either adding or removing items to the container. To
                 * prevent a memory leak we should cleanup the expanded list
                 * from items which was removed.
                 *
                 * However, there will still be a leak if the container is
                 * filtered to show only a subset of the items in the tree and
                 * later unfiltered items are removed from the container. In
                 * that case references to the unfiltered item ids will remain
                 * in the expanded list until the Tree instance is removed and
                 * the list is destroyed, or the container data source is
                 * replaced/updated. To force the removal of the removed items
                 * the application developer needs to a) remove the container
                 * filters temporarly or b) re-apply the container datasource
                 * using setContainerDataSource(getContainerDataSource())
                 */
                cleanupExpandedItems();
            }
        }

    }

    /* Expand event and listener */

    /**
     * Event to fired when a node is expanded. ExapandEvent is fired when a node
     * is to be expanded. it can me used to dynamically fill the sub-nodes of
     * the node.
     *
     * @author Vaadin Ltd.
     * @since 3.0
     */
    @Deprecated
    public static class ExpandEvent extends Component.Event {

        private final Object expandedItemId;

        /**
         * New instance of options change event.
         *
         * @param source
         *            the Source of the event.
         * @param expandedItemId
         */
        public ExpandEvent(Component source, Object expandedItemId) {
            super(source);
            this.expandedItemId = expandedItemId;
        }

        /**
         * Node where the event occurred.
         *
         * @return the Source of the event.
         */
        public Object getItemId() {
            return expandedItemId;
        }
    }

    /**
     * Expand event listener.
     *
     * @author Vaadin Ltd.
     * @since 3.0
     */
    @Deprecated
    public interface ExpandListener extends Serializable {

        public static final Method EXPAND_METHOD = ReflectTools.findMethod(
                ExpandListener.class, "nodeExpand", ExpandEvent.class);

        /**
         * A node has been expanded.
         *
         * @param event
         *            the Expand event.
         */
        public void nodeExpand(ExpandEvent event);
    }

    /**
     * Adds the expand listener.
     *
     * @param listener
     *            the Listener to be added.
     */
    public void addExpandListener(ExpandListener listener) {
        addListener(ExpandEvent.class, listener, ExpandListener.EXPAND_METHOD);
    }

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #addExpandListener(ExpandListener)}
     */
    @Deprecated
    public void addListener(ExpandListener listener) {
        addExpandListener(listener);
    }

    /**
     * Removes the expand listener.
     *
     * @param listener
     *            the Listener to be removed.
     */
    public void removeExpandListener(ExpandListener listener) {
        removeListener(ExpandEvent.class, listener,
                ExpandListener.EXPAND_METHOD);
    }

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #removeExpandListener(ExpandListener)}
     */
    @Deprecated
    public void removeListener(ExpandListener listener) {
        removeExpandListener(listener);
    }

    /**
     * Emits the expand event.
     *
     * @param itemId
     *            the item id.
     */
    protected void fireExpandEvent(Object itemId) {
        fireEvent(new ExpandEvent(this, itemId));
    }

    /* Collapse event */

    /**
     * Collapse event.
     *
     * @author Vaadin Ltd.
     * @since 3.0
     */
    @Deprecated
    public static class CollapseEvent extends Component.Event {

        private final Object collapsedItemId;

        /**
         * New instance of options change event.
         *
         * @param source
         *            the Source of the event.
         * @param collapsedItemId
         */
        public CollapseEvent(Component source, Object collapsedItemId) {
            super(source);
            this.collapsedItemId = collapsedItemId;
        }

        /**
         * Gets tge Collapsed Item id.
         *
         * @return the collapsed item id.
         */
        public Object getItemId() {
            return collapsedItemId;
        }
    }

    /**
     * Collapse event listener.
     *
     * @author Vaadin Ltd.
     * @since 3.0
     */
    @Deprecated
    public interface CollapseListener extends Serializable {

        public static final Method COLLAPSE_METHOD = ReflectTools.findMethod(
                CollapseListener.class, "nodeCollapse", CollapseEvent.class);

        /**
         * A node has been collapsed.
         *
         * @param event
         *            the Collapse event.
         */
        public void nodeCollapse(CollapseEvent event);
    }

    /**
     * Adds the collapse listener.
     *
     * @param listener
     *            the Listener to be added.
     */
    public void addCollapseListener(CollapseListener listener) {
        addListener(CollapseEvent.class, listener,
                CollapseListener.COLLAPSE_METHOD);
    }

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #addCollapseListener(CollapseListener)}
     */
    @Deprecated
    public void addListener(CollapseListener listener) {
        addCollapseListener(listener);
    }

    /**
     * Removes the collapse listener.
     *
     * @param listener
     *            the Listener to be removed.
     */
    public void removeCollapseListener(CollapseListener listener) {
        removeListener(CollapseEvent.class, listener,
                CollapseListener.COLLAPSE_METHOD);
    }

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #removeCollapseListener(CollapseListener)}
     */
    @Deprecated
    public void removeListener(CollapseListener listener) {
        removeCollapseListener(listener);
    }

    /**
     * Emits collapse event.
     *
     * @param itemId
     *            the item id.
     */
    protected void fireCollapseEvent(Object itemId) {
        fireEvent(new CollapseEvent(this, itemId));
    }

    /* Action container */

    /**
     * Adds an action handler.
     *
     * @see com.vaadin.event.Action.Container#addActionHandler(Action.Handler)
     */
    @Override
    public void addActionHandler(Action.Handler actionHandler) {

        if (actionHandler != null) {

            if (actionHandlers == null) {
                actionHandlers = new LinkedList<Action.Handler>();
                actionMapper = new KeyMapper<Action>();
            }

            if (!actionHandlers.contains(actionHandler)) {
                actionHandlers.add(actionHandler);
                markAsDirty();
            }
        }
    }

    /**
     * Removes an action handler.
     *
     * @see com.vaadin.event.Action.Container#removeActionHandler(Action.Handler)
     */
    @Override
    public void removeActionHandler(Action.Handler actionHandler) {

        if (actionHandlers != null && actionHandlers.contains(actionHandler)) {

            actionHandlers.remove(actionHandler);

            if (actionHandlers.isEmpty()) {
                actionHandlers = null;
                actionMapper = null;
            }

            markAsDirty();
        }
    }

    /**
     * Removes all action handlers.
     */
    public void removeAllActionHandlers() {
        actionHandlers = null;
        actionMapper = null;
        markAsDirty();
    }

    /**
     * Gets the visible item ids.
     *
     * @see Select#getVisibleItemIds()
     */
    @Override
    public Collection<?> getVisibleItemIds() {

        final LinkedList<Object> visible = new LinkedList<Object>();

        // Iterates trough hierarchical tree using a stack of iterators
        final Stack<Iterator<?>> iteratorStack = new Stack<Iterator<?>>();
        final Collection<?> ids = rootItemIds();
        if (ids != null) {
            iteratorStack.push(ids.iterator());
        }
        while (!iteratorStack.isEmpty()) {

            // Gets the iterator for current tree level
            final Iterator<?> i = iteratorStack.peek();

            // If the level is finished, back to previous tree level
            if (!i.hasNext()) {

                // Removes used iterator from the stack
                iteratorStack.pop();
            } else {
                // Adds the item on current level
                final Object itemId = i.next();

                visible.add(itemId);

                // Adds children if expanded, or close the tag
                if (isExpanded(itemId) && hasChildren(itemId)) {
                    iteratorStack.push(getChildren(itemId).iterator());
                }
            }
        }

        return visible;
    }

    /**
     * Tree does not support <code>setNullSelectionItemId</code>.
     *
     * @see AbstractSelect#setNullSelectionItemId(java.lang.Object)
     */
    @Override
    public void setNullSelectionItemId(Object nullSelectionItemId)
            throws UnsupportedOperationException {
        if (nullSelectionItemId != null) {
            throw new UnsupportedOperationException();
        }

    }

    /**
     * Adding new items is not supported.
     *
     * @throws UnsupportedOperationException
     *             if set to true.
     * @see Select#setNewItemsAllowed(boolean)
     */
    @Override
    public void setNewItemsAllowed(boolean allowNewOptions)
            throws UnsupportedOperationException {
        if (allowNewOptions) {
            throw new UnsupportedOperationException();
        }
    }

    private ItemStyleGenerator itemStyleGenerator;

    private DropHandler dropHandler;

    private boolean htmlContentAllowed;

    @Override
    public void addItemClickListener(ItemClickListener listener) {
        addListener(TreeConstants.ITEM_CLICK_EVENT_ID, ItemClickEvent.class,
                listener, ItemClickEvent.ITEM_CLICK_METHOD);
    }

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #addItemClickListener(ItemClickListener)}
     */
    @Override
    @Deprecated
    public void addListener(ItemClickListener listener) {
        addItemClickListener(listener);
    }

    @Override
    public void removeItemClickListener(ItemClickListener listener) {
        removeListener(TreeConstants.ITEM_CLICK_EVENT_ID, ItemClickEvent.class,
                listener);
    }

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #removeItemClickListener(ItemClickListener)}
     */
    @Override
    @Deprecated
    public void removeListener(ItemClickListener listener) {
        removeItemClickListener(listener);
    }

    /**
     * Sets the {@link ItemStyleGenerator} to be used with this tree.
     *
     * @param itemStyleGenerator
     *            item style generator or null to remove generator
     */
    public void setItemStyleGenerator(ItemStyleGenerator itemStyleGenerator) {
        if (this.itemStyleGenerator != itemStyleGenerator) {
            this.itemStyleGenerator = itemStyleGenerator;
            markAsDirty();
        }
    }

    /**
     * @return the current {@link ItemStyleGenerator} for this tree. Null if
     *         {@link ItemStyleGenerator} is not set.
     */
    public ItemStyleGenerator getItemStyleGenerator() {
        return itemStyleGenerator;
    }

    /**
     * ItemStyleGenerator can be used to add custom styles to tree items. The
     * CSS class name that will be added to the item content is
     * <tt>v-tree-node-[style name]</tt>.
     */
    @Deprecated
    public interface ItemStyleGenerator extends Serializable {

        /**
         * Called by Tree when an item is painted.
         *
         * @param source
         *            the source Tree
         * @param itemId
         *            The itemId of the item to be painted
         * @return The style name to add to this item. (the CSS class name will
         *         be v-tree-node-[style name]
         */
        public abstract String getStyle(Tree source, Object itemId);
    }

    // Overridden so javadoc comes from Container.Hierarchical
    @Override
    public boolean removeItem(Object itemId)
            throws UnsupportedOperationException {
        return super.removeItem(itemId);
    }

    @Override
    public DropHandler getDropHandler() {
        return dropHandler;
    }

    public void setDropHandler(DropHandler dropHandler) {
        this.dropHandler = dropHandler;
    }

    /**
     * A {@link TargetDetails} implementation with Tree specific api.
     *
     * @since 6.3
     */
    @Deprecated
    public class TreeTargetDetails extends AbstractSelectTargetDetails {

        TreeTargetDetails(Map<String, Object> rawVariables) {
            super(rawVariables);
        }

        @Override
        public Tree getTarget() {
            return (Tree) super.getTarget();
        }

        /**
         * If the event is on a node that can not have children (see
         * {@link Tree#areChildrenAllowed(Object)}), this method returns the
         * parent item id of the target item (see {@link #getItemIdOver()} ).
         * The identifier of the parent node is also returned if the cursor is
         * on the top part of node. Else this method returns the same as
         * {@link #getItemIdOver()}.
         * <p>
         * In other words this method returns the identifier of the "folder"
         * into the drag operation is targeted.
         * <p>
         * If the method returns null, the current target is on a root node or
         * on other undefined area over the tree component.
         * <p>
         * The default Tree implementation marks the targetted tree node with
         * CSS classnames v-tree-node-dragfolder and
         * v-tree-node-caption-dragfolder (for the caption element).
         */
        public Object getItemIdInto() {

            Object itemIdOver = getItemIdOver();
            if (areChildrenAllowed(itemIdOver)
                    && getDropLocation() == VerticalDropLocation.MIDDLE) {
                return itemIdOver;
            }
            return getParent(itemIdOver);
        }

        /**
         * If drop is targeted into "folder node" (see {@link #getItemIdInto()}
         * ), this method returns the item id of the node after the drag was
         * targeted. This method is useful when implementing drop into specific
         * location (between specific nodes) in tree.
         *
         * @return the id of the item after the user targets the drop or null if
         *         "target" is a first item in node list (or the first in root
         *         node list)
         */
        public Object getItemIdAfter() {
            Object itemIdOver = getItemIdOver();
            Object itemIdInto2 = getItemIdInto();
            if (itemIdOver.equals(itemIdInto2)) {
                return null;
            }
            VerticalDropLocation dropLocation = getDropLocation();
            if (VerticalDropLocation.TOP == dropLocation) {
                // if on top of the caption area, add before
                Collection<?> children;
                Object itemIdInto = getItemIdInto();
                if (itemIdInto != null) {
                    // seek the previous from child list
                    children = getChildren(itemIdInto);
                } else {
                    children = rootItemIds();
                }
                Object ref = null;
                for (Object object : children) {
                    if (object.equals(itemIdOver)) {
                        return ref;
                    }
                    ref = object;
                }
            }
            return itemIdOver;
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.vaadin.event.dd.DropTarget#translateDropTargetDetails(java.util.Map)
     */
    @Override
    public TreeTargetDetails translateDropTargetDetails(
            Map<String, Object> clientVariables) {
        return new TreeTargetDetails(clientVariables);
    }

    /**
     * Helper API for {@link TreeDropCriterion}
     *
     * @param itemId
     * @return
     */
    private String key(Object itemId) {
        return itemIdMapper.key(itemId);
    }

    /**
     * Sets the drag mode that controls how Tree behaves as a {@link DragSource}
     * .
     *
     * @param dragMode
     */
    public void setDragMode(TreeDragMode dragMode) {
        this.dragMode = dragMode;
        markAsDirty();
    }

    /**
     * @return the drag mode that controls how Tree behaves as a
     *         {@link DragSource}.
     *
     * @see TreeDragMode
     */
    public TreeDragMode getDragMode() {
        return dragMode;
    }

    /**
     * Concrete implementation of {@link DataBoundTransferable} for data
     * transferred from a tree.
     *
     * @see DataBoundTransferable
     *
     * @since 6.3
     */
    @Deprecated
    protected class TreeTransferable extends DataBoundTransferable {

        public TreeTransferable(Component sourceComponent,
                Map<String, Object> rawVariables) {
            super(sourceComponent, rawVariables);
        }

        @Override
        public Object getItemId() {
            return getData("itemId");
        }

        @Override
        public Object getPropertyId() {
            return getItemCaptionPropertyId();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.event.dd.DragSource#getTransferable(java.util.Map)
     */
    @Override
    public Transferable getTransferable(Map<String, Object> payload) {
        TreeTransferable transferable = new TreeTransferable(this, payload);
        // updating drag source variables
        Object object = payload.get("itemId");
        if (object != null) {
            transferable.setData("itemId", itemIdMapper.get((String) object));
        }

        return transferable;
    }

    /**
     * Lazy loading accept criterion for Tree. Accepted target nodes are loaded
     * from server once per drag and drop operation. Developer must override one
     * method that decides accepted tree nodes for the whole Tree.
     *
     * <p>
     * Initially pretty much no data is sent to client. On first required
     * criterion check (per drag request) the client side data structure is
     * initialized from server and no subsequent requests requests are needed
     * during that drag and drop operation.
     */
    @Deprecated
    public abstract static class TreeDropCriterion extends ServerSideCriterion {

        private Tree tree;

        private Set<Object> allowedItemIds;

        /*
         * (non-Javadoc)
         *
         * @see
         * com.vaadin.event.dd.acceptCriteria.ServerSideCriterion#getIdentifier
         * ()
         */
        @Override
        protected String getIdentifier() {
            return TreeDropCriterion.class.getCanonicalName();
        }

        /*
         * (non-Javadoc)
         *
         * @see
         * com.vaadin.event.dd.acceptCriteria.AcceptCriterion#accepts(com.vaadin
         * .event.dd.DragAndDropEvent)
         */
        @Override
        public boolean accept(DragAndDropEvent dragEvent) {
            AbstractSelectTargetDetails dropTargetData = (AbstractSelectTargetDetails) dragEvent
                    .getTargetDetails();
            tree = (Tree) dragEvent.getTargetDetails().getTarget();
            allowedItemIds = getAllowedItemIds(dragEvent, tree);

            return allowedItemIds.contains(dropTargetData.getItemIdOver());
        }

        /*
         * (non-Javadoc)
         *
         * @see
         * com.vaadin.event.dd.acceptCriteria.AcceptCriterion#paintResponse(
         * com.vaadin.server.PaintTarget)
         */
        @Override
        public void paintResponse(PaintTarget target) throws PaintException {
            /*
             * send allowed nodes to client so subsequent requests can be
             * avoided
             */
            Object[] array = allowedItemIds.toArray();
            for (int i = 0; i < array.length; i++) {
                String key = tree.key(array[i]);
                array[i] = key;
            }
            target.addAttribute("allowedIds", array);
        }

        protected abstract Set<Object> getAllowedItemIds(
                DragAndDropEvent dragEvent, Tree tree);

    }

    /**
     * A criterion that accepts {@link Transferable} only directly on a tree
     * node that can have children.
     * <p>
     * Class is singleton, use {@link TargetItemAllowsChildren#get()} to get the
     * instance.
     *
     * @see Tree#setChildrenAllowed(Object, boolean)
     *
     * @since 6.3
     */
    @Deprecated
    public static class TargetItemAllowsChildren extends TargetDetailIs {

        private static TargetItemAllowsChildren instance = new TargetItemAllowsChildren();

        public static TargetItemAllowsChildren get() {
            return instance;
        }

        private TargetItemAllowsChildren() {
            super("itemIdOverIsNode", Boolean.TRUE);
        }

        /*
         * Uses enhanced server side check
         */
        @Override
        public boolean accept(DragAndDropEvent dragEvent) {
            try {
                // must be over tree node and in the middle of it (not top or
                // bottom
                // part)
                TreeTargetDetails eventDetails = (TreeTargetDetails) dragEvent
                        .getTargetDetails();

                Object itemIdOver = eventDetails.getItemIdOver();
                if (!eventDetails.getTarget().areChildrenAllowed(itemIdOver)) {
                    return false;
                }
                // return true if directly over
                return eventDetails
                        .getDropLocation() == VerticalDropLocation.MIDDLE;
            } catch (Exception e) {
                return false;
            }
        }

    }

    /**
     * An accept criterion that checks the parent node (or parent hierarchy) for
     * the item identifier given in constructor. If the parent is found, content
     * is accepted. Criterion can be used to accepts drags on a specific sub
     * tree only.
     * <p>
     * The root items is also consider to be valid target.
     */
    @Deprecated
    public class TargetInSubtree extends ClientSideCriterion {

        private Object rootId;
        private int depthToCheck = -1;

        /**
         * Constructs a criteria that accepts the drag if the targeted Item is a
         * descendant of Item identified by given id.
         *
         * @param parentItemId
         *            the item identifier of the parent node
         */
        public TargetInSubtree(Object parentItemId) {
            rootId = parentItemId;
        }

        /**
         * Constructs a criteria that accepts drops within given level below the
         * subtree root identified by given id.
         *
         * @param rootId
         *            the item identifier to be sought for
         * @param depthToCheck
         *            the depth that tree is traversed upwards to seek for the
         *            parent, -1 means that the whole structure should be
         *            checked
         */
        public TargetInSubtree(Object rootId, int depthToCheck) {
            this.rootId = rootId;
            this.depthToCheck = depthToCheck;
        }

        @Override
        public boolean accept(DragAndDropEvent dragEvent) {
            try {
                TreeTargetDetails eventDetails = (TreeTargetDetails) dragEvent
                        .getTargetDetails();

                if (eventDetails.getItemIdOver() != null) {
                    Object itemId = eventDetails.getItemIdOver();
                    int i = 0;
                    while (itemId != null
                            && (depthToCheck == -1 || i <= depthToCheck)) {
                        if (itemId.equals(rootId)) {
                            return true;
                        }
                        itemId = getParent(itemId);
                        i++;
                    }
                }
                return false;
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        public void paintContent(PaintTarget target) throws PaintException {
            super.paintContent(target);
            target.addAttribute("depth", depthToCheck);
            target.addAttribute("key", key(rootId));
        }
    }

    /**
     * Set the item description generator which generates tooltips for the tree
     * items.
     *
     * @param generator
     *            The generator to use or null to disable
     */
    public void setItemDescriptionGenerator(
            ItemDescriptionGenerator generator) {
        if (generator != itemDescriptionGenerator) {
            itemDescriptionGenerator = generator;
            markAsDirty();
        }
    }

    /**
     * Get the item description generator which generates tooltips for tree
     * items.
     */
    public ItemDescriptionGenerator getItemDescriptionGenerator() {
        return itemDescriptionGenerator;
    }

    private void cleanupExpandedItems() {
        Set<Object> removedItemIds = new HashSet<Object>();
        for (Object expandedItemId : expanded) {
            if (getItem(expandedItemId) == null) {
                removedItemIds.add(expandedItemId);
                if (this.expandedItemId == expandedItemId) {
                    this.expandedItemId = null;
                }
            }
        }
        expanded.removeAll(removedItemIds);
    }

    /**
     * Reads an Item from a design and inserts it into the data source.
     * Recursively handles any children of the item as well.
     *
     * @since 7.5.0
     * @param node
     *            an element representing the item (tree node).
     * @param selected
     *            A set accumulating selected items. If the item that is read is
     *            marked as selected, its item id should be added to this set.
     * @param context
     *            the DesignContext instance used in parsing
     * @return the item id of the new item
     *
     * @throws DesignException
     *             if the tag name of the {@code node} element is not
     *             {@code node}.
     */
    @Override
    protected String readItem(Element node, Set<String> selected,
            DesignContext context) {

        if (!"node".equals(node.tagName())) {
            throw new DesignException("Unrecognized child element in "
                    + getClass().getSimpleName() + ": " + node.tagName());
        }

        String itemId = node.attr("text");
        addItem(itemId);
        if (node.hasAttr("icon")) {
            Resource icon = DesignAttributeHandler.readAttribute("icon",
                    node.attributes(), Resource.class);
            setItemIcon(itemId, icon);
        }
        if (node.hasAttr("selected")) {
            selected.add(itemId);
        }

        for (Element child : node.children()) {
            String childItemId = readItem(child, selected, context);
            setParent(childItemId, itemId);
        }
        return itemId;
    }

    /**
     * Recursively writes the root items and their children to a design.
     *
     * @since 7.5.0
     * @param design
     *            the element into which to insert the items
     * @param context
     *            the DesignContext instance used in writing
     */
    @Override
    protected void writeItems(Element design, DesignContext context) {
        for (Object itemId : rootItemIds()) {
            writeItem(design, itemId, context);
        }
    }

    /**
     * Recursively writes a data source Item and its children to a design.
     *
     * @since 7.5.0
     * @param design
     *            the element into which to insert the item
     * @param itemId
     *            the id of the item to write
     * @param context
     *            the DesignContext instance used in writing
     * @return
     */
    @Override
    protected Element writeItem(Element design, Object itemId,
            DesignContext context) {
        Element element = design.appendElement("node");

        element.attr("text", itemId.toString());

        Resource icon = getItemIcon(itemId);
        if (icon != null) {
            DesignAttributeHandler.writeAttribute("icon", element.attributes(),
                    icon, null, Resource.class, context);
        }

        if (isSelected(itemId)) {
            element.attr("selected", "");
        }

        Collection<?> children = getChildren(itemId);
        if (children != null) {
            // Yeah... see #5864
            for (Object childItemId : children) {
                writeItem(element, childItemId, context);
            }
        }

        return element;
    }

    /**
     * Sets whether html is allowed in the item captions. If set to
     * <code>true</code>, the captions are passed to the browser as html and the
     * developer is responsible for ensuring no harmful html is used. If set to
     * <code>false</code>, the content is passed to the browser as plain text.
     * The default setting is <code>false</code>
     *
     * @since 7.6
     * @param htmlContentAllowed
     *            <code>true</code> if the captions are used as html,
     *            <code>false</code> if used as plain text
     */
    public void setHtmlContentAllowed(boolean htmlContentAllowed) {
        this.htmlContentAllowed = htmlContentAllowed;
        markAsDirty();
    }

    /**
     * Checks whether captions are interpreted as html or plain text.
     *
     * @since 7.6
     * @return <code>true</code> if the captions are displayed as html,
     *         <code>false</code> if displayed as plain text
     * @see #setHtmlContentAllowed(boolean)
     */
    public boolean isHtmlContentAllowed() {
        return htmlContentAllowed;
    }

    @Override
    protected TreeState getState() {
        return (TreeState) super.getState();
    }
}
