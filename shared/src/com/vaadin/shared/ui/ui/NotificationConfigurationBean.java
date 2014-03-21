/*
 * Copyright 2000-2013 Vaadin Ltd.
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

/**
 * 
 */
package com.vaadin.shared.ui.ui;

import java.io.Serializable;

/**
 * Holds configuration information for a notification type.
 * 
 * @author Vaadin Ltd
 */
public class NotificationConfigurationBean implements Serializable {
    /**
     * Available WAI-ARIA roles for a notification.
     */
    public enum Role {
        ALERT, STATUS
    }

    private String prefix;
    private String postfix;
    private Role role = Role.ALERT;

    public NotificationConfigurationBean() {
    }

    public NotificationConfigurationBean(String prefix, String postfix,
            Role role) {
        this.prefix = prefix;
        this.postfix = postfix;
        this.role = role;
    }

    /**
     * Returns the accessibility prefix, which is placed before the notification
     * content.
     * 
     * @return the prefix
     */
    public String getAssistivePrefix() {
        return prefix;
    }

    /**
     * Sets the accessibility prefix, which is placed before the notification
     * content.
     * 
     * @param pefix
     *            the prefix to set
     */
    public void setAssistivePrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * Checks if an accessibility prefix is set.
     * 
     * @return true when assistivePrefix is not null and has a length > 0, false
     *         otherwise
     */
    public boolean hasAssistivePrefix() {
        return prefix != null && !prefix.isEmpty();
    }

    /**
     * Returns the accessibility postfix, which is placed after the notification
     * content.
     * 
     * @return the postfix
     */
    public String getAssistivePostfix() {
        return postfix;
    }

    /**
     * Sets the accessibility postfix, which is placed after the notification
     * content.
     * 
     * @param postfix
     *            the postfix to set
     */
    public void setAssistivePostfix(String postfix) {
        this.postfix = postfix;
    }

    /**
     * Checks if an accessibility postfix is set.
     * 
     * @return true when postfix is not null and has a length > 0, false
     *         otherwise
     */
    public boolean hasAssistivePostfix() {
        return postfix != null && !postfix.isEmpty();
    }

    /**
     * Returns the WAI-ARIA role that defines how an assistive device will
     * inform the user about a notification.
     * 
     * @return the role
     */
    public Role getAssistiveRole() {
        return role;
    }

    /**
     * Sets the WAI-ARIA role that defines how an assistive device will inform
     * the user about a notification.
     * 
     * Available roles are alert, alertdialog and status (@see <a
     * href="http://www.w3.org/TR/2011/CR-wai-aria-20110118/roles">Roles
     * Model</a>).
     * 
     * @param role
     *            the role to set
     */
    public void setAssistiveRole(Role role) {
        this.role = role;
    }
}
