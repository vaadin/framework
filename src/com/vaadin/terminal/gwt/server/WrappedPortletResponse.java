package com.vaadin.terminal.gwt.server;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.portlet.MimeResponse;
import javax.portlet.PortletResponse;
import javax.portlet.ResourceResponse;

import com.vaadin.terminal.WrappedResponse;

public class WrappedPortletResponse implements WrappedResponse {

    private final PortletResponse response;

    public WrappedPortletResponse(PortletResponse response) {
        this.response = response;
    }

    public OutputStream getOutputStream() throws IOException {
        return ((MimeResponse) response).getPortletOutputStream();
    }

    public PortletResponse getPortletResponse() {
        return response;
    }

    public void setContentType(String type) {
        ((MimeResponse) response).setContentType(type);
    }

    public PrintWriter getWriter() throws IOException {
        return ((MimeResponse) response).getWriter();
    }

    public void setStatus(int responseStatus) {
        response.setProperty(ResourceResponse.HTTP_STATUS_CODE,
                Integer.toString(responseStatus));
    }

    public void setHeader(String name, String value) {
        response.setProperty(name, value);
    }
}