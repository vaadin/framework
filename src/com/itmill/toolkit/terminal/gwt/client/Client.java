package com.itmill.toolkit.terminal.gwt.client;

import java.util.HashMap;
import java.util.Vector;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 * 
 * TODO IDEA: Should be extend Widget here !?!?!
 */
public class Client implements EntryPoint {

	private String appUri = "http://127.0.0.1:8080/tk/HelloWorld";

	// TODO remove repaintAll things start to pile up
	private RequestBuilder rb = new RequestBuilder(RequestBuilder.POST, appUri
			+ "/UIDL/?repaintAll=1&");

	private Console console;

	private Vector pendingVariables = new Vector();

	private HashMap paintables = new HashMap();

	private int requestCount = 0;

	private WidgetFactory widgetFactory = new DefaultWidgetFactory();

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

		console = new Console(RootPanel.get("itmtk-loki"));

		console.log("Makin fake UIDL Request to fool servlet of an app init");
		RequestBuilder rb2 = new RequestBuilder(RequestBuilder.GET, appUri);
		try {
			rb2.sendRequest("", new RequestCallback() {

				public void onResponseReceived(Request request,
						Response response) {
					console
							.log("Got fake response... sending initial UIDL request");
					makeUidlRequest("repaintAll=1");
				}

				public void onError(Request request, Throwable exception) {
					// TODO Auto-generated method stub

				}

			});
		} catch (RequestException e1) {
			e1.printStackTrace();
		}

	}

	private void makeUidlRequest(String requestData) {
		console.log("Making UIDL Request");
		rb = new RequestBuilder(RequestBuilder.GET, appUri
				+ "/UIDL/?requestId=" + (++requestCount) + "&" + requestData);
		try {
			rb.sendRequest(requestData, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					console.error("Got error");
				}

				public void onResponseReceived(Request request,
						Response response) {
					handleReceivedJSONMessage(response);
				}

			});
			console.log("Request sent");

		} catch (RequestException e) {
			console.error(e.getMessage());
		}
	}

	private void handleReceivedJSONMessage(Response response) {
		JSONValue json = JSONParser
				.parse(response.getText().substring(3) + "}");

		// Process changes
		JSONArray changes = (JSONArray) ((JSONObject) json).get("changes");
		for (int i = 0; i < changes.size(); i++) {
			try {
				UIDL change = new UIDL((JSONArray) changes.get(i));
				console.log("Received the following change: " + change);
				UIDL uidl = change.getChildUIDL(0);
				Paintable paintable = getPaintable(uidl.getId());
				if (paintable != null)
					paintable.updateFromUIDL(uidl, this);
				else {
					if (!uidl.getTag().equals("window"))
						throw new IllegalStateException("Received update for "
								+ uidl.getTag()
								+ ", but there is no such paintable ("
								+ uidl.getId() + ") registered yet.");
					Widget window = createWidgetFromUIDL(uidl);
					// We should also handle other windows 
					RootPanel.get("itmtk-ajax-window").add(window);
				}

			} catch (Throwable e) {
				e.printStackTrace();
			}

		}
	}

	public void registerPaintable(String id, Paintable paintable) {
		paintables.put(id, paintable);
	}

	public Paintable getPaintable(String id) {
		return (Paintable) paintables.get(id);
	}

	public Widget createWidgetFromUIDL(UIDL uidlForChild) {
		Widget w = widgetFactory.createWidget(uidlForChild.getTag(), null);
		if (w instanceof Paintable) {
			registerPaintable(uidlForChild.getId(), (Paintable) w);
			((Paintable)w).updateFromUIDL(uidlForChild, this);
		}
		return w;
	}

	private void addVariableToQueue(String paintableId, String variableName,
			String encodedValue, boolean immediate) {
		String id = paintableId + "_" + variableName;
		for (int i = 0; i < pendingVariables.size(); i += 2)
			if ((pendingVariables.get(i)).equals(id)) {
				pendingVariables.remove(i);
				pendingVariables.remove(i);
				break;
			}
		pendingVariables.add(id);
		pendingVariables.add(encodedValue);
		if (immediate)
			sendPendingVariableChanges();
	}

	public void sendPendingVariableChanges() {
		StringBuffer req = new StringBuffer();

		for (int i = 0; i < pendingVariables.size(); i++) {
			req.append(pendingVariables.get(i++));
			req.append("=");
			req.append(pendingVariables.get(i));
		}

		pendingVariables.clear();
		makeUidlRequest(req.toString());
	}

	private String escapeString(String value) {
		// TODO
		return value;
	}

	public void updateVariable(String paintableId, String variableName,
			String newValue, boolean immediate) {
		addVariableToQueue(paintableId, variableName, escapeString(newValue),
				immediate);
	}

	public void updateVariable(String paintableId, String variableName,
			int newValue, boolean immediate) {
		addVariableToQueue(paintableId, variableName, "" + newValue, immediate);
	}

	public void updateVariable(String paintableId, String variableName,
			boolean newValue, boolean immediate) {
		addVariableToQueue(paintableId, variableName, newValue ? "true"
				: "false", immediate);
	}

	public WidgetFactory getWidgetFactory() {
		return widgetFactory;
	}

	public void setWidgetFactory(WidgetFactory widgetFactory) {
		this.widgetFactory = widgetFactory;
	}

}
