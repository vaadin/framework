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

import com.vaadin.server.Terminal.ErrorEvent;
import com.vaadin.ui.AbstractComponent;

public class DefaultErrorListener implements Terminal.ErrorListener {
    @Override
    public void terminalError(ErrorEvent event) {
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
        Object owner = null;
        if (event instanceof VariableOwner.ErrorEvent) {
            owner = ((VariableOwner.ErrorEvent) event).getVariableOwner();
        } else if (event instanceof ChangeVariablesErrorEvent) {
            owner = ((ChangeVariablesErrorEvent) event).getComponent();
        }

        // Shows the error in AbstractComponent
        if (owner instanceof AbstractComponent) {
            ((AbstractComponent) owner).setComponentError(AbstractErrorMessage
                    .getErrorMessageForException(t));
        }

        // also print the error on console
        getLogger().log(Level.SEVERE, "Terminal error:", t);
    }

    private static Logger getLogger() {
        return Logger.getLogger(DefaultErrorListener.class.getName());
    }
}