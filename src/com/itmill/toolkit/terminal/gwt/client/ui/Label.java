package com.itmill.toolkit.terminal.gwt.client.ui;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.itmill.toolkit.terminal.gwt.client.Client;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class Label extends Composite implements Paintable{
	
	com.google.gwt.user.client.ui.Label caption = new com.google.gwt.user.client.ui.Label();;
	HTML content = new HTML();
	
	public Label() {
		VerticalPanel panel = new VerticalPanel();
		panel.add(caption);
		panel.add(content);
		initWidget(panel);
		setStyleName("itmtk-label");
		caption.setStyleName("itmtk-label-caption");
		content.setStyleName("itmtk-label-content");
	}
	
	public void updateFromUIDL(UIDL uidl, Client client) {
		try{
			UIDL child = uidl.getChildUIDL(0).getChildUIDL(0);
			if(child.hasAttribute("xmlns") && 
					child.getStringAttribute("xmlns").
					equals("http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd")) {
				setContent(child.getChildString(0));
			} else {
				setContent("Terminals Label compoent can't handle this content type.");
			}
		} catch (Exception e) {
			setContent(uidl.getChildString(0));
		}
		if(uidl.hasAttribute("caption"))
			setCaption(uidl.getStringAttribute("caption"));
		else
			caption.setVisible(false);
	}
	
	public void setContent(String c) {
		content.setHTML(c);
	}
	public void setCaption(String c) {
		caption.setText(c);
	}
}
