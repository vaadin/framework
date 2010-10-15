package com.vaadin.terminal.gwt.server;

import com.vaadin.terminal.Receiver;
import com.vaadin.terminal.ReceiverOwner.ReceivingFailedEvent;

@SuppressWarnings("serial")
final class ReceivingFailedEventImpl extends AbstractReceivingEvent implements
        ReceivingFailedEvent {

    private final Exception exception;

    public ReceivingFailedEventImpl(Receiver receiver, final String filename,
            final String type, long contentLength, long bytesReceived,
            final Exception exception) {
        super(receiver, filename, type, contentLength, bytesReceived);
        this.exception = exception;
    }

    public final Exception getException() {
        return exception;
    }

}
