package com.vaadin.terminal.gwt.server;

import com.vaadin.terminal.StreamVariable;
import com.vaadin.terminal.StreamVariable.StreamingFailedEvent;

@SuppressWarnings("serial")
final class StreamingFailedEventImpl extends AbstractStreamingEvent implements
        StreamingFailedEvent {

    private final Exception exception;

    public StreamingFailedEventImpl(StreamVariable streamVariable, final String filename,
            final String type, long contentLength, long bytesReceived,
            final Exception exception) {
        super(streamVariable, filename, type, contentLength, bytesReceived);
        this.exception = exception;
    }

    public final Exception getException() {
        return exception;
    }

}
