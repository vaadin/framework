package com.vaadin.terminal.gwt.server;

import com.vaadin.terminal.Receiver;
import com.vaadin.terminal.ReceiverOwner.ReceivingEvent;

/**
 * Abstract base class for ReceivingEvent implementations.
 */
@SuppressWarnings("serial")
abstract class AbstractReceivingEvent implements ReceivingEvent {
    private final String type;
    private final String filename;
    private final Receiver receiver;
    private final long contentLength;
    private final long bytesReceived;

    public final String getFileName() {
        return filename;
    }

    public final String getMimeType() {
        return type;
    }

    protected AbstractReceivingEvent(Receiver receiver, String filename,
            String type, long length, long bytesReceived) {
        this.receiver = receiver;
        this.filename = filename;
        this.type = type;
        contentLength = length;
        this.bytesReceived = bytesReceived;
    }

    public final Receiver getReceiver() {
        return receiver;
    }

    public final long getContentLength() {
        return contentLength;
    }

    public final long getBytesReceived() {
        return bytesReceived;
    }

}
