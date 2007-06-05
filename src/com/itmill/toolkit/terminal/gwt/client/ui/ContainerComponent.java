package com.itmill.toolkit.terminal.gwt.client.ui;

import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.itmill.toolkit.terminal.gwt.client.Component;
import com.itmill.toolkit.terminal.gwt.client.GwtClient;

abstract class ContainerComponent extends Component {

	public ContainerComponent(int id, GwtClient c) {
		super(id, c);
	}

	abstract void appendChild(Component c);
	
	public void renderChildNodes(Node n, GwtClient cli) {
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
