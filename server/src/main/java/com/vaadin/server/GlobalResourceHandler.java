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

package com.vaadin.server;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.vaadin.shared.ApplicationConstants;
import com.vaadin.ui.LegacyComponent;
import com.vaadin.ui.UI;
import com.vaadin.util.CurrentInstance;

/**
 * A {@link RequestHandler} that takes care of {@link ConnectorResource}s that
 * should not be served by the connector.
 *
 * @author Vaadin Ltd
 * @since 7.0.0
 */
public class GlobalResourceHandler implements RequestHandler {
    private static final String LEGACY_TYPE = "legacy";

    private static final String RESOURCE_REQUEST_PATH = "global/";

    /**
     * Used to detect when a resource is no longer used by any connector.
     */
    private final Map<Resource, Set<ClientConnector>> resourceUsers = new HashMap<>();
    /**
     * Used to find the resources that might not be needed any more when a
     * connector is unregistered.
     */
    private final Map<ClientConnector, Set<Resource>> usedResources = new HashMap<>();

    private final Map<ConnectorResource, String> legacyResourceKeys = new HashMap<>();
    private final Map<String, ConnectorResource> legacyResources = new HashMap<>();
    private int nextLegacyId = 0;

    // APP/global/[uiid]/[type]/[id]
    private static final Pattern PATTERN = Pattern
            .compile("^/?" + ApplicationConstants.APP_PATH + '/'
                    + RESOURCE_REQUEST_PATH + "(\\d+)/(([^/]+)(/.*))");

    @Override
    public boolean handleRequest(VaadinSession session, VaadinRequest request,
            VaadinResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            return false;
        }

        Matcher matcher = PATTERN.matcher(pathInfo);
        if (!matcher.matches()) {
            return false;
        }

        String uiid = matcher.group(1);
        String type = matcher.group(3);
        String key = matcher.group(2);

        if (key == null) {
            return error(request, response,
                    pathInfo + " is not a valid global resource path");
        }
        session.lock();
        Map<Class<?>, CurrentInstance> oldInstances = null;
        DownloadStream stream = null;
        try {
            UI ui = session.getUIById(Integer.parseInt(uiid));
            if (ui == null) {
                return error(request, response, "No UI found for id  " + uiid);
            }
            oldInstances = CurrentInstance.setCurrent(ui);
            ConnectorResource resource;
            if (LEGACY_TYPE.equals(type)) {
                resource = legacyResources.get(urlEncodedKey(key));
            } else {
                return error(request, response, "Unknown global resource type "
                        + type + " in requested path " + pathInfo);
            }

            if (resource == null) {
                return error(request, response,
                        "Global resource " + key + " not found");
            }

            stream = resource.getStream();
            if (stream == null) {
                return error(request, response,
                        "Resource " + resource + " didn't produce any stream.");
            }
        } finally {
            session.unlock();
            if (oldInstances != null) {
                CurrentInstance.restoreInstances(oldInstances);
            }
        }

        stream.writeResponse(request, response);
        return true;
    }

    private String urlEncodedKey(String key) {
        // getPathInfo return path decoded but without decoding plus as spaces
        return ResourceReference.encodeFileName(key.replace("+", " "));
    }

    /**
     * Registers a resource to be served with a global URL.
     * <p>
     * A {@link ConnectorResource} registered for a {@link LegacyComponent} will
     * be set to be served with a global URL. Other resource types will be
     * ignored and thus not served by this handler.
     *
     * @param resource
     *            the resource to register
     * @param ownerConnector
     *            the connector to which the resource belongs
     */
    public void register(Resource resource, ClientConnector ownerConnector) {
        if (resource instanceof ConnectorResource) {
            if (!(ownerConnector instanceof LegacyComponent)) {
                throw new IllegalArgumentException(
                        "A normal ConnectorResource can only be registered for legacy components.");
            }
            ConnectorResource connectorResource = (ConnectorResource) resource;
            if (!legacyResourceKeys.containsKey(resource)) {
                String uri = LEGACY_TYPE + '/'
                        + Integer.toString(nextLegacyId++);
                String filename = connectorResource.getFilename();
                if (filename != null && !filename.isEmpty()) {
                    uri += '/' + ResourceReference.encodeFileName(filename);
                }
                legacyResourceKeys.put(connectorResource, uri);
                legacyResources.put(uri, connectorResource);
                registerResourceUsage(connectorResource, ownerConnector);
            }
        }
    }

    private void unregisterResource(Resource resource) {
        String oldUri = legacyResourceKeys.remove(resource);
        if (oldUri != null) {
            legacyResources.remove(oldUri);
        }
    }

    private void registerResourceUsage(Resource resource,
            ClientConnector connector) {
        ensureInSet(resourceUsers, resource, connector);
        ensureInSet(usedResources, connector, resource);
    }

    private <K, V> void ensureInSet(Map<K, Set<V>> map, K key, V value) {
        Set<V> set = map.get(key);
        if (set == null) {
            set = new HashSet<>();
            map.put(key, set);
        }
        set.add(value);
    }

    /**
     * Gets a global URI for a resource if it's registered with this handler.
     *
     * @param connector
     *            the connector for which the uri should be generated.
     * @param resource
     *            the resource for which the uri should be generated.
     * @return an URI string, or <code>null</code> if the resource is not
     *         registered.
     */
    public String getUri(ClientConnector connector,
            ConnectorResource resource) {
        // app://APP/global/[ui]/[type]/[id]
        String uri = legacyResourceKeys.get(resource);
        if (uri != null && !uri.isEmpty()) {
            return ApplicationConstants.APP_PROTOCOL_PREFIX
                    + ApplicationConstants.APP_PATH + '/'
                    + RESOURCE_REQUEST_PATH + connector.getUI().getUIId() + '/'
                    + uri;
        } else {
            return null;
        }
    }

    /**
     * Notifies this handler that resources registered for the given connector
     * can be released.
     *
     * @param connector
     *            the connector for which any registered resources can be
     *            released.
     */
    public void unregisterConnector(ClientConnector connector) {
        Set<Resource> set = usedResources.remove(connector);
        if (set == null) {
            return;
        }

        for (Resource resource : set) {
            Set<ClientConnector> users = resourceUsers.get(resource);
            users.remove(connector);
            if (users.isEmpty()) {
                resourceUsers.remove(resource);
                unregisterResource(resource);
            }
        }
    }

    private static Logger getLogger() {
        return Logger.getLogger(GlobalResourceHandler.class.getName());
    }

    private static boolean error(VaadinRequest request, VaadinResponse response,
            String logMessage) throws IOException {
        getLogger().log(Level.WARNING, logMessage);
        response.sendError(HttpServletResponse.SC_NOT_FOUND,
                request.getPathInfo() + " can not be found");

        // Request handled (though not in a nice way)
        return true;
    }

}
