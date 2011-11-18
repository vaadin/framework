/*
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.vaadin.terminal.WrappedRequest;

/**
 * Concrete wrapper class for {@link HttpServletRequest}.
 * 
 * @see Request
 */
public class WrappedHttpServletRequest implements WrappedRequest {

    private final HttpServletRequest request;
    private final AbstractApplicationServlet servlet;

    public WrappedHttpServletRequest(HttpServletRequest request,
            AbstractApplicationServlet servlet) {
        this.request = request;
        this.servlet = servlet;
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

    public String getRequestID() {
        return "RequestURL:" + request.getRequestURI();
    }

    public Object getWrappedRequest() {
        return request;
    }

    public boolean isRunningInPortlet() {
        return false;
    }

    public void setAttribute(String name, Object o) {
        request.setAttribute(name, o);
    }

    public String getRequestPathInfo() {
        return servlet.getRequestPathInfo(request);
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

    public HttpServletRequest getHttpServletRequest() {
        return request;
    }

    public String getContentType() {
        return request.getContentType();
    }
}