package com.itmill.toolkit.terminal.gwt.client;

import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.itmill.toolkit.terminal.gwt.client.ui.IWindow;

public final class DebugConsole extends IWindow implements Console {

	private Panel panel;

	public DebugConsole() {
		super();
		panel = new FlowPanel();
		ScrollPanel p = new ScrollPanel();
		p.add(panel);
		this.setWidget(p);
		this.setCaption("Debug window");
		minimize();
		show();
	}

	private void minimize() {
		// TODO stack to bottom (create window manager of some sort)
		setPixelSize(60, 60);
		setPopupPosition(Window.getClientWidth() - 80,
				Window.getClientHeight() - 80);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.itmill.toolkit.terminal.gwt.client.Console#log(java.lang.String)
	 */
	public void log(String msg) {
		panel.add(new HTML(msg));
		System.out.println(msg);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.itmill.toolkit.terminal.gwt.client.Console#error(java.lang.String)
	 */
	public void error(String msg) {
		panel.add((new HTML(msg)));
		System.out.println(msg);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.itmill.toolkit.terminal.gwt.client.Console#printObject(java.lang.Object)
	 */
	public void printObject(Object msg) {
		panel.add((new Label(msg.toString())));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.itmill.toolkit.terminal.gwt.client.Console#dirUIDL(com.itmill.toolkit.terminal.gwt.client.UIDL)
	 */
	public void dirUIDL(UIDL u) {
		panel.add(u.print_r());
	}

	public void setSize(Event event, boolean updateVariables) {
		super.setSize(event, false);
	}

}
