/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import com.vaadin.Application;
import com.vaadin.terminal.CombinedRequest;
import com.vaadin.terminal.DeploymentConfiguration;
import com.vaadin.terminal.WrappedRequest;

/**
 * Wrapper for {@link HttpServletRequest}.
 * 
 * @author Vaadin Ltd.
 * @since 7.0
 * 
 * @see WrappedRequest
 * @see WrappedHttpServletResponse
 */
public class WrappedHttpServletRequest extends HttpServletRequestWrapper
        implements WrappedRequest {

    private final DeploymentConfiguration deploymentConfiguration;

    /**
     * Wraps a http servlet request and associates with a deployment
     * configuration
     * 
     * @param request
     *            the http servlet request to wrap
     * @param deploymentConfiguration
     *            the associated deployment configuration
     */
    public WrappedHttpServletRequest(HttpServletRequest request,
            DeploymentConfiguration deploymentConfiguration) {
        super(request);
        this.deploymentConfiguration = deploymentConfiguration;
    }

    public String getRequestPathInfo() {
        return getPathInfo();
    }

    public int getSessionMaxInactiveInterval() {
        return getSession().getMaxInactiveInterval();
    }

    public Object getSessionAttribute(String name) {
        return getSession().getAttribute(name);
    }

    public void setSessionAttribute(String name, Object attribute) {
        getSession().setAttribute(name, attribute);
    }

    /**
     * Gets the original, unwrapped HTTP servlet request.
     * 
     * @return the servlet request
     */
    public HttpServletRequest getHttpServletRequest() {
        return this;
    }

    public DeploymentConfiguration getDeploymentConfiguration() {
        return deploymentConfiguration;
    }

    public BrowserDetails getBrowserDetails() {
        return new BrowserDetails() {
            public String getUriFragment() {
                return null;
            }

            public String getWindowName() {
                return null;
            }

            public WebBrowser getWebBrowser() {
                WebApplicationContext context = (WebApplicationContext) Application
                        .getCurrentApplication().getContext();
                return context.getBrowser();
            }
        };
    }

    /**
     * Helper method to get a <code>WrappedHttpServletRequest</code> from a
     * <code>WrappedRequest</code>. Aside from casting, this method also takes
     * care of situations where there's another level of wrapping.
     * 
     * @param request
     *            a wrapped request
     * @return a wrapped http servlet request
     * @throws ClassCastException
     *             if the wrapped request doesn't wrap a http servlet request
     */
    public static WrappedHttpServletRequest cast(WrappedRequest request) {
        if (request instanceof CombinedRequest) {
            CombinedRequest combinedRequest = (CombinedRequest) request;
            request = combinedRequest.getSecondRequest();
        }
        return (WrappedHttpServletRequest) request;
    }
}