/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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
public class WrappedHttpServletRequest implements WrappedRequest {

    private final HttpServletRequest request;
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
        this.request = request;
        this.deploymentConfiguration = deploymentConfiguration;
    }

    public Object getAttribute(String name) {
        return request.getAttribute(name);
    }

    public int getContentLength() {
        return request.getContentLength();
    }

    public InputStream getInputStream() throws IOException {
        return request.getInputStream();
    }

    public String getParameter(String name) {
        return request.getParameter(name);
    }

    public Map<String, String[]> getParameterMap() {
        return request.getParameterMap();
    }

    public void setAttribute(String name, Object o) {
        request.setAttribute(name, o);
    }

    public String getRequestPathInfo() {
        return request.getPathInfo();
    }

    public int getSessionMaxInactiveInterval() {
        return request.getSession().getMaxInactiveInterval();
    }

    public Object getSessionAttribute(String name) {
        return request.getSession().getAttribute(name);
    }

    public void setSessionAttribute(String name, Object attribute) {
        request.getSession().setAttribute(name, attribute);
    }

    /**
     * Gets the original, unwrapped HTTP servlet request.
     * 
     * @return the servlet request
     */
    public HttpServletRequest getHttpServletRequest() {
        return request;
    }

    public String getContentType() {
        return request.getContentType();
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

    public Locale getLocale() {
        return request.getLocale();
    }

    public String getRemoteAddr() {
        return request.getRemoteAddr();
    }

    public boolean isSecure() {
        return request.isSecure();
    }

    public String getHeader(String headerName) {
        return request.getHeader(headerName);
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