package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.HashMap;
import java.util.Iterator;

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.CaptionWrapper;
import com.itmill.toolkit.terminal.gwt.client.Client;
import com.itmill.toolkit.terminal.gwt.client.Layout;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class ICustomLayout extends SimplePanel implements Paintable, Layout {

	private HashMap componentToWrapper = new HashMap();

	HTMLPanel html;

	String currentStyle;

	String locationPrefix = HTMLPanel.createUniqueId() + "_";

	String scripts = "";

	public void updateFromUIDL(UIDL uidl, Client client) {

		if (client.updateComponent(this, uidl, false))
			return;

		updateHTML(uidl, client);

		componentToWrapper.clear();
		html.clear();
		for (Iterator i = uidl.getChildIterator(); i.hasNext();) {
			UIDL uidlForChild = (UIDL) i.next();
			if (uidlForChild.getTag().equals("location")) {
				String location = uidlForChild.getStringAttribute("name");
				Widget child = client.getWidget(uidlForChild.getChildUIDL(0));
				try {
					html.add(child, locationPrefix + location);
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
		html = new HTMLPanel(template);
		addUniqueIdsForLocations(html.getElement(), locationPrefix);

		Widget parent = getParent();
		while (parent != null && !(parent instanceof IWindow))
			parent = parent.getParent();
		if (parent != null && ((IWindow) parent).getTheme() != null)
			;
		prefixImgSrcs(html.getElement(), "../theme/"
				+ ((IWindow) parent).getTheme() + "/layout/");
		add(html);
	}

	/** Scripts must be evaluated when the document has been rendered */
	protected void onLoad() {
		super.onLoad();
		eval(scripts);
	}

	/** Evaluate given script in browser document */
	private native void eval(String script) /*-{
	 try {
	 eval("{ var document = $doc; var window = $wnd; "+ script + "}");
	 } catch (e) {
	 }
	 }-*/;

	/** Scan for location divs and add unique ids for them */
	private native void addUniqueIdsForLocations(Element e, String idPrefix) /*-{
	try {
	 var divs = e.getElementsByTagName("div"); 
	 for (var i = 0; i < divs.length; i++) {
	 var div = divs[i];
	 var location = div.getAttribute("location");
	 if (location != null) {
	 div.setAttribute("id",idPrefix + location);
	 div.innerHTML="";
	 }
	 }	
	 	 } catch (e) {}
		
	 }-*/;

	/** Prefix all img tag srcs with given prefix. */
	private native void prefixImgSrcs(Element e, String srcPrefix) /*-{
	try {
	 var divs = e.getElementsByTagName("img"); 
	 for (var i = 0; i < divs.length; i++) {
	 var div = divs[i];
	 var src = div.getAttribute("src");
	 if (src.indexOf("http") != 0) div.setAttribute("src",srcPrefix + src);
	 }			
	 } catch (e) {}
	 }-*/;

	/** Exctract body part and script tags from raw html-template.
	 * 
	 * Saves contents of all script-tags to private property: scripts.
	 * Returns contents of the body part for the html without script-tags.
	 * 
	 * @param html Original HTML-template received from server
	 * @return html that is used to create the HTMLPanel.
	 */
	private String extractBodyAndScriptsFromTemplate(String html) {
		
		// Exctract script-tags
		scripts ="";
		int endOfPrevScript = 0;
		int nextPosToCheck = 0;
		String lc = html.toLowerCase();
		String res = "";
		int scriptStart = lc.indexOf("<script", nextPosToCheck);
		while (scriptStart > 0) {
			res += html.substring(endOfPrevScript, scriptStart);
			scriptStart = lc.indexOf(">", scriptStart);
			int j = lc.indexOf("</script>",scriptStart);
			scripts += html.substring(scriptStart+1,j) + ";";
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
			startOfBody = lc.indexOf(">",startOfBody)+1;
			int endOfBody = lc.indexOf("</body>",startOfBody);
			if (endOfBody > startOfBody)
				res = html.substring(startOfBody,endOfBody);
			else 
				res = html.substring(startOfBody);
		}
		
		return res;
	}

	public void replaceChildComponent(Widget from, Widget to) {
		CaptionWrapper wrapper = (CaptionWrapper) componentToWrapper.get(from);
		if (wrapper != null) {
			componentToWrapper.remove(from);
			from = wrapper;
		}
		// TODO
		html.remove(from);
		html.add(to);

	}

	public boolean hasChildComponent(Widget component) {
		// TODO
		return componentToWrapper.get(component) != null;
	}

	public void updateCaption(Widget component, UIDL uidl) {
		// TODO
		/*
		 * CaptionWrapper wrapper = (CaptionWrapper)
		 * componentToWrapper.get(component); if (CaptionWrapper.isNeeded(uidl)) {
		 * if (wrapper == null) { int index = getWidgetIndex(component);
		 * remove(component); wrapper = new CaptionWrapper(component);
		 * insert(wrapper, index); componentToWrapper.put(component, wrapper); }
		 * wrapper.updateCaption(uidl); } else { if (wrapper != null) { int
		 * index = getWidgetIndex(wrapper); remove(wrapper);
		 * insert(wrapper.getWidget(), index);
		 * componentToWrapper.remove(component); } }
		 */
	}

}
