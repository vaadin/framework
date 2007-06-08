package com.itmill.toolkit.terminal.gwt.client.ui;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.Client;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class TkTextField extends Composite implements
		Paintable {

	String id;

	Client client;
	
	Widget field;
	Label caption = new Label();

	private VerticalPanel p;
	
	public TkTextField() {
		p = new VerticalPanel();
		p.add(caption);
		initWidget(p);
	}

	public void updateFromUIDL(UIDL uidl, Client client) {
		this.client = client;
		id = uidl.getId();
		if(uidl.hasAttribute("caption"))
			caption.setText(uidl.getStringAttribute("caption"));
		else
			caption.setVisible(false);
		if(uidl.hasAttribute("rows")) {
			// TODO textarea
			TextArea ta = new TextArea();
			field = ta;
			if(uidl.hasAttribute("cols"))
				ta.setWidth(uidl.getStringAttribute("cols")+"em");
			ta.setHeight(uidl.getStringAttribute("height")+"em");
			
		} else {
			// one line text field
			TextBox tb = new TextBox();
			field = tb;
			if(uidl.hasAttribute("cols"))
				tb.setWidth(uidl.getStringAttribute("cols")+"em");
		}
		p.add(field);
	}

	public void onClick(Widget sender) {
		if (id == null || client == null)
			return;
		client.updateVariable(id, "state", true, true);
	}
}
