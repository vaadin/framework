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
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.server.LegacyApplicationUIProvider;
import com.vaadin.server.SynchronizedRequestHandler;
import com.vaadin.server.UIClassSelectionEvent;
import com.vaadin.server.UICreateEvent;
import com.vaadin.server.UIProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ApplicationConstants;
import com.vaadin.shared.JsonConstants;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.shared.ui.ui.UIConstants;
import com.vaadin.ui.UI;

/**
 * Handles an initial request from the client to initialize a {@link UI}.
 * 
 * @author Vaadin Ltd
 * @since 7.1
 */
public abstract class UIInitHandler extends SynchronizedRequestHandler {

    public static final String BROWSER_DETAILS_PARAMETER = "v-browserDetails";

    protected abstract boolean isInitRequest(VaadinRequest request);

    @Override
    protected boolean canHandleRequest(VaadinRequest request) {
        return isInitRequest(request);
    }

    @Override
    public boolean synchronizedHandleRequest(VaadinSession session,
            VaadinRequest request, VaadinResponse response) throws IOException {
        StringWriter stringWriter = new StringWriter();

        try {
            assert UI.getCurrent() == null;

            // Update browser information from the request
            session.getBrowser().updateRequestDetails(request);

            UI uI = getBrowserDetailsUI(request, session);

            session.getCommunicationManager().repaintAll(uI);

            JSONObject params = new JSONObject();
            params.put(UIConstants.UI_ID_PARAMETER, uI.getUIId());
            String initialUIDL = getInitialUidl(request, uI);
            params.put("uidl", initialUIDL);

            stringWriter.write(params.toString());
        } catch (JSONException e) {
            throw new IOException("Error producing initial UIDL", e);
        } finally {
            stringWriter.close();
        }

        return commitJsonResponse(request, response, stringWriter.toString());
    }

    /**
     * Commit the JSON response. We can't write immediately to the output stream
     * as we want to write only a critical notification if something goes wrong
     * during the response handling.
     * 
     * @param request
     *            The request that resulted in this response
     * @param response
     *            The response to write to
     * @param json
     *            The JSON to write
     * @return true if the JSON was written successfully, false otherwise
     * @throws IOException
     *             If there was an exception while writing to the output
     */
    static boolean commitJsonResponse(VaadinRequest request,
            VaadinResponse response, String json) throws IOException {
        // The response was produced without errors so write it to the client
        response.setContentType(JsonConstants.JSON_CONTENT_TYPE);

        // Ensure that the browser does not cache UIDL responses.
        // iOS 6 Safari requires this (#9732)
        response.setHeader("Cache-Control", "no-cache");

        byte[] b = json.getBytes("UTF-8");
        response.setHeader("Content-Length", String.valueOf(b.length));

        OutputStream outputStream = response.getOutputStream();
        outputStream.write(b);
        // NOTE GateIn requires the buffers to be flushed to work
        outputStream.flush();

        return true;
    }

    private UI getBrowserDetailsUI(VaadinRequest request, VaadinSession session) {
        VaadinService vaadinService = request.getService();

        List<UIProvider> uiProviders = session.getUIProviders();

        UIClassSelectionEvent classSelectionEvent = new UIClassSelectionEvent(
                request);

        UIProvider provider = null;
        Class<? extends UI> uiClass = null;
        for (UIProvider p : uiProviders) {
            // Check for existing LegacyWindow
            if (p instanceof LegacyApplicationUIProvider) {
                LegacyApplicationUIProvider legacyProvider = (LegacyApplicationUIProvider) p;

                UI existingUi = legacyProvider
                        .getExistingUI(classSelectionEvent);
                if (existingUi != null) {
                    reinitUI(existingUi, request);
                    return existingUi;
                }
            }

            uiClass = p.getUIClass(classSelectionEvent);
            if (uiClass != null) {
                provider = p;
                break;
            }
        }

        if (provider == null || uiClass == null) {
            return null;
        }

        // Check for an existing UI based on embed id

        String embedId = getEmbedId(request);

        UI retainedUI = session.getUIByEmbedId(embedId);
        if (retainedUI != null) {
            if (vaadinService.preserveUIOnRefresh(provider, new UICreateEvent(
                    request, uiClass))) {
                if (uiClass.isInstance(retainedUI)) {
                    reinitUI(retainedUI, request);
                    return retainedUI;
                } else {
                    getLogger().info(
                            "Not using the preserved UI " + embedId
                                    + " because it is of type "
                                    + retainedUI.getClass() + " but " + uiClass
                                    + " is expected for the request.");
                }
            }
            /*
             * Previous UI without preserve on refresh will be closed when the
             * new UI gets added to the session.
             */
        }

        // No existing UI found - go on by creating and initializing one

        Integer uiId = Integer.valueOf(session.getNextUIid());

        // Explicit Class.cast to detect if the UIProvider does something
        // unexpected
        UICreateEvent event = new UICreateEvent(request, uiClass, uiId);
        UI ui = uiClass.cast(provider.createInstance(event));

        // Initialize some fields for a newly created UI
        if (ui.getSession() != session) {
            // Session already set for LegacyWindow
            ui.setSession(session);
        }

        PushMode pushMode = provider.getPushMode(event);
        if (pushMode == null) {
            pushMode = session.getService().getDeploymentConfiguration()
                    .getPushMode();
        }
        ui.getPushConfiguration().setPushMode(pushMode);

        Transport transport = provider.getPushTransport(event);
        if (transport != null) {
            ui.getPushConfiguration().setTransport(transport);
        }

        // Set thread local here so it is available in init
        UI.setCurrent(ui);

        ui.doInit(request, uiId.intValue(), embedId);

        session.addUI(ui);

        // Warn if the window can't be preserved
        if (embedId == null
                && vaadinService.preserveUIOnRefresh(provider, event)) {
            getLogger().warning(
                    "There is no embed id available for UI " + uiClass
                            + " that should be preserved.");
        }

        return ui;
    }

    /**
     * Constructs an embed id based on information in the request.
     * 
     * @since 7.2
     * 
     * @param request
     *            the request to get embed information from
     * @return the embed id, or <code>null</code> if id is not available.
     * 
     * @see UI#getEmbedId()
     */
    protected String getEmbedId(VaadinRequest request) {
        // Parameters sent by vaadinBootstrap.js
        String windowName = request.getParameter("v-wn");
        String appId = request.getParameter("v-appId");

        if (windowName != null && appId != null) {
            return windowName + '.' + appId;
        } else {
            return null;
        }
    }

    /**
     * Updates a UI that has already been initialized but is now loaded again,
     * e.g. because of {@link PreserveOnRefresh}.
     * 
     * @param ui
     * @param request
     */
    private void reinitUI(UI ui, VaadinRequest request) {
        UI.setCurrent(ui);
        ui.doRefresh(request);
    }

    /**
     * Generates the initial UIDL message that can e.g. be included in a html
     * page to avoid a separate round trip just for getting the UIDL.
     * 
     * @param request
     *            the request that caused the initialization
     * @param uI
     *            the UI for which the UIDL should be generated
     * @return a string with the initial UIDL message
     * @throws JSONException
     *             if an exception occurs while encoding output
     * @throws IOException
     */
    protected String getInitialUidl(VaadinRequest request, UI uI)
            throws JSONException, IOException {
        StringWriter writer = new StringWriter();
        try {
            writer.write("{");

            VaadinSession session = uI.getSession();
            if (session.getConfiguration().isXsrfProtectionEnabled()) {
                writer.write(getSecurityKeyUIDL(session));
            }
            new UidlWriter().write(uI, writer, true, false);
            writer.write("}");

            String initialUIDL = writer.toString();
            getLogger().log(Level.FINE, "Initial UIDL:" + initialUIDL);
            return initialUIDL;
        } finally {
            writer.close();
        }
    }

    /**
     * Gets the security key (and generates one if needed) as UIDL.
     * 
     * @param session
     *            the vaadin session to which the security key belongs
     * @return the security key UIDL or "" if the feature is turned off
     */
    private static String getSecurityKeyUIDL(VaadinSession session) {
        String seckey = session.getCsrfToken();

        return "\"" + ApplicationConstants.UIDL_SECURITY_TOKEN_ID + "\":\""
                + seckey + "\",";
    }

    private static final Logger getLogger() {
        return Logger.getLogger(UIInitHandler.class.getName());
    }
}
