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

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import com.vaadin.server.ClientConnector.ConnectorErrorEvent;
import com.vaadin.shared.ApplicationConstants;
import com.vaadin.shared.JavaScriptConnectorState;
import com.vaadin.shared.communication.SharedState;
import com.vaadin.ui.Component;
import com.vaadin.ui.ConnectorTracker;
import com.vaadin.ui.HasComponents;
import com.vaadin.ui.SelectiveRenderer;
import com.vaadin.ui.UI;

/**
 * This is a common base class for the server-side implementations of the
 * communication system between the client code (compiled with GWT into
 * JavaScript) and the server side components. Its client side counterpart is
 * {@link com.vaadin.client.ApplicationConnection}.
 * <p>
 * TODO Document better!
 * 
 * @deprecated As of 7.0. Will likely change or be removed in a future version
 */
@Deprecated
@SuppressWarnings("serial")
public class LegacyCommunicationManager implements Serializable {

    // TODO Refactor (#11410)
    private final HashMap<Integer, ClientCache> uiToClientCache = new HashMap<Integer, ClientCache>();

    /**
     * The session this communication manager is used for
     */
    private final VaadinSession session;

    // TODO Refactor (#11412)
    private String requestThemeName;

    // TODO Refactor (#11413)
    private Map<String, Class<?>> publishedFileContexts = new HashMap<String, Class<?>>();

    /**
     * TODO New constructor - document me!
     * 
     * @param session
     */
    public LegacyCommunicationManager(VaadinSession session) {
        this.session = session;
    }

    protected VaadinSession getSession() {
        return session;
    }

    /**
     * @deprecated As of 7.1. See #11411.
     */
    @Deprecated
    public static JSONObject encodeState(ClientConnector connector,
            SharedState state) throws JSONException {
        UI uI = connector.getUI();
        ConnectorTracker connectorTracker = uI.getConnectorTracker();
        Class<? extends SharedState> stateType = connector.getStateType();
        Object diffState = connectorTracker.getDiffState(connector);
        boolean supportsDiffState = !JavaScriptConnectorState.class
                .isAssignableFrom(stateType);
        if (diffState == null && supportsDiffState) {
            // Use an empty state object as reference for full
            // repaints

            try {
                SharedState referenceState = stateType.newInstance();
                EncodeResult encodeResult = JsonCodec.encode(referenceState,
                        null, stateType, uI.getConnectorTracker());
                diffState = encodeResult.getEncodedValue();
            } catch (Exception e) {
                getLogger()
                        .log(Level.WARNING,
                                "Error creating reference object for state of type {0}",
                                stateType.getName());
            }
        }
        EncodeResult encodeResult = JsonCodec.encode(state, diffState,
                stateType, uI.getConnectorTracker());
        if (supportsDiffState) {
            connectorTracker.setDiffState(connector,
                    (JSONObject) encodeResult.getEncodedValue());
        }
        return (JSONObject) encodeResult.getDiff();
    }

    /**
     * Resolves a dependency URI, registering the URI with this
     * {@code LegacyCommunicationManager} if needed and returns a fully
     * qualified URI.
     * 
     * @deprecated As of 7.1. See #11413.
     */
    @Deprecated
    public String registerDependency(String resourceUri, Class<?> context) {
        try {
            URI uri = new URI(resourceUri);
            String protocol = uri.getScheme();

            if (ApplicationConstants.PUBLISHED_PROTOCOL_NAME.equals(protocol)) {
                // Strip initial slash
                String resourceName = uri.getPath().substring(1);
                return registerPublishedFile(resourceName, context);
            }

            if (protocol != null || uri.getHost() != null) {
                return resourceUri;
            }

            // Bare path interpreted as published file
            return registerPublishedFile(resourceUri, context);
        } catch (URISyntaxException e) {
            getLogger().log(Level.WARNING,
                    "Could not parse resource url " + resourceUri, e);
            return resourceUri;
        }
    }

    /**
     * @deprecated As of 7.1. See #11413.
     */
    @Deprecated
    public Map<String, Class<?>> getDependencies() {
        return publishedFileContexts;
    }

    private String registerPublishedFile(String name, Class<?> context) {
        // Add to map of names accepted by servePublishedFile
        if (publishedFileContexts.containsKey(name)) {
            Class<?> oldContext = publishedFileContexts.get(name);
            if (oldContext != context) {
                getLogger()
                        .log(Level.WARNING,
                                "{0} published by both {1} and {2}. File from {2} will be used.",
                                new Object[] { name, context, oldContext });
            }
        } else {
            publishedFileContexts.put(name, context);
        }

        return ApplicationConstants.PUBLISHED_PROTOCOL_PREFIX + "/" + name;
    }

    /**
     * @deprecated As of 7.1. See #11410.
     */
    @Deprecated
    public ClientCache getClientCache(UI uI) {
        Integer uiId = Integer.valueOf(uI.getUIId());
        ClientCache cache = uiToClientCache.get(uiId);
        if (cache == null) {
            cache = new ClientCache();
            uiToClientCache.put(uiId, cache);
        }
        return cache;
    }

    /**
     * Checks if the connector is visible in context. For Components,
     * {@link #isComponentVisibleToClient(Component)} is used. For other types
     * of connectors, the contextual visibility of its first Component ancestor
     * is used. If no Component ancestor is found, the connector is not visible.
     * 
     * @deprecated As of 7.1. See #11411.
     * 
     * @param connector
     *            The connector to check
     * @return <code>true</code> if the connector is visible to the client,
     *         <code>false</code> otherwise
     */
    @Deprecated
    public static boolean isConnectorVisibleToClient(ClientConnector connector) {
        if (connector instanceof Component) {
            return isComponentVisibleToClient((Component) connector);
        } else {
            ClientConnector parent = connector.getParent();
            if (parent == null) {
                return false;
            } else {
                return isConnectorVisibleToClient(parent);
            }
        }
    }

    /**
     * Checks if the component should be visible to the client. Returns false if
     * the child should not be sent to the client, true otherwise.
     * 
     * @deprecated As of 7.1. See #11411.
     * 
     * @param child
     *            The child to check
     * @return true if the child is visible to the client, false otherwise
     */
    @Deprecated
    public static boolean isComponentVisibleToClient(Component child) {
        if (!child.isVisible()) {
            return false;
        }
        HasComponents parent = child.getParent();

        if (parent instanceof SelectiveRenderer) {
            if (!((SelectiveRenderer) parent).isRendered(child)) {
                return false;
            }
        }

        if (parent != null) {
            return isComponentVisibleToClient(parent);
        } else {
            if (child instanceof UI) {
                // UI has no parent and visibility was checked above
                return true;
            } else {
                // Component which is not attached to any UI
                return false;
            }
        }
    }

    /**
     * @deprecated As of 7.1. See #11412.
     */
    @Deprecated
    public String getTheme(UI uI) {
        String themeName = uI.getTheme();
        String requestThemeName = getRequestTheme();

        if (requestThemeName != null) {
            themeName = requestThemeName;
        }
        if (themeName == null) {
            themeName = VaadinServlet.getDefaultTheme();
        }
        return themeName;
    }

    private String getRequestTheme() {
        return requestThemeName;
    }

    /**
     * @deprecated As of 7.1. In 7.2 and later, use
     *             {@link ConnectorTracker#getConnector(String)
     *             uI.getConnectorTracker().getConnector(connectorId)} instead.
     *             See ticket #11411.
     */
    @Deprecated
    public ClientConnector getConnector(UI uI, String connectorId) {
        return uI.getConnectorTracker().getConnector(connectorId);
    }

    /**
     * @deprecated As of 7.1. Will be removed in the future.
     */
    @Deprecated
    public static class InvalidUIDLSecurityKeyException extends
            GeneralSecurityException {

        public InvalidUIDLSecurityKeyException(String message) {
            super(message);
        }
    }

    private final HashMap<Class<? extends ClientConnector>, Integer> typeToKey = new HashMap<Class<? extends ClientConnector>, Integer>();
    private int nextTypeKey = 0;

    /**
     * @deprecated As of 7.1. Will be removed in the future.
     */
    @Deprecated
    public String getTagForType(Class<? extends ClientConnector> class1) {
        Integer id = typeToKey.get(class1);
        if (id == null) {
            id = nextTypeKey++;
            typeToKey.put(class1, id);
            if (getLogger().isLoggable(Level.FINE)) {
                getLogger().log(Level.FINE, "Mapping {0} to {1}",
                        new Object[] { class1.getName(), id });
            }
        }
        return id.toString();
    }

    /**
     * Helper class for terminal to keep track of data that client is expected
     * to know.
     * 
     * TODO make customlayout templates (from theme) to be cached here.
     * 
     * @deprecated As of 7.1. See #11410.
     */
    @Deprecated
    public class ClientCache implements Serializable {

        private final Set<Object> res = new HashSet<Object>();

        /**
         * 
         * @param paintable
         * @return true if the given class was added to cache
         */
        public boolean cache(Object object) {
            return res.add(object);
        }

        public void clear() {
            res.clear();
        }

    }

    /**
     * @deprecated As of 7.1. See #11411.
     */
    @Deprecated
    public String getStreamVariableTargetUrl(ClientConnector owner,
            String name, StreamVariable value) {
        /*
         * We will use the same APP/* URI space as ApplicationResources but
         * prefix url with UPLOAD
         * 
         * eg. APP/UPLOAD/[UIID]/[PID]/[NAME]/[SECKEY]
         * 
         * SECKEY is created on each paint to make URL's unpredictable (to
         * prevent CSRF attacks).
         * 
         * NAME and PID from URI forms a key to fetch StreamVariable when
         * handling post
         */
        String paintableId = owner.getConnectorId();
        UI ui = owner.getUI();
        int uiId = ui.getUIId();
        String key = uiId + "/" + paintableId + "/" + name;

        ConnectorTracker connectorTracker = ui.getConnectorTracker();
        connectorTracker.addStreamVariable(paintableId, name, value);
        String seckey = connectorTracker.getSeckey(value);

        return ApplicationConstants.APP_PROTOCOL_PREFIX
                + ServletPortletHelper.UPLOAD_URL_PREFIX + key + "/" + seckey;

    }

    /**
     * Handles an exception that occurred when processing RPC calls or a file
     * upload.
     * 
     * @deprecated As of 7.1. See #11411.
     * 
     * @param ui
     *            The UI where the exception occured
     * @param throwable
     *            The exception
     * @param connector
     *            The Rpc target
     */
    @Deprecated
    public void handleConnectorRelatedException(ClientConnector connector,
            Throwable throwable) {
        ErrorEvent errorEvent = new ConnectorErrorEvent(connector, throwable);
        ErrorHandler handler = ErrorEvent.findErrorHandler(connector);
        handler.error(errorEvent);
    }

    /**
     * Requests that the given UI should be fully re-rendered on the client
     * side.
     * 
     * @since 7.1
     * @deprecated. As of 7.1. Should be refactored once locales are fixed
     *              (#11378)
     */
    @Deprecated
    public void repaintAll(UI ui) {
        getClientCache(ui).clear();
        ui.getConnectorTracker().markAllConnectorsDirty();
        ui.getConnectorTracker().markAllClientSidesUninitialized();
    }

    private static final Logger getLogger() {
        return Logger.getLogger(LegacyCommunicationManager.class.getName());
    }
}
