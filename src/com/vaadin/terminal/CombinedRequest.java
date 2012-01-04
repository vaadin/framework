/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import com.vaadin.Application;
import com.vaadin.external.json.JSONArray;
import com.vaadin.external.json.JSONException;
import com.vaadin.external.json.JSONObject;
import com.vaadin.terminal.gwt.server.WebApplicationContext;
import com.vaadin.terminal.gwt.server.WebBrowser;

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
    private Map<String, String[]> parameterMap;

    /**
     * Creates a new combined request based on the second request and some
     * details from the first request.
     * 
     * @param secondRequest
     *            the second request which will be used as the foundation of the
     *            combined request
     * @throws JSONException
     *             if the initialParams parameter can not be decoded
     */
    public CombinedRequest(WrappedRequest secondRequest) throws JSONException {
        this.secondRequest = secondRequest;

        HashMap<String, String[]> map = new HashMap<String, String[]>();
        JSONObject initialParams = new JSONObject(
                secondRequest.getParameter("initialParams"));
        for (Iterator<?> keys = initialParams.keys(); keys.hasNext();) {
            String name = (String) keys.next();
            JSONArray jsonValues = initialParams.getJSONArray(name);
            String[] values = new String[jsonValues.length()];
            for (int i = 0; i < values.length; i++) {
                values[i] = jsonValues.getString(i);
            }
            map.put(name, values);
        }

        parameterMap = Collections.unmodifiableMap(map);

    }

    public String getParameter(String parameter) {
        String[] strings = getParameterMap().get(parameter);
        if (strings == null || strings.length == 0) {
            return null;
        } else {
            return strings[0];
        }
    }

    public Map<String, String[]> getParameterMap() {
        return parameterMap;
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
        return secondRequest.getParameter("initialPath");
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

    public BrowserDetails getBrowserDetails() {
        return new BrowserDetails() {
            public String getUriFragment() {
                String fragment = secondRequest.getParameter("fr");
                if (fragment == null) {
                    return "";
                } else {
                    return fragment;
                }
            }

            public String getWindowName() {
                return secondRequest.getParameter("wn");
            }

            public WebBrowser getWebBrowser() {
                WebApplicationContext context = (WebApplicationContext) Application
                        .getCurrentApplication().getContext();
                return context.getBrowser();
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

    public DeploymentConfiguration getDeploymentConfiguration() {
        return secondRequest.getDeploymentConfiguration();
    }
}
