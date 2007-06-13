package com.itmill.toolkit.terminal.gwt.client;

import java.util.Date;
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
import com.itmill.toolkit.terminal.gwt.client.ui.TkButton;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 * 
 * TODO IDEA: Should be extend Widget here !?!?!
 */
public class Client implements EntryPoint {

	private String appUri;

	// TODO remove repaintAll until things start to pile up
	private RequestBuilder rb = new RequestBuilder(RequestBuilder.POST, appUri
			+ "/UIDL/?repaintAll=1&");

	public Console console;

	private Vector pendingVariables = new Vector();

	private HashMap paintables = new HashMap();

	private int requestCount = 0;

	private WidgetFactory widgetFactory = new DefaultWidgetFactory();

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		
		appUri = getAppUri();

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
	
	private native String getAppUri()/*-{
		return $wnd.itmtk.appUri;
	}-*/;

	private void makeUidlRequest(String requestData) {
		console.log("Making UIDL Request with params: " + requestData);
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

		} catch (RequestException e) {
			console.error(e.getMessage());
		}
	}

	private void handleReceivedJSONMessage(Response response) {
		Date start = new Date();
		String jsonText = response.getText().substring(3) + "}";
		JSONValue json = JSONParser.parse(jsonText);
		// Process changes
		JSONArray changes = (JSONArray) ((JSONObject) json).get("changes");
		for (int i = 0; i < changes.size(); i++) {
			try {
				UIDL change = new UIDL((JSONArray) changes.get(i));
				try {
					console.dirUIDL(change);
				} catch (Exception e) {
					console.log(e.getMessage());
					// TODO: dir doesn't work in any browser although it should work (works in hosted mode)
					// it partially did at some part but now broken.
				}
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
					// TODO We should also handle other windows 
					RootPanel.get("itmtk-ajax-window").add(window);
				}

			} catch (Throwable e) {
				e.printStackTrace();
			}

		}
		long prosessingTime = (new Date().getTime()) - start.getTime();
		console.log(" Processing time was " + String.valueOf(prosessingTime) + "ms for "+jsonText.length()+" characters of JSON");

	}

	public void registerPaintable(String id, Paintable paintable) {
		paintables.put(id, paintable);
	}

	public Paintable getPaintable(String id) {
		return (Paintable) paintables.get(id);
	}

	public Widget createWidgetFromUIDL(UIDL uidlForChild) {
		Widget w = widgetFactory.createWidget(uidlForChild, null);
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
			req.append("&");
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
	public void updateVariable(String paintableId, String variableName, Object[] values, boolean immediate) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < values.length; i++) {
			if(i > 0)
				buf.append(",");
			buf.append(escapeString(values[i].toString()));
		}
		addVariableToQueue("array:" + paintableId, variableName, buf.toString(), immediate);
	}


	public WidgetFactory getWidgetFactory() {
		return widgetFactory;
	}

	public void setWidgetFactory(WidgetFactory widgetFactory) {
		this.widgetFactory = widgetFactory;
	}

	public void repaintComponent(Widget component, UIDL uidl) {
		Widget parent = component.getParent();
		while (parent != null && !(parent instanceof Layout)) parent = parent.getParent();
		if (parent != null && ((Layout)parent).hasChildComponent(component)) {
			((Layout) parent).replaceChildComponent(component,createWidgetFromUIDL(uidl));
		}
		
	}


}
