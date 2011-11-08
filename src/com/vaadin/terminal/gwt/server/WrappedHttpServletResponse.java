package com.vaadin.terminal.gwt.server;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import com.vaadin.terminal.WrappedResponse;

/**
 * Concrete wrapper class for {@link HttpServletResponse}.
 * 
 * @see Response
 */
public class WrappedHttpServletResponse implements WrappedResponse {

    private final HttpServletResponse response;

    public WrappedHttpServletResponse(HttpServletResponse response) {
        this.response = response;
    }

    public OutputStream getOutputStream() throws IOException {
        return response.getOutputStream();
    }

    public HttpServletResponse getHttpServletResponse() {
        return response;
    }

    public void setContentType(String type) {
        response.setContentType(type);
    }

    public PrintWriter getWriter() throws IOException {
        return response.getWriter();
    }

    public void setStatus(int responseStatus) {
        response.setStatus(responseStatus);
    }

    public void setHeader(String name, String value) {
        response.setHeader(name, value);
    }

}