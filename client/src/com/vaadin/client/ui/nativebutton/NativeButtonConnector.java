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
package com.vaadin.client.ui.nativebutton;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.vaadin.client.EventHelper;
import com.vaadin.client.VCaption;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.client.ui.Icon;
import com.vaadin.client.ui.VNativeButton;
import com.vaadin.shared.communication.FieldRpc.FocusAndBlurServerRpc;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.button.ButtonServerRpc;
import com.vaadin.shared.ui.button.NativeButtonState;
import com.vaadin.ui.NativeButton;

@Connect(NativeButton.class)
public class NativeButtonConnector extends AbstractComponentConnector implements
        BlurHandler, FocusHandler {

    private HandlerRegistration focusHandlerRegistration;
    private HandlerRegistration blurHandlerRegistration;

    @Override
    public void init() {
        super.init();

        getWidget().buttonRpcProxy = getRpcProxy(ButtonServerRpc.class);
        getWidget().client = getConnection();
        getWidget().paintableId = getConnectorId();
    }

    @Override
    public boolean delegateCaptionHandling() {
        return false;
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);

        getWidget().disableOnClick = getState().disableOnClick;
        focusHandlerRegistration = EventHelper.updateFocusHandler(this,
                focusHandlerRegistration);
        blurHandlerRegistration = EventHelper.updateBlurHandler(this,
                blurHandlerRegistration);

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
            getWidget().getElement().removeChild(
                    getWidget().errorIndicatorElement);
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

    @Override
    public void onFocus(FocusEvent event) {
        // EventHelper.updateFocusHandler ensures that this is called only when
        // there is a listener on server side
        getRpcProxy(FocusAndBlurServerRpc.class).focus();
    }

    @Override
    public void onBlur(BlurEvent event) {
        // EventHelper.updateFocusHandler ensures that this is called only when
        // there is a listener on server side
        getRpcProxy(FocusAndBlurServerRpc.class).blur();
    }

}
