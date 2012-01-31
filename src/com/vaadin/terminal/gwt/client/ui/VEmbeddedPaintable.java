/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.ObjectElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.DomEvent.Type;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.VConsole;
import com.vaadin.terminal.gwt.client.VTooltip;

public class VEmbeddedPaintable extends VAbstractPaintableWidget {

    public static final String CLICK_EVENT_IDENTIFIER = "click";

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        if (client.updateComponent(this, uidl, true)) {
            return;
        }

        // Save details
        getWidgetForPaintable().client = client;

        boolean clearBrowserElement = true;

        clickEventHandler.handleEventHandlerRegistration(client);

        if (uidl.hasAttribute("type")) {
            getWidgetForPaintable().type = uidl.getStringAttribute("type");
            if (getWidgetForPaintable().type.equals("image")) {
                getWidgetForPaintable().addStyleName(
                        VEmbedded.CLASSNAME + "-image");
                Element el = null;
                boolean created = false;
                NodeList<Node> nodes = getWidgetForPaintable().getElement()
                        .getChildNodes();
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
                    getWidgetForPaintable().setHTML("");
                    el = DOM.createImg();
                    created = true;
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
                DOM.setElementProperty(el, "src", getWidgetForPaintable()
                        .getSrc(uidl, client));

                if (created) {
                    // insert in dom late
                    getWidgetForPaintable().getElement().appendChild(el);
                }

                /*
                 * Sink tooltip events so tooltip is displayed when hovering the
                 * image.
                 */
                getWidgetForPaintable().sinkEvents(VTooltip.TOOLTIP_EVENTS);

            } else if (getWidgetForPaintable().type.equals("browser")) {
                getWidgetForPaintable().addStyleName(
                        VEmbedded.CLASSNAME + "-browser");
                if (getWidgetForPaintable().browserElement == null) {
                    getWidgetForPaintable().setHTML(
                            "<iframe width=\"100%\" height=\"100%\" frameborder=\"0\""
                                    + " allowTransparency=\"true\" src=\"\""
                                    + " name=\"" + uidl.getId()
                                    + "\"></iframe>");
                    getWidgetForPaintable().browserElement = DOM
                            .getFirstChild(getWidgetForPaintable().getElement());
                }
                DOM.setElementAttribute(getWidgetForPaintable().browserElement,
                        "src", getWidgetForPaintable().getSrc(uidl, client));
                clearBrowserElement = false;
            } else {
                VConsole.log("Unknown Embedded type '"
                        + getWidgetForPaintable().type + "'");
            }
        } else if (uidl.hasAttribute("mimetype")) {
            final String mime = uidl.getStringAttribute("mimetype");
            if (mime.equals("application/x-shockwave-flash")) {
                // Handle embedding of Flash
                getWidgetForPaintable().addStyleName(
                        VEmbedded.CLASSNAME + "-flash");
                getWidgetForPaintable().setHTML(
                        getWidgetForPaintable().createFlashEmbed(uidl));

            } else if (mime.equals("image/svg+xml")) {
                getWidgetForPaintable().addStyleName(
                        VEmbedded.CLASSNAME + "-svg");
                String data;
                Map<String, String> parameters = VEmbedded.getParameters(uidl);
                if (parameters.get("data") == null) {
                    data = getWidgetForPaintable().getSrc(uidl, client);
                } else {
                    data = "data:image/svg+xml," + parameters.get("data");
                }
                getWidgetForPaintable().setHTML("");
                ObjectElement obj = Document.get().createObjectElement();
                obj.setType(mime);
                obj.setData(data);
                if (getWidgetForPaintable().width != null) {
                    obj.getStyle().setProperty("width", "100%");
                }
                if (getWidgetForPaintable().height != null) {
                    obj.getStyle().setProperty("height", "100%");
                }
                if (uidl.hasAttribute("classid")) {
                    obj.setAttribute("classid",
                            uidl.getStringAttribute("classid"));
                }
                if (uidl.hasAttribute("codebase")) {
                    obj.setAttribute("codebase",
                            uidl.getStringAttribute("codebase"));
                }
                if (uidl.hasAttribute("codetype")) {
                    obj.setAttribute("codetype",
                            uidl.getStringAttribute("codetype"));
                }
                if (uidl.hasAttribute("archive")) {
                    obj.setAttribute("archive",
                            uidl.getStringAttribute("archive"));
                }
                if (uidl.hasAttribute("standby")) {
                    obj.setAttribute("standby",
                            uidl.getStringAttribute("standby"));
                }
                getWidgetForPaintable().getElement().appendChild(obj);

            } else {
                VConsole.log("Unknown Embedded mimetype '" + mime + "'");
            }
        } else {
            VConsole.log("Unknown Embedded; no type or mimetype attribute");
        }

        if (clearBrowserElement) {
            getWidgetForPaintable().browserElement = null;
        }
    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VEmbedded.class);
    }

    @Override
    public VEmbedded getWidgetForPaintable() {
        return (VEmbedded) super.getWidgetForPaintable();
    }

    protected final ClickEventHandler clickEventHandler = new ClickEventHandler(
            this, CLICK_EVENT_IDENTIFIER) {

        @Override
        protected <H extends EventHandler> HandlerRegistration registerHandler(
                H handler, Type<H> type) {
            return getWidgetForPaintable().addDomHandler(handler, type);
        }

    };
}
