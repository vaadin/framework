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
import java.io.StringWriter;
import java.io.Writer;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONException;

import com.vaadin.server.ClientConnector;
import com.vaadin.server.Constants;
import com.vaadin.server.LegacyCommunicationManager;
import com.vaadin.server.LegacyCommunicationManager.Callback;
import com.vaadin.server.LegacyCommunicationManager.InvalidUIDLSecurityKeyException;
import com.vaadin.server.ServletPortletHelper;
import com.vaadin.server.SynchronizedRequestHandler;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ApplicationConstants;
import com.vaadin.shared.Version;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;

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
public class UidlRequestHandler extends SynchronizedRequestHandler {

    public static final String UIDL_PATH = "UIDL/";

    private Callback criticalNotifier;

    private ServerRpcHandler rpcHandler = new ServerRpcHandler();

    public UidlRequestHandler(Callback criticalNotifier) {
        this.criticalNotifier = criticalNotifier;
    }

    @Override
    public boolean synchronizedHandleRequest(VaadinSession session,
            VaadinRequest request, VaadinResponse response) throws IOException {
        if (!ServletPortletHelper.isUIDLRequest(request)) {
            return false;
        }
        UI uI = session.getService().findUI(request);
        if (uI == null) {
            // This should not happen
            getLogger().warning("Could not find the requested UI in session");
            return true;
        }

        checkWidgetsetVersion(request);
        String requestThemeName = request.getParameter("theme");
        ClientConnector highlightedConnector;
        // repaint requested or session has timed out and new one is created
        boolean repaintAll;

        // TODO PUSH repaintAll, analyzeLayouts, highlightConnector should be
        // part of the message payload to make the functionality transport
        // agnostic

        repaintAll = (request
                .getParameter(ApplicationConstants.URL_PARAMETER_REPAINT_ALL) != null);

        boolean analyzeLayouts = false;
        if (repaintAll) {
            // analyzing can be done only with repaintAll
            analyzeLayouts = (request
                    .getParameter(ApplicationConstants.PARAM_ANALYZE_LAYOUTS) != null);

            String pid = request
                    .getParameter(ApplicationConstants.PARAM_HIGHLIGHT_CONNECTOR);
            if (pid != null) {
                highlightedConnector = uI.getConnectorTracker().getConnector(
                        pid);
                highlightConnector(highlightedConnector);
            }
        }

        StringWriter stringWriter = new StringWriter();

        try {
            rpcHandler.handleRpc(uI, request.getReader(), request);

            if (repaintAll) {
                session.getCommunicationManager().repaintAll(uI);
            }

            writeUidl(request, response, uI, stringWriter, repaintAll,
                    analyzeLayouts);
            postHandleRequest(uI);
        } catch (JSONException e) {
            getLogger().log(Level.SEVERE, "Error writing JSON to response", e);
            // Refresh on client side
            criticalNotifier.criticalNotification(request, response, null,
                    null, null, null);
            return true;
        } catch (InvalidUIDLSecurityKeyException e) {
            getLogger().log(Level.WARNING,
                    "Invalid security key received from {}",
                    request.getRemoteHost());
            // Refresh on client side
            criticalNotifier.criticalNotification(request, response, null,
                    null, null, null);
            return true;
        } finally {
            stringWriter.close();
            requestThemeName = null;
        }

        return UIInitHandler.commitJsonResponse(request, response,
                stringWriter.toString());
    }

    /**
     * Checks that the version reported by the client (widgetset) matches that
     * of the server.
     * 
     * @param request
     */
    private void checkWidgetsetVersion(VaadinRequest request) {
        String widgetsetVersion = request.getParameter("v-wsver");
        if (widgetsetVersion == null) {
            // Only check when the widgetset version is reported. It is reported
            // in the first UIDL request (not the initial request as it is a
            // plain GET /)
            return;
        }

        if (!Version.getFullVersion().equals(widgetsetVersion)) {
            getLogger().warning(
                    String.format(Constants.WIDGETSET_MISMATCH_INFO,
                            Version.getFullVersion(), widgetsetVersion));
        }
    }

    private void writeUidl(VaadinRequest request, VaadinResponse response,
            UI ui, Writer writer, boolean repaintAll, boolean analyzeLayouts)
            throws IOException, JSONException {
        openJsonMessage(writer, response);

        // security key
        Object writeSecurityTokenFlag = request
                .getAttribute(LegacyCommunicationManager.WRITE_SECURITY_TOKEN_FLAG);

        if (writeSecurityTokenFlag != null) {
            writer.write(ui.getSession().getCommunicationManager()
                    .getSecurityKeyUIDL(request));
        }

        new UidlWriter().write(ui, writer, repaintAll, analyzeLayouts, false);

        closeJsonMessage(writer);
    }

    /**
     * Method called after the paint phase while still being synchronized on the
     * session
     * 
     * @param uI
     * 
     */
    protected void postHandleRequest(UI uI) {
        // Remove connectors that have been detached from the session during
        // handling of the request
        uI.getConnectorTracker().cleanConnectorMap();
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

    // TODO Does this belong here?
    protected void highlightConnector(ClientConnector highlightedConnector) {
        StringBuilder sb = new StringBuilder();
        sb.append("*** Debug details of a connector:  *** \n");
        sb.append("Type: ");
        sb.append(highlightedConnector.getClass().getName());
        sb.append("\nId:");
        sb.append(highlightedConnector.getConnectorId());
        if (highlightedConnector instanceof Component) {
            Component component = (Component) highlightedConnector;
            if (component.getCaption() != null) {
                sb.append("\nCaption:");
                sb.append(component.getCaption());
            }
        }
        printHighlightedConnectorHierarchy(sb, highlightedConnector);
        getLogger().info(sb.toString());
    }

    // TODO Does this belong here?
    protected void printHighlightedConnectorHierarchy(StringBuilder sb,
            ClientConnector connector) {
        LinkedList<ClientConnector> h = new LinkedList<ClientConnector>();
        h.add(connector);
        ClientConnector parent = connector.getParent();
        while (parent != null) {
            h.addFirst(parent);
            parent = parent.getParent();
        }

        sb.append("\nConnector hierarchy:\n");
        VaadinSession session2 = connector.getUI().getSession();
        sb.append(session2.getClass().getName());
        sb.append("(");
        sb.append(session2.getClass().getSimpleName());
        sb.append(".java");
        sb.append(":1)");
        int l = 1;
        for (ClientConnector connector2 : h) {
            sb.append("\n");
            for (int i = 0; i < l; i++) {
                sb.append("  ");
            }
            l++;
            Class<? extends ClientConnector> connectorClass = connector2
                    .getClass();
            Class<?> topClass = connectorClass;
            while (topClass.getEnclosingClass() != null) {
                topClass = topClass.getEnclosingClass();
            }
            sb.append(connectorClass.getName());
            sb.append("(");
            sb.append(topClass.getSimpleName());
            sb.append(".java:1)");
        }
    }

    private static final Logger getLogger() {
        return Logger.getLogger(UidlRequestHandler.class.getName());
    }
}
