package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;
import com.itmill.toolkit.terminal.gwt.client.Util;

/**
 * TODO dump GWT's Tree implementation and use Toolkit 4 style TODO update node
 * close/opens to server (even if no content fetch is needed)
 * 
 * DOM structure
 * 
 */
public class ITree extends FlowPanel implements Paintable {

	public static final String CLASSNAME = "i-tree";

	private Set selectedIds = new HashSet();
	private ApplicationConnection client;
	private String paintableId;
	private boolean selectable;
	private boolean isMultiselect;

	private HashMap keyToNode = new HashMap();

	/**
	 * This map contains captions and icon urls for actions like: * "33_c" ->
	 * "Edit" * "33_i" -> "http://dom.com/edit.png"
	 */
	private HashMap actionMap = new HashMap();

	private boolean immediate;

	private boolean isNullSelectionAllowed = true;

	private boolean disabled = false;

	public ITree() {
		super();
		setStyleName(CLASSNAME);
	}

	private void updateActionMap(UIDL c) {
		Iterator it = c.getChildIterator();
		while (it.hasNext()) {
			UIDL action = (UIDL) it.next();
			String key = action.getStringAttribute("key");
			String caption = action.getStringAttribute("caption");
			actionMap.put(key + "_c", caption);
			if (action.hasAttribute("icon")) {
				// TODO need some uri handling ??
				actionMap.put(key + "_i", action.getStringAttribute("icon"));
			}
		}

	}

	public String getActionCaption(String actionKey) {
		return (String) actionMap.get(actionKey + "_c");
	}

	public String getActionIcon(String actionKey) {
		return (String) actionMap.get(actionKey + "_i");
	}

	public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
		// Ensure correct implementation and let container manage caption
		if (client.updateComponent(this, uidl, true))
			return;

		this.client = client;

		if (uidl.hasAttribute("partialUpdate")) {
			handleUpdate(uidl);
			return;
		}

		this.paintableId = uidl.getId();

		this.immediate = uidl.hasAttribute("immediate");

		disabled = uidl.getBooleanAttribute("disabled");

		isNullSelectionAllowed = uidl.getBooleanAttribute("nullselect");

		clear();
		for (Iterator i = uidl.getChildIterator(); i.hasNext();) {
			UIDL childUidl = (UIDL) i.next();
			if ("actions".equals(childUidl.getTag())) {
				updateActionMap(childUidl);
				continue;
			}
			TreeNode childTree = new TreeNode();
			this.add(childTree);
			childTree.updateFromUIDL(childUidl, client);
		}
		String selectMode = uidl.getStringAttribute("selectmode");
		selectable = selectMode != null;
		isMultiselect = "multi".equals(selectMode);

		selectedIds = uidl.getStringArrayVariableAsSet("selected");

	}

	private void handleUpdate(UIDL uidl) {
		TreeNode rootNode = (TreeNode) keyToNode.get(uidl
				.getStringAttribute("rootKey"));
		if (rootNode != null) {
			if (!rootNode.getState()) {
				// expanding node happened server side
				rootNode.setState(true, false);
			}
			rootNode.renderChildNodes(uidl.getChildIterator());
		}

	}

	public void setSelected(TreeNode treeNode, boolean selected) {
		if (selected) {
			if (!isMultiselect) {
				while (selectedIds.size() > 0) {
					String id = (String) selectedIds.iterator().next();
					TreeNode oldSelection = (TreeNode) keyToNode.get(id);
					oldSelection.setSelected(false);
					selectedIds.remove(id);
				}
			}
			treeNode.setSelected(true);
			selectedIds.add(treeNode.key);
		} else {
			if (!isNullSelectionAllowed) {
				if (!isMultiselect || selectedIds.size() == 1)
					return;
			}
			selectedIds.remove(treeNode.key);
			treeNode.setSelected(false);
		}
		client.updateVariable(ITree.this.paintableId, "selected", selectedIds
				.toArray(), immediate);
	}

	public boolean isSelected(TreeNode treeNode) {
		return selectedIds.contains(treeNode.key);
	}

	protected class TreeNode extends SimplePanel implements ActionOwner {

		public static final String CLASSNAME = "i-tree-node";

		String key;

		private String[] actionKeys = null;

		private boolean childrenLoaded;

		private Element nodeCaptionDiv;

		protected Element nodeCaptionSpan;

		private FlowPanel childNodeContainer;

		private boolean open;

		public TreeNode() {
			constructDom();
			sinkEvents(Event.ONCLICK);
			setStyleName(CLASSNAME);
		}

		public void onBrowserEvent(Event event) {
			super.onBrowserEvent(event);
			if (disabled)
				return;
			Element target = DOM.eventGetTarget(event);
			if (DOM.compare(target, nodeCaptionSpan)) {
				// caption click = selection change
				toggleSelection();
			} else if (DOM.compare(getElement(), target)) {
				// state change
				toggleState();
			}
		}

		private void toggleSelection() {
			if (selectable)
				ITree.this.setSelected(this, !isSelected());
		}

		private void toggleState() {
			this.setState(!getState(), true);
		}

		protected void constructDom() {
			Element root = DOM.createDiv();
			nodeCaptionDiv = DOM.createDiv();
			DOM.setElementProperty(nodeCaptionDiv, "className", CLASSNAME
					+ "-caption");
			nodeCaptionSpan = DOM.createSpan();
			DOM.appendChild(root, nodeCaptionDiv);
			DOM.appendChild(nodeCaptionDiv, nodeCaptionSpan);
			setElement(root);

			childNodeContainer = new FlowPanel();
			childNodeContainer.setStylePrimaryName(CLASSNAME + "-children");
			setWidget(childNodeContainer);
		}

		public void onDetach() {
			Util.removeContextMenuEvent(nodeCaptionSpan);
			super.onDetach();
		}

		public void onAttach() {
			attachContextMenuEvent(nodeCaptionSpan);
			super.onAttach();
		}

		public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
			this.setText(uidl.getStringAttribute("caption"));
			key = uidl.getStringAttribute("key");

			keyToNode.put(key, this);

			if (uidl.hasAttribute("al"))
				actionKeys = uidl.getStringArrayAttribute("al");

			if (uidl.getTag().equals("node")) {
				if (uidl.getChidlCount() == 0) {
					childNodeContainer.setVisible(false);
				} else {
					renderChildNodes(uidl.getChildIterator());
					childrenLoaded = true;
				}
			} else {
				addStyleName(CLASSNAME + "-leaf");
			}

			if (uidl.getBooleanAttribute("expanded") && !getState()) {
				setState(true, false);
			}

			if (uidl.getBooleanAttribute("selected")) {
				setSelected(true);
			}
		}

		private void setState(boolean state, boolean notifyServer) {
			if (open == state)
				return;
			if (state) {
				if (!childrenLoaded && notifyServer) {
					client.updateVariable(paintableId, "requestChildTree",
							true, false);
				}
				if (notifyServer)
					client.updateVariable(paintableId, "expand",
							new String[] { key }, true);
				addStyleName(CLASSNAME + "-expanded");
				childNodeContainer.setVisible(true);
			} else {
				removeStyleName(CLASSNAME + "-expanded");
				childNodeContainer.setVisible(false);
				if (notifyServer)
					client.updateVariable(paintableId, "collapse",
							new String[] { key }, true);
			}

			open = state;
		}

		private boolean getState() {
			return open;
		}

		private void setText(String text) {
			DOM.setInnerText(nodeCaptionSpan, text);
		}

		private void renderChildNodes(Iterator i) {
			childNodeContainer.clear();
			childNodeContainer.setVisible(true);
			while (i.hasNext()) {
				UIDL childUidl = (UIDL) i.next();
				// actions are in bit weird place, don't mix them with children,
				// but current node's actions
				if ("actions".equals(childUidl.getTag())) {
					updateActionMap(childUidl);
					continue;
				}
				TreeNode childTree = new TreeNode();
				childNodeContainer.add(childTree);
				childTree.updateFromUIDL(childUidl, client);
			}
			childrenLoaded = true;
		}

		public boolean isChildrenLoaded() {
			return childrenLoaded;
		}

		public Action[] getActions() {
			if (actionKeys == null)
				return new Action[] {};
			Action[] actions = new Action[actionKeys.length];
			for (int i = 0; i < actions.length; i++) {
				String actionKey = actionKeys[i];
				TreeAction a = new TreeAction(this, String.valueOf(key),
						actionKey);
				a.setCaption(getActionCaption(actionKey));
				a.setIconUrl(getActionIcon(actionKey));
				actions[i] = a;
			}
			return actions;
		}

		public ApplicationConnection getClient() {
			return client;
		}

		public String getPaintableId() {
			return paintableId;
		}

		/**
		 * Adds/removes IT Mill Toolkit spesific style name. This method ought
		 * to be called only from Tree.
		 * 
		 * @param selected
		 */
		public void setSelected(boolean selected) {
			// add style name to caption dom structure only, not to subtree
			setStyleName(nodeCaptionDiv, "i-tree-node-selected", selected);
		}

		public boolean isSelected() {
			return ITree.this.isSelected(this);
		}

		public void showContextMenu(Event event) {
			if (actionKeys != null) {
				int left = DOM.eventGetClientX(event);
				int top = DOM.eventGetClientY(event);
				top += Window.getScrollTop();
				left += Window.getScrollLeft();
				client.getContextMenu().showAt(this, left, top);
			}
			DOM.eventCancelBubble(event, true);
		}

		private native void attachContextMenuEvent(Element el)
		/*-{
			var node = this;
			el.oncontextmenu = function(e) {
				if(!e)
					e = $wnd.event;
				node.@com.itmill.toolkit.terminal.gwt.client.ui.ITree.TreeNode::showContextMenu(Lcom/google/gwt/user/client/Event;)(e);
				return false;
			};
		}-*/;

	}
}
