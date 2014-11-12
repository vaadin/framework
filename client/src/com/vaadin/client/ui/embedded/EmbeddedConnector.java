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

package com.vaadin.client.ui.embedded;

import java.util.Map;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.ObjectElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.Paintable;
import com.vaadin.client.UIDL;
import com.vaadin.client.VConsole;
import com.vaadin.client.VTooltip;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.client.ui.ClickEventHandler;
import com.vaadin.client.ui.VEmbedded;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.embedded.EmbeddedConstants;
import com.vaadin.shared.ui.embedded.EmbeddedServerRpc;
import com.vaadin.shared.ui.embedded.EmbeddedState;
import com.vaadin.ui.Embedded;

@Connect(Embedded.class)
public class EmbeddedConnector extends AbstractComponentConnector implements
        Paintable {

    private Element resourceElement;
    private ObjectElement objectElement;
    private String resourceUrl;

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);
        // if theme has changed the resourceUrl may need to be updated
        updateResourceIfNecessary();
    }

    private void updateResourceIfNecessary() {
        if (resourceElement != null || objectElement != null) {
            String src = getResourceUrl("src");
            if (src != null && !src.isEmpty()) {
                if (!src.equals(resourceUrl)) {
                    setResourceUrl(src);
                }
            } else if (resourceUrl != null && !resourceUrl.isEmpty()) {
                setResourceUrl("");
            }
        }
    }

    private void setResourceUrl(String src) {
        resourceUrl = src;
        if (resourceElement != null) {
            resourceElement.setAttribute("src", src);
        } else if (objectElement != null) {
            objectElement.setData(src);
        }
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
                style.setProperty("width", getState().width);
                style.setProperty("height", getState().height);

                resourceElement = el;
                objectElement = null;
                setResourceUrl(getResourceUrl("src"));

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
                resourceElement = getWidget().browserElement;
                objectElement = null;
                setResourceUrl(getResourceUrl("src"));
                clearBrowserElement = false;
            } else {
                VConsole.error("Unknown Embedded type '" + getWidget().type
                        + "'");
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
                ObjectElement obj = Document.get().createObjectElement();
                resourceElement = null;
                if (parameters.get("data") == null) {
                    objectElement = obj;
                    data = getResourceUrl("src");
                    setResourceUrl(data);
                } else {
                    objectElement = null;
                    data = "data:image/svg+xml," + parameters.get("data");
                    obj.setData(data);
                }
                getWidget().setHTML("");
                obj.setType(mime);
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
                VConsole.error("Unknown Embedded mimetype '" + mime + "'");
            }
        } else {
            VConsole.error("Unknown Embedded; no type or mimetype attribute");
        }

        if (clearBrowserElement) {
            getWidget().browserElement = null;
        }
    }

    @Override
    public VEmbedded getWidget() {
        return (VEmbedded) super.getWidget();
    }

    @Override
    public EmbeddedState getState() {
        return (EmbeddedState) super.getState();
    }

    protected final ClickEventHandler clickEventHandler = new ClickEventHandler(
            this) {

        @Override
        protected void fireClick(NativeEvent event,
                MouseEventDetails mouseDetails) {
            getRpcProxy(EmbeddedServerRpc.class).click(mouseDetails);
        }

    };

}
