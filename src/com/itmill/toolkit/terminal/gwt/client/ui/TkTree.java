package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.TreeListener;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.itmill.toolkit.terminal.gwt.client.Client;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class TkTree extends Composite implements Paintable {
	
	Label caption = new Label();
	Tree tree = new Tree();
	Set selectedIds = new HashSet();
	Client client;
	String id;
	boolean selectable;
	boolean multiselect;
	
	public TkTree() {
		VerticalPanel panel = new VerticalPanel();
		panel.add(caption);
		panel.add(tree);
		initWidget(panel);
	}

	public void updateFromUIDL(UIDL uidl, Client client) {
		this.client = client;
		id = uidl.getId();
		if (uidl.hasAttribute("caption")) caption.setText(uidl.getStringAttribute("caption")); 
		tree.clear();
		for (Iterator i = uidl.getChildIterator(); i.hasNext();) {
			UIDL childUidl = (UIDL)i.next();
			if(childUidl.getTag().equals("leaf"))
				tree.addItem(childUidl.getStringAttribute("caption"));
			if(childUidl.getTag().equals("node")) {
				TreeNode childTree = new TreeNode();
				childTree.updateFromUIDL(childUidl, client);
				tree.addItem(childTree);
			}
		}
		String selectMode = uidl.getStringAttribute("selectmode");
		selectable = selectMode != null;
		multiselect = "multi".equals(selectMode);
		
		tree.addTreeListener(new TreeListener() {
		
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
					TkTree.this.client.updateVariable(TkTree.this.id, "selected", selectedIds.toArray(), true);
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
				childTree.updateFromUIDL(childUidl, client);
				this.addItem(childTree);
			}
			// TODO if(uidl.getBooleanAttribute("expanded")) setState(true);
		}
		
		
	}
}
