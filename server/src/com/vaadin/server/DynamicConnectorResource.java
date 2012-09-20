/*
 * Copyright 2011 Vaadin Ltd.
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

import java.util.Collections;
import java.util.Map;

import com.vaadin.service.FileTypeResolver;

/**
 * A resource that is served by calling
 * {@link ClientConnector#handleConnectorRequest(VaadinRequest, VaadinResponse, String)}
 * with appropriate parameters.
 * 
 * @author Vaadin Ltd
 * @version @VERSION@
 * @since 7.0.0
 */
public class DynamicConnectorResource implements Resource {

    private final ClientConnector connector;
    private final String path;
    private final Map<String, String> parameters;

    /**
     * Creates a DynamicConnectorResoruce for the given connector that will be
     * served by calling
     * {@link ClientConnector#handleConnectorRequest(VaadinRequest, VaadinResponse, String)}
     * with the given path.
     * 
     * @param connector
     *            the connector that should serve the resource
     * @param path
     *            the relative path of the request
     */
    public DynamicConnectorResource(ClientConnector connector, String path) {
        this(connector, path, null);
    }

    /**
     * Creates a DynamicConnectorResoruce for the given connector that will be
     * served by calling
     * {@link ClientConnector#handleConnectorRequest(VaadinRequest, VaadinResponse, String)}
     * with the given path and the given request parameters.
     * 
     * @param connector
     *            the connector that should serve the resource
     * @param path
     *            the relative path of the request
     * @param parameters
     *            the parameters that should be present in the request
     */
    public DynamicConnectorResource(ClientConnector connector, String path,
            Map<String, String> parameters) {
        this.connector = connector;
        this.path = path;
        this.parameters = parameters;
    }

    @Override
    public String getMIMEType() {
        return FileTypeResolver.getMIMEType(path);
    }

    public String getPath() {
        return path;
    }

    public ClientConnector getConnector() {
        return connector;
    }

    public Map<String, String> getParameters() {
        if (parameters == null) {
            return Collections.emptyMap();
        } else {
            return Collections.unmodifiableMap(parameters);
        }
    }

}
