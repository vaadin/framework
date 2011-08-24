/*
@ITMillApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.server;

@SuppressWarnings("serial")
public class UploadException extends Exception {
    public UploadException(Exception e) {
        super("Upload failed", e);
    }

    public UploadException(String msg) {
        super(msg);
    }
}
