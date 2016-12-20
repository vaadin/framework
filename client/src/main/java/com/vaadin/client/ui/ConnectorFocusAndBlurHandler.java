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
package com.vaadin.client.ui;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasAllFocusHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.EventHelper;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.communication.StateChangeEvent.StateChangeHandler;
import com.vaadin.shared.EventId;
import com.vaadin.shared.communication.FieldRpc.FocusAndBlurServerRpc;

/**
 * A handler for focus and blur events which uses {@link FocusAndBlurServerRpc}
 * to transmit received events to the server. Events are only handled if there
 * is a corresponding listener on the server side.
 *
 * @since 7.6
 * @author Vaadin Ltd
 */
public class ConnectorFocusAndBlurHandler
        implements StateChangeHandler, FocusHandler, BlurHandler {

    private final AbstractComponentConnector connector;
    private final Widget widget;
    private HandlerRegistration focusRegistration = null;
    private HandlerRegistration blurRegistration = null;
    private HandlerRegistration stateChangeRegistration = null;

    /**
     * Add focus/blur handlers to the widget of the {@code connector}.
     *
     * @param connector
     *            connector whose widget is a target to add focus/blur handlers
     * @return ConnectorFocusAndBlurHandler instance to remove all registered
     *         handlers
     */
    public static ConnectorFocusAndBlurHandler addHandlers(
            AbstractComponentConnector connector) {
        return addHandlers(connector, connector.getWidget());
    }

    /**
     * Add focus/blur handlers to the widget and a state change handler for the
     * {@code connector}.
     *
     * @param connector
     *            connector to register state change handler
     * @param widget
     *            widget to register focus/blur handler
     * @return ConnectorFocusAndBlurHandler instance to remove all registered
     *         handlers
     */
    public static ConnectorFocusAndBlurHandler addHandlers(
            AbstractComponentConnector connector, Widget widget) {
        ConnectorFocusAndBlurHandler handler = new ConnectorFocusAndBlurHandler(
                connector, widget);
        handler.stateChangeRegistration = connector
                .addStateChangeHandler("registeredEventListeners", handler);
        return handler;
    }

    private ConnectorFocusAndBlurHandler(AbstractComponentConnector connector,
            Widget widget) {
        this.connector = connector;
        this.widget = widget;
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        if (widget instanceof HasAllFocusHandlers) {
            HasAllFocusHandlers focusHandlers = (HasAllFocusHandlers) widget;
            focusRegistration = EventHelper.updateHandler(connector,
                    EventId.FOCUS, focusRegistration,
                    () -> focusHandlers.addFocusHandler(this));
            blurRegistration = EventHelper.updateHandler(connector,
                    EventId.BLUR, blurRegistration,
                    () -> focusHandlers.addBlurHandler(this));
        } else {
            focusRegistration = EventHelper.updateHandler(connector, this,
                    EventId.FOCUS, focusRegistration, FocusEvent.getType(),
                    widget);
            blurRegistration = EventHelper.updateHandler(connector, this,
                    EventId.BLUR, blurRegistration, BlurEvent.getType(),
                    widget);
        }
    }

    @Override
    public void onFocus(FocusEvent event) {
        // updateHandler ensures that this is called only when
        // there is a listener on the server side
        getRpc().focus();
    }

    @Override
    public void onBlur(BlurEvent event) {
        // updateHandler ensures that this is called only when
        // there is a listener on the server side
        getRpc().blur();
    }

    /**
     * Remove all handlers from the widget and the connector.
     */
    public void removeHandlers() {
        if (focusRegistration != null) {
            focusRegistration.removeHandler();
        }
        if (blurRegistration != null) {
            blurRegistration.removeHandler();
        }
        if (stateChangeRegistration != null) {
            stateChangeRegistration.removeHandler();
        }
    }

    private FocusAndBlurServerRpc getRpc() {
        return connector.getRpcProxy(FocusAndBlurServerRpc.class);
    }
}
