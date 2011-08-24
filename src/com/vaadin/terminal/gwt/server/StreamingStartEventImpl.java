/*
@ITMillApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.server;

import com.vaadin.terminal.StreamVariable.StreamingStartEvent;

@SuppressWarnings("serial")
final class StreamingStartEventImpl extends AbstractStreamingEvent implements
        StreamingStartEvent {

    private boolean disposed;

    public StreamingStartEventImpl(final String filename, final String type,
            long contentLength) {
        super(filename, type, contentLength, 0);
    }

    public void disposeStreamVariable() {
        disposed = true;
    }

    boolean isDisposed() {
        return disposed;
    }

}
