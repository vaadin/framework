package com.vaadin.terminal.gwt.server;

import com.vaadin.terminal.Receiver;
import com.vaadin.terminal.ReceiverOwner.ReceivingStartedEvent;

@SuppressWarnings("serial")
final class ReceivingStartedEventImpl extends AbstractReceivingEvent implements
        ReceivingStartedEvent {

    public ReceivingStartedEventImpl(Receiver receiver, final String filename,
            final String type, long contentLength) {
        super(receiver, filename, type, contentLength, 0);
    }

}
