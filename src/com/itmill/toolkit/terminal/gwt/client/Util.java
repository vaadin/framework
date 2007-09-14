package com.itmill.toolkit.terminal.gwt.client;

public class Util {

	/**
	 * Helper method for debugging purposes.
	 * 
	 * Stops execution on firefox browsers on a breakpoint.
	 *
	 */
	public static native void browserDebugger() /*-{
		if(window.console)
			debugger;
	}-*/;

	
	
}
