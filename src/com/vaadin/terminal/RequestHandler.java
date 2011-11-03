package com.vaadin.terminal;


public interface RequestHandler {

    boolean handleRequest(WrappedRequest request, WrappedResponse response);

}
