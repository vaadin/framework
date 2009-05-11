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
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ItemClickEvent.ItemClickSource;
import com.vaadin.terminal.KeyMapper;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.terminal.Resource;
import com.vaadin.terminal.gwt.client.MouseEventDetails;

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
public class Tree extends AbstractSelect implements Container.Hierarchical,
        Action.Container, ItemClickSource {

    private static final Method EXPAND_METHOD;

    private static final Method COLLAPSE_METHOD;

    static {
        try {
            EXPAND_METHOD = ExpandListener.class.getDeclaredMethod(
                    "nodeExpand", new Class[] { ExpandEvent.class });
            COLLAPSE_METHOD = CollapseListener.class.getDeclaredMethod(
                    "nodeCollapse", new Class[] { CollapseEvent.class });
        } catch (final java.lang.NoSuchMethodException e) {
            // This should never happen
            throw new java.lang.RuntimeException(
                    "Internal error finding methods in Tree");
        }
    }

    /* Private members */

    /**
     * Set of expanded nodes.
     */
    private final HashSet expanded = new HashSet();

    /**
     * List of action handlers.
     */
    private LinkedList<Action.Handler> actionHandlers = null;

    /**
     * Action mapper.
     */
    private KeyMapper actionMapper = null;

    /**
     * Is the tree selectable .
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
        final Stack todo = new Stack();
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
        final Stack todo = new Stack();
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
     * Getter for property selectable.
     * 
     * <p>
     * The tree is selectable by default.
     * </p>
     * 
     * @return the Value of property selectable.
     */
    public boolean isSelectable() {
        return selectable;
    }

    /**
     * Setter for property selectable.
     * 
     * <p>
     * The tree is selectable by default.
     * </p>
     * 
     * @param selectable
     *            the New value of property selectable.
     */
    public void setSelectable(boolean selectable) {
        if (this.selectable != selectable) {
            this.selectable = selectable;
            requestRepaint();
        }
    }

    /* Component API */

    /**
     * Gets the UIDL tag corresponding to the component.
     * 
     * @see com.vaadin.ui.AbstractComponent#getTag()
     */
    @Override
    public String getTag() {
        return "tree";
    }

    /**
     * Called when one or more variables handled by the implementing class are
     * changed.
     * 
     * @see com.vaadin.terminal.VariableOwner#changeVariables(Object
     *      source, Map variables)
     */
    @Override
    public void changeVariables(Object source, Map variables) {

        if (clickListenerCount > 0 && variables.containsKey("clickedKey")) {
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
            variables = new HashMap(variables);
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

        // Selections are handled by the select component
        super.changeVariables(source, variables);

        // Actions
        if (variables.containsKey("action")) {

            final StringTokenizer st = new StringTokenizer((String) variables
                    .get("action"), ",");
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
            } else {
                target.addAttribute("selectmode", "none");
            }
            if (isNewItemsAllowed()) {
                target.addAttribute("allownewitem", true);
            }

            if (isNullSelectionAllowed()) {
                target.addAttribute("nullselect", true);
            }

            if (clickListenerCount > 0) {
                target.addAttribute("listenClicks", true);
            }

        }

        // Initialize variables
        final Set<Action> actionSet = new LinkedHashSet<Action>();
        String[] selectedKeys;
        if (isMultiSelect()) {
            selectedKeys = new String[((Set) getValue()).size()];
        } else {
            selectedKeys = new String[(getValue() == null ? 0 : 1)];
        }
        int keyIndex = 0;
        final LinkedList expandedKeys = new LinkedList();

        // Iterates through hierarchical tree using a stack of iterators
        final Stack<Iterator> iteratorStack = new Stack<Iterator>();
        Collection ids;
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
            final Iterator i = iteratorStack.peek();

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
                    try {
                        selectedKeys[keyIndex++] = key;
                    } catch (Exception e) {
                        // TODO Fix, see TreeExample (featurebrowser)
                        e.printStackTrace();
                    }
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
            // update tree-level selection information in case some selected
            // node(s) were collapsed
            target.addVariable(this, "selected", selectedKeys);

            partialUpdate = false;
        } else {
            // Selected
            target.addVariable(this, "selected", selectedKeys);

            // Expand and collapse
            target.addVariable(this, "expand", new String[] {});
            target.addVariable(this, "collapse", new String[] {});

            // New items
            target.addVariable(this, "newitem", new String[] {});
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
    public Collection getChildren(Object itemId) {
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
    public Collection rootItemIds() {
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
            fireValueChange(false);
        }
        return success;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.data.Container.Hierarchical#setParent(java.lang.Object
     * , java.lang.Object)
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
        addListener(ExpandEvent.class, listener, EXPAND_METHOD);
    }

    /**
     * Removes the expand listener.
     * 
     * @param listener
     *            the Listener to be removed.
     */
    public void removeListener(ExpandListener listener) {
        removeListener(ExpandEvent.class, listener, EXPAND_METHOD);
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
        addListener(CollapseEvent.class, listener, COLLAPSE_METHOD);
    }

    /**
     * Removes the collapse listener.
     * 
     * @param listener
     *            the Listener to be removed.
     */
    public void removeListener(CollapseListener listener) {
        removeListener(CollapseEvent.class, listener, COLLAPSE_METHOD);
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
     * Gets the visible item ids.
     * 
     * @see com.vaadin.ui.Select#getVisibleItemIds()
     */
    @Override
    public Collection getVisibleItemIds() {

        final LinkedList visible = new LinkedList();

        // Iterates trough hierarchical tree using a stack of iterators
        final Stack<Iterator> iteratorStack = new Stack<Iterator>();
        final Collection ids = rootItemIds();
        if (ids != null) {
            iteratorStack.push(ids.iterator());
        }
        while (!iteratorStack.isEmpty()) {

            // Gets the iterator for current tree level
            final Iterator i = iteratorStack.peek();

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
     * Focusing to this component is not supported.
     * 
     * @throws UnsupportedOperationException
     *             if invoked.
     * @see com.vaadin.ui.AbstractField#focus()
     */
    @Override
    public void focus() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
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

    private int clickListenerCount = 0;

    public void addListener(ItemClickListener listener) {
        addListener(ItemClickEvent.class, listener,
                ItemClickEvent.ITEM_CLICK_METHOD);
        clickListenerCount++;
        // repaint needed only if click listening became necessary
        if (clickListenerCount == 1) {
            requestRepaint();
        }
    }

    public void removeListener(ItemClickListener listener) {
        removeListener(ItemClickEvent.class, listener,
                ItemClickEvent.ITEM_CLICK_METHOD);
        clickListenerCount++;
        // repaint needed only if click listening is not needed in client
        // anymore
        if (clickListenerCount == 0) {
            requestRepaint();
        }
    }

}
