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

/**
 * Contains the system messages used to notify the user about various critical
 * situations that can occur.
 * <p>
 * Vaadin gets the SystemMessages from your application by calling a static
 * getSystemMessages() method. By default the Application.getSystemMessages() is
 * used. You can customize this by defining a static
 * MyApplication.getSystemMessages() and returning CustomizedSystemMessages.
 * Note that getSystemMessages() is static - changing the system messages will
 * by default change the message for all users of the application.
 * </p>
 * <p>
 * The default behavior is to show a notification, and restart the application
 * the the user clicks the message. <br/>
 * Instead of restarting the application, you can set a specific URL that the
 * user is taken to.<br/>
 * Setting both caption and message to null will restart the application (or go
 * to the specified URL) without displaying a notification.
 * set*NotificationEnabled(false) will achieve the same thing.
 * </p>
 * <p>
 * The situations are:
 * <li>Session expired: the user session has expired, usually due to inactivity.
 * </li>
 * <li>Communication error: the client failed to contact the server, or the
 * server returned and invalid response.</li>
 * <li>Internal error: unhandled critical server error (e.g out of memory,
 * database crash)
 * <li>Out of sync: the client is not in sync with the server. E.g the user
 * opens two windows showing the same application, but the application does not
 * support this and uses the same Window instance. When the user makes changes
 * in one of the windows - the other window is no longer in sync, and (for
 * instance) pressing a button that is no longer present in the UI will cause a
 * out-of-sync -situation.
 * </p>
 */

public class CustomizedSystemMessages extends SystemMessages implements
        Serializable {

    /**
     * Sets the URL to go to when the session has expired.
     * 
     * @param sessionExpiredURL
     *            the URL to go to, or null to reload current
     */
    public void setSessionExpiredURL(String sessionExpiredURL) {
        this.sessionExpiredURL = sessionExpiredURL;
    }

    /**
     * Enables or disables the notification. If disabled, the set URL (or
     * current) is loaded directly when next transaction between server and
     * client happens.
     * 
     * @param sessionExpiredNotificationEnabled
     *            true = enabled, false = disabled
     */
    public void setSessionExpiredNotificationEnabled(
            boolean sessionExpiredNotificationEnabled) {
        this.sessionExpiredNotificationEnabled = sessionExpiredNotificationEnabled;
    }

    /**
     * Sets the caption of the notification. Set to null for no caption. If both
     * caption and message are null, client automatically forwards to
     * sessionExpiredUrl after timeout timer expires. Timer uses value read from
     * HTTPSession.getMaxInactiveInterval()
     * 
     * @param sessionExpiredCaption
     *            the caption
     */
    public void setSessionExpiredCaption(String sessionExpiredCaption) {
        this.sessionExpiredCaption = sessionExpiredCaption;
    }

    /**
     * Sets the message of the notification. Set to null for no message. If both
     * caption and message are null, client automatically forwards to
     * sessionExpiredUrl after timeout timer expires. Timer uses value read from
     * HTTPSession.getMaxInactiveInterval()
     * 
     * @param sessionExpiredMessage
     *            the message
     */
    public void setSessionExpiredMessage(String sessionExpiredMessage) {
        this.sessionExpiredMessage = sessionExpiredMessage;
    }

    /**
     * Sets the URL to go to when there is a authentication error.
     * 
     * @param authenticationErrorURL
     *            the URL to go to, or null to reload current
     */
    public void setAuthenticationErrorURL(String authenticationErrorURL) {
        this.authenticationErrorURL = authenticationErrorURL;
    }

    /**
     * Enables or disables the notification. If disabled, the set URL (or
     * current) is loaded directly.
     * 
     * @param authenticationErrorNotificationEnabled
     *            true = enabled, false = disabled
     */
    public void setAuthenticationErrorNotificationEnabled(
            boolean authenticationErrorNotificationEnabled) {
        this.authenticationErrorNotificationEnabled = authenticationErrorNotificationEnabled;
    }

    /**
     * Sets the caption of the notification. Set to null for no caption. If both
     * caption and message is null, the notification is disabled;
     * 
     * @param authenticationErrorCaption
     *            the caption
     */
    public void setAuthenticationErrorCaption(String authenticationErrorCaption) {
        this.authenticationErrorCaption = authenticationErrorCaption;
    }

    /**
     * Sets the message of the notification. Set to null for no message. If both
     * caption and message is null, the notification is disabled;
     * 
     * @param authenticationErrorMessage
     *            the message
     */
    public void setAuthenticationErrorMessage(String authenticationErrorMessage) {
        this.authenticationErrorMessage = authenticationErrorMessage;
    }

    /**
     * Sets the URL to go to when there is a communication error.
     * 
     * @param communicationErrorURL
     *            the URL to go to, or null to reload current
     */
    public void setCommunicationErrorURL(String communicationErrorURL) {
        this.communicationErrorURL = communicationErrorURL;
    }

    /**
     * Enables or disables the notification. If disabled, the set URL (or
     * current) is loaded directly.
     * 
     * @param communicationErrorNotificationEnabled
     *            true = enabled, false = disabled
     */
    public void setCommunicationErrorNotificationEnabled(
            boolean communicationErrorNotificationEnabled) {
        this.communicationErrorNotificationEnabled = communicationErrorNotificationEnabled;
    }

    /**
     * Sets the caption of the notification. Set to null for no caption. If both
     * caption and message is null, the notification is disabled;
     * 
     * @param communicationErrorCaption
     *            the caption
     */
    public void setCommunicationErrorCaption(String communicationErrorCaption) {
        this.communicationErrorCaption = communicationErrorCaption;
    }

    /**
     * Sets the message of the notification. Set to null for no message. If both
     * caption and message is null, the notification is disabled;
     * 
     * @param communicationErrorMessage
     *            the message
     */
    public void setCommunicationErrorMessage(String communicationErrorMessage) {
        this.communicationErrorMessage = communicationErrorMessage;
    }

    /**
     * Sets the URL to go to when an internal error occurs.
     * 
     * @param internalErrorURL
     *            the URL to go to, or null to reload current
     */
    public void setInternalErrorURL(String internalErrorURL) {
        this.internalErrorURL = internalErrorURL;
    }

    /**
     * Enables or disables the notification. If disabled, the set URL (or
     * current) is loaded directly.
     * 
     * @param internalErrorNotificationEnabled
     *            true = enabled, false = disabled
     */
    public void setInternalErrorNotificationEnabled(
            boolean internalErrorNotificationEnabled) {
        this.internalErrorNotificationEnabled = internalErrorNotificationEnabled;
    }

    /**
     * Sets the caption of the notification. Set to null for no caption. If both
     * caption and message is null, the notification is disabled;
     * 
     * @param internalErrorCaption
     *            the caption
     */
    public void setInternalErrorCaption(String internalErrorCaption) {
        this.internalErrorCaption = internalErrorCaption;
    }

    /**
     * Sets the message of the notification. Set to null for no message. If both
     * caption and message is null, the notification is disabled;
     * 
     * @param internalErrorMessage
     *            the message
     */
    public void setInternalErrorMessage(String internalErrorMessage) {
        this.internalErrorMessage = internalErrorMessage;
    }

    /**
     * Sets the URL to go to when the client is out-of-sync.
     * 
     * @param outOfSyncURL
     *            the URL to go to, or null to reload current
     */
    public void setOutOfSyncURL(String outOfSyncURL) {
        this.outOfSyncURL = outOfSyncURL;
    }

    /**
     * Enables or disables the notification. If disabled, the set URL (or
     * current) is loaded directly.
     * 
     * @param outOfSyncNotificationEnabled
     *            true = enabled, false = disabled
     */
    public void setOutOfSyncNotificationEnabled(
            boolean outOfSyncNotificationEnabled) {
        this.outOfSyncNotificationEnabled = outOfSyncNotificationEnabled;
    }

    /**
     * Sets the caption of the notification. Set to null for no caption. If both
     * caption and message is null, the notification is disabled;
     * 
     * @param outOfSyncCaption
     *            the caption
     */
    public void setOutOfSyncCaption(String outOfSyncCaption) {
        this.outOfSyncCaption = outOfSyncCaption;
    }

    /**
     * Sets the message of the notification. Set to null for no message. If both
     * caption and message is null, the notification is disabled;
     * 
     * @param outOfSyncMessage
     *            the message
     */
    public void setOutOfSyncMessage(String outOfSyncMessage) {
        this.outOfSyncMessage = outOfSyncMessage;
    }

    /**
     * Sets the URL to redirect to when the browser has cookies disabled.
     * 
     * @param cookiesDisabledURL
     *            the URL to redirect to, or null to reload the current URL
     */
    public void setCookiesDisabledURL(String cookiesDisabledURL) {
        this.cookiesDisabledURL = cookiesDisabledURL;
    }

    /**
     * Enables or disables the notification for "cookies disabled" messages. If
     * disabled, the URL returned by {@link #getCookiesDisabledURL()} is loaded
     * directly.
     * 
     * @param cookiesDisabledNotificationEnabled
     *            true to enable "cookies disabled" messages, false otherwise
     */
    public void setCookiesDisabledNotificationEnabled(
            boolean cookiesDisabledNotificationEnabled) {
        this.cookiesDisabledNotificationEnabled = cookiesDisabledNotificationEnabled;
    }

    /**
     * Sets the caption of the "cookies disabled" notification. Set to null for
     * no caption. If both caption and message is null, the notification is
     * disabled.
     * 
     * @param cookiesDisabledCaption
     *            the caption for the "cookies disabled" notification
     */
    public void setCookiesDisabledCaption(String cookiesDisabledCaption) {
        this.cookiesDisabledCaption = cookiesDisabledCaption;
    }

    /**
     * Sets the message of the "cookies disabled" notification. Set to null for
     * no message. If both caption and message is null, the notification is
     * disabled.
     * 
     * @param cookiesDisabledMessage
     *            the message for the "cookies disabled" notification
     */
    public void setCookiesDisabledMessage(String cookiesDisabledMessage) {
        this.cookiesDisabledMessage = cookiesDisabledMessage;
    }

}
