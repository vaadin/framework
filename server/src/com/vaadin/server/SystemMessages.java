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
 * Use {@link VaadinService#setSystemMessagesProvider(SystemMessagesProvider)}
 * to customize.
 * </p>
 * <p>
 * The defaults defined in this class are:
 * <ul>
 * <li><b>sessionExpiredURL</b> = null</li>
 * <li><b>sessionExpiredNotificationEnabled</b> = true</li>
 * <li><b>sessionExpiredCaption</b> = ""</li>
 * <li><b>sessionExpiredMessage</b> =
 * "Take note of any unsaved data, and <u>click here</u> to continue."</li>
 * <li><b>communicationErrorURL</b> = null</li>
 * <li><b>communicationErrorNotificationEnabled</b> = true</li>
 * <li><b>communicationErrorCaption</b> = "Communication problem"</li>
 * <li><b>communicationErrorMessage</b> =
 * "Take note of any unsaved data, and <u>click here</u> to continue."</li>
 * <li><b>internalErrorURL</b> = null</li>
 * <li><b>internalErrorNotificationEnabled</b> = true</li>
 * <li><b>internalErrorCaption</b> = "Internal error"</li>
 * <li><b>internalErrorMessage</b> = "Please notify the administrator.<br/>
 * Take note of any unsaved data, and <u>click here</u> to continue."</li>
 * <li><b>outOfSyncURL</b> = null</li>
 * <li><b>outOfSyncNotificationEnabled</b> = true</li>
 * <li><b>outOfSyncCaption</b> = "Out of sync"</li>
 * <li><b>outOfSyncMessage</b> = "Something has caused us to be out of sync with
 * the server.<br/>
 * Take note of any unsaved data, and <u>click here</u> to re-sync."</li>
 * <li><b>cookiesDisabledURL</b> = null</li>
 * <li><b>cookiesDisabledNotificationEnabled</b> = true</li>
 * <li><b>cookiesDisabledCaption</b> = "Cookies disabled"</li>
 * <li><b>cookiesDisabledMessage</b> = "This application requires cookies to
 * function.<br/>
 * Please enable cookies in your browser and <u>click here</u> to try again.</li>
 * </ul>
 * </p>
 * 
 */
public class SystemMessages implements Serializable {
    protected String sessionExpiredURL = null;
    protected boolean sessionExpiredNotificationEnabled = true;
    protected String sessionExpiredCaption = "Session Expired";
    protected String sessionExpiredMessage = "Take note of any unsaved data, and <u>click here</u> or press ESC key to continue.";

    protected String communicationErrorURL = null;
    protected boolean communicationErrorNotificationEnabled = true;
    protected String communicationErrorCaption = "Communication problem";
    protected String communicationErrorMessage = "Take note of any unsaved data, and <u>click here</u> or press ESC to continue.";

    protected String authenticationErrorURL = null;
    protected boolean authenticationErrorNotificationEnabled = true;
    protected String authenticationErrorCaption = "Authentication problem";
    protected String authenticationErrorMessage = "Take note of any unsaved data, and <u>click here</u> or press ESC to continue.";

    protected String internalErrorURL = null;
    protected boolean internalErrorNotificationEnabled = true;
    protected String internalErrorCaption = "Internal error";
    protected String internalErrorMessage = "Please notify the administrator.<br/>Take note of any unsaved data, and <u>click here</u> or press ESC to continue.";

    protected String outOfSyncURL = null;
    protected boolean outOfSyncNotificationEnabled = true;
    protected String outOfSyncCaption = "Out of sync";
    protected String outOfSyncMessage = "Something has caused us to be out of sync with the server.<br/>Take note of any unsaved data, and <u>click here</u> or press ESC to re-sync.";

    protected String cookiesDisabledURL = null;
    protected boolean cookiesDisabledNotificationEnabled = true;
    protected String cookiesDisabledCaption = "Cookies disabled";
    protected String cookiesDisabledMessage = "This application requires cookies to function.<br/>Please enable cookies in your browser and <u>click here</u> or press ESC to try again.";

    /**
     * Use {@link CustomizedSystemMessages} to customize
     */
    SystemMessages() {

    }

    /**
     * @return null to indicate that the application will be restarted after
     *         session expired message has been shown.
     */
    public String getSessionExpiredURL() {
        return sessionExpiredURL;
    }

    /**
     * @return true to show session expiration message.
     */
    public boolean isSessionExpiredNotificationEnabled() {
        return sessionExpiredNotificationEnabled;
    }

    /**
     * @return "" to show no caption.
     */
    public String getSessionExpiredCaption() {
        return (sessionExpiredNotificationEnabled ? sessionExpiredCaption
                : null);
    }

    /**
     * @return 
     *         "Take note of any unsaved data, and <u>click here</u> to continue."
     */
    public String getSessionExpiredMessage() {
        return (sessionExpiredNotificationEnabled ? sessionExpiredMessage
                : null);
    }

    /**
     * @return null to reload the application after communication error message.
     */
    public String getCommunicationErrorURL() {
        return communicationErrorURL;
    }

    /**
     * @return true to show the communication error message.
     */
    public boolean isCommunicationErrorNotificationEnabled() {
        return communicationErrorNotificationEnabled;
    }

    /**
     * @return "Communication problem"
     */
    public String getCommunicationErrorCaption() {
        return (communicationErrorNotificationEnabled ? communicationErrorCaption
                : null);
    }

    /**
     * @return 
     *         "Take note of any unsaved data, and <u>click here</u> to continue."
     */
    public String getCommunicationErrorMessage() {
        return (communicationErrorNotificationEnabled ? communicationErrorMessage
                : null);
    }

    /**
     * @return null to reload the application after authentication error
     *         message.
     */
    public String getAuthenticationErrorURL() {
        return authenticationErrorURL;
    }

    /**
     * @return true to show the authentication error message.
     */
    public boolean isAuthenticationErrorNotificationEnabled() {
        return authenticationErrorNotificationEnabled;
    }

    /**
     * @return "Authentication problem"
     */
    public String getAuthenticationErrorCaption() {
        return (authenticationErrorNotificationEnabled ? authenticationErrorCaption
                : null);
    }

    /**
     * @return 
     *         "Take note of any unsaved data, and <u>click here</u> to continue."
     */
    public String getAuthenticationErrorMessage() {
        return (authenticationErrorNotificationEnabled ? authenticationErrorMessage
                : null);
    }

    /**
     * @return null to reload the current URL after internal error message has
     *         been shown.
     */
    public String getInternalErrorURL() {
        return internalErrorURL;
    }

    /**
     * @return true to enable showing of internal error message.
     */
    public boolean isInternalErrorNotificationEnabled() {
        return internalErrorNotificationEnabled;
    }

    /**
     * @return "Internal error"
     */
    public String getInternalErrorCaption() {
        return (internalErrorNotificationEnabled ? internalErrorCaption : null);
    }

    /**
     * @return "Please notify the administrator.<br/>
     *         Take note of any unsaved data, and <u>click here</u> to
     *         continue."
     */
    public String getInternalErrorMessage() {
        return (internalErrorNotificationEnabled ? internalErrorMessage : null);
    }

    /**
     * @return null to reload the application after out of sync message.
     */
    public String getOutOfSyncURL() {
        return outOfSyncURL;
    }

    /**
     * @return true to enable showing out of sync message
     */
    public boolean isOutOfSyncNotificationEnabled() {
        return outOfSyncNotificationEnabled;
    }

    /**
     * @return "Out of sync"
     */
    public String getOutOfSyncCaption() {
        return (outOfSyncNotificationEnabled ? outOfSyncCaption : null);
    }

    /**
     * @return "Something has caused us to be out of sync with the server.<br/>
     *         Take note of any unsaved data, and <u>click here</u> to re-sync."
     */
    public String getOutOfSyncMessage() {
        return (outOfSyncNotificationEnabled ? outOfSyncMessage : null);
    }

    /**
     * Returns the URL the user should be redirected to after dismissing the
     * "you have to enable your cookies" message. Typically null.
     * 
     * @return A URL the user should be redirected to after dismissing the
     *         message or null to reload the current URL.
     */
    public String getCookiesDisabledURL() {
        return cookiesDisabledURL;
    }

    /**
     * Determines if "cookies disabled" messages should be shown to the end user
     * or not. If the notification is disabled the user will be immediately
     * redirected to the URL returned by {@link #getCookiesDisabledURL()}.
     * 
     * @return true to show "cookies disabled" messages to the end user, false
     *         to redirect to the given URL directly
     */
    public boolean isCookiesDisabledNotificationEnabled() {
        return cookiesDisabledNotificationEnabled;
    }

    /**
     * Returns the caption of the message shown to the user when cookies are
     * disabled in the browser.
     * 
     * @return The caption of the "cookies disabled" message
     */
    public String getCookiesDisabledCaption() {
        return (cookiesDisabledNotificationEnabled ? cookiesDisabledCaption
                : null);
    }

    /**
     * Returns the message shown to the user when cookies are disabled in the
     * browser.
     * 
     * @return The "cookies disabled" message
     */
    public String getCookiesDisabledMessage() {
        return (cookiesDisabledNotificationEnabled ? cookiesDisabledMessage
                : null);
    }

}
