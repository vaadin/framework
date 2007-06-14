package com.itmill.toolkit.terminal.gwt.client.ui;

import com.google.gwt.user.client.DOM;

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
	
}
