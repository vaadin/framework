/* *************************************************************************
 
                               Enably Toolkit 

               Development of Browser User Intarfaces Made Easy

                    Copyright (C) 2000-2006 IT Mill Ltd
                     
   *************************************************************************

   This product is distributed under commercial license that can be found
   from the product package on license/license.txt. Use of this product might 
   require purchasing a commercial license from IT Mill Ltd. For guidelines 
   on usage, see license/licensing-guidelines.html

   *************************************************************************
   
   For more information, contact:
   
   IT Mill Ltd                           phone: +358 2 4802 7180
   Ruukinkatu 2-4                        fax:   +358 2 4802 7181
   20540, Turku                          email:  info@enably.com
   Finland                               company www: www.enably.com
   
   Primary source for information and releases: www.enably.com

   ********************************************************************** */

package com.enably.tk.ui;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.StringTokenizer;

import com.enably.tk.data.Container;
import com.enably.tk.data.util.ContainerHierarchicalWrapper;
import com.enably.tk.event.Action;
import com.enably.tk.terminal.KeyMapper;
import com.enably.tk.terminal.PaintException;
import com.enably.tk.terminal.PaintTarget;
import com.enably.tk.terminal.Resource;

/** MenuTree component.
 *  MenuTree can be used to select an item (or multiple items)
 *  from a hierarchical set of items.
 *
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.0
 */
public class Tree extends Select implements Container.Hierarchical, Action.Container {

	/* Static members ***************************************************** */

	private static final Method EXPAND_METHOD;
	private static final Method COLLAPSE_METHOD;

	static {
		try {
			EXPAND_METHOD =
				ExpandListener.class.getDeclaredMethod(
					"nodeExpand",
					new Class[] { ExpandEvent.class });
			COLLAPSE_METHOD =
				CollapseListener.class.getDeclaredMethod(
					"nodeCollapse",
					new Class[] { CollapseEvent.class });
		} catch (java.lang.NoSuchMethodException e) {
			// This should never happen
			e.printStackTrace();
			throw new java.lang.RuntimeException(
				"Internal error, please report");
		}
	}

	/* Private members **************************************************** */

	/** Set of expanded nodes */
	private HashSet expanded = new HashSet();

	/** List of action handlers */
	private LinkedList actionHandlers = null;

	/** Action mapper */
	private KeyMapper actionMapper = null;

	/** Is the tree selectable */
	private boolean selectable = true;

	/* Tree constructors ************************************************** */

	/** Create new empty tree */
	public Tree() {
	}

	/** Create new empty tree with caption. */
	public Tree(String caption) {
		setCaption(caption);
	}

	/** Create new tree with caption and connect it to a Container. */
	public Tree(String caption, Container dataSource) {
		setCaption(caption);
		setContainerDataSource(dataSource);
	}

	/* Expanding and collapsing ******************************************* */

	/** Check is an item is expanded
	 * @return true iff the item is expanded
	 */
	public boolean isExpanded(Object itemId) {
		return expanded.contains(itemId);
	}

	/** Expand an item.
	 * 
	 * @return True iff the expand operation succeeded
	 */
	public boolean expandItem(Object itemId) {

		// Succeeds if the node is already expanded
		if (isExpanded(itemId))
			return true;

		// Nodes that can not have children are not expandable
		if (!areChildrenAllowed(itemId))
			return false;

		// Expand
		expanded.add(itemId);
		requestRepaint();
		fireExpandEvent(itemId);

		return true;
	}

	/** Expand items recursively
	 * 
	 * Expands all the children recursively starting from an item.
	 * Operation succeeds only if all expandable items are expanded.
	 * @return True iff the expand operation succeeded
	 */
	public boolean expandItemsRecursively(Object startItemId) {

		boolean result = true;

		// Initial stack
		Stack todo = new Stack();
		todo.add(startItemId);

		// Expand recursively
		while (!todo.isEmpty()) {
			Object id = todo.pop();
			if (areChildrenAllowed(id) && !expandItem(id)) {
				result = false;
			}
			if (hasChildren(id)) {
				todo.addAll(getChildren(id));
			}
		}

		return result;
	}

	/** Collapse an item.
	 * 
	 * @return True iff the collapse operation succeeded
	 */
	public boolean collapseItem(Object itemId) {

		// Succeeds if the node is already collapsed
		if (!isExpanded(itemId))
			return true;

		// Collapse
		expanded.remove(itemId);
		requestRepaint();
		fireCollapseEvent(itemId);

		return true;
	}

	/** Collapse items recursively
	 * 
	 * Collapse all the children recursively starting from an item.
	 * Operation succeeds only if all expandable items are collapsed.
	 * @return True iff the collapse operation succeeded
	 */
	public boolean collapseItemsRecursively(Object startItemId) {

		boolean result = true;

		// Initial stack
		Stack todo = new Stack();
		todo.add(startItemId);

		// Collapse recursively
		while (!todo.isEmpty()) {
			Object id = todo.pop();
			if (areChildrenAllowed(id) && !collapseItem(id)) {
				result = false;
			}
			if (hasChildren(id)) {
				todo.addAll(getChildren(id));
			}
		}

		return result;
	}

	/** Getter for property selectable.
	 * 
	 * <p>The tree is selectable by default.</p>
	 * 
	 * @return Value of property selectable.
	 */
	public boolean isSelectable() {
		return this.selectable;
	}

	/** Setter for property selectable.
	 * 
	 * <p>The tree is selectable by default.</p>
	 * 
	 * @param selectable New value of property selectable.
	 */
	public void setSelectable(boolean selectable) {
		if (this.selectable != selectable) {
			this.selectable = selectable;
			requestRepaint();
		}
	}

	/* Component API ****************************************************** */

	/**
	 * @see com.enably.tk.ui.AbstractComponent#getTag()
	 */
	public String getTag() {
		return "tree";
	}

	/**
	 * @see com.enably.tk.terminal.VariableOwner#changeVariables(Object source, Map variables)
	 */
	public void changeVariables(Object source, Map variables) {

			// Collapse nodes
			if (variables.containsKey("collapse")) {
				String[] keys = (String[]) variables.get("collapse");
				for (int i = 0; i < keys.length; i++) {
					Object id = itemIdMapper.get(keys[i]);
					if (id != null)
						collapseItem(id);
				}
			}

			// Expand nodes
			if (variables.containsKey("expand")) {
				String[] keys = (String[]) variables.get("expand");
				for (int i = 0; i < keys.length; i++) {
					Object id = itemIdMapper.get(keys[i]);
					if (id != null)
						expandItem(id);
				}
			}

			// Selections are handled by the select component
			super.changeVariables(source, variables);

			// Actions
			if (variables.containsKey("action")) {

				StringTokenizer st =
					new StringTokenizer((String) variables.get("action"), ",");
				if (st.countTokens() == 2) {
					Object itemId = itemIdMapper.get(st.nextToken());
					Action action = (Action) actionMapper.get(st.nextToken());
					if (action != null
						&& containsId(itemId)
						&& actionHandlers != null)
						for (Iterator i = actionHandlers.iterator();
							i.hasNext();
							)
							((Action.Handler) i.next()).handleAction(
								action,
								this,
								itemId);
				}
			}
	}

	/**
	 * @see com.enably.tk.ui.AbstractComponent#paintContent(PaintTarget)
	 */
	public void paintContent(PaintTarget target) throws PaintException {

		// Focus control id
		if (this.getFocusableId() > 0) {
			target.addAttribute("focusid", this.getFocusableId());
		}

		// The tab ordering number
		if (this.getTabIndex() > 0)
			target.addAttribute("tabindex", this.getTabIndex());


		// Paint tree attributes
		if (isSelectable())
			target.addAttribute(
				"selectmode",
				(isMultiSelect() ? "multi" : "single"));
		else
			target.addAttribute("selectmode", "none");
		if (isNewItemsAllowed())
			target.addAttribute("allownewitem", true);

		// Initialize variables
		Set actionSet = new LinkedHashSet();
		String[] selectedKeys;
		if (isMultiSelect())
			selectedKeys = new String[((Set) getValue()).size()];
		else
			selectedKeys = new String[(getValue() == null ? 0 : 1)];
		int keyIndex = 0;
		LinkedList expandedKeys = new LinkedList();

		// Iterate trough hierarchical tree using a stack of iterators
		Stack iteratorStack = new Stack();
		Collection ids = rootItemIds();
		if (ids != null)
			iteratorStack.push(ids.iterator());
		while (!iteratorStack.isEmpty()) {

			// Get the iterator for current tree level
			Iterator i = (Iterator) iteratorStack.peek();

			// If the level is finished, back to previous tree level
			if (!i.hasNext()) {

				// Remove used iterator from the stack
				iteratorStack.pop();

				// Close node
				if (!iteratorStack.isEmpty())
					target.endTag("node");
			}

			// Add the item on current level
			else {
				Object itemId = i.next();

				// Start the item / node
				boolean isNode = areChildrenAllowed(itemId);
				if (isNode)
					target.startTag("node");
				else
					target.startTag("leaf");

				// Add attributes
				target.addAttribute("caption", getItemCaption(itemId));
				Resource icon = getItemIcon(itemId);
				if (icon != null)
					target.addAttribute("icon", getItemIcon(itemId));
				String key = itemIdMapper.key(itemId);
				target.addAttribute("key", key);
				if (isSelected(itemId)) {
					target.addAttribute("selected", true);
					selectedKeys[keyIndex++] = key;
				}
				if (areChildrenAllowed(itemId) && isExpanded(itemId)) {
					target.addAttribute("expanded", true);
					expandedKeys.add(key);
				}

				// Actions
				if (actionHandlers != null) {
					target.startTag("al");
					for (Iterator ahi = actionHandlers.iterator();
						ahi.hasNext();
						) {
						Action[] aa =
							((Action.Handler) ahi.next()).getActions(
								itemId,
								this);
						if (aa != null)
							for (int ai = 0; ai < aa.length; ai++) {
								String akey = actionMapper.key(aa[ai]);
								actionSet.add(aa[ai]);
								target.addSection("ak", akey);
							}
					}
					target.endTag("al");
				}

				// Add children if expanded, or close the tag
				if (isExpanded(itemId) && hasChildren(itemId) 
					&& areChildrenAllowed(itemId)) {
					iteratorStack.push(getChildren(itemId).iterator());
				} else {
					if (isNode)
						target.endTag("node");
					else
						target.endTag("leaf");
				}
			}
		}

		// Actions
		if (!actionSet.isEmpty()) {
			target.startTag("actions");
			target.addVariable(this, "action", "");
			for (Iterator i = actionSet.iterator(); i.hasNext();) {
				Action a = (Action) i.next();
				target.startTag("action");
				if (a.getCaption() != null)
					target.addAttribute("caption", a.getCaption());
				if (a.getIcon() != null)
					target.addAttribute("icon", a.getIcon());
				target.addAttribute("key", actionMapper.key(a));
				target.endTag("action");
			}
			target.endTag("actions");
		}

		// Selected
		target.addVariable(this, "selected", selectedKeys);

		// Expand and collapse
		target.addVariable(this, "expand", new String[] {
		});
		target.addVariable(this, "collapse", new String[] {
		});

		// New items
		target.addVariable(this, "newitem", new String[] {
		});
	}

	/* Container.Hierarchical API ***************************************** */

	/**
	 * @see com.enably.tk.data.Container.Hierarchical#areChildrenAllowed(Object)
	 */
	public boolean areChildrenAllowed(Object itemId) {
		return ((Container.Hierarchical) items).areChildrenAllowed(itemId);
	}

	/**
	 * @see com.enably.tk.data.Container.Hierarchical#getChildren(Object)
	 */
	public Collection getChildren(Object itemId) {
		return ((Container.Hierarchical) items).getChildren(itemId);
	}

	/**
	 * @see com.enably.tk.data.Container.Hierarchical#getParent(Object)
	 */
	public Object getParent(Object itemId) {
		return ((Container.Hierarchical) items).getParent(itemId);
	}

	/**
	 * @see com.enably.tk.data.Container.Hierarchical#hasChildren(Object)
	 */
	public boolean hasChildren(Object itemId) {
		return ((Container.Hierarchical) items).hasChildren(itemId);
	}

	/**
	 * @see com.enably.tk.data.Container.Hierarchical#isRoot(Object)
	 */
	public boolean isRoot(Object itemId) {
		return ((Container.Hierarchical) items).isRoot(itemId);
	}

	/**
	 * @see com.enably.tk.data.Container.Hierarchical#rootItemIds()
	 */
	public Collection rootItemIds() {
		return ((Container.Hierarchical) items).rootItemIds();
	}

	/**
	 * @see com.enably.tk.data.Container.Hierarchical#setChildrenAllowed(Object, boolean)
	 */
	public boolean setChildrenAllowed(
		Object itemId,
		boolean areChildrenAllowed) {
		boolean success =
			((Container.Hierarchical) items).setChildrenAllowed(
				itemId,
				areChildrenAllowed);
		if (success)
			fireValueChange();
		return success;
	}

	/**
	 * @see com.enably.tk.data.Container.Hierarchical#setParent(Object, Object)
	 */
	public boolean setParent(Object itemId, Object newParentId) {
		boolean success =
			((Container.Hierarchical) items).setParent(itemId, newParentId);
		if (success)
			requestRepaint();
		return success;
	}

	/* Overriding select behavior******************************************** */

	/**
	 * @see com.enably.tk.data.Container.Viewer#setContainerDataSource(Container)
	 */
	public void setContainerDataSource(Container newDataSource) {

		// Assure that the data source is ordered by making unordered 
		// containers ordered by wrapping them
		if (Container
			.Hierarchical
			.class
			.isAssignableFrom(newDataSource.getClass()))
			super.setContainerDataSource(newDataSource);
		else
			super.setContainerDataSource(
				new ContainerHierarchicalWrapper(newDataSource));
	}

	/* Expand event and listener ****************************************** */

	/** Event to fired when a node is expanded.
	 *  ExapandEvent is fired when a node is to be expanded.
	 *  it can me used to dynamically fill the sub-nodes of the
	 *  node.
	 * @author IT Mill Ltd.
	 * @version @VERSION@
	 * @since 3.0
	 */
	public class ExpandEvent extends Component.Event {

		/**
         * Serial generated by eclipse.
         */
        private static final long serialVersionUID = 3832624001804481075L;
        private Object expandedItemId;
		/** New instance of options change event
		 * @param source Source of the event.
		 */
		public ExpandEvent(Component source, Object expandedItemId) {
			super(source);
			this.expandedItemId = expandedItemId;
		}

		/** Node where the event occurred
		 * @return Source of the event.
		 */
		public Object getItemId() {
			return this.expandedItemId;
		}
	}

	/** Expand event listener
	 * @author IT Mill Ltd.
	 * @version @VERSION@
	 * @since 3.0 
	 */
	public interface ExpandListener {

		/** A node has been expanded.
		 * @param event Expand event.
		 */
		public void nodeExpand(ExpandEvent event);
	}

	/** Add expand listener
	 * @param listener Listener to be added.
	 */
	public void addListener(ExpandListener listener) {
		addListener(ExpandEvent.class, listener, EXPAND_METHOD);
	}

	/** Remove expand listener
	 * @param listener Listener to be removed.
	 */
	public void removeListener(ExpandListener listener) {
		removeListener(ExpandEvent.class, listener, EXPAND_METHOD);
	}

	/** Emit expand  event. */
	protected void fireExpandEvent(Object itemId) {
		fireEvent(new ExpandEvent(this, itemId));
	}

	/* Collapse  event ****************************************** */

	/** Collapse event 
	 * @author IT Mill Ltd.
		 * @version @VERSION@
		 * @since 3.0
		 */
	public class CollapseEvent extends Component.Event {

		/**
         * Serial generated by eclipse.
         */
        private static final long serialVersionUID = 3257009834783290160L;
        
        private Object collapsedItemId;

		/** New instance of options change event
		 * @param source Source of the event.
		 */
		public CollapseEvent(Component source, Object collapsedItemId) {
			super(source);
			this.collapsedItemId = collapsedItemId;
		}

		/** Node where the event occurred
		 * @return Source of the event.
		 */
		public Object getItemId() {
			return collapsedItemId;
		}
	}

	/** Collapse event listener 
	 * @author IT Mill Ltd.
		 * @version @VERSION@
	 * @since 3.0
	 */
	public interface CollapseListener {

		/** A node has been collapsed.
		 * @param event Collapse event.
		 */
		public void nodeCollapse(CollapseEvent event);
	}

	/** Add collapse listener
	 * @param listener Listener to be added.
	 */
	public void addListener(CollapseListener listener) {
		addListener(CollapseEvent.class, listener, COLLAPSE_METHOD);
	}

	/** Remove collapse listener
	 * @param listener Listener to be removed.
	 */
	public void removeListener(CollapseListener listener) {
		removeListener(CollapseEvent.class, listener, COLLAPSE_METHOD);
	}

	/** Emit collapse  event. */
	protected void fireCollapseEvent(Object itemId) {
		fireEvent(new CollapseEvent(this, itemId));
	}

	/* Action container *************************************************** */

	/** Adds an action handler.
	 * @see com.enably.tk.event.Action.Container#addActionHandler(Action.Handler)
	 */
	public void addActionHandler(Action.Handler actionHandler) {

		if (actionHandler != null) {

			if (actionHandlers == null) {
				actionHandlers = new LinkedList();
				actionMapper = new KeyMapper();
			}

             if(!actionHandlers.contains(actionHandler)){ 
                 actionHandlers.add(actionHandler); 
                 requestRepaint(); 
             }
		}
	}

	/** Removes an action handler.
	 * @see com.enably.tk.event.Action.Container#removeActionHandler(Action.Handler)
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
	 * @see com.enably.tk.ui.Select#getVisibleItemIds()
	 */
	public Collection getVisibleItemIds() {

		LinkedList visible = new LinkedList();

		// Iterate trough hierarchical tree using a stack of iterators
		Stack iteratorStack = new Stack();
		Collection ids = rootItemIds();
		if (ids != null)
			iteratorStack.push(ids.iterator());
		while (!iteratorStack.isEmpty()) {

			// Get the iterator for current tree level
			Iterator i = (Iterator) iteratorStack.peek();

			// If the level is finished, back to previous tree level
			if (!i.hasNext()) {

				// Remove used iterator from the stack
				iteratorStack.pop();
			}

			// Add the item on current level
			else {
				Object itemId = i.next();

				visible.add(itemId);

				// Add children if expanded, or close the tag
				if (isExpanded(itemId) && hasChildren(itemId)) {
					iteratorStack.push(getChildren(itemId).iterator());
				}
			}
		}

		return visible;
	}

	/** Adding new items is not supported. 
	 * @see com.enably.tk.ui.Select#setNewItemsAllowed(boolean)
	 * @throws UnsupportedOperationException if set to true.
	 */
	public void setNewItemsAllowed(boolean allowNewOptions)
		throws UnsupportedOperationException {
		if (allowNewOptions)
			throw new UnsupportedOperationException();
	}

	/** Focusing to this component is not supported.
	 * @see com.enably.tk.ui.AbstractField#focus()
	 * @throws UnsupportedOperationException if invoked.
	 */
	public void focus() throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

}
