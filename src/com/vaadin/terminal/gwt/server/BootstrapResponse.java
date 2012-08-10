/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.server;

import java.util.EventObject;
import java.util.Map;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.vaadin.Application;
import com.vaadin.terminal.WrappedRequest;
import com.vaadin.ui.Root;

public class BootstrapResponse extends EventObject {
    private final Document document;
    private final WrappedRequest request;
    private final Map<String, Object> headers;
    private final Element applicationTag;
    private final Application application;
    private final Integer rootId;

    public BootstrapResponse(BootstrapHandler handler, WrappedRequest request,
            Document document, Element applicationTag,
            Map<String, Object> headers, Application application, Integer rootId) {
        super(handler);
        this.request = request;
        this.document = document;
        this.applicationTag = applicationTag;
        this.headers = headers;
        this.application = application;
        this.rootId = rootId;
    }

    public void setHeader(String name, String value) {
        headers.put(name, value);
    }

    public void setDateHeader(String name, long timestamp) {
        headers.put(name, Long.valueOf(timestamp));
    }

    public BootstrapHandler getBootstrapHandler() {
        return (BootstrapHandler) getSource();
    }

    public WrappedRequest getRequest() {
        return request;
    }

    public Document getDocument() {
        return document;
    }

    public Element getApplicationTag() {
        return applicationTag;
    }

    public Application getApplication() {
        return application;
    }

    public Integer getRootId() {
        return rootId;
    }

    public Root getRoot() {
        return Root.getCurrent();
    }
}
