package com.itmill.toolkit.terminal.gwt.client;

import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.NamedNodeMap;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;

public class TkTextField extends Component {


	private TextBox tb;

	public TkTextField(Node uidl, GwtClient cli) {
		super(getIdFromUidl(uidl),cli);

		tb = new TextBox();
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
		tb.setText(text);
		if(description != null)
			tb.setTitle(description);
	}

	public Widget getWidget() {
		return tb;
	}

}
