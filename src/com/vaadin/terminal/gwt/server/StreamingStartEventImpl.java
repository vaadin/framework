package com.vaadin.terminal.gwt.server;

import com.vaadin.terminal.StreamVariable.StreamingStartEvent;

@SuppressWarnings("serial")
final class StreamingStartEventImpl extends AbstractStreamingEvent implements
        StreamingStartEvent {

    public StreamingStartEventImpl(final String filename, final String type,
            long contentLength) {
        super(filename, type, contentLength, 0);
    }

}
