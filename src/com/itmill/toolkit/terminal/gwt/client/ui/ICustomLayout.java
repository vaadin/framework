package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.CaptionWrapper;
import com.itmill.toolkit.terminal.gwt.client.Client;
import com.itmill.toolkit.terminal.gwt.client.Layout;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class ICustomLayout extends ComplexPanel implements Paintable, Layout {

	private HashMap locationToElement = new HashMap();
	
	private HashMap locationToWidget = new HashMap();

	String currentStyle;

	String scripts = "";
	
	String pid;

	public ICustomLayout() {
		setElement(DOM.createDiv());
	}

	public void add(Widget widget, String location) {
		Element elem = (Element) locationToElement.get(location);
		if (elem == null) {
			throw new NoSuchElementException();
		}
		Widget previous = (Widget) locationToWidget.get(location);
		if (widget.equals(previous)) return;
		remove(previous);
		super.add(widget, elem);
		locationToWidget.put(location,widget);
	}

	public void updateFromUIDL(UIDL uidl, Client client) {

		if (client.updateComponent(this, uidl, false))
			return;

		pid = uidl.getId();
		
		updateHTML(uidl, client);

		for (Iterator i = uidl.getChildIterator(); i.hasNext();) {
			UIDL uidlForChild = (UIDL) i.next();
			if (uidlForChild.getTag().equals("location")) {
				String location = uidlForChild.getStringAttribute("name");
				Widget child = client.getWidget(uidlForChild.getChildUIDL(0));
				try {
					add(child, location);
				} catch (Exception e) {
					// If no location is found, this component is not visible
				}
				((Paintable) child).updateFromUIDL(
						uidlForChild.getChildUIDL(0), client);

			}
		}

	}

	/** Update implementing HTML-layout if needed. */
	private void updateHTML(UIDL uidl, Client client) {
		String newStyle = uidl.getStringAttribute("style");
		if (currentStyle != null && currentStyle.equals(newStyle))
			return;

		String template = client.getResource("layout/" + newStyle + ".html");
		if (template == null) {
			template = "Layout " + newStyle + " is missing";
		} else {
			currentStyle = newStyle;
		}
		template = extractBodyAndScriptsFromTemplate(template);
		DOM.setInnerHTML(getElement(), template);

		locationToElement.clear();
		scanForLocations(getElement());

		Widget parent = getParent();
		while (parent != null && !(parent instanceof IWindow))
			parent = parent.getParent();
		if (parent != null && ((IWindow) parent).getTheme() != null)
			;
		prefixImgSrcs(getElement(), "../theme/"
				+ ((IWindow) parent).getTheme() + "/layout/");
	}

	private void scanForLocations(Element elem) {

		String location = getLocation(elem);
		if (location != null) {
			locationToElement.put(location, elem);
			DOM.setInnerHTML(elem, "");
		} else {
			int len = DOM.getChildCount(elem);
			for (int i=0; i<len; i++) {
				System.out.print(i);
				scanForLocations(DOM.getChild(elem, i));
			}
			
		}

	}
	
	private static native String getLocation(Element elem) /*-{
		return elem.getAttribute("location");
	}-*/;

	/** Scripts must be evaluated when the document has been rendered */
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
	private native void prefixImgSrcs(Element e, String srcPrefix) /*-{
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
	 * Exctract body part and script tags from raw html-template.
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

	public void replaceChildComponent(Widget from, Widget to) {
		String location = getLocation(from);
		if (location == null) throw new IllegalArgumentException();
		add(to,location);
	}

	public boolean hasChildComponent(Widget component) {
		return locationToWidget.containsValue(component);
	}

	public void updateCaption(Widget component, UIDL uidl) {
		// TODO Currently not supported
	}
	
	public String getLocation(Widget w) {
		for (Iterator i = locationToWidget.keySet().iterator(); i.hasNext();) {
			String location = (String) i.next();
			if (locationToWidget.get(location) == w) 
				return location;
		}
		return null;
	}

	public boolean remove(Widget w) {
		String location = getLocation(w);
		if (location != null)
			locationToWidget.remove(location);
		return super.remove(w);
	}

	public void add(Widget w) {
		throw new UnsupportedOperationException();
	}

	public void clear() {
		super.clear();
		locationToWidget.clear();
	}

}
