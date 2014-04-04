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
package com.vaadin.shared.ui.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vaadin.shared.communication.PushMode;
import com.vaadin.shared.ui.TabIndexState;
import com.vaadin.shared.ui.ui.NotificationConfigurationBean.Role;

public class UIState extends TabIndexState {
    public TooltipConfigurationState tooltipConfiguration = new TooltipConfigurationState();
    public LoadingIndicatorConfigurationState loadingIndicatorConfiguration = new LoadingIndicatorConfigurationState();
    public NotificationConfigurationState notificationConfiguration = new NotificationConfigurationState();
    public int pollInterval = -1;

    // Informing users of assistive devices, that the content of this container
    // is announced automatically and does not need to be navigated into
    public String overlayContainerLabel = "This content is announced automatically and does not need to be navigated into.";

    public static class LoadingIndicatorConfigurationState implements
            Serializable {
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

    public static class NotificationConfigurationState implements Serializable {
        public Map<String, NotificationConfigurationBean> setup = new HashMap<String, NotificationConfigurationBean>();
        {
            setup.put("error", new NotificationConfigurationBean("Error: ",
                    " - close with ESC-key", Role.ALERT));
            setup.put("warning", new NotificationConfigurationBean("Warning: ",
                    null, Role.ALERT));
            setup.put("humanized", new NotificationConfigurationBean("Info: ",
                    null, Role.ALERT));
            setup.put("tray", new NotificationConfigurationBean("Status: ",
                    null, Role.STATUS));
            setup.put("assistive", new NotificationConfigurationBean("Note: ",
                    null, Role.STATUS));
        }
    }

    public static class PushConfigurationState implements Serializable {
        public static final String TRANSPORT_PARAM = "transport";
        public static final String FALLBACK_TRANSPORT_PARAM = "fallbackTransport";

        public PushMode mode = PushMode.DISABLED;
        public Map<String, String> parameters = new HashMap<String, String>();
        {
            parameters
                    .put(TRANSPORT_PARAM, Transport.WEBSOCKET.getIdentifier());
            parameters.put(FALLBACK_TRANSPORT_PARAM,
                    Transport.LONG_POLLING.getIdentifier());
        }
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
     * Configuration for the push channel
     */
    public PushConfigurationState pushConfiguration = new PushConfigurationState();

    {
        primaryStyleName = "v-ui";
        // Default is 1 for legacy reasons
        tabIndex = 1;
    }

    public static class LocaleServiceState implements Serializable {
        public List<LocaleData> localeData = new ArrayList<LocaleData>();
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
