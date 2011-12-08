/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

public class UploadIFrameOnloadStrategy {

    native void hookEvents(com.google.gwt.dom.client.Element iframe,
            VUpload upload)
    /*-{
        iframe.onload = function() {
            upload.@com.vaadin.terminal.gwt.client.ui.VUpload::onSubmitComplete()();
        };
    }-*/;

    /**
     * @param iframe
     *            the iframe whose onLoad event is to be cleaned
     */
    native void unHookEvents(com.google.gwt.dom.client.Element iframe)
    /*-{
        iframe.onload = null;
    }-*/;

}
