package com.vaadin.terminal.gwt.server;

import com.vaadin.terminal.Receiver;
import com.vaadin.terminal.ReceiverOwner.ReceivingEndedEvent;

@SuppressWarnings("serial")
class ReceivingEndedEventImpl extends AbstractReceivingEvent implements
        ReceivingEndedEvent {

    public ReceivingEndedEventImpl(Receiver receiver, String filename,
            String type, long totalBytes) {
        super(receiver, filename, type, totalBytes);
    }

}
