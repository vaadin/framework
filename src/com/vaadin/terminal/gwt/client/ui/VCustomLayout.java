/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.BrowserInfo;
import com.vaadin.terminal.gwt.client.Container;
import com.vaadin.terminal.gwt.client.ContainerResizedListener;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.RenderSpace;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.Util;
import com.vaadin.terminal.gwt.client.VCaption;
import com.vaadin.terminal.gwt.client.VCaptionWrapper;
import com.vaadin.terminal.gwt.client.RenderInformation.FloatSize;

/**
 * Custom Layout implements complex layout defined with HTML template.
 * 
 * @author IT Mill
 * 
 */
public class VCustomLayout extends ComplexPanel implements Paintable,
        Container, ContainerResizedListener {

    public static final String CLASSNAME = "v-customlayout";

    /** Location-name to containing element in DOM map */
    private final HashMap locationToElement = new HashMap();

    /** Location-name to contained widget map */
    private final HashMap<String, Widget> locationToWidget = new HashMap<String, Widget>();

    /** Widget to captionwrapper map */
    private final HashMap widgetToCaptionWrapper = new HashMap();

    /** Name of the currently rendered style */
    String currentTemplateName;

    /** Unexecuted scripts loaded from the template */
    private String scripts = "";

    /** Paintable ID of this paintable */
    private String pid;

    private ApplicationConnection client;

    /** Has the template been loaded from contents passed in UIDL **/
    private boolean hasTemplateContents = false;

    private Element elementWithNativeResizeFunction;

    private String height = "";

    private String width = "";

    private HashMap<String, FloatSize> locationToExtraSize = new HashMap<String, FloatSize>();

    public VCustomLayout() {
        setElement(DOM.createDiv());
        // Clear any unwanted styling
        DOM.setStyleAttribute(getElement(), "border", "none");
        DOM.setStyleAttribute(getElement(), "margin", "0");
        DOM.setStyleAttribute(getElement(), "padding", "0");

        if (BrowserInfo.get().isIE()) {
            DOM.setStyleAttribute(getElement(), "position", "relative");
        }

        setStyleName(CLASSNAME);
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
        Element elem = (Element) locationToElement.get(location);
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

    /** Update the layout from UIDL */
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        this.client = client;
        // ApplicationConnection manages generic component features
        if (client.updateComponent(this, uidl, true)) {
            return;
        }

        pid = uidl.getId();
        if (!hasTemplate()) {
            // Update HTML template only once
            initializeHTML(uidl, client);
        }

        // Evaluate scripts
        eval(scripts);
        scripts = null;

        iLayout();
        // TODO Check if this is needed
        client.runDescendentsLayout(this);

        Set oldWidgets = new HashSet();
        oldWidgets.addAll(locationToWidget.values());

        // For all contained widgets
        for (final Iterator i = uidl.getChildIterator(); i.hasNext();) {
            final UIDL uidlForChild = (UIDL) i.next();
            if (uidlForChild.getTag().equals("location")) {
                final String location = uidlForChild.getStringAttribute("name");
                final Paintable child = client.getPaintable(uidlForChild
                        .getChildUIDL(0));
                try {
                    setWidget((Widget) child, location);
                    child.updateFromUIDL(uidlForChild.getChildUIDL(0), client);
                } catch (final IllegalArgumentException e) {
                    // If no location is found, this component is not visible
                }
                oldWidgets.remove(child);
            }
        }
        for (Iterator iterator = oldWidgets.iterator(); iterator.hasNext();) {
            Widget oldWidget = (Widget) iterator.next();
            if (oldWidget.isAttached()) {
                // slot of this widget is emptied, remove it
                remove(oldWidget);
            }
        }

        iLayout();
        // TODO Check if this is needed
        client.runDescendentsLayout(this);

    }

    /** Initialize HTML-layout. */
    private void initializeHTML(UIDL uidl, ApplicationConnection client) {

        final String newTemplateContents = uidl
                .getStringAttribute("templateContents");
        final String newTemplate = uidl.getStringAttribute("template");

        currentTemplateName = null;
        hasTemplateContents = false;

        String template = "";
        if (newTemplate != null) {
            // Get the HTML-template from client
            template = client.getResource("layouts/" + newTemplate + ".html");
            if (template == null) {
                template = "<em>Layout file layouts/"
                        + newTemplate
                        + ".html is missing. Components will be drawn for debug purposes.</em>";
            } else {
                currentTemplateName = newTemplate;
            }
        } else {
            hasTemplateContents = true;
            template = newTemplateContents;
        }

        // Connect body of the template to DOM
        template = extractBodyAndScriptsFromTemplate(template);

        // TODO prefix img src:s here with a regeps, cannot work further with IE

        String themeUri = client.getThemeUri();
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

    }

    private native boolean uriEndsWithSlash()
    /*-{
        var path =  $wnd.location.pathname;
        if(path.charAt(path.length - 1) == "/")
            return true;
        return false;
    }-*/;

    private boolean hasTemplate() {
        if (currentTemplateName == null && !hasTemplateContents) {
            return false;
        } else {
            return true;
        }
    }

    /** Collect locations from template */
    private void scanForLocations(Element elem) {

        final String location = elem.getAttribute("location");
        if (!"".equals(location)) {
            locationToElement.put(location, elem);
            elem.setInnerHTML("");
            int x = Util.measureHorizontalPaddingAndBorder(elem, 0);
            int y = Util.measureVerticalPaddingAndBorder(elem, 0);

            FloatSize fs = new FloatSize(x, y);

            locationToExtraSize.put(location, fs);

        } else {
            final int len = DOM.getChildCount(elem);
            for (int i = 0; i < len; i++) {
                scanForLocations(DOM.getChild(elem, i));
            }
        }
    }

    /** Evaluate given script in browser document */
    private static native void eval(String script)
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
        NodeList<com.google.gwt.dom.client.Element> nodeList = getElement()
                .getElementsByTagName("IMG");
        for (int i = 0; i < nodeList.getLength(); i++) {
            com.google.gwt.dom.client.ImageElement img = (ImageElement) nodeList
                    .getItem(i);
            DOM.sinkEvents((Element) img.cast(), Event.ONLOAD);
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

    /** Replace child components */
    public void replaceChildComponent(Widget from, Widget to) {
        final String location = getLocation(from);
        if (location == null) {
            throw new IllegalArgumentException();
        }
        setWidget(to, location);
    }

    /** Does this layout contain given child */
    public boolean hasChildComponent(Widget component) {
        return locationToWidget.containsValue(component);
    }

    /** Update caption for given widget */
    public void updateCaption(Paintable component, UIDL uidl) {
        VCaptionWrapper wrapper = (VCaptionWrapper) widgetToCaptionWrapper
                .get(component);
        if (VCaption.isNeeded(uidl)) {
            if (wrapper == null) {
                final String loc = getLocation((Widget) component);
                super.remove((Widget) component);
                wrapper = new VCaptionWrapper(component, client);
                super.add(wrapper, (Element) locationToElement.get(loc));
                widgetToCaptionWrapper.put(component, wrapper);
            }
            wrapper.updateCaption(uidl);
        } else {
            if (wrapper != null) {
                final String loc = getLocation((Widget) component);
                super.remove(wrapper);
                super.add((Widget) wrapper.getPaintable(),
                        (Element) locationToElement.get(loc));
                widgetToCaptionWrapper.remove(component);
            }
        }
    }

    /** Get the location of an widget */
    public String getLocation(Widget w) {
        for (final Iterator i = locationToWidget.keySet().iterator(); i
                .hasNext();) {
            final String location = (String) i.next();
            if (locationToWidget.get(location) == w) {
                return location;
            }
        }
        return null;
    }

    /** Removes given widget from the layout */
    @Override
    public boolean remove(Widget w) {
        client.unregisterPaintable((Paintable) w);
        final String location = getLocation(w);
        if (location != null) {
            locationToWidget.remove(location);
        }
        final VCaptionWrapper cw = (VCaptionWrapper) widgetToCaptionWrapper
                .get(w);
        if (cw != null) {
            widgetToCaptionWrapper.remove(w);
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
        widgetToCaptionWrapper.clear();
    }

    public void iLayout() {
        iLayoutJS(DOM.getFirstChild(getElement()));
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
    	element.notifyChildrenOfSizeChange = function() {
    		self.@com.vaadin.terminal.gwt.client.ui.VCustomLayout::notifyChildrenOfSizeChange()();
    	};
    }-*/;

    /**
     * In custom layout one may want to run layout functions made with
     * JavaScript. This function tests if one exists (with name "iLayoutJS" in
     * layouts first DOM node) and runs et. Return value is used to determine if
     * children needs to be notified of size changes.
     * 
     * Note! When implementing a JS layout function you most likely want to call
     * notifyChildrenOfSizeChange() function on your custom layouts main
     * element. That method is used to control whether child components layout
     * functions are to be run.
     * 
     * @param el
     * @return true if layout function exists and was run successfully, else
     *         false.
     */
    private native boolean iLayoutJS(Element el)
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

    public boolean requestLayout(Set<Paintable> child) {
        updateRelativeSizedComponents(true, true);

        if (width.equals("") || height.equals("")) {
            /* Automatically propagated upwards if the size can change */
            return false;
        }

        return true;
    }

    public RenderSpace getAllocatedSpace(Widget child) {
        com.google.gwt.dom.client.Element pe = child.getElement()
                .getParentElement();

        FloatSize extra = locationToExtraSize.get(getLocation(child));
        return new RenderSpace(pe.getOffsetWidth() - (int) extra.getWidth(), pe
                .getOffsetHeight()
                - (int) extra.getHeight(), Util.mayHaveScrollBars(pe));
    }

    @Override
    public void onBrowserEvent(Event event) {
        super.onBrowserEvent(event);
        if (event.getTypeInt() == Event.ONLOAD) {
            Util.notifyParentOfSizeChange(this, true);
            event.cancelBubble(true);
        }
    }

    @Override
    public void setHeight(String height) {
        if (this.height.equals(height)) {
            return;
        }

        boolean shrinking = true;
        if (isLarger(height, this.height)) {
            shrinking = false;
        }

        this.height = height;
        super.setHeight(height);

        /*
         * If the height shrinks we must remove all components with relative
         * height from the DOM, update their height when they do not affect the
         * available space and finally restore them to the original state
         */
        if (shrinking) {
            updateRelativeSizedComponents(false, true);
        }
    }

    @Override
    public void setWidth(String width) {
        if (this.width.equals(width)) {
            return;
        }

        boolean shrinking = true;
        if (isLarger(width, this.width)) {
            shrinking = false;
        }

        super.setWidth(width);
        this.width = width;

        /*
         * If the width shrinks we must remove all components with relative
         * width from the DOM, update their width when they do not affect the
         * available space and finally restore them to the original state
         */
        if (shrinking) {
            updateRelativeSizedComponents(true, false);
        }
    }

    private void updateRelativeSizedComponents(boolean relativeWidth,
            boolean relativeHeight) {

        Set<Widget> relativeSizeWidgets = new HashSet<Widget>();

        for (Widget widget : locationToWidget.values()) {
            FloatSize relativeSize = client.getRelativeSize(widget);
            if (relativeSize != null) {
                if ((relativeWidth && (relativeSize.getWidth() >= 0.0f))
                        || (relativeHeight && (relativeSize.getHeight() >= 0.0f))) {

                    relativeSizeWidgets.add(widget);
                    widget.getElement().getStyle().setProperty("position",
                            "absolute");
                }
            }
        }

        for (Widget widget : relativeSizeWidgets) {
            client.handleComponentRelativeSize(widget);
            widget.getElement().getStyle().setProperty("position", "");
        }
    }

    /**
     * Compares newSize with currentSize and returns true if it is clear that
     * newSize is larger than currentSize. Returns false if newSize is smaller
     * or if it is unclear which one is smaller.
     * 
     * @param newSize
     * @param currentSize
     * @return
     */
    private boolean isLarger(String newSize, String currentSize) {
        if (newSize.equals("") || currentSize.equals("")) {
            return false;
        }

        if (!newSize.endsWith("px") || !currentSize.endsWith("px")) {
            return false;
        }

        int newSizePx = Integer.parseInt(newSize.substring(0,
                newSize.length() - 2));
        int currentSizePx = Integer.parseInt(currentSize.substring(0,
                currentSize.length() - 2));

        boolean larger = newSizePx > currentSizePx;
        return larger;
    }

}
