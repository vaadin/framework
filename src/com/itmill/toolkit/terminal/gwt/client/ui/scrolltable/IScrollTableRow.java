package com.itmill.toolkit.terminal.gwt.client.ui.scrolltable;

import java.util.Iterator;
import java.util.Vector;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.Client;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class IScrollTableRow extends Panel {
	
	Vector childWidgets = new Vector();
	
	private IScrollTableRow() {
		setElement(DOM.createElement("tr"));
	}
	
	public IScrollTableRow(UIDL uidl, Client client) {
		this();
		if(uidl.hasAttribute("caption"))
			addCell(uidl.getStringAttribute("caption"));
		Iterator cells = uidl.getChildIterator();
		while(cells.hasNext()) {
			Object cell = cells.next();
			if (cell instanceof String) {
				addCell(cell.toString());
			} else {
			 	Widget cellContent = client.getWidget((UIDL) cell);
			 	(( Paintable) cellContent).updateFromUIDL((UIDL) cell, client);
			}
		}
	}
	
	public void addCell(String text) {
		addCell(new Label(text));
	}
	
	public void addCell(Widget w) {
		Element td = DOM.createTD();
		Element container = DOM.createDiv();
		DOM.setAttribute(container, "className", "iscrolltable-cellContent");
		DOM.appendChild(td, container);
		DOM.appendChild(getElement(), td);
		adopt(w, container);
		childWidgets.add(w);
	}

	public Iterator iterator() {
		return childWidgets.iterator();
	}

	public boolean remove(Widget w) {
		// TODO Auto-generated method stub
		return false;
	}

}
