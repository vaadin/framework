/*
 * Copyright 2000-2018 Vaadin Ltd.
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
package com.vaadin.shared.ui.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vaadin.shared.annotations.NoLayout;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.shared.ui.AbstractSingleComponentContainerState;

public class UIState extends AbstractSingleComponentContainerState {
    /**
     * The <i>tabulator index</i> of the field.
     */
    @NoLayout
    public int tabIndex = 0;

    public TooltipConfigurationState tooltipConfiguration = new TooltipConfigurationState();
    public LoadingIndicatorConfigurationState loadingIndicatorConfiguration = new LoadingIndicatorConfigurationState();
    public int pollInterval = -1;

    // Informing users of assistive devices, that the content of this container
    // is announced automatically and does not need to be navigated into
    public String overlayContainerLabel = "This content is announced automatically and does not need to be navigated into.";
    public Map<String, NotificationTypeConfiguration> notificationConfigurations = new HashMap<>();
    {
        notificationConfigurations.put("error",
                new NotificationTypeConfiguration("Error: ",
                        " - close with ESC-key", NotificationRole.ALERT));
        notificationConfigurations.put("warning",
                new NotificationTypeConfiguration("Warning: ", null,
                        NotificationRole.ALERT));
        notificationConfigurations.put("humanized",
                new NotificationTypeConfiguration("Info: ", null,
                        NotificationRole.ALERT));

        // We use alert instead of status for all notifications because
        // (at least) Jaws 16 and earlier fail to announce any role=status
        // message in Chrome/Firefox
        notificationConfigurations.put("tray",
                new NotificationTypeConfiguration("Status: ", null,
                        NotificationRole.ALERT));
        notificationConfigurations.put("assistive",
                new NotificationTypeConfiguration("Note: ", null,
                        NotificationRole.ALERT));
    }
    /**
     * State related to the Page class.
     */
    public PageState pageState = new PageState();
    /**
     * State related to the LocaleService class.
     */
    public LocaleServiceState localeServiceState = new LocaleServiceState();
    /**
     * Configuration for the push channel.
     */
    public PushConfigurationState pushConfiguration = new PushConfigurationState();
    /**
     * Currently used theme.
     *
     * @since 7.3
     */
    public String theme;
    public ReconnectDialogConfigurationState reconnectDialogConfiguration = new ReconnectDialogConfigurationState();
    {
        primaryStyleName = "v-ui";
        // Default is 1 for legacy reasons
        tabIndex = 1;
    }

    /**
     * Enable Mobile HTML5 DnD support.
     *
     * @since 8.1
     */
    public boolean enableMobileHTML5DnD = false;

    public static class LoadingIndicatorConfigurationState
            implements Serializable {
        public int firstDelay = 300;
        public int secondDelay = 1500;
        public int thirdDelay = 5000;
    }

    public static class TooltipConfigurationState implements Serializable {
        public int openDelay = 750;
        public int quickOpenDelay = 100;
        public int quickOpenTimeout = 1000;
        public int closeTimeout = 300;
        public int maxWidth = 500;
    }

    public static class NotificationTypeConfiguration implements Serializable {
        public String prefix;
        public String postfix;
        public NotificationRole notificationRole = NotificationRole.ALERT;

        public NotificationTypeConfiguration() {
        }

        public NotificationTypeConfiguration(String prefix, String postfix,
                NotificationRole role) {
            this.prefix = prefix;
            this.postfix = postfix;
            notificationRole = role;
        }
    }

    public static class PushConfigurationState implements Serializable {
        public static final String TRANSPORT_PARAM = "transport";
        public static final String FALLBACK_TRANSPORT_PARAM = "fallbackTransport";

        public boolean alwaysUseXhrForServerRequests = false;
        public PushMode mode = PushMode.DISABLED;
        public String pushUrl = null;
        public Map<String, String> parameters = new HashMap<>();
        {
            parameters.put(TRANSPORT_PARAM,
                    Transport.WEBSOCKET.getIdentifier());
            parameters.put(FALLBACK_TRANSPORT_PARAM,
                    Transport.LONG_POLLING.getIdentifier());
        }
    }

    public static class ReconnectDialogConfigurationState
            implements Serializable {
        public String dialogText = "Server connection lost, trying to reconnect...";
        public String dialogTextGaveUp = "Server connection lost.";
        public int reconnectAttempts = 10000;
        public int reconnectInterval = 5000;
        public int dialogGracePeriod = 400;
        public boolean dialogModal = false;
    }

    public static class LocaleServiceState implements Serializable {
        public List<LocaleData> localeData = new ArrayList<>();
    }

    public static class LocaleData implements Serializable {
        public String name;
        public String[] monthNames;
        public String[] shortMonthNames;
        public String[] shortDayNames;
        public String[] dayNames;
        public int firstDayOfWeek;
        public String dateFormat;
        public boolean twelveHourClock;
        public String hourMinuteDelimiter;
        public String am;
        public String pm;

    }

}
