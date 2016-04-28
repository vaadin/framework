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

import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.UI;
import com.vaadin.util.FileTypeResolver;

/**
 * <code>ClassResource</code> is a named resource accessed with the class
 * loader.
 * 
 * This can be used to access resources such as icons, files, etc.
 * 
 * @see java.lang.Class#getResource(java.lang.String)
 * 
 * @author Vaadin Ltd.
 * @since 3.0
 */
@SuppressWarnings("serial")
public class ClassResource implements ConnectorResource, Serializable {

    /**
     * Default buffer size for this stream resource.
     */
    private int bufferSize = 0;

    /**
     * Default cache time for this stream resource.
     */
    private long cacheTime = DownloadStream.DEFAULT_CACHETIME;

    /**
     * Associated class used for identifying the source of the resource.
     */
    private final Class<?> associatedClass;

    /**
     * Name of the resource is relative to the associated class.
     */
    private final String resourceName;

    /**
     * Creates a new application resource instance. The resource id is relative
     * to the location of the UI of the component using this resource (or the
     * Application if using LegacyWindow).
     * 
     * @param resourceName
     *            the Unique identifier of the resource within the application.
     */
    public ClassResource(String resourceName) {
        this(null, resourceName);
    }

    /**
     * Creates a new application resource instance.
     * 
     * @param associatedClass
     *            the class of the which the resource is associated.
     * @param resourceName
     *            the Unique identifier of the resource within the application.
     */
    public ClassResource(Class<?> associatedClass, String resourceName) {
        this.associatedClass = associatedClass;
        this.resourceName = resourceName;
        if (resourceName == null) {
            throw new NullPointerException();
        }
    }

    /**
     * Gets the MIME type of this resource.
     * 
     * @see com.vaadin.server.Resource#getMIMEType()
     */
    @Override
    public String getMIMEType() {
        return FileTypeResolver.getMIMEType(resourceName);
    }

    @Override
    public String getFilename() {
        String[] parts = resourceName.split("/");
        return parts[parts.length - 1];
    }

    @Override
    public DownloadStream getStream() {
        final DownloadStream ds = new DownloadStream(getAssociatedClass()
                .getResourceAsStream(resourceName), getMIMEType(),
                getFilename());
        ds.setBufferSize(getBufferSize());
        ds.setCacheTime(getCacheTime());
        return ds;
    }

    protected Class<?> getAssociatedClass() {
        if (associatedClass == null) {
            UI current = UI.getCurrent();
            if (current instanceof LegacyWindow) {
                LegacyWindow legacyWindow = (LegacyWindow) current;
                return legacyWindow.getApplication().getClass();
            } else {
                return current.getClass();
            }
        }
        return associatedClass;
    }

    /**
     * Gets the size of the download buffer used for this resource.
     * 
     * <p>
     * If the buffer size is 0, the buffer size is decided by the terminal
     * adapter. The default value is 0.
     * </p>
     * 
     * @return the size of the buffer in bytes.
     */
    public int getBufferSize() {
        return bufferSize;
    }

    /**
     * Sets the size of the download buffer used for this resource.
     * 
     * @param bufferSize
     *            the size of the buffer in bytes.
     * 
     * @see #getBufferSize()
     */
    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    /**
     * Gets the length of cache expiration time.
     * 
     * <p>
     * This gives the adapter the possibility cache streams sent to the client.
     * The caching may be made in adapter or at the client if the client
     * supports caching. Default is {@link DownloadStream#DEFAULT_CACHETIME}.
     * </p>
     * 
     * @return Cache time in milliseconds
     */
    public long getCacheTime() {
        return cacheTime;
    }

    /**
     * Sets the length of cache expiration time.
     * 
     * <p>
     * This gives the adapter the possibility cache streams sent to the client.
     * The caching may be made in adapter or at the client if the client
     * supports caching. Zero or negative value disables the caching of this
     * stream.
     * </p>
     * 
     * @param cacheTime
     *            the cache time in milliseconds.
     * 
     */
    public void setCacheTime(long cacheTime) {
        this.cacheTime = cacheTime;
    }
}
