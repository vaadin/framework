package com.itmill.toolkit.terminal.gwt.client.ui;

import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.Client;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class TkTextField extends Composite implements
		Paintable, ChangeListener {

	String id;

	Client client;
	
	TextBoxBase field;
	Label caption = new Label();

	private VerticalPanel p;
	
	private boolean multiline = false;

	private boolean immediate = false;
	
	public TkTextField() {
		p = new VerticalPanel();
		p.add(caption);
		initWidget(p);
	}

	public void updateFromUIDL(UIDL uidl, Client client) {
		this.client = client;
		id = uidl.getId();
		if(uidl.hasAttribute("immediate") && uidl.getBooleanAttribute("immediate"))
			immediate  = true;
		if(uidl.hasAttribute("caption"))
			caption.setText(uidl.getStringAttribute("caption"));
		else
			caption.setVisible(false);
		if(field != null && (uidl.hasAttribute("rows") && !multiline )) {
			// field type has changed
			p.remove(p.getWidgetIndex(field));
		}
		if(field == null) {
			if(uidl.hasAttribute("rows")) {
				field = new TextArea();
				multiline = true;
			} else {
				field = new TextBox();
			}
			p.add(field);
		}
		if(multiline)
			field.setHeight(uidl.getStringAttribute("height")+"em");
		if(uidl.hasAttribute("cols"))
			field.setWidth(uidl.getStringAttribute("cols")+"em");
			
		field.addChangeListener(this);

	}

	public void onChange(Widget sender) {
		client.updateVariable(id, "text", field.getText() , immediate);
	}
}
