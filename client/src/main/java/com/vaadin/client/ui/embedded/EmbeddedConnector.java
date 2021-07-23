/*
 * Copyright 2000-2021 Vaadin Ltd.
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
import java.util.logging.Logger;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.ObjectElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.vaadin.client.VTooltip;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.client.ui.ClickEventHandler;
import com.vaadin.client.ui.VEmbedded;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.embedded.EmbeddedServerRpc;
import com.vaadin.shared.ui.embedded.EmbeddedState;
import com.vaadin.ui.Embedded;

/**
 * A connector class for the Embedded component.
 *
 * @author Vaadin Ltd
 */
@Connect(Embedded.class)
public class EmbeddedConnector extends AbstractComponentConnector {

    private Element resourceElement;
    private ObjectElement objectElement;
    private String resourceUrl;

    @SuppressWarnings("deprecation")
    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);
        // if theme has changed the resourceUrl may need to be updated
        updateResourceIfNecessary();

        VEmbedded widget = getWidget();
        // Save details
        widget.client = getConnection();

        boolean clearBrowserElement = true;

        clickEventHandler.handleEventHandlerRegistration();

        final EmbeddedState state = getState();
        if (state.type != Embedded.TYPE_OBJECT) {
            // remove old style name related to type
            if (widget.type != null) {
                widget.removeStyleName(VEmbedded.CLASSNAME + "-" + widget.type);
            }
            // remove old style name related to mime type
            if (widget.mimetype != null) {
                widget.removeStyleName(
                        VEmbedded.CLASSNAME + "-" + widget.mimetype);
            }
            widget.type = state.type == Embedded.TYPE_IMAGE ? "image"
                    : "browser";
            if (widget.type.equals("image")) {
                widget.addStyleName(VEmbedded.CLASSNAME + "-image");
                Element el = null;
                boolean created = false;
                NodeList<Node> nodes = widget.getElement().getChildNodes();
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
                    widget.setHTML("");
                    el = DOM.createImg();
                    created = true;
                    DOM.sinkEvents(el, Event.ONLOAD);
                }

                // Set attributes
                Style style = el.getStyle();
                style.setProperty("width", state.width);
                style.setProperty("height", state.height);

                resourceElement = el;
                objectElement = null;
                setResourceUrl(getResourceUrl("src"));

                if (state.altText != null) {
                    el.setPropertyString("alt", state.altText);
                }

                if (created) {
                    // insert in dom late
                    widget.getElement().appendChild(el);
                }

                /*
                 * Sink tooltip events so tooltip is displayed when hovering the
                 * image.
                 */
                widget.sinkEvents(VTooltip.TOOLTIP_EVENTS);

            } else if (widget.type.equals("browser")) {
                widget.addStyleName(VEmbedded.CLASSNAME + "-browser");
                if (widget.browserElement == null) {
                    widget.setHTML(
                            "<iframe width=\"100%\" height=\"100%\" frameborder=\"0\""
                                    + " allowTransparency=\"true\" src=\"\""
                                    + " name=\"" + getConnectorId()
                                    + "\"></iframe>");
                    widget.browserElement = DOM
                            .getFirstChild(widget.getElement());
                }
                resourceElement = widget.browserElement;
                objectElement = null;
                setResourceUrl(getResourceUrl("src"));
                clearBrowserElement = false;
            } else {
                getLogger()
                        .severe("Unknown Embedded type '" + widget.type + "'");
            }
        } else if (state.mimeType != null) {
            // remove old style name related to type
            if (widget.type != null) {
                widget.removeStyleName(VEmbedded.CLASSNAME + "-" + widget.type);
            }
            // remove old style name related to mime type
            if (widget.mimetype != null) {
                widget.removeStyleName(
                        VEmbedded.CLASSNAME + "-" + widget.mimetype);
            }
            final String mime = state.mimeType;
            if (mime.equals("application/x-shockwave-flash")) {
                widget.mimetype = "flash";
                // Handle embedding of Flash
                widget.addStyleName(VEmbedded.CLASSNAME + "-flash");
                widget.setHTML(
                        widget.createFlashEmbed(state, getResourceUrl("src")));

            } else if (mime.equals("image/svg+xml")) {
                widget.mimetype = "svg";
                widget.addStyleName(VEmbedded.CLASSNAME + "-svg");
                String data;
                Map<String, String> parameters = state.parameters;
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
                widget.setHTML("");
                obj.setType(mime);
                if (!isUndefinedWidth()) {
                    obj.getStyle().setProperty("width", "100%");
                }
                if (!isUndefinedHeight()) {
                    obj.getStyle().setProperty("height", "100%");
                }
                if (state.classId != null) {
                    obj.setAttribute("classid", state.classId);
                }
                if (state.codebase != null) {
                    obj.setAttribute("codebase", state.codebase);
                }
                if (state.codetype != null) {
                    obj.setAttribute("codetype", state.codetype);
                }
                if (state.archive != null) {
                    obj.setAttribute("archive", state.archive);
                }
                if (state.standby != null) {
                    obj.setAttribute("standby", state.standby);
                }
                widget.getElement().appendChild(obj);
                if (state.altText != null) {
                    obj.setInnerText(state.altText);
                }
            } else {
                getLogger().severe("Unknown Embedded mimetype '" + mime + "'");
            }
        } else {
            getLogger()
                    .severe("Unknown Embedded; no type or mimetype attribute");
        }

        if (clearBrowserElement) {
            widget.browserElement = null;
        }
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
    public VEmbedded getWidget() {
        return (VEmbedded) super.getWidget();
    }

    @Override
    public EmbeddedState getState() {
        return (EmbeddedState) super.getState();
    }

    /** Click event handler for sending click data to the server. */
    protected final ClickEventHandler clickEventHandler = new ClickEventHandler(
            this) {

        @Override
        protected void fireClick(NativeEvent event,
                MouseEventDetails mouseDetails) {
            getRpcProxy(EmbeddedServerRpc.class).click(mouseDetails);
        }

    };

    private static Logger getLogger() {
        return Logger.getLogger(EmbeddedConnector.class.getName());
    }
}
