package com.itmill.toolkit.terminal.gwt.client.ui;

import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.Node;
import com.itmill.toolkit.terminal.gwt.client.Client;

public abstract class Component {
	
	private ContainerComponent parent;
	protected Client client;
	private final int id;
	
	public Component(int id, Client c) {
		client = c;
		this.id = id;
	}

	public abstract void updateFromUidl(Node n);

	public abstract Widget getWidget(); 
	
	public static boolean isComponent(String nodeName) {
		if(
				nodeName.equals("label") ||
				nodeName.equals("button") ||
				nodeName.equals("textfield") ||
				nodeName.equals("select") ||
				nodeName.equals("orderedlayout")
		) return true;
		return false;
	}

	public static Component createComponent(Node uidl, Client cli) {
		Component c = null;
		String nodeName = uidl.getNodeName();
		if(nodeName.equals("label")) {
			c = new TkLabel(uidl, cli);
		} else if(nodeName.equals("orderedlayout")) {
			c = new TkOrderedLayout(uidl, cli);
		} else if(nodeName.equals("button")) {
			c = new TkButton(uidl, cli);
		} else if(nodeName.equals("textfield")) {
			c = new TkTextField(uidl, cli);
		} else if(nodeName.equals("select")) {
			c = new TkLegacyComponent(uidl, cli);
		} else {
			c = new TkUnknown(uidl, cli);
		}
		return c;
	}
	
	public void appendTo(ContainerComponent cont) {
		this.parent = cont;
		getClient().registerComponent(this);
		cont.appendChild(this);
	}
	
	public ContainerComponent getParent() {
		return parent;
	}
	
	public Client getClient() {
		if(client == null)
			client = parent.getClient();
		return client;
	}
	
	public static int getIdFromUidl(Node uidl) {
		Node pid = uidl.getAttributes().getNamedItem("id");
		return Integer.parseInt(pid.getNodeValue().substring(3));
	}

	public int getId() {
		return id;
	}

}
