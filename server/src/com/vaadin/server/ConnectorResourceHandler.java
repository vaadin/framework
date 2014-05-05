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
package com.vaadin.server;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import com.vaadin.shared.ApplicationConstants;
import com.vaadin.ui.UI;
import com.vaadin.util.CurrentInstance;

public class ConnectorResourceHandler implements RequestHandler {
    // APP/connector/[uiid]/[cid]/[filename.xyz]
    private static final Pattern CONNECTOR_RESOURCE_PATTERN = Pattern
            .compile("^/?" + ApplicationConstants.APP_PATH + '/'
                    + ConnectorResource.CONNECTOR_PATH + '/'
                    + "(\\d+)/(\\d+)/(.*)");

    private static Logger getLogger() {
        return Logger.getLogger(ConnectorResourceHandler.class.getName());

    }

    @Override
    public boolean handleRequest(VaadinSession session, VaadinRequest request,
            VaadinResponse response) throws IOException {
        String requestPath = request.getPathInfo();
        if (requestPath == null) {
            return false;
        }
        Matcher matcher = CONNECTOR_RESOURCE_PATTERN.matcher(requestPath);
        if (!matcher.matches()) {
            return false;
        }
        String uiId = matcher.group(1);
        String cid = matcher.group(2);
        String key = matcher.group(3);

        session.lock();
        UI ui;
        ClientConnector connector;
        try {
            ui = session.getUIById(Integer.parseInt(uiId));
            if (ui == null) {
                return error(request, response,
                        "Ignoring connector request for no-existent root "
                                + uiId);
            }

            connector = ui.getConnectorTracker().getConnector(cid);
            if (connector == null) {
                return error(request, response,
                        "Ignoring connector request for no-existent connector "
                                + cid + " in root " + uiId);
            }

        } finally {
            session.unlock();
        }

        Map<Class<?>, CurrentInstance> oldInstances = CurrentInstance
                .setCurrent(ui);
        try {
            if (!connector.handleConnectorRequest(request, response, key)) {
                return error(request, response, connector.getClass()
                        .getSimpleName()
                        + " ("
                        + connector.getConnectorId()
                        + ") did not handle connector request for " + key);
            }
        } catch (Exception e) {
            session.lock();
            try {
                session.getCommunicationManager()
                        .handleConnectorRelatedException(connector, e);
            } finally {
                session.unlock();
            }
        } finally {
            CurrentInstance.restoreInstances(oldInstances);
        }

        return true;
    }

    private static boolean error(VaadinRequest request,
            VaadinResponse response, String logMessage) throws IOException {
        getLogger().log(Level.WARNING, logMessage);
        response.sendError(HttpServletResponse.SC_NOT_FOUND,
                request.getPathInfo() + " can not be found");

        // Request handled (though not in a nice way)
        return true;
    }
}
