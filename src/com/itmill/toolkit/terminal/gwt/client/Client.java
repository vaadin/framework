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
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

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
				+ "/UIDL/?requestId=" + (Math.random()) + "&" + requestData);
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
					// TODO: dir doesn't work in any browser although it should
					// work (works in hosted mode)
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
					Widget window = widgetFactory.createWidget(uidl);
					registerPaintable(uidl.getId(), (Paintable) window);
					((Paintable) window).updateFromUIDL(uidl, this);

					// TODO We should also handle other windows
					RootPanel.get("itmtk-ajax-window").add(window);
				}

			} catch (Throwable e) {
				e.printStackTrace();
			}

		}
		long prosessingTime = (new Date().getTime()) - start.getTime();
		console.log(" Processing time was " + String.valueOf(prosessingTime)
				+ "ms for " + jsonText.length() + " characters of JSON");

	}

	public void registerPaintable(String id, Paintable paintable) {
		paintables.put(id, paintable);
	}

	public Paintable getPaintable(String id) {
		return (Paintable) paintables.get(id);
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

	public void updateVariable(String paintableId, String variableName,
			Object[] values, boolean immediate) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < values.length; i++) {
			if (i > 0)
				buf.append(",");
			buf.append(escapeString(values[i].toString()));
		}
		addVariableToQueue("array:" + paintableId, variableName,
				buf.toString(), immediate);
	}

	public WidgetFactory getWidgetFactory() {
		return widgetFactory;
	}

	public void setWidgetFactory(WidgetFactory widgetFactory) {
		this.widgetFactory = widgetFactory;
	}

	public static Layout getParentLayout(Widget component) {
		Widget parent = component.getParent();
		while (parent != null && !(parent instanceof Layout))
			parent = parent.getParent();
		if (parent != null && ((Layout) parent).hasChildComponent(component))
			return (Layout) parent;
		return null;
	}

	/**
	 * Update generic component features.
	 * 
	 * <h2>Selecting correct implementation</h2>
	 * 
	 * <p>
	 * The implementation of a component depends on many properties, including
	 * styles, component features, etc. Sometimes the user changes those
	 * properties after the component has been created. Calling this method in
	 * the beginning of your updateFromUIDL -method automatically replaces your
	 * component with more appropriate if the requested implementation changes.
	 * </p>
	 * 
	 * <h2>Caption, icon, error messages and description</h2>
	 * 
	 * <p>
	 * Component can delegate management of caption, icon, error messages and
	 * description to parent layout. This is optional an should be decided by
	 * component author
	 * </p>
	 * 
	 * <h2>Component visibility and disabling</h2>
	 * 
	 * This method will manage component visibility automatically and if
	 * component is an instanceof FocusWidget, also handle component disabling
	 * when needed.
	 * 
	 * @param currentWidget
	 *            Current widget that might need replacement
	 * @param uidl
	 *            UIDL to be painted
	 * @param manageCaption
	 *            True if you want to delegate caption, icon, description and
	 *            error message management to parent.
	 * 
	 * @return Returns true iff no further painting is needed by caller
	 */
	public boolean updateComponent(Widget component, UIDL uidl,
			boolean manageCaption) {

		// Switch to correct implementation if neede
		if (!widgetFactory.isCorrectImplementation(component, uidl)) {
			Layout parent = getParentLayout(component);
			if (parent != null) {
				Widget w = widgetFactory.createWidget(uidl);
				registerPaintable(uidl.getId(), (Paintable) w);
				parent.replaceChildComponent(component, w);
				((Paintable) w).updateFromUIDL(uidl, this);
				return true;
			}
		}

		// Set captions
		// TODO Manage Error messages
		if (manageCaption) {
			Layout parent = getParentLayout(component);
			if (parent != null)
				parent.updateCaption(component, uidl);
		}

		// Visibility, Disabling and read-only status
		if (component instanceof FocusWidget) {
			boolean enabled = true;
			if(uidl.hasAttribute("disabled"))
				enabled = !uidl.getBooleanAttribute("disabled");
			else if(uidl.hasAttribute("readonly")) 
				enabled = !uidl.getBooleanAttribute("readonly");
			((FocusWidget) component).setEnabled(enabled);
		}
		boolean visible = !uidl.getBooleanAttribute("invisible");
		component.setVisible(visible);
		if (!visible)
			return true;

		return false;
	}

	/**
	 * Get either existing or new widget for given UIDL.
	 * 
	 * If corresponding paintable has been previously painted, return it.
	 * Otherwise create and register a new widget from UIDL. Caller must update
	 * the returned widget from UIDL after it has been connected to parent.
	 * 
	 * @param uidl
	 *            UIDL to create widget from.
	 * @return Either existing or new widget corresponding to UIDL.
	 */
	public Widget getWidget(UIDL uidl) {
		String id = uidl.getId();
		Widget w = (Widget) getPaintable(id);
		if (w != null)
			return w;
		w = widgetFactory.createWidget(uidl);
		registerPaintable(id, (Paintable) w);
		return w;
	}

}
