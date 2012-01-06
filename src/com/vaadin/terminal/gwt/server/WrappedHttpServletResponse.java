/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.server;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

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
public class WrappedHttpServletResponse extends HttpServletResponseWrapper
        implements WrappedResponse {

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
        super(response);
        this.deploymentConfiguration = deploymentConfiguration;
    }

    /**
     * Gets the original unwrapped <code>HttpServletResponse</code>
     * 
     * @return the unwrapped response
     */
    public HttpServletResponse getHttpServletResponse() {
        return this;
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

    public DeploymentConfiguration getDeploymentConfiguration() {
        return deploymentConfiguration;
    }
}