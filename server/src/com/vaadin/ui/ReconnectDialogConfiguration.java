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

package com.vaadin.ui;

import java.io.Serializable;

/**
 * Provides method for configuring the reconnect dialog.
 * 
 * @since 7.6
 * @author Vaadin Ltd
 */
public interface ReconnectDialogConfiguration extends Serializable {
    /**
     * Gets the text to show in the reconnect dialog when trying to re-establish
     * the server connection
     * 
     * @return the text to show in the reconnect dialog
     */
    public String getDialogText();

    /**
     * Sets the text to show in the reconnect dialog when trying to re-establish
     * the server connection
     * 
     * @param dialogText
     *            the text to show in the reconnect dialog
     */
    public void setDialogText(String dialogText);

    /**
     * Gets the text to show in the reconnect dialog after giving up trying to
     * reconnect ({@link #getReconnectAttempts()} reached)
     * 
     * @return the text to show in the reconnect dialog after giving up
     */
    public String getDialogTextGaveUp();

    /**
     * Sets the text to show in the reconnect dialog after giving up trying to
     * reconnect ({@link #getReconnectAttempts()} reached)
     * 
     * @param dialogText
     *            the text to show in the reconnect dialog after giving up
     */
    public void setDialogTextGaveUp(String dialogTextGaveUp);

    /**
     * Gets the number of times to try to reconnect to the server before giving
     * up
     * 
     * @return the number of times to try to reconnect
     */
    public int getReconnectAttempts();

    /**
     * Sets the number of times to try to reconnect to the server before giving
     * up
     * 
     * @param reconnectAttempts
     *            the number of times to try to reconnect
     */
    public void setReconnectAttempts(int reconnectAttempts);

    /**
     * Gets the interval (in milliseconds) between reconnect attempts
     * 
     * @return the interval (in ms) between reconnect attempts
     */
    public int getReconnectInterval();

    /**
     * Sets the interval (in milliseconds) between reconnect attempts
     * 
     * @param reconnectInterval
     *            the interval (in ms) between reconnect attempts
     */
    public void setReconnectInterval(int reconnectInterval);
}

class ReconnectDialogConfigurationImpl implements ReconnectDialogConfiguration {
    private UI ui;

    public ReconnectDialogConfigurationImpl(UI ui) {
        this.ui = ui;
    }

    @Override
    public String getDialogText() {
        return ui.getState(false).reconnectDialog.dialogText;
    }

    @Override
    public void setDialogText(String dialogText) {
        ui.getState().reconnectDialog.dialogText = dialogText;
    }

    @Override
    public String getDialogTextGaveUp() {
        return ui.getState(false).reconnectDialog.dialogTextGaveUp;
    }

    @Override
    public void setDialogTextGaveUp(String dialogTextGaveUp) {
        ui.getState().reconnectDialog.dialogTextGaveUp = dialogTextGaveUp;
    }

    @Override
    public int getReconnectAttempts() {
        return ui.getState(false).reconnectDialog.reconnectAttempts;
    }

    @Override
    public void setReconnectAttempts(int reconnectAttempts) {
        ui.getState().reconnectDialog.reconnectAttempts = reconnectAttempts;
    }

    @Override
    public int getReconnectInterval() {
        return ui.getState(false).reconnectDialog.reconnectInterval;
    }

    @Override
    public void setReconnectInterval(int reconnectInterval) {
        ui.getState().reconnectDialog.reconnectInterval = reconnectInterval;
    }

}
