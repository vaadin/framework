package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.TreeListener;
import com.itmill.toolkit.terminal.gwt.client.Client;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class ITree extends Tree implements Paintable {
	
	public static final String CLASSNAME = "i-tree";

	Set selectedIds = new HashSet();
	Client client;
	String id;
	private boolean selectable;
	private boolean multiselect;
	
	public ITree() {
		super();
		setStyleName(CLASSNAME);
	}

	public void updateFromUIDL(UIDL uidl, Client client) {
		// Ensure correct implementation and let container manage caption
		if (client.updateComponent(this, uidl, true))
			return;
		
		this.client = client;
		this.id = uidl.getId();
		
		clear();
		for (Iterator i = uidl.getChildIterator(); i.hasNext();) {
			UIDL childUidl = (UIDL)i.next();
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
					ITree.this.client.updateVariable(ITree.this.id, "selected", selectedIds.toArray(), true);
				}
			}
		
		});
		
		selectedIds = uidl.getStringArrayVariableAsSet("selected");
		
	}
	
	private class TreeNode extends TreeItem {
		
		String key;
		
		public void updateFromUIDL(UIDL uidl, Client client) {
			this.setText(uidl.getStringAttribute("caption"));
			key = uidl.getStringAttribute("key");
			for (Iterator i = uidl.getChildIterator(); i.hasNext();) {
				UIDL childUidl = (UIDL)i.next();
				TreeNode childTree = new TreeNode();
				this.addItem(childTree);
				childTree.updateFromUIDL(childUidl, client);
			}
			setState(uidl.getBooleanAttribute("expanded"));
			setSelected(uidl.getBooleanAttribute("selected"));
		}
		
		
	}
}
