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
package com.vaadin.client.communication;

import java.util.logging.Logger;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Timer;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ApplicationConnection.ApplicationStoppedEvent;
import com.vaadin.client.ApplicationConnection.ConnectionStatusEvent;
import com.vaadin.shared.ApplicationConstants;
import com.vaadin.shared.ui.ui.UIConstants;
import com.vaadin.shared.util.SharedUtil;

/**
 * Handles sending of heartbeats to the server and reacting to the response
 * 
 * @since 7.2
 * @author Vaadin Ltd
 */
public class Heartbeat {

    private Timer timer = new Timer() {
        @Override
        public void run() {
            send();
        }
    };

    private ApplicationConnection connection;
    private String uri;
    private int interval = -1;

    private static Logger getLogger() {
        return Logger.getLogger(Heartbeat.class.getName());
    }

    /**
     * Initializes the heartbeat for the given application connection
     * 
     * @param connection
     *            the connection
     */
    public void init(ApplicationConnection applicationConnection) {
        connection = applicationConnection;

        setInterval(connection.getConfiguration().getHeartbeatInterval());

        uri = SharedUtil.addGetParameters(connection
                .translateVaadinUri(ApplicationConstants.APP_PROTOCOL_PREFIX
                        + ApplicationConstants.HEARTBEAT_PATH + '/'),
                UIConstants.UI_ID_PARAMETER + "="
                        + connection.getConfiguration().getUIId());

        connection.addHandler(
                ApplicationConnection.ApplicationStoppedEvent.TYPE,
                new ApplicationConnection.ApplicationStoppedHandler() {

                    @Override
                    public void onApplicationStopped(
                            ApplicationStoppedEvent event) {
                        setInterval(-1);
                    }
                });
    }

    /**
     * Sends a heartbeat to the server
     */
    public void send() {
        timer.cancel();

        final RequestBuilder rb = new RequestBuilder(RequestBuilder.POST, uri);

        final RequestCallback callback = new RequestCallback() {

            @Override
            public void onResponseReceived(Request request, Response response) {
                int status = response.getStatusCode();

                // Notify network observers about response status
                connection.fireEvent(new ConnectionStatusEvent(status));

                if (status == Response.SC_OK) {
                    getLogger().fine("Heartbeat response OK");
                } else if (status == 0) {
                    getLogger().warning(
                            "Failed sending heartbeat, server is unreachable, retrying in "
                                    + interval + "secs.");
                } else if (status >= 500) {
                    getLogger().warning(
                            "Failed sending heartbeat, see server logs, retrying in "
                                    + interval + "secs.");
                } else if (status == Response.SC_GONE) {
                    connection.showSessionExpiredError(null);
                    // If session is expired break the loop
                    return;
                } else {
                    getLogger().warning(
                            "Failed sending heartbeat to server. Error code: "
                                    + status);
                }

                // Don't break the loop
                schedule();
            }

            @Override
            public void onError(Request request, Throwable exception) {
                getLogger().severe(
                        "Exception sending heartbeat: "
                                + exception.getMessage());
                // Notify network observers about response status
                connection.fireEvent(new ConnectionStatusEvent(0));
                // Don't break the loop
                schedule();
            }
        };

        rb.setCallback(callback);

        try {
            getLogger().fine("Sending heartbeat request...");
            rb.send();
        } catch (RequestException re) {
            callback.onError(null, re);
        }

    }

    /**
     * @return the interval at which heartbeat requests are sent
     */
    public int getInterval() {
        return interval;
    }

    /**
     * Updates the schedule of the heartbeat to match the set interval. A
     * negative interval disables the heartbeat.
     */
    public void schedule() {
        if (interval > 0) {
            getLogger()
                    .fine("Scheduling heartbeat in " + interval + " seconds");
            timer.schedule(interval * 1000);
        } else {
            getLogger().fine("Disabling heartbeat");
            timer.cancel();
        }
    }

    /**
     * @return the application connection
     */
    @Deprecated
    protected ApplicationConnection getConnection() {
        return connection;
    }

    /**
     * Changes the heartbeatInterval in runtime and applies it.
     * 
     * @param heartbeatInterval
     *            new interval in seconds.
     */
    public void setInterval(int heartbeatInterval) {
        getLogger().info(
                "Setting hearbeat interval to " + heartbeatInterval + "sec.");
        interval = heartbeatInterval;
        schedule();
    }
}
