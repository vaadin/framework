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
     * @param dialogTextGaveUp
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

    /**
     * Gets the timeout (in milliseconds) between noticing a loss of connection
     * and showing the dialog.
     *
     * @return the time to wait before showing a dialog
     */
    public int getDialogGracePeriod();

    /**
     * Sets the timeout (in milliseconds) between noticing a loss of connection
     * and showing the dialog.
     *
     * @param dialogGracePeriod
     *            the time to wait before showing a dialog
     */
    public void setDialogGracePeriod(int dialogGracePeriod);

    /**
     * Sets the modality of the dialog.
     * <p>
     * If the dialog is set to modal, it will prevent the usage of the
     * application while the dialog is being shown. If not modal, the user can
     * continue to use the application as normally and all server events will be
     * queued until connection has been re-established.
     *
     * @param dialogModal
     *            true to make the dialog modal, false otherwise
     */
    public void setDialogModal(boolean dialogModal);

    /**
     * Checks the modality of the dialog.
     * <p>
     *
     * @see #setDialogModal(boolean)
     * @return true if the dialog is modal, false otherwise
     */
    public boolean isDialogModal();
}

class ReconnectDialogConfigurationImpl implements ReconnectDialogConfiguration {
    private final UI ui;

    public ReconnectDialogConfigurationImpl(UI ui) {
        this.ui = ui;
    }

    @Override
    public String getDialogText() {
        return ui.getState(false).reconnectDialogConfiguration.dialogText;
    }

    @Override
    public void setDialogText(String dialogText) {
        ui.getState().reconnectDialogConfiguration.dialogText = dialogText;
    }

    @Override
    public String getDialogTextGaveUp() {
        return ui.getState(false).reconnectDialogConfiguration.dialogTextGaveUp;
    }

    @Override
    public void setDialogTextGaveUp(String dialogTextGaveUp) {
        ui.getState().reconnectDialogConfiguration.dialogTextGaveUp = dialogTextGaveUp;
    }

    @Override
    public int getReconnectAttempts() {
        return ui
                .getState(false).reconnectDialogConfiguration.reconnectAttempts;
    }

    @Override
    public void setReconnectAttempts(int reconnectAttempts) {
        ui.getState().reconnectDialogConfiguration.reconnectAttempts = reconnectAttempts;
    }

    @Override
    public int getReconnectInterval() {
        return ui
                .getState(false).reconnectDialogConfiguration.reconnectInterval;
    }

    @Override
    public void setReconnectInterval(int reconnectInterval) {
        ui.getState().reconnectDialogConfiguration.reconnectInterval = reconnectInterval;
    }

    @Override
    public int getDialogGracePeriod() {
        return ui
                .getState(false).reconnectDialogConfiguration.dialogGracePeriod;
    }

    @Override
    public void setDialogGracePeriod(int dialogGracePeriod) {
        ui.getState().reconnectDialogConfiguration.dialogGracePeriod = dialogGracePeriod;
    }

    @Override
    public boolean isDialogModal() {
        return ui.getState(false).reconnectDialogConfiguration.dialogModal;
    }

    @Override
    public void setDialogModal(boolean dialogModal) {
        ui.getState().reconnectDialogConfiguration.dialogModal = dialogModal;
    }

}
