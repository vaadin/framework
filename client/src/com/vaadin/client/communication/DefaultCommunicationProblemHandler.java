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

/**
 * Default implementation of the communication problem handler.
 * 
 * Implements error handling by assuming all problems are terminal and simply
 * showing a notification to the user
 * 
 * @since 7.6
 * @author Vaadin Ltd
 */
public class DefaultCommunicationProblemHandler implements
        CommunicationProblemHandler {

    private ApplicationConnection connection;

    @Override
    public void setConnection(ApplicationConnection connection) {
        this.connection = connection;
    }

    protected ApplicationConnection getConnection() {
        return connection;
    }

    public static Logger getLogger() {
        return Logger.getLogger(DefaultCommunicationProblemHandler.class
                .getName());
    }

    @Override
    public void xhrException(CommunicationProblemEvent event) {
        handleUnrecoverableCommunicationError(
                event.getException().getMessage(), event);

    }

    @Override
    public void xhrInvalidStatusCode(final CommunicationProblemEvent event) {
        Response response = event.getResponse();
        int statusCode = response.getStatusCode();
        if (statusCode == 0) {
            handleNoConnection(event);
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
                    // send does not call startRequest so we do
                    // not call endRequest before it
                    getServerCommunicationHandler().send(event.getPayload());
                }
            }).schedule(Integer.parseInt(delay));
            return;
        } else {
            String msg = "Server error. Error code: "
                    + event.getResponse().getStatusCode();
            handleUnrecoverableCommunicationError(msg, event);
        }

    }

    private void handleAuthorizationFailed(CommunicationProblemEvent event) {
        /*
         * Authorization has failed (401). Could be that the session has timed
         * out and the container is redirecting to a login page.
         */
        connection.showAuthenticationError("");
        endRequestAndStopApplication();
    }

    private void endRequestAndStopApplication() {
        getServerCommunicationHandler().endRequest();

        // Consider application not running any more and prevent all
        // future requests
        connection.setApplicationRunning(false);
    }

    private void handleNoConnection(final CommunicationProblemEvent event) {
        handleUnrecoverableCommunicationError(
                "Invalid status code 0 (server down?)", event);

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

    @Override
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

    @Override
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

    @Override
    public boolean heartbeatException(Request request, Throwable exception) {
        getLogger().severe(
                "Exception sending heartbeat: " + exception.getMessage());
        return true;
    }

    @Override
    public void pushError(PushConnection pushConnection) {
        connection.handleCommunicationError("Push connection using "
                + pushConnection.getTransportType() + " failed!", -1);
    }

    @Override
    public void pushClientTimeout(PushConnection pushConnection) {
        connection
                .handleCommunicationError(
                        "Client unexpectedly disconnected. Ensure client timeout is disabled.",
                        -1);

    }

    @Override
    public void pushScriptLoadError(String resourceUrl) {
        connection.handleCommunicationError(resourceUrl
                + " could not be loaded. Push will not work.", 0);

    }

    @Override
    public void heartbeatOk() {
        getLogger().fine("Heartbeat response OK");
    }

    @Override
    public void xhrOk() {

    }

    @Override
    public void pushClosed(PushConnection pushConnection) {
    }

    @Override
    public void pushReconnectPending(PushConnection pushConnection) {
    }

    @Override
    public void pushOk(PushConnection pushConnection) {
    }
}
