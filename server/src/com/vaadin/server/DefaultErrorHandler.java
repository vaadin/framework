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

package com.vaadin.server;

import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.server.ClientConnector.ConnectorErrorEvent;
import com.vaadin.shared.Connector;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;

public class DefaultErrorHandler implements ErrorHandler {
    @Override
    public void error(ErrorEvent event) {
        doDefault(event);
    }

    public static void doDefault(ErrorEvent event) {
        final Throwable t = event.getThrowable();
        if (t instanceof SocketException) {
            // Most likely client browser closed socket
            getLogger().info(
                    "SocketException in CommunicationManager."
                            + " Most likely client (browser) closed socket.");
            return;
        }

        // Finds the original source of the error/exception
        AbstractComponent component = findAbstractComponent(event);
        if (component != null) {
            // Shows the error in AbstractComponent
            ErrorMessage errorMessage = AbstractErrorMessage
                    .getErrorMessageForException(t);
            component.setComponentError(errorMessage);
        }

        // also print the error on console
        getLogger().log(Level.SEVERE, "", t);
    }

    private static Logger getLogger() {
        return Logger.getLogger(DefaultErrorHandler.class.getName());
    }

    /**
     * Returns the AbstractComponent associated with the given error if such can
     * be found
     * 
     * @param event
     *            The error to investigate
     * @return The {@link AbstractComponent} to error relates to or null if
     *         could not be determined or if the error does not relate to any
     *         AbstractComponent.
     */
    public static AbstractComponent findAbstractComponent(
            com.vaadin.server.ErrorEvent event) {
        if (event instanceof ConnectorErrorEvent) {
            Component c = findComponent(((ConnectorErrorEvent) event)
                    .getConnector());
            if (c instanceof AbstractComponent) {
                return (AbstractComponent) c;
            }
        }

        return null;
    }

    /**
     * Finds the nearest component by traversing upwards in the hierarchy. If
     * connector is a Component, that Component is returned. Otherwise, looks
     * upwards in the hierarchy until it finds a {@link Component}.
     * 
     * @return A Component or null if no component was found
     */
    public static Component findComponent(Connector connector) {
        if (connector instanceof Component) {
            return (Component) connector;
        }
        if (connector.getParent() != null) {
            return findComponent(connector.getParent());
        }

        return null;
    }

}