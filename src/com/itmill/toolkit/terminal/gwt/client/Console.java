package com.itmill.toolkit.terminal.gwt.client;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

public final class Console {
	
	private RootPanel rp;

	public Console(RootPanel rp) {
		this.rp = rp;
	}
	
	public void log(String msg) {
		rp.add(new Label(msg));
		System.out.println(msg);
	}

	public void error(String msg) {
		rp.add((new Label(msg)));
		System.out.println(msg);
	}

	public void printObject(Object msg) {
		rp.add((new Label(msg.toString())));
	}
	
	public void dirUIDL(UIDL u) {
		rp.add(u.print_r());
	}
}
