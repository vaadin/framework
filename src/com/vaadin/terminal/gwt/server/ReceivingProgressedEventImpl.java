package com.vaadin.terminal.gwt.server;

import com.vaadin.terminal.Receiver;
import com.vaadin.terminal.ReceiverOwner.ReceivingProgressedEvent;

@SuppressWarnings("serial")
class ReceivingProgressedEventImpl extends AbstractReceivingEvent implements
        ReceivingProgressedEvent {

    public ReceivingProgressedEventImpl(Receiver receiver,
            final String filename, final String type, long contentLength) {
        super(receiver, filename, type, contentLength);
    }

}
