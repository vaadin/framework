package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.HashMap;
import java.util.Iterator;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.Caption;
import com.itmill.toolkit.terminal.gwt.client.CaptionWrapper;
import com.itmill.toolkit.terminal.gwt.client.Container;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

/**
 * Custom Layout implements complex layout defined with HTML template.
 * 
 * @author IT Mill
 * 
 */
public class ICustomLayout extends ComplexPanel implements Paintable, Container {

	/** Location-name to containing element in DOM map */
	private HashMap locationToElement = new HashMap();

	/** Location-name to contained widget map */
	private HashMap locationToWidget = new HashMap();

	/** Widget to captionwrapper map */
	private HashMap widgetToCaptionWrapper = new HashMap();

	/** Currently rendered style */
	String currentTemplate;

	/** Unexecuted scripts loaded from the template */
	private String scripts = "";

	/** Paintable ID of this paintable */
	private String pid;

	private ApplicationConnection client;

	public ICustomLayout() {
		setElement(DOM.createDiv());
	}

	/**
	 * Sets widget to given location. 
	 * 
	 * If location already contains a widget it will be removed.
	 * 
	 * @param widget Widget to be set into location.
	 * @param location location name where widget will be added
	 * 
	 * @throws IllegalArgumentException
	 *             if no such location is found in the layout.
	 */
	public void setWidget(Widget widget, String location) {

		if (widget == null)
			return;

		// If no given location is found in the layout, and exception is throws
		Element elem = (Element) locationToElement.get(location);
		if (elem == null && hasTemplate()) {
			throw new IllegalArgumentException("No location " + location
					+ " found");
		}

		// Get previous widget
		Widget previous = (Widget) locationToWidget.get(location);
		// NOP if given widget already exists in this location
		if (previous == widget)
			return;
		remove(previous);
		
		// if template is missing add element in order
		if(!hasTemplate())
			elem = getElement();

		// Add widget to location
		super.add(widget, elem);
		locationToWidget.put(location, widget);
	}

	/** Update the layout from UIDL */
	public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
		this.client = client;
		// Client manages general cases
		if (client.updateComponent(this, uidl, false))
			return;

		// Update PID
		pid = uidl.getId();

		if(!hasTemplate()) {
			// Update HTML template only once
			initializeHTML(uidl, client);
		}

		// For all contained widgets
		for (Iterator i = uidl.getChildIterator(); i.hasNext();) {
			UIDL uidlForChild = (UIDL) i.next();
			if (uidlForChild.getTag().equals("location")) {
				String location = uidlForChild.getStringAttribute("name");
				Widget child = client.getWidget(uidlForChild.getChildUIDL(0));
				try {
					setWidget(child, location);
					((Paintable) child).updateFromUIDL(uidlForChild
							.getChildUIDL(0), client);
				} catch (IllegalArgumentException e) {
					// If no location is found, this component is not visible
				}
			}
		}
	}

	/** Initialize HTML-layout. */
	private void initializeHTML(UIDL uidl, ApplicationConnection client) {

		String newTemplate = uidl.getStringAttribute("template");

		// Get the HTML-template from client
		String template = client.getResource("layouts/" + newTemplate + ".html");
		if (template == null) {
			template = "<em>Layout file layouts/" + newTemplate + ".html is missing. Components will be drawn for debug purposes.</em>";
		} else {
			currentTemplate = newTemplate;
		}

		// Connect body of the template to DOM
		template = extractBodyAndScriptsFromTemplate(template);
		DOM.setInnerHTML(getElement(), template);

		// Remap locations to elements
		locationToElement.clear();
		scanForLocations(getElement());

		// Remap image srcs in layout
		Widget parent = getParent();
		while (parent != null && !(parent instanceof IView))
			parent = parent.getParent();
		if (parent != null && ((IView) parent).getTheme() != null)
			;
		prefixImgSrcs(getElement(), "../ITK-INF/themes/" + ((IView) parent).getTheme()
				+ "/layouts/");
	}
	
	private boolean hasTemplate() {
		if(currentTemplate == null)
			return false;
		else
			return true;
	}

	/** Collect locations from template */
	private void scanForLocations(Element elem) {

		String location = getLocation(elem);
		if (location != null) {
			locationToElement.put(location, elem);
			DOM.setInnerHTML(elem, "");
		} else {
			int len = DOM.getChildCount(elem);
			for (int i = 0; i < len; i++) {
				scanForLocations(DOM.getChild(elem, i));
			}
		}
	}

	/** Get the location attribute for given element */
	private static native String getLocation(Element elem) /*-{
	 return elem.getAttribute("location");
	 }-*/;

	/** Scripts are evaluated when the document has been rendered */
	protected void onLoad() {
		super.onLoad();
		// Evaluate scripts only once
		if (scripts != null) {
			eval(scripts);
			scripts = null;
		}
	}

	/** Evaluate given script in browser document */
	private static native void eval(String script) /*-{
	 try { 
	 eval("{ var document = $doc; var window = $wnd; "+ script + "}");
	 } catch (e) {
	 }
	 }-*/;

	/** Prefix all img tag srcs with given prefix. */
	private static native void prefixImgSrcs(Element e, String srcPrefix) /*-{
	 try {
	 var divs = e.getElementsByTagName("img"); 
	 var base = "" + $doc.location;
	 var l = base.length-1;
	 while (l >= 0 && base.charAt(l) != "/") l--;
	 base = base.substring(0,l+1);
	 for (var i = 0; i < divs.length; i++) {
	 var div = divs[i];
	 var src = div.getAttribute("src");
	 if (src.indexOf(base) == 0) div.setAttribute("src",base + srcPrefix + src.substring(base.length));
	 else if (src.indexOf("http") != 0) div.setAttribute("src",srcPrefix + src);
	 }			
	 } catch (e) { alert(e + " " + srcPrefix);}
	 }-*/;

	/**
	 * Extract body part and script tags from raw html-template.
	 * 
	 * Saves contents of all script-tags to private property: scripts. Returns
	 * contents of the body part for the html without script-tags. Also replaces
	 * all _UID_ tags with an unique id-string.
	 * 
	 * @param html
	 *            Original HTML-template received from server
	 * @return html that is used to create the HTMLPanel.
	 */
	private String extractBodyAndScriptsFromTemplate(String html) {

		// Replace UID:s
		html = html.replaceAll("_UID_", pid + "__");

		// Exctract script-tags
		scripts = "";
		int endOfPrevScript = 0;
		int nextPosToCheck = 0;
		String lc = html.toLowerCase();
		String res = "";
		int scriptStart = lc.indexOf("<script", nextPosToCheck);
		while (scriptStart > 0) {
			res += html.substring(endOfPrevScript, scriptStart);
			scriptStart = lc.indexOf(">", scriptStart);
			int j = lc.indexOf("</script>", scriptStart);
			scripts += html.substring(scriptStart + 1, j) + ";";
			nextPosToCheck = endOfPrevScript = j + "</script>".length();
			scriptStart = lc.indexOf("<script", nextPosToCheck);
		}
		res += html.substring(endOfPrevScript);

		// Extract body
		html = res;
		lc = html.toLowerCase();
		int startOfBody = lc.indexOf("<body");
		if (startOfBody < 0) {
			res = html;
		} else {
			res = "";
			startOfBody = lc.indexOf(">", startOfBody) + 1;
			int endOfBody = lc.indexOf("</body>", startOfBody);
			if (endOfBody > startOfBody)
				res = html.substring(startOfBody, endOfBody);
			else
				res = html.substring(startOfBody);
		}

		return res;
	}

	/** Replace child components */
	public void replaceChildComponent(Widget from, Widget to) {
		String location = getLocation(from);
		if (location == null)
			throw new IllegalArgumentException();
		setWidget(to, location);
	}

	/** Does this layout contain given child*/
	public boolean hasChildComponent(Widget component) {
		return locationToWidget.containsValue(component);
	}

	/** Update caption for given widget */
	public void updateCaption(Paintable component, UIDL uidl) {
		CaptionWrapper wrapper = (CaptionWrapper) widgetToCaptionWrapper.get(component);
		if (Caption.isNeeded(uidl)) {
			if (wrapper == null) {
				String loc = getLocation((Widget) component);
				super.remove((Widget) component);
				wrapper = new CaptionWrapper(component);
				super.add(wrapper, (Element) locationToElement.get(loc));
				widgetToCaptionWrapper.put(component, wrapper);
			}
			wrapper.updateCaption(uidl);
		} else {
			if (wrapper != null) { 
				String loc = getLocation((Widget) component);
				super.remove(wrapper);
				super.add((Widget) wrapper.getPaintable(), (Element) locationToElement.get(loc));
				widgetToCaptionWrapper.remove(component);
			}
		}
	}

	/** Get the location of an widget */
	public String getLocation(Widget w) {
		for (Iterator i = locationToWidget.keySet().iterator(); i.hasNext();) {
			String location = (String) i.next();
			if (locationToWidget.get(location) == w)
				return location;
		}
		return null;
	}

	/** Removes given widget from the layout */
	public boolean remove(Widget w) {
		client.unregisterPaintable((Paintable) w);
		String location = getLocation(w);
		if (location != null)
			locationToWidget.remove(location);
		CaptionWrapper cw = (CaptionWrapper) widgetToCaptionWrapper.get(w);
		if (cw != null) {
			widgetToCaptionWrapper.remove(w);
			return super.remove(cw);
		} else if(w != null)
			return super.remove(w);
		return false;
	}

	/** Adding widget without specifying location is not supported */
	public void add(Widget w) {
		throw new UnsupportedOperationException();
	}

	/** Clear all widgets from the layout */
	public void clear() {
		super.clear();
		locationToWidget.clear();
		widgetToCaptionWrapper.clear();
	}
}
