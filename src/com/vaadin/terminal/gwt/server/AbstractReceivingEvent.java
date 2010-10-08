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
    private Receiver receiver;
    private long contentLength;
    private long bytesReceived;

    public String getFileName() {
        return filename;
    }

    public String getMimeType() {
        return type;
    }

    public AbstractReceivingEvent(Receiver receiver, String filename,
            String type, long length) {
        this.receiver = receiver;
        this.filename = filename;
        this.type = type;
        contentLength = length;
    }

    public Receiver getReceiver() {
        return receiver;
    }

    public long getContentLength() {
        return contentLength;
    }

    public long getBytesReceived() {
        return bytesReceived;
    }

    void setBytesReceived(long bytesReceived) {
        this.bytesReceived = bytesReceived;
    }

}
