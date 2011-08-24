/*
@ITMillApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.server;

import com.vaadin.terminal.StreamVariable.StreamingProgressEvent;

@SuppressWarnings("serial")
final class StreamingProgressEventImpl extends AbstractStreamingEvent implements
        StreamingProgressEvent {

    public StreamingProgressEventImpl(final String filename, final String type,
            long contentLength, long bytesReceived) {
        super(filename, type, contentLength, bytesReceived);
    }

}
