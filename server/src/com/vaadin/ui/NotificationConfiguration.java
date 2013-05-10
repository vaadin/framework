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
package com.vaadin.ui;

import java.io.Serializable;

import com.vaadin.shared.ui.ui.NotificationConfigurationBean;
import com.vaadin.shared.ui.ui.NotificationConfigurationBean.Role;
import com.vaadin.shared.ui.ui.UIState.NotificationConfigurationState;

/**
 * Provides methods for configuring the notification.
 * 
 * @author Vaadin Ltd
 * @since 7.1
 */
public interface NotificationConfiguration extends Serializable {
    public void setStyleConfiguration(String style, String prefix,
            String postfix, Role ariaRole);

    /**
     * Returns the complete configuration object for the given notification
     * style.
     * 
     * @param style
     *            String of the notification style to return
     * @return The notification configuration object
     */
    public NotificationConfigurationBean getStyleConfiguration(String style);

    /**
     * Sets the accessibility prefix for the given notification style.
     * 
     * This prefix is read to assistive device users in front of the content of
     * the notification, but not visible on the page.
     * 
     * @param style
     *            String of the notification style
     * @param prefix
     *            String that is placed before the notification content
     */
    public void setAssistivePrefixForStyle(String style, String prefix);

    /**
     * Returns the accessibility prefix for the given notification style.
     * 
     * This prefix is read to assistive device users in front of the content of
     * the notification, but not visible on the page.
     * 
     * @param style
     *            String of the notification style
     * @return The prefix of the provided notification style
     */
    public String getAssistivePrefixForStyle(String style);

    /**
     * Sets the accessibility postfix for the given notification style.
     * 
     * This postfix is read to assistive device users after the content of the
     * notification, but not visible on the page.
     * 
     * @param style
     *            String of the notification style
     * @param postfix
     *            String that is placed after the notification content
     */
    public void setAssistivePostfixForStyle(String style, String postfix);

    /**
     * Returns the accessibility postfix for the given notification style.
     * 
     * This postfix is read to assistive device users after the content of the
     * notification, but not visible on the page.
     * 
     * @param style
     *            String of the notification style
     * @return The postfix of the provided notification style
     */
    public String getAssistivePostfixForStyle(String style);

    /**
     * Sets the WAI-ARIA role for a notification style.
     * 
     * This role defines how an assistive device handles a notification.
     * Available roles are alert, alertdialog and status (@see <a
     * href="http://www.w3.org/TR/2011/CR-wai-aria-20110118/roles">Roles
     * Model</a>)
     * 
     * The default role is alert.
     * 
     * @param style
     *            String of the notification style
     * @param role
     *            Role to set for the notification type
     */
    public void setAssistiveRoleForStyle(String style, Role role);

    /**
     * Returns the WAI-ARIA role for a notification style.
     * 
     * This role defines how an assistive device handles a notification.
     * Available roles are alert, alertdialog and status (@see <a
     * href="http://www.w3.org/TR/2011/CR-wai-aria-20110118/roles">Roles
     * Model</a> )
     * 
     * The default role is alert.
     * 
     * @param style
     *            String of the notification style
     * @return The current Role for the notification type
     */
    public Role getAssistiveRoleForStyle(String style);
}

class NotificationConfigurationImpl implements NotificationConfiguration {

    private UI ui;

    public NotificationConfigurationImpl(UI ui) {
        this.ui = ui;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.ui.NotificationConfiguration#setStyleConfiguration(java.lang
     * .String, java.lang.String, java.lang.String,
     * com.vaadin.ui.NotificationConfiguration.Role)
     */
    @Override
    public void setStyleConfiguration(String style, String prefix,
            String postfix, Role ariaRole) {
        getState().setup.put(style, new NotificationConfigurationBean(prefix,
                postfix, ariaRole));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.ui.NotificationConfiguration#getStyleConfiguration(java.lang
     * .String)
     */
    @Override
    public NotificationConfigurationBean getStyleConfiguration(String style) {
        return getState(false).setup.get(style);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.ui.NotificationConfiguration#setStylePrefix(java.lang.String,
     * java.lang.String)
     */
    @Override
    public void setAssistivePrefixForStyle(String style, String prefix) {
        getConfigurationBean(style).setAssistivePrefix(prefix);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.ui.NotificationConfiguration#getStylePrefix(java.lang.String)
     */
    @Override
    public String getAssistivePrefixForStyle(String style) {
        NotificationConfigurationBean styleSetup = getState().setup.get(style);
        if (styleSetup != null) {
            return styleSetup.getAssistivePrefix();
        }

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.ui.NotificationConfiguration#setStylePostfix(com.vaadin.ui
     * .Notification.Type, java.lang.String)
     */
    @Override
    public void setAssistivePostfixForStyle(String style, String postfix) {
        getConfigurationBean(style).setAssistivePostfix(postfix);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.ui.NotificationConfiguration#getStylePostfix(com.vaadin.ui
     * .Notification.Type)
     */
    @Override
    public String getAssistivePostfixForStyle(String style) {
        NotificationConfigurationBean styleSetup = getState().setup.get(style);
        if (styleSetup != null) {
            return styleSetup.getAssistivePostfix();
        }

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.NotificationConfiguration#setStyleRole(com.vaadin.ui.
     * Notification.Type, com.vaadin.ui.NotificationConfiguration.Role)
     */
    @Override
    public void setAssistiveRoleForStyle(String style, Role role) {
        getConfigurationBean(style).setAssistiveRole(role);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.NotificationConfiguration#getStyleRole(com.vaadin.ui.
     * Notification.Type)
     */
    @Override
    public Role getAssistiveRoleForStyle(String style) {
        NotificationConfigurationBean styleSetup = getState().setup.get(style);
        if (styleSetup != null) {
            return styleSetup.getAssistiveRole();
        }

        return null;
    }

    private NotificationConfigurationBean getConfigurationBean(String style) {
        NotificationConfigurationBean styleSetup = getState().setup.get(style);
        if (styleSetup == null) {
            styleSetup = new NotificationConfigurationBean();
            getState().setup.put(style, styleSetup);
        }

        return styleSetup;
    }

    private NotificationConfigurationState getState() {
        return ui.getState().notificationConfiguration;
    }

    private NotificationConfigurationState getState(boolean markAsDirty) {
        return ui.getState(markAsDirty).notificationConfiguration;
    }

}
