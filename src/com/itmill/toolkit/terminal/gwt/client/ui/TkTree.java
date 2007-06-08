package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.Iterator;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.itmill.toolkit.terminal.gwt.client.Client;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class TkTree extends Composite implements Paintable {
	
	Label caption = new Label();
	Tree tree = new Tree();
	
	public TkTree() {
		VerticalPanel panel = new VerticalPanel();
		panel.add(caption);
		panel.add(tree);
		initWidget(panel);
	}

	public void updateFromUIDL(UIDL uidl, Client client) {
		
		if (uidl.hasAttribute("caption")) caption.setText(uidl.getStringAttribute("caption")); 
		
		TreeItem rootNode = new TreeItem();
		for (Iterator i = uidl.getChildIterator(); i.hasNext();) {
			UIDL childUidl = (UIDL)i.next();
			if(childUidl.getTag().equals("leaf"))
				rootNode.addItem(childUidl.getStringAttribute("caption"));
			if(childUidl.getTag().equals("node")) {
				TreeNode childTree = new TreeNode();
				childTree.updateFromUIDL(childUidl, client);
				if(uidl.getBooleanAttribute("expanded"))
					childTree.setState(true);
				rootNode.addItem(childTree);
			}
		}
	}
	private class TreeNode extends TreeItem {
		
		public void updateFromUIDL(UIDL uidl, Client client) {
			this.setText(uidl.getStringAttribute("caption"));
			for (Iterator i = uidl.getChildIterator(); i.hasNext();) {
				UIDL childUidl = (UIDL)i.next();
				if(childUidl.getTag().equals("leaf"))
					this.addItem(childUidl.getStringAttribute("caption"));
				if(childUidl.getTag().equals("node")) {
					TreeNode childTree = new TreeNode();
					childTree.updateFromUIDL(childUidl, client);
					this.addItem(childTree);
				}
			}
			if(uidl.getBooleanAttribute("expanded"))
				setState(true);
		}
	}
}
