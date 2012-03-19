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

public class EmbeddedConnector extends AbstractComponentConnector {

    public static final String CLICK_EVENT_IDENTIFIER = "click";

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        super.updateFromUIDL(uidl, client);
        if (!isRealUpdate(uidl)) {
            return;
        }

        // Save details
        getWidget().client = client;

        boolean clearBrowserElement = true;

        clickEventHandler.handleEventHandlerRegistration();

        if (uidl.hasAttribute("type")) {
            getWidget().type = uidl.getStringAttribute("type");
            if (getWidget().type.equals("image")) {
                getWidget().addStyleName(VEmbedded.CLASSNAME + "-image");
                Element el = null;
                boolean created = false;
                NodeList<Node> nodes = getWidget().getElement().getChildNodes();
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
                    getWidget().setHTML("");
                    el = DOM.createImg();
                    created = true;
                    DOM.sinkEvents(el, Event.ONLOAD);
                }

                // Set attributes
                Style style = el.getStyle();
                style.setProperty("width", getState().getWidth());
                style.setProperty("height", getState().getHeight());

                DOM.setElementProperty(el, "src",
                        getWidget().getSrc(uidl, client));

                if (created) {
                    // insert in dom late
                    getWidget().getElement().appendChild(el);
                }

                /*
                 * Sink tooltip events so tooltip is displayed when hovering the
                 * image.
                 */
                getWidget().sinkEvents(VTooltip.TOOLTIP_EVENTS);

            } else if (getWidget().type.equals("browser")) {
                getWidget().addStyleName(VEmbedded.CLASSNAME + "-browser");
                if (getWidget().browserElement == null) {
                    getWidget().setHTML(
                            "<iframe width=\"100%\" height=\"100%\" frameborder=\"0\""
                                    + " allowTransparency=\"true\" src=\"\""
                                    + " name=\"" + uidl.getId()
                                    + "\"></iframe>");
                    getWidget().browserElement = DOM.getFirstChild(getWidget()
                            .getElement());
                }
                DOM.setElementAttribute(getWidget().browserElement, "src",
                        getWidget().getSrc(uidl, client));
                clearBrowserElement = false;
            } else {
                VConsole.log("Unknown Embedded type '" + getWidget().type + "'");
            }
        } else if (uidl.hasAttribute("mimetype")) {
            final String mime = uidl.getStringAttribute("mimetype");
            if (mime.equals("application/x-shockwave-flash")) {
                // Handle embedding of Flash
                getWidget().addStyleName(VEmbedded.CLASSNAME + "-flash");
                getWidget().setHTML(getWidget().createFlashEmbed(uidl));

            } else if (mime.equals("image/svg+xml")) {
                getWidget().addStyleName(VEmbedded.CLASSNAME + "-svg");
                String data;
                Map<String, String> parameters = VEmbedded.getParameters(uidl);
                if (parameters.get("data") == null) {
                    data = getWidget().getSrc(uidl, client);
                } else {
                    data = "data:image/svg+xml," + parameters.get("data");
                }
                getWidget().setHTML("");
                ObjectElement obj = Document.get().createObjectElement();
                obj.setType(mime);
                obj.setData(data);
                if (!isUndefinedWidth()) {
                    obj.getStyle().setProperty("width", "100%");
                }
                if (!isUndefinedHeight()) {
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
                getWidget().getElement().appendChild(obj);

            } else {
                VConsole.log("Unknown Embedded mimetype '" + mime + "'");
            }
        } else {
            VConsole.log("Unknown Embedded; no type or mimetype attribute");
        }

        if (clearBrowserElement) {
            getWidget().browserElement = null;
        }
    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VEmbedded.class);
    }

    @Override
    public VEmbedded getWidget() {
        return (VEmbedded) super.getWidget();
    }

    protected final ClickEventHandler clickEventHandler = new ClickEventHandler(
            this, CLICK_EVENT_IDENTIFIER) {

        @Override
        protected <H extends EventHandler> HandlerRegistration registerHandler(
                H handler, Type<H> type) {
            return getWidget().addDomHandler(handler, type);
        }

    };

}