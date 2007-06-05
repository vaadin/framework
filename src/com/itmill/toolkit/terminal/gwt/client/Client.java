package com.itmill.toolkit.terminal.gwt.client;

import java.util.HashMap;
import java.util.Vector;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;
import com.itmill.toolkit.terminal.gwt.client.ui.RootWindow;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Client implements EntryPoint {

	private String appUri = "http://localhost:8080/tk/HelloWorld";
	
	// TODO remove repaintAll things start to pile up
	private RequestBuilder rb = new RequestBuilder(RequestBuilder.POST, appUri + "/UIDL/?repaintAll=1&");
	
	private Console console;

	private RootWindow rw;
	
	private Vector pendingVariables = new Vector();
	
	private HashMap components = new HashMap();
	
	private int requestCount = 0;

	private LegacyClientWrapper lClient;

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		
		console = new Console(RootPanel.get("itmtk-loki"));
		
		console.log("muutos");

		console.log("Starting app");
		
		console.log("Makin fake UIDL Request to fool servlet of an app init");
		RequestBuilder rb2 = new RequestBuilder(RequestBuilder.GET, appUri);
		try {
			rb2.sendRequest("", new RequestCallback() {
			
				public void onResponseReceived(Request request, Response response) {
					console.log("Got fake response... sending initial UIDL request");
					makeUidlRequest("repaintAll=1");
				}
			
				public void onError(Request request, Throwable exception) {
					// TODO Auto-generated method stub
			
				}
			
			});
		} catch (RequestException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}

	private void makeUidlRequest(String requestData) {
		console.log("Making UIDL Request");
		rb = new RequestBuilder(RequestBuilder.GET, appUri + "/UIDL/?requestId=" + (++requestCount) + "&" + requestData);
		try {
			rb.sendRequest(requestData, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					console.error("Got error");
				}
				public void onResponseReceived(Request request, Response response) {
					console.log("Got response:");
					Document doc = XMLParser.parse(response.getText());
					console.log(doc.toString());
					handleUIDL(doc);
				}
			});
			console.log("Request sent");
			
		} catch (RequestException e) {
			console.error(e.getMessage());
		}
	}
	
	private void handleUIDL(Document doc) {
		NodeList changes = doc.getElementsByTagName("change");
		
		for(int i = 0; i < changes.getLength(); i++) {
			applyChange(changes.item(i).getFirstChild());
		}
	}
	
	private void applyChange(Node n) {
		if(n.getNodeName().equals("window")) {
			console.log("Rendering main window");
			rw = new RootWindow(n, this);
			rw.setClient(this);
		} else {
			int pid = Component.getIdFromUidl(n);
			console.log("Updating node: " + n.getNodeName() + ", PID:"+pid);
			Component c = getPaintable(pid);
			c.updateFromUidl(n);
		}
	}

	/**
	 * Queues a changed variable to be sent to server
	 * 
	 * @param variable
	 */
	public void updateVariable(Variable variable) {
		// remove variable first so we will maintain the correct order (in case of "double change")
		pendingVariables.remove(variable);
		pendingVariables.add(variable);
	}

	/**
	 * Sends queued variables to server
	 *
	 */
	public void flushVariables() {

	    StringBuffer sb = new StringBuffer();

		int i = 0;
	    while (!pendingVariables.isEmpty()) {
			Variable v = (Variable) pendingVariables.lastElement();
			pendingVariables.removeElement(v);

			if (i > 0) {
				sb.append("&");
			}
			// encode the characters in the name
			String encodedName = URL.encodeComponent(v.getId());
			sb.append(encodedName);
			sb.append("=");
	    
			// encode the characters in the value
			String encodedValue = URL.encodeComponent(v.getEncodedValue());
			sb.append(encodedValue);
	    }
	    
	    String buf = sb.toString();
		
		console.log("Making following request to server:");
		console.log(buf);
		
		makeUidlRequest(buf);
	}

	public void registerComponent(Component component) {
		components.put(""+component.getId(), component );
	}
	
	public Component getPaintable(int pid) {
		return (Component) components.get(""+pid);
	}
	
	public LegacyClientWrapper getLegacyClient() {
		if(lClient == null)
			lClient = new LegacyClientWrapper();
		return lClient;
	}
}

