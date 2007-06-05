package com.itmill.toolkit.terminal.gwt.client;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.NamedNodeMap;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;

public class TkLabel extends Component {

	private Label l;

	public TkLabel(Node uidl, GwtClient cli) {
		super(getIdFromUidl(uidl), cli);

		l = new Label();
		updateFromUidl(uidl);
	}

	public void updateFromUidl(Node n) {
		NodeList children = n.getChildNodes();
		String text = "";
		String description = null;
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if(child.getNodeName().equals("description"))
				description  = child.getNodeValue();
			else if(child.getNodeType() == Node.TEXT_NODE)
				text  = child.toString();
		}
		NamedNodeMap attributes = n.getAttributes();
		Node caption = attributes.getNamedItem("caption");
		if(caption != null)
			text = caption.getNodeValue();
		l.setText(text);
		if(description != null)
			l.setTitle(description);
	}

	public Widget getWidget() {
		return l;
	}

}
