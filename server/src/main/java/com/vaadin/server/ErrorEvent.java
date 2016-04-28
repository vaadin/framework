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
package com.vaadin.server;

import java.io.Serializable;

import com.vaadin.shared.Connector;
import com.vaadin.ui.UI;

/**
 * An error thrown by the framework and handled by an {@link ErrorHandler}.
 * Typically handled by {@link VaadinSession#getErrorHandler()} but can also be
 * handled by a {@link Connector} specific handler, set using
 * {@link ClientConnector#setErrorHandler(ErrorHandler)}.
 * 
 */
public class ErrorEvent implements Serializable {

    private Throwable throwable;

    public ErrorEvent(Throwable t) {
        setThrowable(t);
    }

    /**
     * Gets the contained throwable, the cause of the error.
     * 
     * @return
     */
    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    /**
     * Method for finding the error handler for the given connector. Uses
     * connector hierarchy to find a connector with an error handler. Falls back
     * to the VaadinSession error handler if no connector has specified an error
     * handler.
     * <p>
     * Returns a {@link DefaultErrorHandler} if no error handler was found
     * </p>
     * 
     * @param connector
     *            The target connector
     * @return An ErrorHandler for the connector
     */
    public static ErrorHandler findErrorHandler(ClientConnector connector) {
        if (connector != null) {
            ErrorHandler errorHandler = connector.getErrorHandler();
            if (errorHandler != null) {
                return errorHandler;
            }

            ClientConnector parent = connector.getParent();
            if (parent != null) {
                return findErrorHandler(parent);
            }

            /*
             * Reached UI and found no error handler. Try session which
             * typically has one.
             */
            UI ui = connector.getUI();
            if (ui != null) {
                errorHandler = findErrorHandler(ui.getSession());
                if (errorHandler != null) {
                    return errorHandler;
                }
            }
        }

        /*
         * No connector known or the connector is not attached to a session. Try
         * the current session
         */
        if (VaadinSession.getCurrent() != null) {
            ErrorHandler errorHandler = VaadinSession.getCurrent()
                    .getErrorHandler();
            if (errorHandler != null) {
                return errorHandler;
            }
        }

        /*
         * We should never really get here as at least the session should have
         * an error handler. If for some reason it does not we use the default
         * error handler.
         */
        return new DefaultErrorHandler();
    }

    /**
     * Method for finding the error handler for the given session.
     * 
     * @param connector
     *            The target connector
     * 
     * @return An ErrorHandler for the session or null if none was found
     */
    public static ErrorHandler findErrorHandler(VaadinSession session) {
        if (session == null) {
            return null;
        }
        return session.getErrorHandler();
    }

}
