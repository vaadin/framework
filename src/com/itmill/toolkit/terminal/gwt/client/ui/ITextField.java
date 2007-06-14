package com.itmill.toolkit.terminal.gwt.client.ui;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.FocusListener;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.Client;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

/**
 * This class represents a basic text input field with one row.
 * 
 * @author IT Mill Ltd.
 *
 */
public class ITextField extends TextBoxBase implements
		Paintable, ChangeListener, FocusListener {
	
	/**
	 * The input node CSS classname.
	 */
	private static final String CLASSNAME = "i-textfield";
	
	/**
	 * This CSS classname is added to the input node on hover.
	 */
	private static final String CLASSNAME_FOCUS = "i-textfield-focus";

	private String id;

	private Client client;

	private boolean immediate = false;
	
	public ITextField() {
		this(DOM.createInputText());
	}
	
	protected ITextField(Element node) {
		super(node);
		setStyleName(CLASSNAME);
		addChangeListener(this);
		addFocusListener(this);
	}

	public void updateFromUIDL(UIDL uidl, Client client) {
		this.client = client;
		id = uidl.getId();
		
		if(client.updateComponent(this, uidl, true))
			return;
		
		immediate  = uidl.getBooleanAttribute("immediate");

		if(uidl.hasAttribute("cols"))
			setWidth(uidl.getStringAttribute("cols")+"em");
		
		setText(uidl.getStringVariable("text"));
		

	}

	public void onChange(Widget sender) {
		client.updateVariable(id, "text", getText() , immediate);
	}

	public void onFocus(Widget sender) {
		addStyleName(CLASSNAME_FOCUS);	
	}

	public void onLostFocus(Widget sender) {
		removeStyleName(CLASSNAME_FOCUS);
	}
}
