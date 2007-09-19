package com.itmill.toolkit.terminal.gwt.client;

import com.google.gwt.user.client.Element;

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

	/**
	 * Detects if current browser is IE6. Use to isola
	 * 
	 * @return true if IE6
	 */
	public static native boolean isIE6() /*-{
		var browser=$wnd.navigator.appName;
		var version=parseFloat($wnd.navigator.appVersion);
		if (browser=="Microsoft Internet Explorer" && (version < 7) ) {
			return true;
		}
		return false;
	}-*/;
	
	/**
	 * Nulls oncontextmenu function on given element. We need to manually clear
	 * context menu events due bad browsers memory leaks, since we GWT don't
	 * support them.
	 * 
	 * @param el
	 */
	public native static void removeContextMenuEvent(Element el) /*-{
		el.oncontextmenu = null;
	}-*/;

}
