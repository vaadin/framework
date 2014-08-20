/*
 * Copyright 2000-2014 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.vaadin.client.ui;

import java.util.HashMap;
import java.util.Iterator;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.BrowserInfo;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.StyleConstants;
import com.vaadin.client.Util;
import com.vaadin.client.VCaption;
import com.vaadin.client.VCaptionWrapper;

/**
 * Custom Layout implements complex layout defined with HTML template.
 * 
 * @author Vaadin Ltd
 * 
 */
public class VCustomLayout extends ComplexPanel {

    public static final String CLASSNAME = "v-customlayout";

    /** Location-name to containing element in DOM map */
    private final HashMap<String, Element> locationToElement = new HashMap<String, Element>();

    /** Location-name to contained widget map */
    final HashMap<String, Widget> locationToWidget = new HashMap<String, Widget>();

    /** Widget to captionwrapper map */
    private final HashMap<Widget, VCaptionWrapper> childWidgetToCaptionWrapper = new HashMap<Widget, VCaptionWrapper>();

    /**
     * Unexecuted scripts loaded from the template.
     * <p>
     * For internal use only. May be removed or replaced in the future.
     */
    public String scripts = "";

    /**
     * Paintable ID of this paintable.
     * <p>
     * For internal use only. May be removed or replaced in the future.
     */
    public String pid;

    /** For internal use only. May be removed or replaced in the future. */
    public ApplicationConnection client;

    private boolean htmlInitialized = false;

    private Element elementWithNativeResizeFunction;

    private String height = "";

    private String width = "";

    public VCustomLayout() {
        setElement(DOM.createDiv());
        // Clear any unwanted styling
        Style style = getElement().getStyle();
        style.setBorderStyle(BorderStyle.NONE);
        style.setMargin(0, Unit.PX);
        style.setPadding(0, Unit.PX);

        if (BrowserInfo.get().isIE()) {
            style.setPosition(Position.RELATIVE);
        }

        setStyleName(CLASSNAME);
    }

    @Override
    public void setStyleName(String style) {
        super.setStyleName(style);
        addStyleName(StyleConstants.UI_LAYOUT);
    }

    /**
     * Sets widget to given location.
     * 
     * If location already contains a widget it will be removed.
     * 
     * @param widget
     *            Widget to be set into location.
     * @param location
     *            location name where widget will be added
     * 
     * @throws IllegalArgumentException
     *             if no such location is found in the layout.
     */
    public void setWidget(Widget widget, String location) {

        if (widget == null) {
            return;
        }

        // If no given location is found in the layout, and exception is throws
        Element elem = locationToElement.get(location);
        if (elem == null && hasTemplate()) {
            throw new IllegalArgumentException("No location " + location
                    + " found");
        }

        // Get previous widget
        final Widget previous = locationToWidget.get(location);
        // NOP if given widget already exists in this location
        if (previous == widget) {
            return;
        }

        if (previous != null) {
            remove(previous);
        }

        // if template is missing add element in order
        if (!hasTemplate()) {
            elem = getElement();
        }

        // Add widget to location
        super.add(widget, elem);
        locationToWidget.put(location, widget);
    }

    /** Initialize HTML-layout. */
    public void initializeHTML(String template, String themeUri) {

        // Connect body of the template to DOM
        template = extractBodyAndScriptsFromTemplate(template);

        // TODO prefix img src:s here with a regeps, cannot work further with IE

        String relImgPrefix = themeUri + "/layouts/";

        // prefix all relative image elements to point to theme dir with a
        // regexp search
        template = template.replaceAll(
                "<((?:img)|(?:IMG))\\s([^>]*)src=\"((?![a-z]+:)[^/][^\"]+)\"",
                "<$1 $2src=\"" + relImgPrefix + "$3\"");
        // also support src attributes without quotes
        template = template
                .replaceAll(
                        "<((?:img)|(?:IMG))\\s([^>]*)src=[^\"]((?![a-z]+:)[^/][^ />]+)[ />]",
                        "<$1 $2src=\"" + relImgPrefix + "$3\"");
        // also prefix relative style="...url(...)..."
        template = template
                .replaceAll(
                        "(<[^>]+style=\"[^\"]*url\\()((?![a-z]+:)[^/][^\"]+)(\\)[^>]*>)",
                        "$1 " + relImgPrefix + "$2 $3");

        getElement().setInnerHTML(template);

        // Remap locations to elements
        locationToElement.clear();
        scanForLocations(getElement());

        initImgElements();

        elementWithNativeResizeFunction = DOM.getFirstChild(getElement());
        if (elementWithNativeResizeFunction == null) {
            elementWithNativeResizeFunction = getElement();
        }
        publishResizedFunction(elementWithNativeResizeFunction);

        htmlInitialized = true;
    }

    private native boolean uriEndsWithSlash()
    /*-{
        var path =  $wnd.location.pathname;
        if(path.charAt(path.length - 1) == "/")
            return true;
        return false;
    }-*/;

    /** For internal use only. May be removed or replaced in the future. */
    public boolean hasTemplate() {
        return htmlInitialized;
    }

    /** Collect locations from template */
    private void scanForLocations(Element elem) {
        if (elem.hasAttribute("location")) {
            final String location = elem.getAttribute("location");
            locationToElement.put(location, elem);
            elem.setInnerHTML("");
        } else {
            final int len = DOM.getChildCount(elem);
            for (int i = 0; i < len; i++) {
                scanForLocations(DOM.getChild(elem, i));
            }
        }
    }

    /**
     * Evaluate given script in browser document.
     * <p>
     * For internal use only. May be removed or replaced in the future.
     */
    public static native void eval(String script)
    /*-{
      try {
     	 if (script != null)
      eval("{ var document = $doc; var window = $wnd; "+ script + "}");
      } catch (e) {
      }
    }-*/;

    /**
     * Img elements needs some special handling in custom layout. Img elements
     * will get their onload events sunk. This way custom layout can notify
     * parent about possible size change.
     */
    private void initImgElements() {
        NodeList<Element> nodeList = getElement().getElementsByTagName("IMG");
        for (int i = 0; i < nodeList.getLength(); i++) {
            ImageElement img = ImageElement.as(nodeList.getItem(i));
            DOM.sinkEvents(img, Event.ONLOAD);
        }
    }

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
            final int j = lc.indexOf("</script>", scriptStart);
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
            final int endOfBody = lc.indexOf("</body>", startOfBody);
            if (endOfBody > startOfBody) {
                res = html.substring(startOfBody, endOfBody);
            } else {
                res = html.substring(startOfBody);
            }
        }

        return res;
    }

    /** Update caption for given widget */
    public void updateCaption(ComponentConnector paintable) {
        Widget widget = paintable.getWidget();
        VCaptionWrapper wrapper = childWidgetToCaptionWrapper.get(widget);
        if (VCaption.isNeeded(paintable.getState())) {
            if (wrapper == null) {
                // Add a wrapper between the layout and the child widget
                final String loc = getLocation(widget);
                super.remove(widget);
                wrapper = new VCaptionWrapper(paintable, client);
                super.add(wrapper, locationToElement.get(loc));
                childWidgetToCaptionWrapper.put(widget, wrapper);
            }
            wrapper.updateCaption();
        } else {
            if (wrapper != null) {
                // Remove the wrapper and add the widget directly to the layout
                final String loc = getLocation(widget);
                super.remove(wrapper);
                super.add(widget, locationToElement.get(loc));
                childWidgetToCaptionWrapper.remove(widget);
            }
        }
    }

    /** Get the location of an widget */
    public String getLocation(Widget w) {
        for (final Iterator<String> i = locationToWidget.keySet().iterator(); i
                .hasNext();) {
            final String location = i.next();
            if (locationToWidget.get(location) == w) {
                return location;
            }
        }
        return null;
    }

    /** Removes given widget from the layout */
    @Override
    public boolean remove(Widget w) {
        final String location = getLocation(w);
        if (location != null) {
            locationToWidget.remove(location);
        }
        final VCaptionWrapper cw = childWidgetToCaptionWrapper.get(w);
        if (cw != null) {
            childWidgetToCaptionWrapper.remove(w);
            return super.remove(cw);
        } else if (w != null) {
            return super.remove(w);
        }
        return false;
    }

    /** Adding widget without specifying location is not supported */
    @Override
    public void add(Widget w) {
        throw new UnsupportedOperationException();
    }

    /** Clear all widgets from the layout */
    @Override
    public void clear() {
        super.clear();
        locationToWidget.clear();
        childWidgetToCaptionWrapper.clear();
    }

    /**
     * This method is published to JS side with the same name into first DOM
     * node of custom layout. This way if one implements some resizeable
     * containers in custom layout he/she can notify children after resize.
     */
    public void notifyChildrenOfSizeChange() {
        client.runDescendentsLayout(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (elementWithNativeResizeFunction != null) {
            detachResizedFunction(elementWithNativeResizeFunction);
        }
    }

    private native void detachResizedFunction(Element element)
    /*-{
    	element.notifyChildrenOfSizeChange = null;
    }-*/;

    private native void publishResizedFunction(Element element)
    /*-{
    	var self = this;
    	element.notifyChildrenOfSizeChange = $entry(function() {
    		self.@com.vaadin.client.ui.VCustomLayout::notifyChildrenOfSizeChange()();
    	});
    }-*/;

    /**
     * In custom layout one may want to run layout functions made with
     * JavaScript. This function tests if one exists (with name "iLayoutJS" in
     * layouts first DOM node) and runs et. Return value is used to determine if
     * children needs to be notified of size changes.
     * <p>
     * Note! When implementing a JS layout function you most likely want to call
     * notifyChildrenOfSizeChange() function on your custom layouts main
     * element. That method is used to control whether child components layout
     * functions are to be run.
     * <p>
     * For internal use only. May be removed or replaced in the future.
     * 
     * @param el
     * @return true if layout function exists and was run successfully, else
     *         false.
     */
    public native boolean iLayoutJS(com.google.gwt.user.client.Element el)
    /*-{
    	if(el && el.iLayoutJS) {
    		try {
    			el.iLayoutJS();
    			return true;
    		} catch (e) {
    			return false;
    		}
    	} else {
    		return false;
    	}
    }-*/;

    @Override
    public void onBrowserEvent(Event event) {
        super.onBrowserEvent(event);
        if (event.getTypeInt() == Event.ONLOAD) {
            Util.notifyParentOfSizeChange(this, true);
            event.cancelBubble(true);
        }
    }

}
