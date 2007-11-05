package com.itmill.toolkit.terminal.gwt.client.ui.richtextarea;

import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusListener;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

/**
 * This class represents a basic text input field with one row.
 * 
 * @author IT Mill Ltd.
 * 
 */
public class IRichTextArea extends Composite implements Paintable,
		ChangeListener, FocusListener {

	/**
	 * The input node CSS classname.
	 */
	public static final String CLASSNAME = "i-richtextarea";

	protected String id;

	protected ApplicationConnection client;

	private boolean immediate = false;

	RichTextArea rta = new RichTextArea();

	RichTextToolbar formatter = new RichTextToolbar(rta);

	public IRichTextArea() {
		FlowPanel fp = new FlowPanel();
		fp.add(formatter);

		rta.setWidth("100%");
		rta.addFocusListener(this);

		fp.add(rta);

		initWidget(fp);
		setStyleName(CLASSNAME);
	}

	public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
		this.client = client;
		id = uidl.getId();

		if (client.updateComponent(this, uidl, true)) {
			return;
		}

		immediate = uidl.getBooleanAttribute("immediate");

		rta.setHTML(uidl.getStringVariable("text"));

	}

	public void onChange(Widget sender) {
		if (client != null && id != null) {
			client.updateVariable(id, "text", rta.getText(), immediate);
		}
	}

	public void onFocus(Widget sender) {

	}

	public void onLostFocus(Widget sender) {
		String html = rta.getHTML();
		client.updateVariable(id, "text", html, immediate);

	}

}
