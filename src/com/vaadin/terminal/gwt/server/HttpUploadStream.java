/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.server;

import java.io.InputStream;

/**
 * AjaxAdapter implementation of the UploadStream interface.
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 5.0
 */
@SuppressWarnings("serial")
public class HttpUploadStream implements
        com.vaadin.terminal.UploadStream {

    /**
     * Holds value of property variableName.
     */
    private final String streamName;

    private final String contentName;

    private final String contentType;

    /**
     * Holds value of property variableValue.
     */
    private final InputStream stream;

    /**
     * Creates a new instance of UploadStreamImpl.
     * 
     * @param name
     *            the name of the stream.
     * @param stream
     *            the input stream.
     * @param contentName
     *            the name of the content.
     * @param contentType
     *            the type of the content.
     */
    public HttpUploadStream(String name, InputStream stream,
            String contentName, String contentType) {
        streamName = name;
        this.stream = stream;
        this.contentName = contentName;
        this.contentType = contentType;
    }

    /**
     * Gets the name of the stream.
     * 
     * @return the name of the stream.
     */
    public String getStreamName() {
        return streamName;
    }

    /**
     * Gets the input stream.
     * 
     * @return the Input stream.
     */
    public InputStream getStream() {
        return stream;
    }

    /**
     * Gets the input stream content type.
     * 
     * @return the content type of the input stream.
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Gets the stream content name. Stream content name usually differs from
     * the actual stream name. It is used to identify the content of the stream.
     * 
     * @return the Name of the stream content.
     */
    public String getContentName() {
        return contentName;
    }
}
