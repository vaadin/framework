/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;

/**
 * A {@link WrappedRequest} with path and parameters from one request and
 * {@link WrappedRequest.BrowserDetails} extracted from another request.
 * 
 * This class is intended to be used for a two request initialization where the
 * first request fetches the actual application page and the second request
 * contains information extracted from the browser using javascript.
 * 
 */
public class CombinedRequest implements WrappedRequest {

    private final WrappedRequest secondRequest;
    private final Map<String, String[]> parameterMap;
    private final String pathInfo;

    /**
     * Creates a new combined request based on the second request and some
     * details from the first request.
     * 
     * @param secondRequest
     *            the second request which will be used as the foundation of the
     *            combined request
     * @param parameterMap
     *            the parameter map from the first request
     * @param pathInfo
     *            the path info from string the first request
     */
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
            public String getUriFragment() {
                String fragment = secondRequest.getParameter("f");
                if (fragment == null || fragment.length() == 0) {
                    return "";
                } else {
                    // Trim the initial # char
                    return fragment.substring(1);
                }
            }

            public String getWindowName() {
                return secondRequest.getParameter("wn");
            }
        };
    }

    /**
     * Gets the original second request. This can be used e.g. if a request
     * parameter from the second request is required.
     * 
     * @return the original second wrapped request
     */
    public WrappedRequest getSecondRequest() {
        return secondRequest;
    }

    public Locale getLocale() {
        return secondRequest.getLocale();
    }

    public String getRemoteAddr() {
        return secondRequest.getRemoteAddr();
    }

    public boolean isSecure() {
        return secondRequest.isSecure();
    }

    public String getHeader(String name) {
        return secondRequest.getHeader(name);
    }
}
