/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.server;

import com.vaadin.terminal.StreamVariable.StreamingEndEvent;

@SuppressWarnings("serial")
final class StreamingEndEventImpl extends AbstractStreamingEvent implements
        StreamingEndEvent {

    public StreamingEndEventImpl(String filename, String type, long totalBytes) {
        super(filename, type, totalBytes, totalBytes);
    }

}
