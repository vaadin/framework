/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.ui;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.StringTokenizer;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.ContainerHierarchicalWrapper;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.Action;
import com.vaadin.event.DataBoundTransferable;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ItemClickEvent.ItemClickSource;
import com.vaadin.event.Transferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DragSource;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.DropTarget;
import com.vaadin.event.dd.TargetDetails;
import com.vaadin.event.dd.acceptcriteria.ClientCriterion;
import com.vaadin.event.dd.acceptcriteria.ClientSideCriterion;
import com.vaadin.event.dd.acceptcriteria.ServerSideCriterion;
import com.vaadin.event.dd.acceptcriteria.TargetDetailIs;
import com.vaadin.terminal.KeyMapper;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.terminal.Resource;
import com.vaadin.terminal.gwt.client.MouseEventDetails;
import com.vaadin.terminal.gwt.client.ui.VTree;
import com.vaadin.terminal.gwt.client.ui.dd.VLazyInitItemIdentifiers;
import com.vaadin.terminal.gwt.client.ui.dd.VTargetInSubtree;
import com.vaadin.terminal.gwt.client.ui.dd.VerticalDropLocation;
import com.vaadin.tools.ReflectTools;

/**
 * Tree component. A Tree can be used to select an item (or multiple items) from
 * a hierarchical set of items.
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.0
 */
@SuppressWarnings("serial")
@ClientWidget(VTree.class)
public class Tree extends AbstractSelect implements Container.Hierarchical,
        Action.Container, ItemClickSource, DragSource, DropTarget {

    /* Private members */

    /**
     * Set of expanded nodes.
     */
    private final HashSet<Object> expanded = new HashSet<Object>();

    /**
     * List of action handlers.
     */
    private LinkedList<Action.Handler> actionHandlers = null;

    /**
     * Action mapper.
     */
    private KeyMapper actionMapper = null;

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
     * Supported drag modes for Tree.
     */
    public enum TreeDragMode {
        /**
         * When drag mode is NONE, draggin from Tree is not supported. Browsers
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
    }

    /**
     * Creates a new empty tree with caption.
     * 
     * @param caption
     */
    public Tree(String caption) {
        setCaption(caption);
    }

    /**
     * Creates a new tree with caption and connect it to a Container.
     * 
     * @param caption
     * @param dataSource
     */
    public Tree(String caption, Container dataSource) {
        setCaption(caption);
        setContainerDataSource(dataSource);
    }

    /* Expanding and collapsing */

    /**
     * Check is an item is expanded
     * 
     * @param itemId
     *            the item id.
     * @return true iff the item is expanded.
     */
    public boolean isExpanded(Object itemId) {
        return expanded.contains(itemId);
    }

    /**
     * Expands an item.
     * 
     * @param itemId
     *            the item id.
     * @return True iff the expand operation succeeded
     */
    public boolean expandItem(Object itemId) {
        boolean success = expandItem(itemId, true);
        requestRepaint();
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
     * @return True iff the expand operation succeeded
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
            requestRepaint();
        } else if (sendChildTree) {
            requestPartialRepaint();
        }
        fireExpandEvent(itemId);

        return true;
    }

    @Override
    public void requestRepaint() {
        super.requestRepaint();
        partialUpdate = false;
    }

    private void requestPartialRepaint() {
        super.requestRepaint();
        partialUpdate = true;
    }

    /**
     * Expands the items recursively
     * 
     * Expands all the children recursively starting from an item. Operation
     * succeeds only if all expandable items are expanded.
     * 
     * @param startItemId
     * @return True iff the expand operation succeeded
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
        requestRepaint();
        return result;
    }

    /**
     * Collapses an item.
     * 
     * @param itemId
     *            the item id.
     * @return True iff the collapse operation succeeded
     */
    public boolean collapseItem(Object itemId) {

        // Succeeds if the node is already collapsed
        if (!isExpanded(itemId)) {
            return true;
        }

        // Collapse
        expanded.remove(itemId);
        requestRepaint();
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
     * @return True iff the collapse operation succeeded
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
            requestRepaint();
        }
    }

    /**
     * Sets the behavior of the multiselect mode
     * 
     * @param mode
     *            The mode to set
     */
    public void setMultiselectMode(MultiSelectMode mode) {
        if (multiSelectMode != mode && mode != null) {
            multiSelectMode = mode;
            requestRepaint();
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

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.AbstractSelect#changeVariables(java.lang.Object,
     * java.util.Map)
     */
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
            final String[] keys = (String[]) variables.get("collapse");
            for (int i = 0; i < keys.length; i++) {
                final Object id = itemIdMapper.get(keys[i]);
                if (id != null && isExpanded(id)) {
                    expanded.remove(id);
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
            final String[] keys = (String[]) variables.get("expand");
            for (int i = 0; i < keys.length; i++) {
                final Object id = itemIdMapper.get(keys[i]);
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
                final Action action = (Action) actionMapper.get(st.nextToken());
                if (action != null && containsId(itemId)
                        && actionHandlers != null) {
                    for (final Iterator<Action.Handler> i = actionHandlers
                            .iterator(); i.hasNext();) {
                        i.next().handleAction(action, this, itemId);
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
        final String[] ka = (String[]) variables.get("selected");

        // Converts the key-array to id-set
        final LinkedList<Object> s = new LinkedList<Object>();
        for (int i = 0; i < ka.length; i++) {
            final Object id = itemIdMapper.get(ka[i]);
            if (!isNullSelectionAllowed()
                    && (id == null || id == getNullSelectionItemId())) {
                // skip empty selection if nullselection is not allowed
                requestRepaint();
            } else if (id != null && containsId(id)) {
                s.add(id);
            }
        }

        if (!isNullSelectionAllowed() && s.size() < 1) {
            // empty selection not allowed, keep old value
            requestRepaint();
            return;
        }

        setValue(s, true);
    }

    /**
     * Paints any needed component-specific things to the given UIDL stream.
     * 
     * @see com.vaadin.ui.AbstractComponent#paintContent(PaintTarget)
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
                target.addAttribute("selectmode", (isMultiSelect() ? "multi"
                        : "single"));
                if (isMultiSelect()) {
                    target.addAttribute("multiselectmode",
                            multiSelectMode.ordinal());
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
            }

            // Adds the item on current level
            else {
                final Object itemId = i.next();

                // Starts the item / node
                final boolean isNode = areChildrenAllowed(itemId);
                if (isNode) {
                    target.startTag("node");
                } else {
                    target.startTag("leaf");
                }

                if (itemStyleGenerator != null) {
                    String stylename = itemStyleGenerator.getStyle(itemId);
                    if (stylename != null) {
                        target.addAttribute("style", stylename);
                    }
                }

                // Adds the attributes
                target.addAttribute("caption", getItemCaption(itemId));
                final Resource icon = getItemIcon(itemId);
                if (icon != null) {
                    target.addAttribute("icon", getItemIcon(itemId));
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
                    final ArrayList<String> keys = new ArrayList<String>();
                    final Iterator<Action.Handler> ahi = actionHandlers
                            .iterator();
                    while (ahi.hasNext()) {
                        final Action[] aa = ahi.next().getActions(itemId, this);
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
                    target.addAttribute("caption", a.getCaption());
                }
                if (a.getIcon() != null) {
                    target.addAttribute("icon", a.getIcon());
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
     * @see com.vaadin.data.Container.Hierarchical#areChildrenAllowed(Object)
     */
    public boolean areChildrenAllowed(Object itemId) {
        return ((Container.Hierarchical) items).areChildrenAllowed(itemId);
    }

    /**
     * Gets the IDs of all Items that are children of the specified Item.
     * 
     * @see com.vaadin.data.Container.Hierarchical#getChildren(Object)
     */
    public Collection<?> getChildren(Object itemId) {
        return ((Container.Hierarchical) items).getChildren(itemId);
    }

    /**
     * Gets the ID of the parent Item of the specified Item.
     * 
     * @see com.vaadin.data.Container.Hierarchical#getParent(Object)
     */
    public Object getParent(Object itemId) {
        return ((Container.Hierarchical) items).getParent(itemId);
    }

    /**
     * Tests if the Item specified with <code>itemId</code> has child Items.
     * 
     * @see com.vaadin.data.Container.Hierarchical#hasChildren(Object)
     */
    public boolean hasChildren(Object itemId) {
        return ((Container.Hierarchical) items).hasChildren(itemId);
    }

    /**
     * Tests if the Item specified with <code>itemId</code> is a root Item.
     * 
     * @see com.vaadin.data.Container.Hierarchical#isRoot(Object)
     */
    public boolean isRoot(Object itemId) {
        return ((Container.Hierarchical) items).isRoot(itemId);
    }

    /**
     * Gets the IDs of all Items in the container that don't have a parent.
     * 
     * @see com.vaadin.data.Container.Hierarchical#rootItemIds()
     */
    public Collection<?> rootItemIds() {
        return ((Container.Hierarchical) items).rootItemIds();
    }

    /**
     * Sets the given Item's capability to have children.
     * 
     * @see com.vaadin.data.Container.Hierarchical#setChildrenAllowed(Object,
     *      boolean)
     */
    public boolean setChildrenAllowed(Object itemId, boolean areChildrenAllowed) {
        final boolean success = ((Container.Hierarchical) items)
                .setChildrenAllowed(itemId, areChildrenAllowed);
        if (success) {
            requestRepaint();
        }
        return success;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container.Hierarchical#setParent(java.lang.Object ,
     * java.lang.Object)
     */
    public boolean setParent(Object itemId, Object newParentId) {
        final boolean success = ((Container.Hierarchical) items).setParent(
                itemId, newParentId);
        if (success) {
            requestRepaint();
        }
        return success;
    }

    /* Overriding select behavior */

    /**
     * Sets the Container that serves as the data source of the viewer.
     * 
     * @see com.vaadin.data.Container.Viewer#setContainerDataSource(Container)
     */
    @Override
    public void setContainerDataSource(Container newDataSource) {
        if (newDataSource == null) {
            // Note: using wrapped IndexedContainer to match constructor (super
            // creates an IndexedContainer, which is then wrapped).
            newDataSource = new ContainerHierarchicalWrapper(
                    new IndexedContainer());
        }

        // Assure that the data source is ordered by making unordered
        // containers ordered by wrapping them
        if (Container.Hierarchical.class.isAssignableFrom(newDataSource
                .getClass())) {
            super.setContainerDataSource(newDataSource);
        } else {
            super.setContainerDataSource(new ContainerHierarchicalWrapper(
                    newDataSource));
        }
    }

    /* Expand event and listener */

    /**
     * Event to fired when a node is expanded. ExapandEvent is fired when a node
     * is to be expanded. it can me used to dynamically fill the sub-nodes of
     * the node.
     * 
     * @author IT Mill Ltd.
     * @version
     * @VERSION@
     * @since 3.0
     */
    public class ExpandEvent extends Component.Event {

        private final Object expandedItemId;

        /**
         * New instance of options change event
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
     * @author IT Mill Ltd.
     * @version
     * @VERSION@
     * @since 3.0
     */
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
    public void addListener(ExpandListener listener) {
        addListener(ExpandEvent.class, listener, ExpandListener.EXPAND_METHOD);
    }

    /**
     * Removes the expand listener.
     * 
     * @param listener
     *            the Listener to be removed.
     */
    public void removeListener(ExpandListener listener) {
        removeListener(ExpandEvent.class, listener,
                ExpandListener.EXPAND_METHOD);
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
     * Collapse event
     * 
     * @author IT Mill Ltd.
     * @version
     * @VERSION@
     * @since 3.0
     */
    public class CollapseEvent extends Component.Event {

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
     * @author IT Mill Ltd.
     * @version
     * @VERSION@
     * @since 3.0
     */
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
    public void addListener(CollapseListener listener) {
        addListener(CollapseEvent.class, listener,
                CollapseListener.COLLAPSE_METHOD);
    }

    /**
     * Removes the collapse listener.
     * 
     * @param listener
     *            the Listener to be removed.
     */
    public void removeListener(CollapseListener listener) {
        removeListener(CollapseEvent.class, listener,
                CollapseListener.COLLAPSE_METHOD);
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
    public void addActionHandler(Action.Handler actionHandler) {

        if (actionHandler != null) {

            if (actionHandlers == null) {
                actionHandlers = new LinkedList<Action.Handler>();
                actionMapper = new KeyMapper();
            }

            if (!actionHandlers.contains(actionHandler)) {
                actionHandlers.add(actionHandler);
                requestRepaint();
            }
        }
    }

    /**
     * Removes an action handler.
     * 
     * @see com.vaadin.event.Action.Container#removeActionHandler(Action.Handler)
     */
    public void removeActionHandler(Action.Handler actionHandler) {

        if (actionHandlers != null && actionHandlers.contains(actionHandler)) {

            actionHandlers.remove(actionHandler);

            if (actionHandlers.isEmpty()) {
                actionHandlers = null;
                actionMapper = null;
            }

            requestRepaint();
        }
    }

    /**
     * Removes all action handlers
     */
    public void removeAllActionHandlers() {
        actionHandlers = null;
        actionMapper = null;
        requestRepaint();
    }

    /**
     * Gets the visible item ids.
     * 
     * @see com.vaadin.ui.Select#getVisibleItemIds()
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
            }

            // Adds the item on current level
            else {
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
     * @see com.vaadin.ui.AbstractSelect#setNullSelectionItemId(java.lang.Object)
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
     * @see com.vaadin.ui.Select#setNewItemsAllowed(boolean)
     */
    @Override
    public void setNewItemsAllowed(boolean allowNewOptions)
            throws UnsupportedOperationException {
        if (allowNewOptions) {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * Tree does not support lazy options loading mode. Setting this true will
     * throw UnsupportedOperationException.
     * 
     * @see com.vaadin.ui.Select#setLazyLoading(boolean)
     */
    public void setLazyLoading(boolean useLazyLoading) {
        if (useLazyLoading) {
            throw new UnsupportedOperationException(
                    "Lazy options loading is not supported by Tree.");
        }
    }

    private ItemStyleGenerator itemStyleGenerator;

    private DropHandler dropHandler;

    public void addListener(ItemClickListener listener) {
        addListener(VTree.ITEM_CLICK_EVENT_ID, ItemClickEvent.class, listener,
                ItemClickEvent.ITEM_CLICK_METHOD);
    }

    public void removeListener(ItemClickListener listener) {
        removeListener(VTree.ITEM_CLICK_EVENT_ID, ItemClickEvent.class,
                listener);
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
            requestRepaint();
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
     * CSS class name that will be added to the cell content is
     * <tt>v-tree-node-[style name]</tt>.
     */
    public interface ItemStyleGenerator extends Serializable {

        /**
         * Called by Tree when an item is painted.
         * 
         * @param itemId
         *            The itemId of the item to be painted
         * @return The style name to add to this item. (the CSS class name will
         *         be v-tree-node-[style name]
         */
        public abstract String getStyle(Object itemId);
    }

    // Overriden so javadoc comes from Container.Hierarchical
    @Override
    public boolean removeItem(Object itemId)
            throws UnsupportedOperationException {
        return super.removeItem(itemId);
    }

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
     * @see {@link DataBoundTransferable}.
     * 
     * @since 6.3
     */
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
    @ClientCriterion(VLazyInitItemIdentifiers.class)
    public static abstract class TreeDropCriterion extends ServerSideCriterion {

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
         * com.vaadin.terminal.PaintTarget)
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
                return eventDetails.getDropLocation() == VerticalDropLocation.MIDDLE;
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
    @ClientCriterion(VTargetInSubtree.class)
    public class TargetInSubtree extends ClientSideCriterion {

        private Object rootId;
        private int depthToCheck = -1;

        /**
         * Constructs a criteria that accepts the drag if the targeted Item is a
         * descendant of Item identified by given id
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

}
