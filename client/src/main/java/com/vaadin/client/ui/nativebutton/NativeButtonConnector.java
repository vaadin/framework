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
package com.vaadin.client.ui.nativebutton;

import com.google.gwt.user.client.DOM;
import com.vaadin.client.VCaption;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.client.ui.ConnectorFocusAndBlurHandler;
import com.vaadin.client.ui.Icon;
import com.vaadin.client.ui.VNativeButton;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.button.ButtonServerRpc;
import com.vaadin.shared.ui.button.NativeButtonState;
import com.vaadin.ui.NativeButton;

@Connect(NativeButton.class)
public class NativeButtonConnector extends AbstractComponentConnector {

    @Override
    public void init() {
        super.init();

        getWidget().buttonRpcProxy = getRpcProxy(ButtonServerRpc.class);
        getWidget().client = getConnection();
        getWidget().paintableId = getConnectorId();

        ConnectorFocusAndBlurHandler.addHandlers(this);
    }

    @Override
    public boolean delegateCaptionHandling() {
        return false;
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);

        getWidget().disableOnClick = getState().disableOnClick;

        // Set text
        VCaption.setCaptionText(getWidget(), getState());

        // handle error
        if (null != getState().errorMessage) {
            if (getWidget().errorIndicatorElement == null) {
                getWidget().errorIndicatorElement = DOM.createSpan();
                getWidget().errorIndicatorElement
                        .setClassName("v-errorindicator");
            }
            getWidget().getElement().insertBefore(
                    getWidget().errorIndicatorElement,
                    getWidget().captionElement);

        } else if (getWidget().errorIndicatorElement != null) {
            getWidget().getElement()
                    .removeChild(getWidget().errorIndicatorElement);
            getWidget().errorIndicatorElement = null;
        }

        if (getWidget().icon != null) {
            getWidget().getElement().removeChild(getWidget().icon.getElement());
            getWidget().icon = null;
        }
        Icon icon = getIcon();
        if (icon != null) {
            getWidget().icon = icon;
            getWidget().getElement().insertBefore(icon.getElement(),
                    getWidget().captionElement);
            icon.setAlternateText(getState().iconAltText);
        }

    }

    @Override
    public VNativeButton getWidget() {
        return (VNativeButton) super.getWidget();
    }

    @Override
    public NativeButtonState getState() {
        return (NativeButtonState) super.getState();
    }
}
