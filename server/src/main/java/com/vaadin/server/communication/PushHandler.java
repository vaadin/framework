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

package com.vaadin.server.communication;

import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.atmosphere.cpr.AtmosphereRequest;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResource.TRANSPORT;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.cpr.AtmosphereResourceImpl;

import com.vaadin.server.ErrorEvent;
import com.vaadin.server.ErrorHandler;
import com.vaadin.server.LegacyCommunicationManager.InvalidUIDLSecurityKeyException;
import com.vaadin.server.ServiceException;
import com.vaadin.server.ServletPortletHelper;
import com.vaadin.server.SessionExpiredException;
import com.vaadin.server.SystemMessages;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServletRequest;
import com.vaadin.server.VaadinServletService;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ApplicationConstants;
import com.vaadin.shared.JsonConstants;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.ui.UI;

import elemental.json.JsonException;

/**
 * Handles incoming push connections and messages and dispatches them to the
 * correct {@link UI}/ {@link AtmospherePushConnection}.
 *
 * @author Vaadin Ltd
 * @since 7.1
 */
public class PushHandler {

    private int longPollingSuspendTimeout = -1;

    private final ServerRpcHandler rpcHandler = createRpcHandler();

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
    private final PushEventCallback establishCallback = (
            AtmosphereResource resource, UI ui) -> {
        getLogger().log(Level.FINER,
                "New push connection for resource {0} with transport {1}",
                new Object[] { resource.uuid(), resource.transport() });

        resource.getResponse().setContentType("text/plain; charset=UTF-8");

        VaadinSession session = ui.getSession();
        if (resource.transport() == TRANSPORT.STREAMING) {
            // Must ensure that the streaming response contains
            // "Connection: close", otherwise iOS 6 will wait for the
            // response to this request before sending another request to
            // the same server (as it will apparently try to reuse the same
            // connection)
            resource.getResponse().addHeader("Connection", "close");
        }

        String requestToken = resource.getRequest()
                .getParameter(ApplicationConstants.PUSH_ID_PARAMETER);
        if (!isPushIdValid(session, requestToken)) {
            getLogger().log(Level.WARNING,
                    "Invalid identifier in new connection received from {0}",
                    resource.getRequest().getRemoteHost());
            // Refresh on client side, create connection just for
            // sending a message
            sendRefreshAndDisconnect(resource);
            return;
        }

        suspend(resource);

        AtmospherePushConnection connection = getConnectionForUI(ui);
        assert (connection != null);
        connection.connect(resource);
    };

    /**
     * Callback used when we receive a UIDL request through Atmosphere. If the
     * push channel is bidirectional (websockets), the request was sent via the
     * same channel. Otherwise, the client used a separate AJAX request. Handle
     * the request and send changed UI state via the push channel (we do not
     * respond to the request directly.)
     */
    private final PushEventCallback receiveCallback = (
            AtmosphereResource resource, UI ui) -> {
        getLogger().log(Level.FINER, "Received message from resource {0}",
                resource.uuid());

        AtmosphereRequest req = resource.getRequest();

        AtmospherePushConnection connection = getConnectionForUI(ui);

        assert connection != null : "Got push from the client "
                + "even though the connection does not seem to be "
                + "valid. This might happen if a HttpSession is "
                + "serialized and deserialized while the push "
                + "connection is kept open or if the UI has a "
                + "connection of unexpected type.";

        Reader reader = connection.receiveMessage(req.getReader());
        if (reader == null) {
            // The whole message was not yet received
            return;
        }

        // Should be set up by caller
        VaadinRequest vaadinRequest = VaadinService.getCurrentRequest();
        assert vaadinRequest != null;

        try {
            rpcHandler.handleRpc(ui, reader, vaadinRequest);
            connection.push(false);
        } catch (JsonException e) {
            getLogger().log(Level.SEVERE, "Error writing JSON to response", e);
            // Refresh on client side
            sendRefreshAndDisconnect(resource);
        } catch (InvalidUIDLSecurityKeyException e) {
            getLogger().log(Level.WARNING,
                    "Invalid security key received from {0}",
                    resource.getRequest().getRemoteHost());
            // Refresh on client side
            sendRefreshAndDisconnect(resource);
        }
    };

    private final VaadinServletService service;

    public PushHandler(VaadinServletService service) {
        this.service = service;
    }

    /**
     * Creates the ServerRpcHandler to use.
     *
     * @return the ServerRpcHandler to use
     * @since 8.5
     */
    protected ServerRpcHandler createRpcHandler() {
        return new ServerRpcHandler();
    }

    /**
     * Suspends the given resource.
     *
     * @since 7.6
     * @param resource
     *            the resource to suspend
     */
    protected void suspend(AtmosphereResource resource) {
        if (resource.transport() == TRANSPORT.LONG_POLLING) {
            resource.suspend(getLongPollingSuspendTimeout());
        } else {
            resource.suspend(-1);
        }
    }

    /**
     * Find the UI for the atmosphere resource, lock it and invoke the callback.
     *
     * @param resource
     *            the atmosphere resource for the current request
     * @param callback
     *            the push callback to call when a UI is found and locked
     * @param websocket
     *            true if this is a websocket message (as opposed to a HTTP
     *            request)
     */
    private void callWithUi(final AtmosphereResource resource,
            final PushEventCallback callback, boolean websocket) {
        AtmosphereRequest req = resource.getRequest();
        VaadinServletRequest vaadinRequest = new VaadinServletRequest(req,
                service);
        VaadinSession session = null;

        if (websocket) {
            // For any HTTP request we have already started the request in the
            // servlet
            service.requestStart(vaadinRequest, null);
        }
        try {
            try {
                session = service.findVaadinSession(vaadinRequest);
                assert VaadinSession.getCurrent() == session;

            } catch (ServiceException e) {
                getLogger().log(Level.SEVERE,
                        "Could not get session. This should never happen", e);
                return;
            } catch (SessionExpiredException e) {
                SystemMessages msg = service
                        .getSystemMessages(ServletPortletHelper.findLocale(null,
                                null, vaadinRequest), vaadinRequest);
                sendNotificationAndDisconnect(resource,
                        VaadinService.createCriticalNotificationJSON(
                                msg.getSessionExpiredCaption(),
                                msg.getSessionExpiredMessage(), null,
                                msg.getSessionExpiredURL()));
                return;
            }

            UI ui = null;
            session.lock();
            try {
                ui = service.findUI(vaadinRequest);
                assert UI.getCurrent() == ui;

                if (ui == null) {
                    sendNotificationAndDisconnect(resource, UidlRequestHandler
                            .getUINotFoundErrorJSON(service, vaadinRequest));
                } else {
                    callback.run(resource, ui);
                }
            } catch (final IOException e) {
                callErrorHandler(session, e);
            } catch (final Exception e) {
                SystemMessages msg = service
                        .getSystemMessages(ServletPortletHelper.findLocale(null,
                                null, vaadinRequest), vaadinRequest);

                AtmosphereResource errorResource = resource;
                if (ui != null && ui.getPushConnection() != null) {
                    // We MUST use the opened push connection if there is one.
                    // Otherwise we will write the response to the wrong request
                    // when using streaming (the client -> server request
                    // instead of the opened push channel)
                    errorResource = ((AtmospherePushConnection) ui
                            .getPushConnection()).getResource();
                }

                sendNotificationAndDisconnect(errorResource,
                        VaadinService.createCriticalNotificationJSON(
                                msg.getInternalErrorCaption(),
                                msg.getInternalErrorMessage(), null,
                                msg.getInternalErrorURL()));
                callErrorHandler(session, e);
            } finally {
                try {
                    session.unlock();
                } catch (Exception e) {
                    getLogger().log(Level.WARNING,
                            "Error while unlocking session", e);
                    // can't call ErrorHandler, we (hopefully) don't have a lock
                }
            }
        } finally {
            try {
                if (websocket) {
                    service.requestEnd(vaadinRequest, null, session);
                }
            } catch (Exception e) {
                getLogger().log(Level.WARNING, "Error while ending request", e);

                // can't call ErrorHandler, we don't have a lock
            }
        }
    }

    /**
     * Call the session's {@link ErrorHandler}, if it has one, with the given
     * exception wrapped in an {@link ErrorEvent}.
     */
    private void callErrorHandler(VaadinSession session, Exception e) {
        try {
            ErrorHandler errorHandler = ErrorEvent.findErrorHandler(session);
            if (errorHandler != null) {
                errorHandler.error(new ErrorEvent(e));
            }
        } catch (Exception ex) {
            // Let's not allow error handling to cause trouble; log fails
            getLogger().log(Level.WARNING, "ErrorHandler call failed", ex);
        }
    }

    private static AtmospherePushConnection getConnectionForUI(UI ui) {
        PushConnection pushConnection = ui.getPushConnection();
        if (pushConnection instanceof AtmospherePushConnection) {
            return (AtmospherePushConnection) pushConnection;
        } else {
            return null;
        }
    }

    void connectionLost(AtmosphereResourceEvent event) {
        // We don't want to use callWithUi here, as it assumes there's a client
        // request active and does requestStart and requestEnd among other
        // things.
        if (event == null) {
            getLogger().log(Level.SEVERE,
                    "Could not get event. This should never happen.");
            return;
        }

        AtmosphereResource resource = event.getResource();

        if (resource == null) {
            getLogger().log(Level.SEVERE,
                    "Could not get resource. This should never happen.");
            return;
        }

        VaadinServletRequest vaadinRequest = new VaadinServletRequest(
                resource.getRequest(), service);
        VaadinSession session = null;

        try {
            session = service.findVaadinSession(vaadinRequest);
        } catch (ServiceException e) {
            getLogger().log(Level.SEVERE,
                    "Could not get session. This should never happen", e);
            return;
        } catch (SessionExpiredException e) {
            // This happens at least if the server is restarted without
            // preserving the session. After restart the client reconnects, gets
            // a session expired notification and then closes the connection and
            // ends up here
            getLogger().log(Level.FINER,
                    "Session expired before push disconnect event was received",
                    e);
            return;
        }

        UI ui = null;
        session.lock();
        try {
            VaadinSession.setCurrent(session);
            // Sets UI.currentInstance
            ui = service.findUI(vaadinRequest);
            if (ui == null) {
                /*
                 * UI not found, could be because FF has asynchronously closed
                 * the websocket connection and Atmosphere has already done
                 * cleanup of the request attributes. In that case, we still
                 * have a chance of finding the right UI by iterating through
                 * the UIs in the session looking for one using the same
                 * AtmosphereResource.
                 */
                ui = findUiUsingResource(resource, session.getUIs());

                if (ui == null) {
                    getLogger().log(Level.FINE,
                            "Could not get UI. This should never happen,"
                                    + " except when reloading in Firefox and Chrome -"
                                    + " see http://dev.vaadin.com/ticket/14251.");
                    return;
                } else {
                    getLogger().log(Level.INFO,
                            "No UI was found based on data in the request,"
                                    + " but a slower lookup based on the AtmosphereResource succeeded."
                                    + " See http://dev.vaadin.com/ticket/14251 for more details.");
                }
            }

            PushMode pushMode = ui.getPushConfiguration().getPushMode();
            AtmospherePushConnection pushConnection = getConnectionForUI(ui);

            String id = resource.uuid();

            if (pushConnection == null) {
                getLogger().log(Level.WARNING,
                        "Could not find push connection to close: {0} with transport {1}",
                        new Object[] { id, resource.transport() });
            } else {
                if (!pushMode.isEnabled()) {
                    /*
                     * The client is expected to close the connection after push
                     * mode has been set to disabled.
                     */
                    getLogger().log(Level.FINER,
                            "Connection closed for resource {0}", id);
                } else {
                    /*
                     * Unexpected cancel, e.g. if the user closes the browser
                     * tab.
                     */
                    getLogger().log(Level.FINER,
                            "Connection unexpectedly closed for resource {0} with transport {1}",
                            new Object[] { id, resource.transport() });
                }

                pushConnection.connectionLost();
            }

        } catch (final Exception e) {
            callErrorHandler(session, e);
        } finally {
            try {
                session.unlock();
            } catch (Exception e) {
                getLogger().log(Level.WARNING, "Error while unlocking session",
                        e);
                // can't call ErrorHandler, we (hopefully) don't have a lock
            }
        }
    }

    private static UI findUiUsingResource(AtmosphereResource resource,
            Collection<UI> uIs) {
        for (UI ui : uIs) {
            PushConnection pushConnection = ui.getPushConnection();
            if (pushConnection instanceof AtmospherePushConnection) {
                if (((AtmospherePushConnection) pushConnection)
                        .getResource() == resource) {
                    return ui;
                }
            }
        }
        return null;
    }

    /**
     * Sends a refresh message to the given atmosphere resource. Uses an
     * AtmosphereResource instead of an AtmospherePushConnection even though it
     * might be possible to look up the AtmospherePushConnection from the UI to
     * ensure border cases work correctly, especially when there temporarily are
     * two push connections which try to use the same UI. Using the
     * AtmosphereResource directly guarantees the message goes to the correct
     * recipient.
     *
     * @param resource
     *            The atmosphere resource to send refresh to
     *
     */
    private static void sendRefreshAndDisconnect(AtmosphereResource resource)
            throws IOException {
        sendNotificationAndDisconnect(resource, VaadinService
                .createCriticalNotificationJSON(null, null, null, null));
    }

    /**
     * Tries to send a critical notification to the client and close the
     * connection. Does nothing if the connection is already closed.
     */
    private static void sendNotificationAndDisconnect(
            AtmosphereResource resource, String notificationJson) {
        // TODO Implemented differently from sendRefreshAndDisconnect
        try {
            if (resource instanceof AtmosphereResourceImpl
                    && !((AtmosphereResourceImpl) resource).isInScope()) {
                // The resource is no longer valid so we should not write
                // anything to it
                getLogger().fine(
                        "sendNotificationAndDisconnect called for resource no longer in scope");
                return;
            }
            resource.getResponse()
                    .setContentType(JsonConstants.JSON_CONTENT_TYPE);
            resource.getResponse().getWriter().write(notificationJson);
            resource.resume();
        } catch (Exception e) {
            getLogger().log(Level.FINEST,
                    "Failed to send critical notification to client", e);
        }
    }

    private static final Logger getLogger() {
        return Logger.getLogger(PushHandler.class.getName());
    }

    /**
     * Checks whether a given push id matches the session's push id.
     *
     * @param session
     *            the vaadin session for which the check should be done
     * @param requestPushId
     *            the push id provided in the request
     * @return {@code true} if the id is valid, {@code false} otherwise
     */
    private static boolean isPushIdValid(VaadinSession session,
            String requestPushId) {

        String sessionPushId = session.getPushId();
        if (requestPushId == null || !requestPushId.equals(sessionPushId)) {
            return false;
        }
        return true;
    }

    /**
     * Called when a new push connection is requested to be opened by the client
     *
     * @since 7.5.0
     * @param resource
     *            The related atmosphere resources
     */
    void onConnect(AtmosphereResource resource) {
        callWithUi(resource, establishCallback, false);
    }

    /**
     * Called when a message is received through the push connection
     *
     * @since 7.5.0
     * @param resource
     *            The related atmosphere resources
     */
    void onMessage(AtmosphereResource resource) {
        callWithUi(resource, receiveCallback,
                resource.transport() == TRANSPORT.WEBSOCKET);
    }

    /**
     * Sets the timeout used for suspend calls when using long polling.
     *
     * If you are using a proxy with a defined idle timeout, set the suspend
     * timeout to a value smaller than the proxy timeout so that the server is
     * aware of a reconnect taking place.
     *
     * @since 7.6
     * @param longPollingSuspendTimeout
     *            the timeout to use for suspended AtmosphereResources
     */
    public void setLongPollingSuspendTimeout(int longPollingSuspendTimeout) {
        this.longPollingSuspendTimeout = longPollingSuspendTimeout;
    }

    /**
     * Gets the timeout used for suspend calls when using long polling.
     *
     * @since 7.6
     * @return the timeout to use for suspended AtmosphereResources
     */
    public int getLongPollingSuspendTimeout() {
        return longPollingSuspendTimeout;
    }
}
