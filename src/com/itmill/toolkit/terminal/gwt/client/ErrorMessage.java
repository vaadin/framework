package com.itmill.toolkit.terminal.gwt.client;

import java.util.Iterator;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class ErrorMessage extends FlowPanel {
	public static final String CLASSNAME = "i-error";
	public ErrorMessage() {
		super();
		setStyleName(CLASSNAME);
	}

	public void updateFromUIDL(UIDL uidl) {
		clear();
		// TODO handle error level indication
		for(Iterator it = uidl.getChildIterator();it.hasNext();) {
			Object child = it.next();
			if (child instanceof String) {
				String errorMessage = (String) child;
				add(new Label(errorMessage));
			} else {
				ErrorMessage childError = new ErrorMessage();
				add(childError);
				childError.updateFromUIDL((UIDL) child);
			}
		}
	}
}
