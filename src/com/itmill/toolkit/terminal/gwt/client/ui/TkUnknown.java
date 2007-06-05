package com.itmill.toolkit.terminal.gwt.client.ui;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.Node;
import com.itmill.toolkit.terminal.gwt.client.Component;
import com.itmill.toolkit.terminal.gwt.client.GwtClient;

public class TkUnknown extends Component {

	private Label l;

	public TkUnknown(Node uidl, GwtClient cli) {
		super(getIdFromUidl(uidl),cli);

		l = new Label("No client side component found for " + uidl.getNodeName());
	}

	public void updateFromUidl(Node n) {
	}

	public Widget getWidget() {
		return l;
	}

}
