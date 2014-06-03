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

/**
 * A resource that is served through the Connector that is using the resource.
 * 
 * @see AbstractClientConnector#setResource(String, Resource)
 * 
 * @author Vaadin Ltd
 * @version @VERSION@
 * @since 7.0.0
 */
public interface ConnectorResource extends Resource {
    public static final String CONNECTOR_PATH = "connector";

    /**
     * Gets resource as stream.
     * <p>
     * Note that this method is called while the session is locked to prevent
     * race conditions but the methods in the returned {@link DownloadStream}
     * are assumed to be unrelated to the VaadinSession and are called without
     * holding session locks (to prevent locking the session during long file
     * downloads).
     * </p>
     * 
     * @return A download stream which produces the resource content
     */
    public DownloadStream getStream();

    /**
     * Gets the virtual filename for this resource.
     * 
     * @return the file name associated to this resource.
     */
    public String getFilename();
}
