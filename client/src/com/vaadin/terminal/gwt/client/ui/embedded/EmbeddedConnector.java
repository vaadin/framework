/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui.embedded;

import java.util.Map;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.ObjectElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.embedded.EmbeddedConstants;
import com.vaadin.shared.ui.embedded.EmbeddedServerRpc;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.VConsole;
import com.vaadin.terminal.gwt.client.VTooltip;
import com.vaadin.terminal.gwt.client.communication.RpcProxy;
import com.vaadin.terminal.gwt.client.ui.AbstractComponentConnector;
import com.vaadin.terminal.gwt.client.ui.ClickEventHandler;
import com.vaadin.ui.Embedded;

@Connect(Embedded.class)
public class EmbeddedConnector extends AbstractComponentConnector implements
        Paintable {

    EmbeddedServerRpc rpc;

    @Override
    protected void init() {
        super.init();
        rpc = RpcProxy.create(EmbeddedServerRpc.class, this);
    }

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        if (!isRealUpdate(uidl)) {
            return;
        }

        // Save details
        getWidget().client = client;

        boolean clearBrowserElement = true;

        clickEventHandler.handleEventHandlerRegistration();

        if (uidl.hasAttribute("type")) {
            // remove old style name related to type
            if (getWidget().type != null) {
                getWidget().removeStyleName(
                        VEmbedded.CLASSNAME + "-" + getWidget().type);
            }
            // remove old style name related to mime type
            if (getWidget().mimetype != null) {
                getWidget().removeStyleName(
                        VEmbedded.CLASSNAME + "-" + getWidget().mimetype);
            }
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

                if (uidl.hasAttribute(EmbeddedConstants.ALTERNATE_TEXT)) {
                    el.setPropertyString(
                            EmbeddedConstants.ALTERNATE_TEXT,
                            uidl.getStringAttribute(EmbeddedConstants.ALTERNATE_TEXT));
                }

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
            // remove old style name related to type
            if (getWidget().type != null) {
                getWidget().removeStyleName(
                        VEmbedded.CLASSNAME + "-" + getWidget().type);
            }
            // remove old style name related to mime type
            if (getWidget().mimetype != null) {
                getWidget().removeStyleName(
                        VEmbedded.CLASSNAME + "-" + getWidget().mimetype);
            }
            final String mime = uidl.getStringAttribute("mimetype");
            if (mime.equals("application/x-shockwave-flash")) {
                getWidget().mimetype = "flash";
                // Handle embedding of Flash
                getWidget().addStyleName(VEmbedded.CLASSNAME + "-flash");
                getWidget().setHTML(getWidget().createFlashEmbed(uidl));

            } else if (mime.equals("image/svg+xml")) {
                getWidget().mimetype = "svg";
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
                if (uidl.hasAttribute(EmbeddedConstants.ALTERNATE_TEXT)) {
                    obj.setInnerText(uidl
                            .getStringAttribute(EmbeddedConstants.ALTERNATE_TEXT));
                }
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
    public VEmbedded getWidget() {
        return (VEmbedded) super.getWidget();
    }

    protected final ClickEventHandler clickEventHandler = new ClickEventHandler(
            this) {

        @Override
        protected void fireClick(NativeEvent event,
                MouseEventDetails mouseDetails) {
            rpc.click(mouseDetails);
        }

    };

}