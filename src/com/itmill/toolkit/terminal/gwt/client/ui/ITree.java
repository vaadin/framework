package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.TreeListener;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class ITree extends Tree implements Paintable {
	
	public static final String CLASSNAME = "i-tree";

	Set selectedIds = new HashSet();
	ApplicationConnection client;
	String paintableId;
	private boolean selectable;
	private boolean multiselect;
	
	/**
	 * This map contains captions and icon urls for 
	 * actions like:
	 *   * "33_c" -> "Edit"
	 *   * "33_i" -> "http://dom.com/edit.png"
	 */
	private HashMap actionMap = new HashMap();

	
	public ITree() {
		super();
		setStyleName(CLASSNAME);
	}
	
	private void updateActionMap(UIDL c) {
		Iterator it = c.getChildIterator();
		while(it.hasNext()) {
			UIDL action = (UIDL) it.next();
			String key = action.getStringAttribute("key");
			String caption = action.getStringAttribute("caption");
			actionMap.put(key + "_c", caption);
			if(action.hasAttribute("icon")) {
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
		this.paintableId = uidl.getId();
		
		clear();
		for (Iterator i = uidl.getChildIterator(); i.hasNext();) {
			UIDL childUidl = (UIDL)i.next();
			if("actions".equals(childUidl.getTag())){
				updateActionMap(childUidl);
				continue;
			}
			TreeNode childTree = new TreeNode();
			addItem(childTree);
			childTree.updateFromUIDL(childUidl, client);
		}
		String selectMode = uidl.getStringAttribute("selectmode");
		selectable = selectMode != null;
		multiselect = "multi".equals(selectMode);
		
		addTreeListener(new TreeListener() {
		
			public void onTreeItemStateChanged(TreeItem item) {
			}
		
			public void onTreeItemSelected(TreeItem item) {
				if (!selectable) return;
				item.setSelected(true);
				String key = ((TreeNode)item).key;
				if (key != null) {
					if (!multiselect) selectedIds.clear();
					if (selectedIds.contains(key)) selectedIds.remove(key);
					else selectedIds.add(key);
					ITree.this.client.updateVariable(ITree.this.paintableId, "selected", selectedIds.toArray(), true);
				}
			}
		
		});
		
		selectedIds = uidl.getStringArrayVariableAsSet("selected");
		
	}
	
	private class TreeNode extends TreeItem implements IActionOwner {
		
		String key;
		
		private String[] actionKeys = null;
		
		public TreeNode() {
			super();
			attachContextMenuEvent(getElement());
		}

		
		public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
			this.setText(uidl.getStringAttribute("caption"));
			key = uidl.getStringAttribute("key");
			
			if(uidl.hasAttribute("al"))
				actionKeys = uidl.getStringArrayAttribute("al");
			
			for (Iterator i = uidl.getChildIterator(); i.hasNext();) {
				UIDL childUidl = (UIDL)i.next();
				TreeNode childTree = new TreeNode();
				this.addItem(childTree);
				childTree.updateFromUIDL(childUidl, client);
			}
			setState(uidl.getBooleanAttribute("expanded"));
			setSelected(uidl.getBooleanAttribute("selected"));
		}

		public IAction[] getActions() {
			if(actionKeys == null)
				return new IAction[] {};
			IAction[] actions = new IAction[actionKeys.length];
			for (int i = 0; i < actions.length; i++) {
				String actionKey = actionKeys[i];
				ITreeAction a = new ITreeAction(this, String.valueOf(key), actionKey);
				a.setCaption(getActionCaption(actionKey));
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
		
		public void showContextMenu(Event event) {
			client.console.log("Context menu");
			if(actionKeys != null) {
				int left = DOM.eventGetClientX(event);
				int top = DOM.eventGetClientY(event);
				client.getContextMenu().showAt(this, left, top);
			}
		}
		
		private native void attachContextMenuEvent(Element el) /*-{
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
