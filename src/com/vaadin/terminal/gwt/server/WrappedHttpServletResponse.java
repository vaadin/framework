/*
@ITMillApache2LicenseForJavaFiles@
 */

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

    public void setCacheTime(long milliseconds) {
        doSetCacheTime(this, milliseconds);
    }

    // Implementation shared with WrappedPortletResponse
    static void doSetCacheTime(WrappedResponse response, long milliseconds) {
        if (milliseconds <= 0) {
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Pragma", "no-cache");
            response.setDateHeader("Expires", 0);
        } else {
            response.setHeader("Cache-Control", "max-age=" + milliseconds
                    / 1000);
            response.setDateHeader("Expires", System.currentTimeMillis()
                    + milliseconds);
            // Required to apply caching in some Tomcats
            response.setHeader("Pragma", "cache");
        }
    }

    public void setDateHeader(String name, long timestamp) {
        response.setDateHeader(name, timestamp);
    }

    public void sendError(int errorCode, String message) throws IOException {
        response.sendError(errorCode, message);
    }
}