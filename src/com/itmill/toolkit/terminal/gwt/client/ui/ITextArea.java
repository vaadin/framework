package com.itmill.toolkit.terminal.gwt.client.ui;

import com.google.gwt.user.client.DOM;
import com.itmill.toolkit.terminal.gwt.client.Client;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

/**
 * This class represents a multiline textfield (textarea).
 * 
 * @author IT Mill Ltd.
 *
 */
public class ITextArea extends ITextField {

	public ITextArea() {
		super(DOM.createTextArea());
	}
	
	public void updateFromUIDL(UIDL uidl, Client client) {
		// Call parent renderer explicitly
		super.updateFromUIDL(uidl, client);
		
		if(uidl.hasAttribute("rows"))
			setRows(new Integer(uidl.getStringAttribute("rows")).intValue());
	}
	
}
