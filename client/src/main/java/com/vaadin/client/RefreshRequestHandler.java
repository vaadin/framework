/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.vaadin.shared.ApplicationConstants;

/**
 * Handles automatic reloading after a server restart or redeploy when not
 * running in production mode.
 *
 * @author Vaadin Ltd
 */
public class RefreshRequestHandler {
    private ApplicationConnection connection;

    private class RequestRefreshCallback implements RequestCallback {
        private int refreshRetryCount = 0;

        @Override
        public void onResponseReceived(Request request, Response response) {
            int code = response.getStatusCode();
            if (code == 0) {
                retryRefreshRequest();
                return;
            }
            if (code == 410
                    && !"Session expired".equals(response.getStatusText())) {
                // Pretend it's a generic server error
                code = 400;
            }

            if (code != 200 && code != 410) {
                getLogger().log(Level.INFO,
                        "Got status code " + code + " for refresh request");
                return;
            }
            String token = response.getHeader("X-VAADIN-REFRESH");
            if (token == null && code != 410) {
                // token is null, we need to
                // refresh
                retryRefreshRequest();
            } else if (refreshToken == null) {
                /*
                 * we don't have a refreshtoken yet, let's set it to match the
                 * serverside token
                 */
                refreshToken = token;

                startRefreshRequest();
            } else if (!refreshToken.equals(token) || code == 410) {

                /*
                 * The token we have doesn't match the token the server has.
                 * This means that the servlet has been reloaded and we need to
                 * refresh the page.
                 */

                refreshRetryCount = 0;

                refreshToken = token;

                /*
                 * Reload or do repaint all depending on whether server still
                 * has an UI with our id
                 */
                String uiIds = response.getText();
                String uiId = connection.getUIConnector().getConnectorId();
                if (uiIds.contains('|' + uiId + '|')) {
                    getLogger().log(Level.INFO,
                            "Got refresh token " + token + " containing UI id "
                                    + uiId + " . Repainting all.");
                    connection.getMessageSender().resynchronize();
                } else {
                    getLogger().log(Level.INFO, "Got refresh token " + token
                            + " without UI id " + uiId + " . Refreshing.");
                    Window.Location.reload();
                }
            } else {
                /*
                 * Expected connection cycling, just request again
                 */
                startRefreshRequest();
            }
        }

        @Override
        public void onError(Request request, Throwable exception) {
            getLogger().log(Level.INFO, "Refresh request error", exception);
            retryRefreshRequest();
        }

        private void retryRefreshRequest() {
            if (refreshRetryCount++ < 10) {
                new Timer() {
                    @Override
                    public void run() {
                        startRefreshRequest();
                    }
                }.schedule(500 * refreshRetryCount);
            } else {
                getLogger().log(Level.INFO,
                        "Refresh request retry limit reached");
            }
        }
    }

    public RefreshRequestHandler(ApplicationConnection connection) {
        this.connection = connection;
    }

    // Token to recognize a redeployed application, initialized based on the
    // first response we get
    private String refreshToken = null;

    public void startRefreshRequest() {
        /*
         * This is a hanging request; if nothing changes (i.e. the server and
         * client both agree on the value of the refresh token, it will cycle
         * (default duration five minutes). However, if the servlet is closing
         * down we will receive a response.
         *
         * If the servlet and client disagree on the value of the refresh token,
         * we will try again until they do agree.
         */
        try {
            getLogger().log(Level.INFO,
                    "Sending refresh request with token " + refreshToken);
            String uri = connection
                    .translateVaadinUri(ApplicationConstants.APP_PROTOCOL_PREFIX
                            + ApplicationConstants.REFRESH_PATH
                            + "?refresh-token=" + refreshToken);
            new RequestBuilder(RequestBuilder.GET, uri).sendRequest(null,
                    new RequestRefreshCallback());
        } catch (RequestException e) {
            getLogger().log(Level.WARNING, "Refresh request failed", e);
        }
    }

    private static Logger getLogger() {
        return Logger.getLogger(RefreshRequestHandler.class.getName());
    }

}
