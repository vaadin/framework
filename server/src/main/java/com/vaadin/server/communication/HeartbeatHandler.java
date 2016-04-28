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

package com.vaadin.server.communication;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import com.vaadin.server.ServletPortletHelper;
import com.vaadin.server.SessionExpiredHandler;
import com.vaadin.server.SynchronizedRequestHandler;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.ui.UIConstants;
import com.vaadin.ui.UI;

/**
 * Handles heartbeat requests. Heartbeat requests are periodically sent by the
 * client-side to inform the server that the UI sending the heartbeat is still
 * alive (the browser window is open, the connection is up) even when there are
 * no UIDL requests for a prolonged period of time. UIs that do not receive
 * either heartbeat or UIDL requests are eventually removed from the session and
 * garbage collected.
 * 
 * @author Vaadin Ltd
 * @since 7.1
 */
public class HeartbeatHandler extends SynchronizedRequestHandler implements
        SessionExpiredHandler {

    @Override
    protected boolean canHandleRequest(VaadinRequest request) {
        return ServletPortletHelper.isHeartbeatRequest(request);
    }

    /**
     * Handles a heartbeat request for the given session. Reads the GET
     * parameter named {@link UIConstants#UI_ID_PARAMETER} to identify the UI.
     * If the UI is found in the session, sets it
     * {@link UI#getLastHeartbeatTimestamp() heartbeat timestamp} to the current
     * time. Otherwise, writes a HTTP Not Found error to the response.
     */
    @Override
    public boolean synchronizedHandleRequest(VaadinSession session,
            VaadinRequest request, VaadinResponse response) throws IOException {
        UI ui = session.getService().findUI(request);
        if (ui != null) {
            ui.setLastHeartbeatTimestamp(System.currentTimeMillis());
            // Ensure that the browser does not cache heartbeat responses.
            // iOS 6 Safari requires this (#10370)
            response.setHeader("Cache-Control", "no-cache");
            // If Content-Type is not set, browsers assume text/html and may
            // complain about the empty response body (#12182)
            response.setHeader("Content-Type", "text/plain");
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "UI not found");
        }

        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.server.SessionExpiredHandler#handleSessionExpired(com.vaadin
     * .server.VaadinRequest, com.vaadin.server.VaadinResponse)
     */
    @Override
    public boolean handleSessionExpired(VaadinRequest request,
            VaadinResponse response) throws IOException {
        if (!ServletPortletHelper.isHeartbeatRequest(request)) {
            return false;
        }

        response.sendError(HttpServletResponse.SC_GONE, "Session expired");
        return true;
    }
}
