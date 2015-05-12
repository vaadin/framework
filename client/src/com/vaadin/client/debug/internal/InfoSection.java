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

package com.vaadin.client.debug.internal;

import java.util.List;
import java.util.logging.Level;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ApplicationConfiguration;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ValueMap;
import com.vaadin.shared.Version;
import com.vaadin.shared.util.SharedUtil;

/**
 * Information section of the debug window
 * 
 * @since 7.1
 * @author Vaadin Ltd
 */
public class InfoSection implements Section {

    private static final String THEME_VERSION_CLASSNAME = "v-vaadin-version";
    private static final String PRIMARY_STYLE_NAME = VDebugWindow.STYLENAME
            + "-info";
    private static final String ERROR_STYLE = Level.SEVERE.getName();
    private final HTML content = new HTML();
    private DebugButton tabButton = new DebugButton(Icon.INFO,
            "General information about the application(s)");
    private HTML controls = new HTML(tabButton.getTitle());

    private Timer refresher = new Timer() {
        @Override
        public void run() {
            refresh();
        }
    };

    /**
     * 
     */
    public InfoSection() {
        createContent();
    }

    /**
     * @since 7.1
     */
    private void createContent() {
        content.setStylePrimaryName(PRIMARY_STYLE_NAME);
        refresh();
    }

    private void addRow(String parameter, String value) {
        addRow(parameter, value, null);
    }

    private void addRow(String parameter, String value, String className) {
        Element row = DOM.createDiv();
        row.addClassName(VDebugWindow.STYLENAME + "-row");
        if (className != null) {
            row.addClassName(className);
        }
        Element span = DOM.createSpan();
        span.setClassName("caption");
        span.setInnerText(parameter);
        row.appendChild(span);
        span = DOM.createSpan();
        span.setClassName("value");
        span.setInnerText(value);
        row.appendChild(span);
        content.getElement().appendChild(row);

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.client.debug.internal.Section#getTabButton()
     */
    @Override
    public DebugButton getTabButton() {
        return tabButton;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.client.debug.internal.Section#getControls()
     */
    @Override
    public Widget getControls() {
        return controls;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.client.debug.internal.Section#getContent()
     */
    @Override
    public Widget getContent() {
        return content;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.client.debug.internal.Section#show()
     */
    @Override
    public void show() {
        refresh();
    }

    /**
     * Updates the information for all running applications
     * 
     * @since 7.1
     */
    private void refresh() {
        clear();
        List<ApplicationConnection> apps = ApplicationConfiguration
                .getRunningApplications();
        if (apps.size() == 0) {
            // try again in a while
            refresher.schedule(1000);
        } else {
            for (ApplicationConnection application : apps) {
                refresh(application);
            }
        }
    }

    /**
     * Updates the information for a single running application
     * 
     * @since 7.1
     */
    private void refresh(ApplicationConnection connection) {
        clear();
        ApplicationConfiguration configuration = connection.getConfiguration();

        addVersionInfo(configuration);
        addRow("Widget set", GWT.getModuleName());
        addRow("Theme", connection.getUIConnector().getActiveTheme());

        String communicationMethodInfo = connection
                .getCommunicationMethodName();
        int pollInterval = connection.getUIConnector().getState().pollInterval;
        if (pollInterval > 0) {
            communicationMethodInfo += " (poll interval " + pollInterval
                    + "ms)";
        }
        addRow("Communication method", communicationMethodInfo);

        String heartBeatInfo;
        if (configuration.getHeartbeatInterval() < 0) {
            heartBeatInfo = "Disabled";
        } else {
            heartBeatInfo = configuration.getHeartbeatInterval() + "s";
        }
        addRow("Heartbeat", heartBeatInfo);
    }

    /**
     * Logs version information for client/server/theme.
     * 
     * @param applicationConfiguration
     * @since 7.1
     */
    private void addVersionInfo(
            ApplicationConfiguration applicationConfiguration) {
        String clientVersion = Version.getFullVersion();
        String servletVersion = applicationConfiguration.getServletVersion();
        String atmosphereVersion = applicationConfiguration
                .getAtmosphereVersion();
        String jsVersion = applicationConfiguration.getAtmosphereJSVersion();

        String themeVersion;
        boolean themeOk;
        if (com.vaadin.client.BrowserInfo.get().isIE8()) {
            themeVersion = "<IE8 can't detect this>";
            themeOk = true;
        } else {
            themeVersion = getThemeVersion();
            themeOk = equalsEither(themeVersion, clientVersion, servletVersion);
        }

        boolean clientOk = equalsEither(clientVersion, servletVersion,
                themeVersion);
        boolean servletOk = equalsEither(servletVersion, clientVersion,
                themeVersion);
        addRow("Client engine version", clientVersion, clientOk ? null
                : ERROR_STYLE);
        addRow("Server engine version", servletVersion, servletOk ? null
                : ERROR_STYLE);
        addRow("Theme version", themeVersion, themeOk ? null : ERROR_STYLE);
        if (jsVersion != null) {
            addRow("Push server version", atmosphereVersion);
            addRow("Push client version", jsVersion
                    + " (note: does not need to match server version)");
        }
    }

    /**
     * Checks if the target value equals one of the reference values
     * 
     * @param target
     *            The value to compare
     * @param reference1
     *            A reference value
     * @param reference2
     *            A reference value
     * @return true if target equals one of the references, false otherwise
     */
    private boolean equalsEither(String target, String reference1,
            String reference2) {
        if (SharedUtil.equals(target, reference1)) {
            return true;
        }
        if (SharedUtil.equals(target, reference2)) {
            return true;
        }

        return false;
    }

    /**
     * Finds out the version of the current theme (i.e. the version of Vaadin
     * used to compile it)
     * 
     * @since 7.1
     * @return The full version as a string
     */
    private String getThemeVersion() {
        Element div = DOM.createDiv();
        div.setClassName(THEME_VERSION_CLASSNAME);
        RootPanel.get().getElement().appendChild(div);
        String version = getComputedStyle(div, ":after", "content");
        div.removeFromParent();
        if (version != null) {
            // String version = new ComputedStyle(div).getProperty("content");
            version = version.replace("'", "");
            version = version.replace("\"", "");
        }
        return version;
    }

    private native String getComputedStyle(Element elem, String pseudoElement,
            String property)
    /*-{
         if ($wnd.document.defaultView && $wnd.document.defaultView.getComputedStyle) {
             return $wnd.document.defaultView.getComputedStyle(elem, pseudoElement)[property];
        } else {
            return null;
        }
    }-*/;

    /**
     * Removes all content
     * 
     * @since 7.1
     */
    private void clear() {
        content.getElement().setInnerHTML("");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.client.debug.internal.Section#hide()
     */
    @Override
    public void hide() {
        refresher.cancel();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.client.debug.internal.Section#meta(com.vaadin.client.
     * ApplicationConnection, com.vaadin.client.ValueMap)
     */
    @Override
    public void meta(ApplicationConnection ac, ValueMap meta) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.client.debug.internal.Section#uidl(com.vaadin.client.
     * ApplicationConnection, com.vaadin.client.ValueMap)
     */
    @Override
    public void uidl(ApplicationConnection ac, ValueMap uidl) {
    }

}
