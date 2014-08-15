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

/**
 * 
 */
package com.vaadin.ui;

import java.io.Serializable;

import com.vaadin.shared.ui.ui.NotificationRole;
import com.vaadin.shared.ui.ui.UIState.NotificationTypeConfiguration;
import com.vaadin.ui.Notification.Type;

/**
 * Provides methods for configuring the notification.
 *
 * @author Vaadin Ltd
 * @since 7.2
 */
public interface NotificationConfiguration extends Serializable {
    /**
     * Sets the accessibility prefix for a notification type.
     * <p>
     * This prefix is read to assistive device users before the content of the
     * notification, but not visible on the page.
     *
     * @param type
     *            type of the notification
     * @param prefix
     *            string that is placed before the notification content
     */
    public void setAssistivePrefix(Type type, String prefix);

    /**
     * Gets the accessibility prefix for a notification type.
     * <p>
     * This prefix is read to assistive device users before the content of the
     * notification, but not visible on the page.
     *
     * @param type
     *            type of the notification
     * @return The accessibility prefix for the provided notification type
     */
    public String getAssistivePrefix(Type type);

    /**
     * Sets the accessibility postfix for a notification type.
     * <p>
     * This postfix is read to assistive device users after the content of the
     * notification, but not visible on the page.
     *
     * @param type
     *            type of the notification
     * @param postfix
     *            string that is placed after the notification content
     */
    public void setAssistivePostfix(Type type, String postfix);

    /**
     * Gets the accessibility postfix for a notification type.
     * <p>
     * This postfix is read to assistive device users after the content of the
     * notification, but not visible on the page.
     *
     * @param type
     *            type of the notification
     * @return The accessibility postfix for the provided notification type
     */
    public String getAssistivePostfix(Type type);

    /**
     * Sets the WAI-ARIA role for a notification type.
     * <p>
     * This role defines how an assistive device handles a notification.
     * Available roles are alert and status (@see <a
     * href="http://www.w3.org/TR/2011/CR-wai-aria-20110118/roles">Roles
     * Model</a>).
     *
     * The default role is alert.
     *
     * @param type
     *            type of the notification
     * @param role
     *            role to set for the notification type
     */
    public void setAssistiveRole(Type type, NotificationRole role);

    /**
     * Gets the WAI-ARIA role for a notification type.
     * <p>
     * This role defines how an assistive device handles a notification.
     * Available roles are alert and status (@see <a
     * href="http://www.w3.org/TR/2011/CR-wai-aria-20110118/roles">Roles
     * Model</a>)
     * <p>
     * The default role is alert.
     *
     * @param type
     *            type of the notification
     * @return role to set for the notification type
     */
    public NotificationRole getAssistiveRole(Type type);
}

class NotificationConfigurationImpl implements NotificationConfiguration {

    private UI ui;

    public NotificationConfigurationImpl(UI ui) {
        this.ui = ui;
    }

    @Override
    public void setAssistivePrefix(Type type, String prefix) {
        getConfigurationBean(type).prefix = prefix;
    }

    @Override
    public String getAssistivePrefix(Type type) {
        NotificationTypeConfiguration styleSetup = getTypeConf(type);
        if (styleSetup != null) {
            return styleSetup.prefix;
        }

        return null;
    }

    @Override
    public void setAssistivePostfix(Type type, String postfix) {
        getConfigurationBean(type).postfix = postfix;
    }

    @Override
    public String getAssistivePostfix(Type type) {
        NotificationTypeConfiguration styleSetup = getTypeConf(type);
        if (styleSetup != null) {
            return styleSetup.postfix;
        }

        return null;
    }

    @Override
    public void setAssistiveRole(Type type, NotificationRole role) {
        getConfigurationBean(type).notificationRole = role;
    }

    @Override
    public NotificationRole getAssistiveRole(Type type) {
        NotificationTypeConfiguration styleSetup = getTypeConf(type);
        if (styleSetup != null) {
            return styleSetup.notificationRole;
        }

        return null;
    }

    private NotificationTypeConfiguration getConfigurationBean(Type type) {
        NotificationTypeConfiguration styleSetup = getTypeConf(type);
        if (styleSetup == null) {
            styleSetup = new NotificationTypeConfiguration();
            ui.getState().notificationConfigurations.put(type.getStyle(),
                    styleSetup);
        }

        return styleSetup;
    }

    private NotificationTypeConfiguration getTypeConf(Type type) {
        return ui.getState().notificationConfigurations.get(type.getStyle());
    }
}
