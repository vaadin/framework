package com.itmill.toolkit.terminal.gwt.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Element;
import com.google.gwt.xml.client.Node;

public class LegacyClientWrapper {
	
	JavaScriptObject lClient;
	
	LegacyClientWrapper() {
		instantiateLegacyClient();
	}

	private native void instantiateLegacyClient()/*-{
		var client = new $wnd.itmill.Client();
		client.start();
		this.@com.itmill.gwtclient.client.LegacyClientWrapper::lClient = client;
		debugger;
		
	}-*/;

	public native void renderUidl(Element e, Node n)/*-{
//	var uidlNode = n.hE; //sneeked obf. reference to DOM-node, changes on almost every change
	// should fork GWT and make method for getting jsObject
//	var uidlNode = cQ.gE;
	eval('var uidlNode = cQ.gE;');
	
	//ok so in eval compiler don't look
	var client = this.@com.itmill.gwtclient.client.LegacyClientWrapper::lClient;
	
	debugger;

	client.renderUIDL(uidlNode,e);

	
}-*/;

}
