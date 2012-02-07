/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Map;

import javax.portlet.ClientDataRequest;
import javax.portlet.PortletRequest;
import javax.portlet.ResourceRequest;

import com.vaadin.terminal.CombinedRequest;
import com.vaadin.terminal.DeploymentConfiguration;
import com.vaadin.terminal.WrappedRequest;

/**
 * Wrapper for {@link PortletRequest} and its subclasses.
 * 
 * @author Vaadin Ltd.
 * @since 7.0
 * 
 * @see WrappedRequest
 * @see WrappedPortletResponse
 */
public class WrappedPortletRequest implements WrappedRequest {

    private final PortletRequest request;
    private final DeploymentConfiguration deploymentConfiguration;

    /**
     * Wraps a portlet request and an associated deployment configuration
     * 
     * @param request
     *            the portlet request to wrap
     * @param deploymentConfiguration
     *            the associated deployment configuration
     */
    public WrappedPortletRequest(PortletRequest request,
            DeploymentConfiguration deploymentConfiguration) {
        this.request = request;
        this.deploymentConfiguration = deploymentConfiguration;
    }

    public Object getAttribute(String name) {
        return request.getAttribute(name);
    }

    public int getContentLength() {
        try {
            return ((ClientDataRequest) request).getContentLength();
        } catch (ClassCastException e) {
            throw new IllegalStateException(
                    "Content lenght only available for ClientDataRequests");
        }
    }

    public InputStream getInputStream() throws IOException {
        try {
            return ((ClientDataRequest) request).getPortletInputStream();
        } catch (ClassCastException e) {
            throw new IllegalStateException(
                    "Input data only available for ClientDataRequests");
        }
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
        if (request instanceof ResourceRequest) {
            return ((ResourceRequest) request).getResourceID();
        } else {
            return null;
        }
    }

    public int getSessionMaxInactiveInterval() {
        return request.getPortletSession().getMaxInactiveInterval();
    }

    public Object getSessionAttribute(String name) {
        return request.getPortletSession().getAttribute(name);
    }

    public void setSessionAttribute(String name, Object attribute) {
        request.getPortletSession().setAttribute(name, attribute);
    }

    /**
     * Gets the original, unwrapped portlet request.
     * 
     * @return the unwrapped portlet request
     */
    public PortletRequest getPortletRequest() {
        return request;
    }

    public String getContentType() {
        try {
            return ((ResourceRequest) request).getContentType();
        } catch (ClassCastException e) {
            throw new IllegalStateException(
                    "Content type only available for ResourceRequests");
        }
    }

    public BrowserDetails getBrowserDetails() {
        // No browserDetails available for normal requests
        return null;
    }

    public Locale getLocale() {
        return request.getLocale();
    }

    public String getRemoteAddr() {
        return null;
    }

    public boolean isSecure() {
        return request.isSecure();
    }

    public String getHeader(String string) {
        return null;
    }

    /**
     * Reads a portal property from the portal context of the wrapped request.
     * 
     * @param name
     *            a string with the name of the portal property to get
     * @return a string with the value of the property, or <code>null</code> if
     *         the property is not defined
     */
    public String getPortalProperty(String name) {
        return request.getPortalContext().getProperty(name);
    }

    public DeploymentConfiguration getDeploymentConfiguration() {
        return deploymentConfiguration;
    }

    /**
     * Helper method to get a <code>WrappedPortlettRequest</code> from a
     * <code>WrappedRequest</code>. Aside from casting, this method also takes
     * care of situations where there's another level of wrapping.
     * 
     * @param request
     *            a wrapped request
     * @return a wrapped portlet request
     * @throws ClassCastException
     *             if the wrapped request doesn't wrap a portlet request
     */
    public static WrappedPortletRequest cast(WrappedRequest request) {
        if (request instanceof CombinedRequest) {
            CombinedRequest combinedRequest = (CombinedRequest) request;
            request = combinedRequest.getSecondRequest();
        }
        return (WrappedPortletRequest) request;
    }
}
