/*
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;

public class CombinedRequest implements WrappedRequest {

    private final WrappedRequest secondRequest;
    private final Map<String, String[]> parameterMap;
    private final String pathInfo;

    public CombinedRequest(WrappedRequest secondRequest,
            Map<String, String[]> parameterMap, String pathInfo) {
        this.secondRequest = secondRequest;
        this.parameterMap = parameterMap;
        this.pathInfo = pathInfo;
    }

    public String getParameter(String parameter) {
        String[] strings = parameterMap.get(parameter);
        if (strings == null || strings.length == 0) {
            return null;
        } else {
            return strings[0];
        }
    }

    public Map<String, String[]> getParameterMap() {
        return Collections.unmodifiableMap(parameterMap);
    }

    public int getContentLength() {
        return secondRequest.getContentLength();
    }

    public InputStream getInputStream() throws IOException {
        return secondRequest.getInputStream();
    }

    public Object getAttribute(String name) {
        return secondRequest.getAttribute(name);
    }

    public void setAttribute(String name, Object value) {
        secondRequest.setAttribute(name, value);
    }

    public String getRequestPathInfo() {
        return pathInfo;
    }

    public int getSessionMaxInactiveInterval() {
        return secondRequest.getSessionMaxInactiveInterval();
    }

    public Object getSessionAttribute(String name) {
        return secondRequest.getSessionAttribute(name);
    }

    public void setSessionAttribute(String name, Object attribute) {
        secondRequest.setSessionAttribute(name, attribute);
    }

    public String getContentType() {
        return secondRequest.getContentType();
    }

    public String getStaticFileLocation() {
        return secondRequest.getStaticFileLocation();
    }

    public BrowserDetails getBrowserDetails() {
        return new BrowserDetails() {
            public String getUriFragmet() {
                return secondRequest.getParameter("f");
            }
        };
    }

    public WrappedRequest getSecondRequest() {
        return secondRequest;
    }
}
