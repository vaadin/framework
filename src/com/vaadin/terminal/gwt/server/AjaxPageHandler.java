package com.vaadin.terminal.gwt.server;

import java.io.IOException;

import com.vaadin.Application;
import com.vaadin.terminal.RequestHandler;
import com.vaadin.terminal.WrappedRequest;
import com.vaadin.terminal.WrappedResponse;
import com.vaadin.ui.Root;

public abstract class AjaxPageHandler implements RequestHandler {

    public boolean handleRequest(Application application,
            WrappedRequest request, WrappedResponse response)
            throws IOException {

        // TODO Should all urls be handled here?
        Root root = application.getRoot(request);

        if (root == null) {
            writeError(response, null);
            return true;
        }

        writeAjaxPage(request, response, root);

        return true;
    }

    protected abstract void writeAjaxPage(WrappedRequest request,
            WrappedResponse response, Root root) throws IOException;

    protected void writeError(WrappedResponse response, Throwable e)
            throws IOException {
        response.setStatus(500);
        response.getWriter().println("Error");
    }

}
