package com.itmill.toolkit.terminal.gwt.client.ui;

import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.itmill.toolkit.terminal.gwt.client.Client;

abstract class ContainerComponent extends Component {

	public ContainerComponent(int id, Client c) {
		super(id, c);
	}

	abstract void appendChild(Component c);
	
	public void renderChildNodes(Node n, Client cli) {
		NodeList children = n.getChildNodes();
		for(int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if(Component.isComponent(child.getNodeName())) {
				Component c = Component.createComponent(child, cli);
				c.appendTo(this);
			}
		}
	}

}
