package com.vaadin.terminal.gwt.server;

import com.vaadin.terminal.Receiver;
import com.vaadin.terminal.ReceiverOwner.ReceivingFailedEvent;

@SuppressWarnings("serial")
class ReceivingFailedEventImpl extends AbstractReceivingEvent implements
        ReceivingFailedEvent {

    private final Exception exception;

    public ReceivingFailedEventImpl(Receiver receiver, final String filename,
            final String type, long contentLength, final Exception exception) {
        super(receiver, filename, type, contentLength);
        this.exception = exception;
    }

    public Exception getException() {
        return exception;
    }

}
