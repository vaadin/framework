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

import com.vaadin.terminal.WrappedRequest;

public class WrappedPortletRequest implements WrappedRequest {

    private final PortletRequest request;

    public WrappedPortletRequest(PortletRequest request) {
        this.request = request;
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

    public String getRequestID() {
        return "WindowID:" + request.getWindowID();
    }

    public Object getWrappedRequest() {
        return request;
    }

    public void setAttribute(String name, Object o) {
        request.setAttribute(name, o);
    }

    public String getRequestPathInfo() {
        if (request instanceof ResourceRequest) {
            return ((ResourceRequest) request).getResourceID();
        } else {
            // We do not use paths in portlet mode
            throw new IllegalStateException(
                    "PathInfo only available when using ResourceRequests");
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

    public String getStaticFileLocation() {
        throw new UnsupportedOperationException("Please implement me!");
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

    public String getPortalProperty(String name) {
        return request.getPortalContext().getProperty(name);
    }

}