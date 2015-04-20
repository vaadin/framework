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
import com.google.gwt.http.client.Response;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.Timer;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.WidgetUtil;

import elemental.json.JsonObject;

/**
 * TODO
 * 
 * @since
 * @author Vaadin Ltd
 */
public class CommunicationProblemHandler {

    private ApplicationConnection connection;

    /**
     * Sets the application connection this queue is connected to
     *
     * @param connection
     *            the application connection this queue is connected to
     */
    public void setConnection(ApplicationConnection connection) {
        this.connection = connection;
    }

    public static Logger getLogger() {
        return Logger.getLogger(CommunicationProblemHandler.class.getName());
    }

    /**
     * @param payload
     * @param communicationProblemEvent
     */
    public void xhrException(JsonObject payload, CommunicationProblemEvent event) {
        handleUnrecoverableCommunicationError(
                event.getException().getMessage(), event);

    }

    /**
     * @param event
     * @param retry
     */
    public void xhrInvalidStatusCode(final CommunicationProblemEvent event,
            boolean retry) {
        Response response = event.getResponse();
        int statusCode = response.getStatusCode();
        if (statusCode == 0) {
            handleNoConnection(event, retry);
        } else if (statusCode == 401) {
            handleAuthorizationFailed(event);
        } else if (statusCode == 503) {
            handleServiceUnavailable(event);
        } else if ((statusCode / 100) == 4) {
            // Handle all 4xx errors the same way as (they are
            // all permanent errors)
            String msg = "UIDL could not be read from server."
                    + " Check servlets mappings. Error code: " + statusCode;
            handleUnrecoverableCommunicationError(msg, event);
        } else if ((statusCode / 100) == 5) {
            // Something's wrong on the server, there's nothing the
            // client can do except maybe try again.
            String msg = "Server error. Error code: " + statusCode;
            handleUnrecoverableCommunicationError(msg, event);
            return;
        }

    }

    /**
     * @since
     * @param event
     */
    private void handleServiceUnavailable(final CommunicationProblemEvent event) {
        /*
         * We'll assume msec instead of the usual seconds. If there's no
         * Retry-After header, handle the error like a 500, as per RFC 2616
         * section 10.5.4.
         */
        String delay = event.getResponse().getHeader("Retry-After");
        if (delay != null) {
            getLogger().warning("503, retrying in " + delay + "msec");
            (new Timer() {
                @Override
                public void run() {
                    // doUidlRequest does not call startRequest so we do
                    // not call endRequest before it
                    getServerCommunicationHandler().doUidlRequest(
                            event.getUri(), event.getPayload(), true);
                }
            }).schedule(Integer.parseInt(delay));
            return;
        } else {
            String msg = "Server error. Error code: "
                    + event.getResponse().getStatusCode();
            handleUnrecoverableCommunicationError(msg, event);
        }

    }

    /**
     * @since
     * @param event
     */
    private void handleAuthorizationFailed(CommunicationProblemEvent event) {
        /*
         * Authorization has failed (401). Could be that the session has timed
         * out and the container is redirecting to a login page.
         */
        connection.showAuthenticationError("");
        endRequestAndStopApplication();
    }

    /**
     * @since
     */
    private void endRequestAndStopApplication() {
        getServerCommunicationHandler().endRequest();

        // Consider application not running any more and prevent all
        // future requests
        connection.setApplicationRunning(false);
    }

    /**
     * @since
     * @param event
     * @param retry
     */
    private void handleNoConnection(final CommunicationProblemEvent event,
            boolean retry) {
        if (retry) {
            /*
             * There are 2 situations where the error can pop up:
             * 
             * 1) Request was most likely canceled because the browser is maybe
             * navigating away from the page. Just send the request again
             * without displaying any error in case the navigation isn't carried
             * through.
             * 
             * 2) The browser failed to establish a network connection. This was
             * observed with keep-alive requests, and under wi-fi roaming
             * conditions.
             * 
             * Status code 0 does indicate that there was no server side
             * processing, so we can retry the request.
             */
            getLogger().warning("Status code 0, retrying");
            (new Timer() {
                @Override
                public void run() {
                    // doUidlRequest does not call startRequest so we do
                    // not call endRequest before it
                    getServerCommunicationHandler().doUidlRequest(
                            event.getUri(), event.getPayload(), false);
                }
            }).schedule(100);
        } else {
            handleUnrecoverableCommunicationError(
                    "Invalid status code 0 (server down?)", event);
        }

    }

    private void handleUnrecoverableCommunicationError(String details,
            CommunicationProblemEvent event) {
        Response response = event.getResponse();
        int statusCode = -1;
        if (response != null) {
            statusCode = response.getStatusCode();
        }
        connection.handleCommunicationError(details, statusCode);

        endRequestAndStopApplication();
    }

    /**
     * @since
     * @param event
     */
    public void xhrInvalidContent(CommunicationProblemEvent event) {
        String responseText = event.getResponse().getText();
        /*
         * A servlet filter or equivalent may have intercepted the request and
         * served non-UIDL content (for instance, a login page if the session
         * has expired.) If the response contains a magic substring, do a
         * synchronous refresh. See #8241.
         */
        MatchResult refreshToken = RegExp.compile(
                ApplicationConnection.UIDL_REFRESH_TOKEN
                        + "(:\\s*(.*?))?(\\s|$)").exec(responseText);
        if (refreshToken != null) {
            WidgetUtil.redirect(refreshToken.getGroup(2));
        } else {
            handleUnrecoverableCommunicationError(
                    "Invalid JSON response from server: " + responseText, event);
        }

    }

    private ServerCommunicationHandler getServerCommunicationHandler() {
        return connection.getServerCommunicationHandler();
    }

    /**
     * Called when a heartbeat request returns a status code other than 200
     * 
     * @param request
     *            The heartbeat request
     * @param response
     *            The heartbeat response
     * @return true if a new heartbeat should be sent, false if no further
     *         heartbeats should be sent
     */
    public boolean heartbeatInvalidStatusCode(Request request, Response response) {
        int status = response.getStatusCode();
        int interval = connection.getHeartbeat().getInterval();
        if (status == 0) {
            getLogger().warning(
                    "Failed sending heartbeat, server is unreachable, retrying in "
                            + interval + "secs.");
        } else if (status == Response.SC_GONE) {
            // FIXME Stop application?
            connection.showSessionExpiredError(null);
            // If session is expired break the loop
            return false;
        } else if (status >= 500) {
            getLogger().warning(
                    "Failed sending heartbeat, see server logs, retrying in "
                            + interval + "secs.");
        } else {
            getLogger()
                    .warning(
                            "Failed sending heartbeat to server. Error code: "
                                    + status);
        }

        return true;
    }

    /**
     * Called when an exception occurs during a heartbeat request
     * 
     * @param request
     *            The heartbeat request
     * @param exception
     *            The exception which occurred
     * @return true if a new heartbeat should be sent, false if no further
     *         heartbeats should be sent
     */
    public boolean heartbeatException(Request request, Throwable exception) {
        getLogger().severe(
                "Exception sending heartbeat: " + exception.getMessage());
        return true;
    }
}
