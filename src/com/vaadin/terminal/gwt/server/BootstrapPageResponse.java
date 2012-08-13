/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.server;

import java.util.Map;

import org.jsoup.nodes.Document;

import com.vaadin.Application;
import com.vaadin.terminal.WrappedRequest;

public class BootstrapPageResponse extends BootstrapResponse {

    private final Map<String, Object> headers;
    private final Document document;

    public BootstrapPageResponse(BootstrapHandler handler,
            WrappedRequest request, Document document,
            Map<String, Object> headers, Application application, Integer rootId) {
        super(handler, request, application, rootId);
        this.headers = headers;
        this.document = document;
    }

    public void setHeader(String name, String value) {
        headers.put(name, value);
    }

    public void setDateHeader(String name, long timestamp) {
        headers.put(name, Long.valueOf(timestamp));
    }

    public Document getDocument() {
        return document;
    }

}
