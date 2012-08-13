/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.server;

import java.util.EventObject;

import com.vaadin.Application;
import com.vaadin.terminal.WrappedRequest;
import com.vaadin.ui.Root;

public abstract class BootstrapResponse extends EventObject {
    private final WrappedRequest request;
    private final Application application;
    private final Integer rootId;

    public BootstrapResponse(BootstrapHandler handler, WrappedRequest request,
            Application application, Integer rootId) {
        super(handler);
        this.request = request;
        this.application = application;
        this.rootId = rootId;
    }

    public BootstrapHandler getBootstrapHandler() {
        return (BootstrapHandler) getSource();
    }

    public WrappedRequest getRequest() {
        return request;
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
