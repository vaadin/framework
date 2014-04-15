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

package com.vaadin.client.ui.link;

import com.google.gwt.user.client.DOM;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.Paintable;
import com.vaadin.client.UIDL;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.communication.StateChangeEvent.StateChangeHandler;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.client.ui.Icon;
import com.vaadin.client.ui.VLink;
import com.vaadin.shared.ui.BorderStyle;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.link.LinkConstants;
import com.vaadin.shared.ui.link.LinkState;
import com.vaadin.ui.Link;

@Connect(Link.class)
public class LinkConnector extends AbstractComponentConnector implements
        Paintable {

    @Override
    protected void init() {
        super.init();
        addStateChangeHandler("resources", new StateChangeHandler() {
            @Override
            public void onStateChanged(StateChangeEvent stateChangeEvent) {
                getWidget().src = getResourceUrl(LinkConstants.HREF_RESOURCE);
                if (getWidget().src == null) {
                    getWidget().anchor.removeAttribute("href");
                } else {
                    getWidget().anchor.setAttribute("href", getWidget().src);
                }
            }
        });
    }

    @Override
    public LinkState getState() {
        return (LinkState) super.getState();
    }

    @Override
    public boolean delegateCaptionHandling() {
        return false;
    }

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {

        if (!isRealUpdate(uidl)) {
            return;
        }

        getWidget().client = client;

        getWidget().enabled = isEnabled();

        if (uidl.hasAttribute("name")) {
            getWidget().target = uidl.getStringAttribute("name");
            getWidget().anchor.setAttribute("target", getWidget().target);
        }

        if (uidl.hasAttribute("border")) {
            if ("none".equals(uidl.getStringAttribute("border"))) {
                getWidget().borderStyle = BorderStyle.NONE;
            } else {
                getWidget().borderStyle = BorderStyle.MINIMAL;
            }
        } else {
            getWidget().borderStyle = BorderStyle.DEFAULT;
        }

        getWidget().targetHeight = uidl.hasAttribute("targetHeight") ? uidl
                .getIntAttribute("targetHeight") : -1;
        getWidget().targetWidth = uidl.hasAttribute("targetWidth") ? uidl
                .getIntAttribute("targetWidth") : -1;

        // Set link caption
        getWidget().captionElement.setInnerText(getState().caption);

        // handle error
        if (null != getState().errorMessage) {
            if (getWidget().errorIndicatorElement == null) {
                getWidget().errorIndicatorElement = DOM.createDiv();
                DOM.setElementProperty(getWidget().errorIndicatorElement,
                        "className", "v-errorindicator");
            }
            DOM.insertChild(getWidget().getElement(),
                    getWidget().errorIndicatorElement, 0);
        } else if (getWidget().errorIndicatorElement != null) {
            DOM.setStyleAttribute(getWidget().errorIndicatorElement, "display",
                    "none");
        }

        if (getIcon() != null) {
            if (getWidget().icon == null) {
                getWidget().icon = new Icon(client);
                getWidget().anchor.insertBefore(getWidget().icon.getElement(),
                        getWidget().captionElement);
            }
            getWidget().icon.setUri(getIcon());
        }

    }

    @Override
    public VLink getWidget() {
        return (VLink) super.getWidget();
    }
}
