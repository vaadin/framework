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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
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
import com.vaadin.shared.ui.ui.UIConstants;
import com.vaadin.ui.UI;

/**
 * Handles an initial request from the client to initialize a {@link UI}.
 * 
 * @author Vaadin Ltd
 * @since 7.1
 */
public class UIInitHandler extends SynchronizedRequestHandler {

    @Override
    public boolean synchronizedHandleRequest(VaadinSession session,
            VaadinRequest request, VaadinResponse response) throws IOException {

        // NOTE! GateIn requires, for some weird reason, getOutputStream
        // to be used instead of getWriter() (it seems to interpret
        // application/json as a binary content type)
        final OutputStream out = response.getOutputStream();
        final PrintWriter outWriter = new PrintWriter(new BufferedWriter(
                new OutputStreamWriter(out, "UTF-8")));

        try {
            assert UI.getCurrent() == null;

            // Set browser information from the request
            session.getBrowser().updateRequestDetails(request);

            response.setContentType("application/json; charset=UTF-8");

            UI uI = getBrowserDetailsUI(request, session);

            session.getCommunicationManager().repaintAll(uI);

            JSONObject params = new JSONObject();
            params.put(UIConstants.UI_ID_PARAMETER, uI.getUIId());
            String initialUIDL = getInitialUidl(request, uI);
            params.put("uidl", initialUIDL);

            outWriter.write(params.toString());
            // NOTE GateIn requires the buffers to be flushed to work
            outWriter.flush();
            out.flush();
        } catch (JSONException e) {
            // TODO PUSH handle
            e.printStackTrace();
        } finally {
            outWriter.close();
        }

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

        // Check for an existing UI based on window.name

        // Special parameter sent by vaadinBootstrap.js
        String windowName = request.getParameter("v-wn");

        Map<String, Integer> retainOnRefreshUIs = session
                .getPreserveOnRefreshUIs();
        if (windowName != null && !retainOnRefreshUIs.isEmpty()) {
            // Check for a known UI

            Integer retainedUIId = retainOnRefreshUIs.get(windowName);

            if (retainedUIId != null) {
                UI retainedUI = session.getUIById(retainedUIId.intValue());
                if (uiClass.isInstance(retainedUI)) {
                    reinitUI(retainedUI, request);
                    return retainedUI;
                } else {
                    getLogger().info(
                            "Not using retained UI in " + windowName
                                    + " because retained UI was of type "
                                    + retainedUI.getClass() + " but " + uiClass
                                    + " is expected for the request.");
                }
            }
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

        // Set thread local here so it is available in init
        UI.setCurrent(ui);

        ui.doInit(request, uiId.intValue());

        session.addUI(ui);

        // Remember if it should be remembered
        if (vaadinService.preserveUIOnRefresh(provider, event)) {
            // Remember this UI
            if (windowName == null) {
                getLogger().warning(
                        "There is no window.name available for UI " + uiClass
                                + " that should be preserved.");
            } else {
                session.getPreserveOnRefreshUIs().put(windowName, uiId);
            }
        }

        return ui;
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

        // Fire fragment change if the fragment has changed
        String location = request.getParameter("v-loc");
        if (location != null) {
            ui.getPage().updateLocation(location);
        }
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
            if (uI.getSession().getConfiguration().isXsrfProtectionEnabled()) {
                writer.write(uI.getSession().getCommunicationManager()
                        .getSecurityKeyUIDL(request));
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

    private static final Logger getLogger() {
        return Logger.getLogger(UIInitHandler.class.getName());
    }
}
