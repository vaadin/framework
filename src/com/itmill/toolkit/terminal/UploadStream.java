/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal;

import java.io.InputStream;

/**
 * Defines a variable type, that is used for passing uploaded files from
 * terminal. Most often, file upload is implented using the
 * {@link com.itmill.toolkit.ui.Upload Upload} component.
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.0
 */
public interface UploadStream {

    /**
     * Gets the name of the stream.
     * 
     * @return the name of the stream.
     */
    public String getStreamName();

    /**
     * Gets the input stream.
     * 
     * @return the Input stream.
     */
    public InputStream getStream();

    /**
     * Gets the input stream content type.
     * 
     * @return the content type of the input stream.
     */
    public String getContentType();

    /**
     * Gets stream content name. Stream content name usually differs from the
     * actual stream name. It is used to identify the content of the stream.
     * 
     * @return the Name of the stream content.
     */
    public String getContentName();
}
