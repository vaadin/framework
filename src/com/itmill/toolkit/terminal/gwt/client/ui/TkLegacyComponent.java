package com.itmill.toolkit.terminal.gwt.client.ui;

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.Node;
import com.itmill.toolkit.terminal.gwt.client.Component;
import com.itmill.toolkit.terminal.gwt.client.GwtClient;
import com.itmill.toolkit.terminal.gwt.client.LegacyClientWrapper;

public class TkLegacyComponent extends Component {

	private Label l;

	public TkLegacyComponent(Node uidl, GwtClient cli) {
		super(getIdFromUidl(uidl), cli);
		
		// TODO Check if client has legacy client instantiated
		
		l = new Label();
//		updateFromUidl(uidl);	
	}

	public void updateFromUidl(Node n) {
		LegacyClientWrapper lc = client.getLegacyClient();
		
		Element e = l.getElement();
		
		lc.renderUidl(e,n);
	}
	
	public Widget getWidget() {
		return l;
	}

}
