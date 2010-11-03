package com.vaadin.terminal.gwt.server;

import com.vaadin.terminal.StreamVariable;
import com.vaadin.terminal.StreamVariable.StreamingEndedEvent;

@SuppressWarnings("serial")
final class StremingEndedEventImpl extends AbstractStreamingEvent implements
        StreamingEndedEvent {

    public StremingEndedEventImpl(StreamVariable streamVariable, String filename,
            String type, long totalBytes) {
        super(streamVariable, filename, type, totalBytes, totalBytes);
    }

}
