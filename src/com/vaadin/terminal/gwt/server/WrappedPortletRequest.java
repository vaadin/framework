/*
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.server;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Map;

import javax.portlet.ClientDataRequest;
import javax.portlet.PortletRequest;
import javax.portlet.ResourceRequest;
import javax.servlet.http.HttpServletRequestWrapper;

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
        return ((ClientDataRequest) request).getContentLength();
    }

    public InputStream getInputStream() throws IOException {
        return ((ClientDataRequest) request).getPortletInputStream();
    }

    public String getParameter(String name) {
        String value = request.getParameter(name);
        if (value == null) {
            // for GateIn portlet container simple-portal
            try {
                Method getRealReq = request.getClass().getMethod(
                        "getRealRequest");
                HttpServletRequestWrapper origRequest = (HttpServletRequestWrapper) getRealReq
                        .invoke(request);
                value = origRequest.getParameter(name);
            } catch (Exception e) {
                // do nothing - not on GateIn simple-portal
            }
        }
        return value;
    }

    public Map<String, String[]> getParameterMap() {
        return request.getParameterMap();
        // TODO GateIn hack required here as well?
    }

    public String getRequestID() {
        return "WindowID:" + request.getWindowID();
    }

    public Object getWrappedRequest() {
        return request;
    }

    public boolean isRunningInPortlet() {
        return true;
    }

    public void setAttribute(String name, Object o) {
        request.setAttribute(name, o);
    }

    public String getRequestPathInfo() {
        if (request instanceof ResourceRequest) {
            return ((ResourceRequest) request).getResourceID();
        } else {
            // We do not use paths in portlet mode
            throw new UnsupportedOperationException(
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
        return ((ResourceRequest) request).getContentType();
    }

    public String getStaticFileLocation() {
        throw new UnsupportedOperationException("Please implement me!");
    }

    public BrowserDetails getBrowserDetails() {
        // No browserDetails available for normal requests
        return null;
    }

}