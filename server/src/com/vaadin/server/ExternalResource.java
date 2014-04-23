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
import java.net.URL;

import com.vaadin.util.FileTypeResolver;

/**
 * <code>ExternalResource</code> implements source for resources fetched from
 * location specified by URL:s. The resources are fetched directly by the client
 * terminal and are not fetched trough the terminal adapter.
 * 
 * @author Vaadin Ltd.
 * @since 3.0
 */
@SuppressWarnings("serial")
public class ExternalResource implements Resource, Serializable {

    /**
     * Url of the download.
     */
    private String sourceURL = null;

    /**
     * MIME Type for the resource
     */
    private String mimeType = null;

    /**
     * Creates a new download component for downloading directly from given URL.
     * 
     * @param sourceURL
     *            the source URL.
     */
    public ExternalResource(URL sourceURL) {
        if (sourceURL == null) {
            throw new RuntimeException("Source must be non-null");
        }

        this.sourceURL = sourceURL.toString();
    }

    /**
     * Creates a new download component for downloading directly from given URL.
     * 
     * @param sourceURL
     *            the source URL.
     * @param mimeType
     *            the MIME Type
     */
    public ExternalResource(URL sourceURL, String mimeType) {
        this(sourceURL);
        this.mimeType = mimeType;
    }

    /**
     * Creates a new download component for downloading directly from given URL.
     * 
     * @param sourceURL
     *            the source URL.
     */
    public ExternalResource(String sourceURL) {
        if (sourceURL == null) {
            throw new RuntimeException("Source must be non-null");
        }

        this.sourceURL = sourceURL.toString();
    }

    /**
     * Creates a new download component for downloading directly from given URL.
     * 
     * @param sourceURL
     *            the source URL.
     * @param mimeType
     *            the MIME Type
     */
    public ExternalResource(String sourceURL, String mimeType) {
        this(sourceURL);
        this.mimeType = mimeType;
    }

    /**
     * Gets the URL of the external resource.
     * 
     * @return the URL of the external resource.
     */
    public String getURL() {
        return sourceURL;
    }

    /**
     * Gets the MIME type of the resource.
     * 
     * @see com.vaadin.server.Resource#getMIMEType()
     */
    @Override
    public String getMIMEType() {
        if (mimeType == null) {
            mimeType = FileTypeResolver.getMIMEType(getURL().toString());
        }
        return mimeType;
    }

    /**
     * Sets the MIME type of the resource.
     */
    public void setMIMEType(String mimeType) {
        this.mimeType = mimeType;
    }

}
