/*
 * Copyright 2000-2016 Vaadin Ltd.
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

package com.vaadin.client.ui.button;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.vaadin.client.MouseEventDetailsBuilder;
import com.vaadin.client.VCaption;
import com.vaadin.client.annotations.OnStateChange;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.client.ui.ConnectorFocusAndBlurHandler;
import com.vaadin.client.ui.Icon;
import com.vaadin.client.ui.VButton;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.Connect.LoadStyle;
import com.vaadin.shared.ui.button.ButtonServerRpc;
import com.vaadin.shared.ui.button.ButtonState;
import com.vaadin.ui.Button;

@Connect(value = Button.class, loadStyle = LoadStyle.EAGER)
public class ButtonConnector extends AbstractComponentConnector
        implements ClickHandler {

    @Override
    public boolean delegateCaptionHandling() {
        return false;
    }

    @Override
    public void init() {
        super.init();
        getWidget().addClickHandler(this);
        getWidget().client = getConnection();
        ConnectorFocusAndBlurHandler.addHandlers(this);
    }

    @OnStateChange("errorMessage")
    void setErrorMessage() {
        if (null != getState().errorMessage) {
            if (getWidget().errorIndicatorElement == null) {
                getWidget().errorIndicatorElement = DOM.createSpan();
                getWidget().errorIndicatorElement
                        .setClassName("v-errorindicator");
            }
            getWidget().wrapper.insertFirst(getWidget().errorIndicatorElement);

        } else if (getWidget().errorIndicatorElement != null) {
            getWidget().wrapper.removeChild(getWidget().errorIndicatorElement);
            getWidget().errorIndicatorElement = null;
        }
    }

    @OnStateChange("resources")
    void onResourceChange() {
        if (getWidget().icon != null) {
            getWidget().wrapper.removeChild(getWidget().icon.getElement());
            getWidget().icon = null;
        }
        Icon icon = getIcon();
        if (icon != null) {
            getWidget().icon = icon;
            icon.setAlternateText(getState().iconAltText);
            getWidget().wrapper.insertBefore(icon.getElement(),
                    getWidget().captionElement);
        }
    }

    @OnStateChange({ "caption", "captionAsHtml" })
    void setCaption() {
        VCaption.setCaptionText(getWidget().captionElement, getState());
    }

    @OnStateChange("iconAltText")
    void setIconAltText() {
        if (getWidget().icon != null) {
            getWidget().icon.setAlternateText(getState().iconAltText);
        }
    }

    @OnStateChange("clickShortcutKeyCode")
    void setClickShortcut() {
        getWidget().clickShortcut = getState().clickShortcutKeyCode;
    }

    @Override
    public VButton getWidget() {
        return (VButton) super.getWidget();
    }

    @Override
    public ButtonState getState() {
        return (ButtonState) super.getState();
    }

    @Override
    public void onClick(ClickEvent event) {
        if (getState().disableOnClick) {
            // Simulate getting disabled from the server without waiting for the
            // round trip. The server-side RPC handler takes care of updating
            // the server-side state in a similar way to ensure subsequent
            // changes are properly propagated. Changing state on client is not
            // generally supported.
            getState().enabled = false;
            super.updateEnabledState(false);
            getRpcProxy(ButtonServerRpc.class).disableOnClick();
        }

        // Add mouse details
        MouseEventDetails details = MouseEventDetailsBuilder
                .buildMouseEventDetails(event.getNativeEvent(),
                        getWidget().getElement());
        getRpcProxy(ButtonServerRpc.class).click(details);

    }

}
