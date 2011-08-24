/*
@ITMillApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client;

import com.google.gwt.xhr.client.XMLHttpRequest;

public class SynchronousXHR extends XMLHttpRequest {

    protected SynchronousXHR() {
    }

    public native final void synchronousPost(String uri, String requestData)
    /*-{
        try {
            this.open("POST", uri, false);
            this.setRequestHeader("Content-Type", "text/plain;charset=utf-8");
            this.send(requestData);
        } catch (e) {
           // No errors are managed as this is synchronous forceful send that can just fail
        }
    }-*/;

}
