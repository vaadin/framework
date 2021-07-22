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

package com.vaadin.client.ui.link;

import com.vaadin.client.VCaption;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.client.ui.Icon;
import com.vaadin.client.ui.VLink;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.link.LinkConstants;
import com.vaadin.shared.ui.link.LinkState;
import com.vaadin.ui.Link;

/**
 * A connector class for the Link component.
 *
 * @author Vaadin Ltd
 */
@Connect(Link.class)
public class LinkConnector extends AbstractComponentConnector {

    @Override
    public LinkState getState() {
        return (LinkState) super.getState();
    }

    @Override
    public boolean delegateCaptionHandling() {
        return false;
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);

        getWidget().enabled = isEnabled();

        if (stateChangeEvent.hasPropertyChanged("resources")) {
            getWidget().src = getResourceUrl(LinkConstants.HREF_RESOURCE);
            if (getWidget().src == null) {
                getWidget().anchor.removeAttribute("href");
            } else {
                getWidget().anchor.setAttribute("href", getWidget().src);
            }
        }

        getWidget().target = getState().target;
        if (getWidget().target == null) {
            getWidget().anchor.removeAttribute("target");
        } else {
            getWidget().anchor.setAttribute("target", getWidget().target);
        }

        getWidget().borderStyle = getState().targetBorder;
        getWidget().targetWidth = getState().targetWidth;
        getWidget().targetHeight = getState().targetHeight;

        // Set link caption
        VCaption.setCaptionText(getWidget().captionElement, getState());

        if (getWidget().icon != null) {
            getWidget().anchor.removeChild(getWidget().icon.getElement());
            getWidget().icon = null;
        }
        Icon icon = getIcon();
        if (icon != null) {
            getWidget().icon = icon;
            getWidget().anchor.insertBefore(icon.getElement(),
                    getWidget().captionElement);
        }
    }

    @Override
    public VLink getWidget() {
        return (VLink) super.getWidget();
    }
}
