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
import java.io.StringWriter;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.server.LegacyCommunicationManager.InvalidUIDLSecurityKeyException;
import com.vaadin.server.ServletPortletHelper;
import com.vaadin.server.SessionExpiredHandler;
import com.vaadin.server.SynchronizedRequestHandler;
import com.vaadin.server.SystemMessages;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.JsonConstants;
import com.vaadin.ui.UI;

import elemental.json.JsonException;

/**
 * Processes a UIDL request from the client.
 *
 * Uses {@link ServerRpcHandler} to execute client-to-server RPC invocations and
 * {@link UidlWriter} to write state changes and client RPC calls back to the
 * client.
 *
 * @author Vaadin Ltd
 * @since 7.1
 */
public class UidlRequestHandler extends SynchronizedRequestHandler
        implements SessionExpiredHandler {

    public static final String UIDL_PATH = "UIDL/";

    private final ServerRpcHandler rpcHandler;

    public UidlRequestHandler() {
        rpcHandler = createRpcHandler();
    }

    /**
     * Creates the ServerRpcHandler to use.
     *
     * @since 7.7
     * @return the ServerRpcHandler to use
     */
    protected ServerRpcHandler createRpcHandler() {
        return new ServerRpcHandler();
    }

    @Override
    protected boolean canHandleRequest(VaadinRequest request) {
        return ServletPortletHelper.isUIDLRequest(request);
    }

    @Override
    public boolean synchronizedHandleRequest(VaadinSession session,
            VaadinRequest request, VaadinResponse response) throws IOException {
        UI uI = session.getService().findUI(request);
        if (uI == null) {
            // This should not happen but it will if the UI has been closed. We
            // really don't want to see it in the server logs though
            UIInitHandler.commitJsonResponse(request, response,
                    getUINotFoundErrorJSON(session.getService(), request));
            return true;
        }

        StringWriter stringWriter = new StringWriter();

        try {
            rpcHandler.handleRpc(uI, request.getReader(), request);

            writeUidl(request, response, uI, stringWriter);
        } catch (JsonException e) {
            getLogger().log(Level.SEVERE, "Error writing JSON to response", e);
            // Refresh on client side
            writeRefresh(request, response);
            return true;
        } catch (InvalidUIDLSecurityKeyException e) {
            getLogger().log(Level.WARNING,
                    "Invalid security key received from {0}",
                    request.getRemoteHost());
            // Refresh on client side
            writeRefresh(request, response);
            return true;
        } finally {
            stringWriter.close();
        }

        return UIInitHandler.commitJsonResponse(request, response,
                stringWriter.toString());
    }

    private void writeRefresh(VaadinRequest request, VaadinResponse response)
            throws IOException {
        String json = VaadinService.createCriticalNotificationJSON(null, null,
                null, null);
        UIInitHandler.commitJsonResponse(request, response, json);
    }

    private void writeUidl(VaadinRequest request, VaadinResponse response,
            UI ui, Writer writer) throws IOException {
        openJsonMessage(writer, response);

        new UidlWriter().write(ui, writer, false);

        closeJsonMessage(writer);
    }

    protected void closeJsonMessage(Writer outWriter) throws IOException {
        outWriter.write("}]");
    }

    /**
     * Writes the opening of JSON message to be sent to client.
     *
     * @param outWriter
     * @param response
     * @throws IOException
     */
    protected void openJsonMessage(Writer outWriter, VaadinResponse response)
            throws IOException {
        // some dirt to prevent cross site scripting
        outWriter.write("for(;;);[{");
    }

    private static final Logger getLogger() {
        return Logger.getLogger(UidlRequestHandler.class.getName());
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
        if (!ServletPortletHelper.isUIDLRequest(request)) {
            return false;
        }
        VaadinService service = request.getService();
        SystemMessages systemMessages = service.getSystemMessages(
                ServletPortletHelper.findLocale(null, null, request), request);

        service.writeUncachedStringResponse(response,
                JsonConstants.JSON_CONTENT_TYPE,
                VaadinService.createCriticalNotificationJSON(
                        systemMessages.getSessionExpiredCaption(),
                        systemMessages.getSessionExpiredMessage(), null,
                        systemMessages.getSessionExpiredURL()));

        return true;
    }

    /**
     * Returns the JSON which should be returned to the client when a request
     * for a non-existent UI arrives.
     *
     * @param service
     *            The VaadinService
     * @param vaadinRequest
     *            The request which triggered this, or null if not available
     * @since 7.1
     * @return A JSON string
     */
    static String getUINotFoundErrorJSON(VaadinService service,
            VaadinRequest vaadinRequest) {
        SystemMessages ci = service.getSystemMessages(vaadinRequest.getLocale(),
                vaadinRequest);
        // Session Expired is not really the correct message as the
        // session exists but the requested UI does not.
        // Using Communication Error for now.
        String json = VaadinService.createCriticalNotificationJSON(
                ci.getCommunicationErrorCaption(),
                ci.getCommunicationErrorMessage(), null,
                ci.getCommunicationErrorURL());

        return json;
    }

}
