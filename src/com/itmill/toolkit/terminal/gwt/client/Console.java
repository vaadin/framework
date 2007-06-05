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
	}

	public void error(String msg) {
		rp.add((new Label(msg)));
	}

	public void printObject(Object msg) {
		rp.add((new Label(msg.toString())));
	}

	
//	public native void log(String msg) 
///*-{
//	console.log(msg);
//}-*/;
//
//	public native void warn(String msg) 
//	/*-{
//		console.warn(msg);
//	}-*/;
//
//	public native void error(String msg) 
//	/*-{
//		console.error(msg);
//	}-*/;
//
//	public native void printObject(Object msg) 
//	/*-{
//		console.dir(msg);
//	}-*/;

}
