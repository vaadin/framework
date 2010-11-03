package com.vaadin.terminal.gwt.server;

import com.vaadin.terminal.StreamVariable;
import com.vaadin.terminal.StreamVariable.StreamingProgressedEvent;

@SuppressWarnings("serial")
final class StreamingProgressedEventImpl extends AbstractStreamingEvent
        implements StreamingProgressedEvent {

    public StreamingProgressedEventImpl(StreamVariable streamVariable,
            final String filename, final String type, long contentLength,
            long bytesReceived) {
        super(streamVariable, filename, type, contentLength, bytesReceived);
    }

}
