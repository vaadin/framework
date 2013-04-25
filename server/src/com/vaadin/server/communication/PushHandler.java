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

package com.vaadin.server.communication;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.atmosphere.cpr.AtmosphereHandler;
import org.atmosphere.cpr.AtmosphereRequest;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResource.TRANSPORT;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.json.JSONException;

import com.vaadin.server.LegacyCommunicationManager.InvalidUIDLSecurityKeyException;
import com.vaadin.server.ServiceException;
import com.vaadin.server.SessionExpiredException;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServletRequest;
import com.vaadin.server.VaadinServletService;
import com.vaadin.server.VaadinSession;
import com.vaadin.server.WebBrowser;
import com.vaadin.shared.ApplicationConstants;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.ui.UI;

/**
 * Establishes bidirectional ("push") communication channels
 * 
 * @author Vaadin Ltd
 * @since 7.1
 */
public class PushHandler implements AtmosphereHandler {

    /**
     * Callback interface used internally to process an event with the
     * corresponding UI properly locked.
     */
    private interface PushEventCallback {
        public void run(AtmosphereResource resource, UI ui) throws IOException;
    }

    /**
     * Callback used when we receive a request to establish a push channel for a
     * UI. Associate the AtmosphereResource with the UI and leave the connection
     * open by calling resource.suspend(). If there is a pending push, send it
     * now.
     */
    private static PushEventCallback establishCallback = new PushEventCallback() {
        @Override
        public void run(AtmosphereResource resource, UI ui) throws IOException {
            getLogger().log(Level.FINER,
                    "New push connection with transport {0}",
                    resource.transport());
            resource.getResponse().setContentType("text/plain; charset=UTF-8");

            VaadinSession session = ui.getSession();
            if (resource.transport() == TRANSPORT.STREAMING) {
                // IE8 requires a longer padding to work properly if the
                // initial message is small (#11573). Chrome does not work
                // without the original padding...
                WebBrowser browser = session.getBrowser();
                if (browser.isIE() && browser.getBrowserMajorVersion() == 8) {
                    resource.padding(LONG_PADDING);
                }
            }

            String requestToken = resource.getRequest().getParameter(
                    ApplicationConstants.CSRF_TOKEN_PARAMETER);
            if (!VaadinService.isCsrfTokenValid(session, requestToken)) {
                getLogger()
                        .log(Level.WARNING,
                                "Invalid CSRF token in new connection received from {0}",
                                resource.getRequest().getRemoteHost());
                // Refresh on client side, create connection just for
                // sending a message
                AtmospherePushConnection connection = new AtmospherePushConnection(
                        ui);
                connection.connect(resource);
                sendRefresh(connection);
                return;
            }

            resource.suspend();

            AtmospherePushConnection connection = new AtmospherePushConnection(
                    ui);
            connection.connect(resource);

            ui.setPushConnection(connection);
        }
    };

    /**
     * Callback used when we receive a UIDL request through Atmosphere. If the
     * push channel is bidirectional (websockets), the request was sent via the
     * same channel. Otherwise, the client used a separate AJAX request. Handle
     * the request and send changed UI state via the push channel (we do not
     * respond to the request directly.)
     */
    private static PushEventCallback receiveCallback = new PushEventCallback() {
        @Override
        public void run(AtmosphereResource resource, UI ui) throws IOException {
            AtmosphereRequest req = resource.getRequest();

            AtmospherePushConnection connection = getConnectionForUI(ui);

            assert connection != null : "Got push from the client "
                    + "even though the connection does not seem to be "
                    + "valid. This might happen if a HttpSession is "
                    + "serialized and deserialized while the push "
                    + "connection is kept open or if the UI has a "
                    + "connection of unexpected type.";

            // Should be set up by caller
            VaadinRequest vaadinRequest = VaadinService.getCurrentRequest();
            assert vaadinRequest != null;

            try {
                new ServerRpcHandler().handleRpc(ui, req.getReader(),
                        vaadinRequest);
                connection.push(false);
            } catch (JSONException e) {
                getLogger().log(Level.SEVERE, "Error writing JSON to response",
                        e);
                // Refresh on client side
                sendRefresh(connection);
            } catch (InvalidUIDLSecurityKeyException e) {
                getLogger().log(Level.WARNING,
                        "Invalid security key received from {0}",
                        resource.getRequest().getRemoteHost());
                // Refresh on client side
                sendRefresh(connection);
            }
        }
    };

    /**
     * Callback used when a connection is closed by the client.
     */
    PushEventCallback disconnectCallback = new PushEventCallback() {
        @Override
        public void run(AtmosphereResource resource, UI ui) throws IOException {
            PushMode pushMode = ui.getPushMode();
            AtmospherePushConnection pushConnection = getConnectionForUI(ui);

            String id = resource.uuid();

            if (pushConnection == null) {
                getLogger()
                        .log(Level.WARNING,
                                "Could not find push connection to close: {0} with transport {1}",
                                new Object[] { id, resource.transport() });
            } else {
                if (!pushMode.isEnabled()) {
                    /*
                     * The client is expected to close the connection after push
                     * mode has been set to disabled, just clean up some stuff
                     * and be done with it
                     */
                    getLogger().log(Level.FINEST,
                            "Connection closed for resource {0}", id);
                } else {
                    /*
                     * Unexpected cancel, e.g. if the user closes the browser
                     * tab.
                     */
                    getLogger()
                            .log(Level.FINE,
                                    "Connection unexpectedly closed for resource {0} with transport {1}",
                                    new Object[] { id, resource.transport() });
                }
                ui.setPushConnection(null);
            }
        }
    };

    private static final String LONG_PADDING;

    static {
        char[] array = new char[4096];
        Arrays.fill(array, '-');
        LONG_PADDING = String.copyValueOf(array);

    }
    private VaadinServletService service;

    public PushHandler(VaadinServletService service) {
        this.service = service;
    }

    /**
     * Find the UI for the atmosphere resource, lock it and invoke the callback.
     * 
     * @param resource
     *            the atmosphere resource for the current request
     * @param callback
     *            the push callback to call when a UI is found and locked
     */
    private void callWithUi(final AtmosphereResource resource,
            final PushEventCallback callback) {
        AtmosphereRequest req = resource.getRequest();
        VaadinServletRequest vaadinRequest = new VaadinServletRequest(req,
                service);
        VaadinSession session = null;

        service.requestStart(vaadinRequest, null);
        try {
            try {
                session = service.findVaadinSession(vaadinRequest);
            } catch (ServiceException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return;
            } catch (SessionExpiredException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return;
            }

            session.lock();
            try {
                VaadinSession.setCurrent(session);
                // Sets UI.currentInstance
                final UI ui = service.findUI(vaadinRequest);
                if (ui == null) {
                    // This should not happen
                    getLogger().warning(
                            "Could not find the requested UI in session");
                    return;
                }

                callback.run(resource, ui);
            } catch (IOException e) {
                getLogger().log(Level.INFO,
                        "An error occured while writing a push response", e);
            } finally {
                session.unlock();
            }
        } finally {
            service.requestEnd(vaadinRequest, null, session);
        }
    }

    @Override
    public void onRequest(AtmosphereResource resource) {
        AtmosphereRequest req = resource.getRequest();

        if (req.getMethod().equalsIgnoreCase("GET")) {
            callWithUi(resource, establishCallback);
        } else if (req.getMethod().equalsIgnoreCase("POST")) {
            callWithUi(resource, receiveCallback);
        }
    }

    private static AtmospherePushConnection getConnectionForUI(UI ui) {
        PushConnection pushConnection = ui.getPushConnection();
        if (pushConnection instanceof AtmospherePushConnection) {
            AtmospherePushConnection apc = (AtmospherePushConnection) pushConnection;
            if (apc.isConnected()) {
                return apc;
            }
        }

        return null;
    }

    @Override
    public void onStateChange(AtmosphereResourceEvent event) throws IOException {
        AtmosphereResource resource = event.getResource();

        String id = resource.uuid();
        if (event.isCancelled()) {
            callWithUi(resource, disconnectCallback);
        } else if (event.isResuming()) {
            // A connection that was suspended earlier was resumed (committed to
            // the client.) Should only happen if the transport is JSONP or
            // long-polling.
            getLogger().log(Level.FINER, "Resuming request for resource {0}",
                    id);
        } else {
            // A message was broadcast to this resource and should be sent to
            // the client. We don't do any actual broadcasting, in the sense of
            // sending to multiple recipients; any UIDL message is specific to a
            // single client.
            getLogger().log(Level.FINER, "Writing message to resource {0}", id);

            Writer writer = resource.getResponse().getWriter();
            writer.write("for(;;);[{" + event.getMessage() + "}]");

            switch (resource.transport()) {
            case SSE:
            case WEBSOCKET:
                break;
            case STREAMING:
                writer.flush();
                break;
            case JSONP:
            case LONG_POLLING:
                resource.resume();
                break;
            default:
                getLogger().log(Level.SEVERE, "Unknown transport {0}",
                        resource.transport());
            }
        }
    }

    @Override
    public void destroy() {
    }

    private static void sendRefresh(AtmospherePushConnection connection) {
        connection.sendMessage(VaadinService.createCriticalNotificationJSON(
                null, null, null, null));
    }

    private static final Logger getLogger() {
        return Logger.getLogger(PushHandler.class.getName());
    }
}
