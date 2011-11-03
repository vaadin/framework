package com.vaadin.terminal.gwt.server;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import com.vaadin.terminal.WrappedResponse;

public class WrappedHttpServletResponse implements WrappedResponse {

    private final HttpServletResponse response;

    public WrappedHttpServletResponse(HttpServletResponse response) {
        this.response = response;
    }

    public HttpServletResponse getHttpServletResponse() {
        return response;
    }

    public void setStatus(int statusCode) {
        response.setStatus(statusCode);
    }

    public void setContentType(String contentType) {
        response.setContentType(contentType);
    }

    public void setContentLenght(int contentLength) {
        response.setContentLength(contentLength);
    }

    public OutputStream getOutputStream() throws IOException {
        return response.getOutputStream();
    }

    public PrintWriter getWriter() throws IOException {
        return response.getWriter();
    }

}
