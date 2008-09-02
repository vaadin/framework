/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal;

import java.net.URL;

import com.itmill.toolkit.service.FileTypeResolver;

/**
 * <code>ExternalResource</code> implements source for resources fetched from
 * location specified by URL:s. The resources are fetched directly by the client
 * terminal and are not fetched trough the terminal adapter.
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.0
 */
public class ExternalResource implements Resource {

    /**
     * Url of the download.
     */
    private String sourceURL = null;

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
     */
    public ExternalResource(String sourceURL) {
        if (sourceURL == null) {
            throw new RuntimeException("Source must be non-null");
        }

        this.sourceURL = sourceURL.toString();
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
     * @see com.itmill.toolkit.terminal.Resource#getMIMEType()
     */
    public String getMIMEType() {
        return FileTypeResolver.getMIMEType(getURL().toString());
    }

}
