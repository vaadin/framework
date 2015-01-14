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
package com.vaadin.client;

import static com.vaadin.shared.EventId.BLUR;
import static com.vaadin.shared.EventId.FOCUS;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.DomEvent.Type;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;

/**
 * Helper class for attaching/detaching handlers for Vaadin client side
 * components, based on identifiers in UIDL. Helpers expect Paintables to be
 * both listeners and sources for events. This helper cannot be used for more
 * complex widgets.
 * <p>
 * Possible current registration is given as parameter. The returned
 * registration (possibly the same as given, should be store for next update.
 * <p>
 * Pseudocode what helpers do:
 * 
 * <pre>
 * 
 * if paintable has event listener in UIDL
 *      if registration is null
 *              register paintable as as handler for event
 *      return the registration
 * else 
 *      if registration is not null
 *              remove the handler from paintable
 *      return null
 * 
 * 
 * </pre>
 * 
 */
public class EventHelper {

    /**
     * Adds or removes a focus handler depending on if the connector has focus
     * listeners on the server side or not.
     * 
     * @param connector
     *            The connector to update. Must implement focusHandler.
     * @param handlerRegistration
     *            The old registration reference or null if no handler has been
     *            registered previously
     * @return a new registration handler that can be used to unregister the
     *         handler later
     */
    public static <T extends ComponentConnector & FocusHandler> HandlerRegistration updateFocusHandler(
            T connector, HandlerRegistration handlerRegistration) {
        return updateHandler(connector, FOCUS, handlerRegistration,
                FocusEvent.getType(), connector.getWidget());
    }

    /**
     * Adds or removes a focus handler depending on if the connector has focus
     * listeners on the server side or not.
     * 
     * @param connector
     *            The connector to update. Must implement focusHandler.
     * @param handlerRegistration
     *            The old registration reference or null if no handler has been
     *            registered previously
     * @param widget
     *            The widget which emits focus events
     * @return a new registration handler that can be used to unregister the
     *         handler later
     */
    public static <T extends ComponentConnector & FocusHandler> HandlerRegistration updateFocusHandler(
            T connector, HandlerRegistration handlerRegistration, Widget widget) {
        return updateHandler(connector, FOCUS, handlerRegistration,
                FocusEvent.getType(), widget);
    }

    /**
     * Adds or removes a blur handler depending on if the connector has blur
     * listeners on the server side or not.
     * 
     * @param connector
     *            The connector to update. Must implement BlurHandler.
     * @param handlerRegistration
     *            The old registration reference or null if no handler has been
     *            registered previously
     * @return a new registration handler that can be used to unregister the
     *         handler later
     */
    public static <T extends ComponentConnector & BlurHandler> HandlerRegistration updateBlurHandler(
            T connector, HandlerRegistration handlerRegistration) {
        return updateHandler(connector, BLUR, handlerRegistration,
                BlurEvent.getType(), connector.getWidget());
    }

    /**
     * Adds or removes a blur handler depending on if the connector has blur
     * listeners on the server side or not.
     * 
     * @param connector
     *            The connector to update. Must implement BlurHandler.
     * @param handlerRegistration
     *            The old registration reference or null if no handler has been
     *            registered previously
     * @param widget
     *            The widget which emits blur events
     * 
     * @return a new registration handler that can be used to unregister the
     *         handler later
     */
    public static <T extends ComponentConnector & BlurHandler> HandlerRegistration updateBlurHandler(
            T connector, HandlerRegistration handlerRegistration, Widget widget) {
        return updateHandler(connector, BLUR, handlerRegistration,
                BlurEvent.getType(), widget);
    }

    private static <H extends EventHandler> HandlerRegistration updateHandler(
            ComponentConnector connector, String eventIdentifier,
            HandlerRegistration handlerRegistration, Type<H> type, Widget widget) {
        if (connector.hasEventListener(eventIdentifier)) {
            if (handlerRegistration == null) {
                handlerRegistration = widget.addDomHandler((H) connector, type);
            }
        } else if (handlerRegistration != null) {
            handlerRegistration.removeHandler();
            handlerRegistration = null;
        }
        return handlerRegistration;

    }

}
