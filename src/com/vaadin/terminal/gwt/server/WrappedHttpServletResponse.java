/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.server;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import com.vaadin.terminal.DeploymentConfiguration;
import com.vaadin.terminal.WrappedResponse;

/**
 * Wrapper for {@link HttpServletResponse}.
 * 
 * @author Vaadin Ltd.
 * @since 7.0
 * 
 * @see WrappedResponse
 * @see WrappedHttpServletRequest
 */
public class WrappedHttpServletResponse implements WrappedResponse {

    private final HttpServletResponse response;
    private DeploymentConfiguration deploymentConfiguration;

    /**
     * Wraps a http servlet response and an associated deployment configuration
     * 
     * @param response
     *            the http servlet response to wrap
     * @param deploymentConfiguration
     *            the associated deployment configuration
     */
    public WrappedHttpServletResponse(HttpServletResponse response,
            DeploymentConfiguration deploymentConfiguration) {
        this.response = response;
        this.deploymentConfiguration = deploymentConfiguration;
    }

    public OutputStream getOutputStream() throws IOException {
        return response.getOutputStream();
    }

    /**
     * Gets the original unwrapped <code>HttpServletResponse</code>
     * 
     * @return the unwrapped response
     */
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

    public DeploymentConfiguration getDeploymentConfiguration() {
        return deploymentConfiguration;
    }
}