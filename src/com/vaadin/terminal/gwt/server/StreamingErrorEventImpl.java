/*
@ITMillApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.server;

import com.vaadin.terminal.StreamVariable.StreamingErrorEvent;

@SuppressWarnings("serial")
final class StreamingErrorEventImpl extends AbstractStreamingEvent implements
        StreamingErrorEvent {

    private final Exception exception;

    public StreamingErrorEventImpl(final String filename, final String type,
            long contentLength, long bytesReceived, final Exception exception) {
        super(filename, type, contentLength, bytesReceived);
        this.exception = exception;
    }

    public final Exception getException() {
        return exception;
    }

}
