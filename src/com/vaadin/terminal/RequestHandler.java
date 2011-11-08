package com.vaadin.terminal;

import java.io.IOException;

import com.vaadin.Application;

public interface RequestHandler {

    boolean handleRequest(Application application, WrappedRequest request, WrappedResponse response)
            throws IOException;

}
