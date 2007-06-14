package com.itmill.toolkit.terminal.gwt.client.ui;

import com.google.gwt.user.client.DOM;
import com.itmill.toolkit.terminal.gwt.client.Client;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

/**
 * This class represents a password field.
 * 
 * @author IT Mill Ltd.
 *
 */
public class TkPasswordField extends TkTextField {
	
	public TkPasswordField() {
		super(DOM.createInputPassword());
	}
	
	public void updateFromUIDL(UIDL uidl, Client client) {
		// Call parent renderer explicitly
		super.updateFromUIDL(uidl, client);
		
	}
	
}
