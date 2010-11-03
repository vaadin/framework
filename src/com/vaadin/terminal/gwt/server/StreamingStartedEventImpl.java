package com.vaadin.terminal.gwt.server;

import com.vaadin.terminal.StreamVariable;
import com.vaadin.terminal.StreamVariable.StreamingStartedEvent;

@SuppressWarnings("serial")
final class StreamingStartedEventImpl extends AbstractStreamingEvent implements
        StreamingStartedEvent {

    public StreamingStartedEventImpl(StreamVariable streamVariable, final String filename,
            final String type, long contentLength) {
        super(streamVariable, filename, type, contentLength, 0);
    }

}
