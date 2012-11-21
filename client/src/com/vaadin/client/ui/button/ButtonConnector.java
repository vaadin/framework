/*
 * Copyright 2011 Vaadin Ltd.
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

import java.util.Set;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.vaadin.client.EventHelper;
import com.vaadin.client.MouseEventDetailsBuilder;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.communication.StateChangeEvent.StateChangeHandler;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.client.ui.Icon;
import com.vaadin.client.ui.ShortcutAction;
import com.vaadin.client.ui.ShortcutActionTarget;
import com.vaadin.client.ui.VButton;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.communication.FieldRpc.FocusAndBlurServerRpc;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.Connect.LoadStyle;
import com.vaadin.shared.ui.button.ButtonServerRpc;
import com.vaadin.shared.ui.button.ButtonState;
import com.vaadin.ui.Button;

@Connect(value = Button.class, loadStyle = LoadStyle.EAGER)
public class ButtonConnector extends AbstractComponentConnector implements
        BlurHandler, FocusHandler, ClickHandler, ShortcutActionTarget {

    private HandlerRegistration focusHandlerRegistration = null;
    private HandlerRegistration blurHandlerRegistration = null;

    @Override
    public boolean delegateCaptionHandling() {
        return false;
    }

    @Override
    public void init() {
        super.init();
        getWidget().addClickHandler(this);
        getWidget().client = getConnection();
        addStateChangeHandler("errorMessage", new StateChangeHandler() {
            @Override
            public void onStateChanged(StateChangeEvent stateChangeEvent) {
                if (null != getState().errorMessage) {
                    if (getWidget().errorIndicatorElement == null) {
                        getWidget().errorIndicatorElement = DOM.createSpan();
                        getWidget().errorIndicatorElement
                                .setClassName("v-errorindicator");
                    }
                    getWidget().wrapper.insertBefore(
                            getWidget().errorIndicatorElement,
                            getWidget().captionElement);

                } else if (getWidget().errorIndicatorElement != null) {
                    getWidget().wrapper
                            .removeChild(getWidget().errorIndicatorElement);
                    getWidget().errorIndicatorElement = null;
                }
            }
        });

        addStateChangeHandler("resources.icon", new StateChangeHandler() {
            @Override
            public void onStateChanged(StateChangeEvent stateChangeEvent) {
                if (getIcon() != null) {
                    if (getWidget().icon == null) {
                        getWidget().icon = new Icon(getConnection());
                        getWidget().wrapper.insertBefore(
                                getWidget().icon.getElement(),
                                getWidget().captionElement);
                    }
                    getWidget().icon.setUri(getIcon());
                } else {
                    if (getWidget().icon != null) {
                        getWidget().wrapper.removeChild(getWidget().icon
                                .getElement());
                        getWidget().icon = null;
                    }
                }
            }
        });
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);
        focusHandlerRegistration = EventHelper.updateFocusHandler(this,
                focusHandlerRegistration);
        blurHandlerRegistration = EventHelper.updateBlurHandler(this,
                blurHandlerRegistration);

        Set<String> changedProperties = stateChangeEvent.getChangedProperties();
        if (changedProperties.contains("caption")
                || changedProperties.contains("htmlContentAllowed")) {
            // Set text
            if (getState().htmlContentAllowed) {
                getWidget().setHtml(getState().caption);
            } else {
                getWidget().setText(getState().caption);
            }
        }

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
                .buildMouseEventDetails(event.getNativeEvent(), getWidget()
                        .getElement());
        getRpcProxy(ButtonServerRpc.class).click(details);

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.terminal.gwt.client.ui.ShortcutActionTarget#handleAction(com
     * .vaadin.terminal.gwt.client.ui.ShortcutAction)
     */
    @Override
    public boolean handleAction(ShortcutAction action) {
        if ("click".equals(action.getTargetAction())) {
            getWidget().onClick();
            return true;
        }
        return false;
    }

}
