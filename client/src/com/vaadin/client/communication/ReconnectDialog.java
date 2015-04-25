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
package com.vaadin.client.communication;

import com.vaadin.client.ApplicationConnection;

/**
 * Interface which must be implemented by the reconnect dialog
 * 
 * @since 7.6
 * @author Vaadin Ltd
 */
public interface ReconnectDialog {

    /**
     * Sets the main text shown in the dialog
     * 
     * @param text
     *            the text to show
     */
    void setText(String text);

    /**
     * Sets the reconnecting state, which is true if we are trying to
     * re-establish a connection with the server.
     * 
     * @param reconnecting
     *            true if we are trying to re-establish the server connection,
     *            false if we have given up
     */
    void setReconnecting(boolean reconnecting);

    /**
     * Checks if the reconnect dialog is visible to the user
     * 
     * @return true if the user can see the dialog, false otherwise
     */
    boolean isVisible();

    /**
     * Shows the dialog to the user
     * 
     * @param connection
     *            the application connection this is related to
     */
    void show(ApplicationConnection connection);

    /**
     * Hides the dialog from the user
     */
    void hide();
}
