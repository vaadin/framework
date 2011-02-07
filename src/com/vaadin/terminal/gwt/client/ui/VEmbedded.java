/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.ObjectElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.DomEvent.Type;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HTML;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.BrowserInfo;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.Util;
import com.vaadin.terminal.gwt.client.VConsole;
import com.vaadin.terminal.gwt.client.VTooltip;

public class VEmbedded extends HTML implements Paintable {
    public static final String CLICK_EVENT_IDENTIFIER = "click";

    private static String CLASSNAME = "v-embedded";

    private String height;
    private String width;
    private Element browserElement;

    private String type;

    private ApplicationConnection client;

    private final ClickEventHandler clickEventHandler = new ClickEventHandler(
            this, CLICK_EVENT_IDENTIFIER) {

        @Override
        protected <H extends EventHandler> HandlerRegistration registerHandler(
                H handler, Type<H> type) {
            return addDomHandler(handler, type);
        }

    };

    public VEmbedded() {
        setStyleName(CLASSNAME);
    }

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        if (client.updateComponent(this, uidl, true)) {
            return;
        }
        this.client = client;

        boolean clearBrowserElement = true;

        clickEventHandler.handleEventHandlerRegistration(client);

        if (uidl.hasAttribute("type")) {
            type = uidl.getStringAttribute("type");
            if (type.equals("image")) {
                addStyleName(CLASSNAME + "-image");
                Element el = null;
                boolean created = false;
                NodeList<Node> nodes = getElement().getChildNodes();
                if (nodes != null && nodes.getLength() == 1) {
                    Node n = nodes.getItem(0);
                    if (n.getNodeType() == Node.ELEMENT_NODE) {
                        Element e = (Element) n;
                        if (e.getTagName().equals("IMG")) {
                            el = e;
                        }
                    }
                }
                if (el == null) {
                    setHTML("");
                    el = DOM.createImg();
                    created = true;
                    client.addPngFix(el);
                    DOM.sinkEvents(el, Event.ONLOAD);
                }

                // Set attributes
                Style style = el.getStyle();
                String w = uidl.getStringAttribute("width");
                if (w != null) {
                    style.setProperty("width", w);
                } else {
                    style.setProperty("width", "");
                }
                String h = uidl.getStringAttribute("height");
                if (h != null) {
                    style.setProperty("height", h);
                } else {
                    style.setProperty("height", "");
                }
                DOM.setElementProperty(el, "src", getSrc(uidl, client));

                if (created) {
                    // insert in dom late
                    getElement().appendChild(el);
                }

                /*
                 * Sink tooltip events so tooltip is displayed when hovering the
                 * image.
                 */
                sinkEvents(VTooltip.TOOLTIP_EVENTS);

            } else if (type.equals("browser")) {
                addStyleName(CLASSNAME + "-browser");
                if (browserElement == null) {
                    setHTML("<iframe width=\"100%\" height=\"100%\" frameborder=\"0\" allowTransparency=\"true\" src=\""
                            + getSrc(uidl, client)
                            + "\" name=\""
                            + uidl.getId() + "\"></iframe>");
                    browserElement = DOM.getFirstChild(getElement());
                } else {
                    DOM.setElementAttribute(browserElement, "src",
                            getSrc(uidl, client));
                }
                clearBrowserElement = false;
            } else {
                VConsole.log("Unknown Embedded type '" + type + "'");
            }
        } else if (uidl.hasAttribute("mimetype")) {
            final String mime = uidl.getStringAttribute("mimetype");
            if (mime.equals("application/x-shockwave-flash")) {
                addStyleName(CLASSNAME + "-flash");
                String html = "<object "
                        + "type=\"application/x-shockwave-flash\" "
                        + "width=\"" + width + "\" height=\"" + height + "\">";

                Map<String, String> parameters = getParameters(uidl);
                if (parameters.get("movie") == null) {
                    parameters.put("movie", getSrc(uidl, client));
                }

                // Add the parameters to the Object
                for (String name : parameters.keySet()) {
                    html += "<param name=\"" + escapeAttribute(name)
                            + "\" value=\""
                            + escapeAttribute(parameters.get(name)) + "\"/>";
                }

                html += "<embed src=\"" + getSrc(uidl, client) + "\" width=\""
                        + width + "\" height=\"" + height + "\" "
                        + "type=\"application/x-shockwave-flash\" ";

                // Add the parameters to the Embed
                for (String name : parameters.keySet()) {
                    html += escapeAttribute(name) + "=\""
                            + escapeAttribute(parameters.get(name)) + "\" ";
                }

                html += "></embed></object>";
                setHTML(html);
            } else if (mime.equals("image/svg+xml")) {
                addStyleName(CLASSNAME + "-svg");
                String data;
                Map<String, String> parameters = getParameters(uidl);
                if (parameters.get("data") == null) {
                    data = getSrc(uidl, client);
                } else {
                    data = "data:image/svg+xml," + parameters.get("data");
                }
                setHTML("");
                ObjectElement obj = Document.get().createObjectElement();
                obj.setType(mime);
                obj.setData(data);
                if (width != null) {
                    obj.getStyle().setProperty("width", "100%");
                }
                if (height != null) {
                    obj.getStyle().setProperty("height", "100%");
                }
                getElement().appendChild(obj);

            } else {
                VConsole.log("Unknown Embedded mimetype '" + mime + "'");
            }
        } else {
            VConsole.log("Unknown Embedded; no type or mimetype attribute");
        }

        if (clearBrowserElement) {
            browserElement = null;
        }

    }

    /**
     * Escapes the string so it is safe to write inside an HTML attribute.
     * 
     * @param attribute
     *            The string to escape
     * @return An escaped version of <literal>attribute</literal>.
     */
    private String escapeAttribute(String attribute) {
        attribute = attribute.replace("\"", "&quot;");
        attribute = attribute.replace("'", "&#39;");
        attribute = attribute.replace(">", "&gt;");
        attribute = attribute.replace("<", "&lt;");
        attribute = attribute.replace("&", "&amp;");
        return attribute;
    }

    /**
     * Returns a map (name -> value) of all parameters in the UIDL.
     * 
     * @param uidl
     * @return
     */
    private static Map<String, String> getParameters(UIDL uidl) {
        Map<String, String> parameters = new HashMap<String, String>();

        Iterator<Object> childIterator = uidl.getChildIterator();
        while (childIterator.hasNext()) {

            Object child = childIterator.next();
            if (child instanceof UIDL) {

                UIDL childUIDL = (UIDL) child;
                if (childUIDL.getTag().equals("embeddedparam")) {
                    String name = childUIDL.getStringAttribute("name");
                    String value = childUIDL.getStringAttribute("value");
                    parameters.put(name, value);
                }
            }

        }

        return parameters;
    }

    /**
     * Helper to return translated src-attribute from embedded's UIDL
     * 
     * @param uidl
     * @param client
     * @return
     */
    private String getSrc(UIDL uidl, ApplicationConnection client) {
        String url = client.translateVaadinUri(uidl.getStringAttribute("src"));
        if (url == null) {
            return "";
        }
        return url;
    }

    @Override
    public void setWidth(String width) {
        this.width = width;
        if (isDynamicHeight()) {
            int oldHeight = getOffsetHeight();
            super.setWidth(width);
            int newHeight = getOffsetHeight();
            /*
             * Must notify parent if the height changes as a result of a width
             * change
             */
            if (oldHeight != newHeight) {
                Util.notifyParentOfSizeChange(this, false);
            }
        } else {
            super.setWidth(width);
        }

    }

    private boolean isDynamicWidth() {
        return width == null || width.equals("");
    }

    private boolean isDynamicHeight() {
        return height == null || height.equals("");
    }

    @Override
    public void setHeight(String height) {
        this.height = height;
        super.setHeight(height);
    }

    @Override
    protected void onDetach() {
        if (BrowserInfo.get().isIE()) {
            // Force browser to fire unload event when component is detached
            // from the view (IE doesn't do this automatically)
            if (browserElement != null) {
                DOM.setElementAttribute(browserElement, "src",
                        "javascript:false");
            }
        }
        super.onDetach();
    }

    @Override
    public void onBrowserEvent(Event event) {
        super.onBrowserEvent(event);
        if (DOM.eventGetType(event) == Event.ONLOAD) {
            if ("image".equals(type)) {
                updateElementDynamicSizeFromImage();
            }
            Util.notifyParentOfSizeChange(this, true);
        }

        client.handleTooltipEvent(event, this);
    }

    /**
     * Updates the size of the embedded component's element if size is
     * undefined. Without this embeddeds containing images will remain the wrong
     * size in certain cases (e.g. #6304).
     */
    private void updateElementDynamicSizeFromImage() {
        if (isDynamicWidth()) {
            getElement().getStyle().setWidth(
                    getElement().getFirstChildElement().getOffsetWidth(),
                    Unit.PX);
        }
        if (isDynamicHeight()) {
            getElement().getStyle().setHeight(
                    getElement().getFirstChildElement().getOffsetHeight(),
                    Unit.PX);
        }
    }

}
