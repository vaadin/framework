/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.Container;
import com.itmill.toolkit.terminal.gwt.client.ContainerResizedListener;
import com.itmill.toolkit.terminal.gwt.client.ICaption;
import com.itmill.toolkit.terminal.gwt.client.ICaptionWrapper;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;
import com.itmill.toolkit.terminal.gwt.client.RenderInformation.Size;

/**
 * Custom Layout implements complex layout defined with HTML template.
 * 
 * @author IT Mill
 * 
 */
public class ICustomLayout extends ComplexPanel implements Paintable,
        Container, ContainerResizedListener {

    public static final String CLASSNAME = "i-customlayout";

    /** Location-name to containing element in DOM map */
    private final HashMap locationToElement = new HashMap();

    /** Location-name to contained widget map */
    private final HashMap locationToWidget = new HashMap();

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

    public ICustomLayout() {
        setElement(DOM.createDiv());
        // Clear any unwanted styling
        DOM.setStyleAttribute(getElement(), "border", "none");
        DOM.setStyleAttribute(getElement(), "margin", "0");
        DOM.setStyleAttribute(getElement(), "padding", "0");
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
        final Widget previous = (Widget) locationToWidget.get(location);
        // NOP if given widget already exists in this location
        if (previous == widget) {
            return;
        }
        remove(previous);

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

        // Set size
        if (uidl.hasAttribute("width")) {
            setWidth(uidl.getStringAttribute("width"));
        } else {
            setWidth("100%");
        }
        if (uidl.hasAttribute("height")) {
            setHeight(uidl.getStringAttribute("height"));
        } else {
            setHeight("100%");
        }

        // Evaluate scripts
        eval(scripts);
        scripts = null;

        iLayout();

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
        DOM.setInnerHTML(getElement(), template);

        // Remap locations to elements
        locationToElement.clear();
        scanForLocations(getElement());

        String themeUri = client.getThemeUri();
        prefixImgSrcs(getElement(), themeUri + "/layouts/");

        publishResizedFunction(DOM.getFirstChild(getElement()));

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

        final String location = getLocation(elem);
        if (location != null) {
            locationToElement.put(location, elem);
            DOM.setInnerHTML(elem, "");
        } else {
            final int len = DOM.getChildCount(elem);
            for (int i = 0; i < len; i++) {
                scanForLocations(DOM.getChild(elem, i));
            }
        }
    }

    /** Get the location attribute for given element */
    private static native String getLocation(Element elem)
    /*-{
        return elem.getAttribute("location");
    }-*/;

    /** Evaluate given script in browser document */
    private static native void eval(String script)
    /*-{
      try {
     	 if (script != null) 
      eval("{ var document = $doc; var window = $wnd; "+ script + "}");
      } catch (e) {
      }
    }-*/;

    /** Prefix all img tag srcs with given prefix. */
    private static native void prefixImgSrcs(Element e, String srcPrefix)
    /*-{
      try {
          var divs = e.getElementsByTagName("img"); 
          var base = "" + $doc.location;
          var l = base.length-1;
          while (l >= 0 && base.charAt(l) != "/") l--;
          base = base.substring(0,l+1);
          for (var i = 0; i < divs.length; i++) {
              var div = divs[i];
              var src = div.getAttribute("src");
              if (src.indexOf("/")==0 || src.match(/\w+:\/\//)) {
                  continue;
              }
              div.setAttribute("src",srcPrefix + src);             
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
        ICaptionWrapper wrapper = (ICaptionWrapper) widgetToCaptionWrapper
                .get(component);
        if (ICaption.isNeeded(uidl)) {
            if (wrapper == null) {
                final String loc = getLocation((Widget) component);
                super.remove((Widget) component);
                wrapper = new ICaptionWrapper(component, client);
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
    public boolean remove(Widget w) {
        client.unregisterPaintable((Paintable) w);
        final String location = getLocation(w);
        if (location != null) {
            locationToWidget.remove(location);
        }
        final ICaptionWrapper cw = (ICaptionWrapper) widgetToCaptionWrapper
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
    public void add(Widget w) {
        throw new UnsupportedOperationException();
    }

    /** Clear all widgets from the layout */
    public void clear() {
        super.clear();
        locationToWidget.clear();
        widgetToCaptionWrapper.clear();
    }

    public void iLayout() {
        if (!iLayoutJS(DOM.getFirstChild(getElement()))) {
            client.runDescendentsLayout(this);
        }
    }

    /**
     * This method is published to JS side with the same name into first DOM
     * node of custom layout. This way if one implements some resizeable
     * containers in custom layout he/she can notify children after resize.
     */
    public void notifyChildrenOfSizeChange() {
        client.runDescendentsLayout(this);
    }

    public void onDetach() {
        detachResizedFunction(DOM.getFirstChild(getElement()));
    }

    private native void detachResizedFunction(Element element)
    /*-{
    	element.notifyChildrenOfSizeChange = null;
    }-*/;

    private native void publishResizedFunction(Element element)
    /*-{
    	var self = this;
    	element.notifyChildrenOfSizeChange = function() {
    		self.@com.itmill.toolkit.terminal.gwt.client.ui.ICustomLayout::notifyChildrenOfSizeChange()();
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
        // TODO Auto-generated method stub
        return false;
    }

    public Size getAllocatedSpace(Widget child) {
        // TODO Auto-generated method stub
        return null;
    }

}
