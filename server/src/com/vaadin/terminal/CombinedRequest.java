/*
 * Copyright 2011 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
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

    @Override
    public String getParameter(String parameter) {
        String[] strings = getParameterMap().get(parameter);
        if (strings == null || strings.length == 0) {
            return null;
        } else {
            return strings[0];
        }
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return parameterMap;
    }

    @Override
    public int getContentLength() {
        return secondRequest.getContentLength();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return secondRequest.getInputStream();
    }

    @Override
    public Object getAttribute(String name) {
        return secondRequest.getAttribute(name);
    }

    @Override
    public void setAttribute(String name, Object value) {
        secondRequest.setAttribute(name, value);
    }

    @Override
    public String getRequestPathInfo() {
        return secondRequest.getParameter("initialPath");
    }

    @Override
    public int getSessionMaxInactiveInterval() {
        return secondRequest.getSessionMaxInactiveInterval();
    }

    @Override
    public Object getSessionAttribute(String name) {
        return secondRequest.getSessionAttribute(name);
    }

    @Override
    public void setSessionAttribute(String name, Object attribute) {
        secondRequest.setSessionAttribute(name, attribute);
    }

    @Override
    public String getContentType() {
        return secondRequest.getContentType();
    }

    @Override
    public BrowserDetails getBrowserDetails() {
        return new BrowserDetails() {
            @Override
            public String getUriFragment() {
                String fragment = secondRequest.getParameter("fr");
                if (fragment == null) {
                    return "";
                } else {
                    return fragment;
                }
            }

            @Override
            public String getWindowName() {
                return secondRequest.getParameter("wn");
            }

            @Override
            public WebBrowser getWebBrowser() {
                WebApplicationContext context = (WebApplicationContext) Application
                        .getCurrent().getContext();
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

    @Override
    public Locale getLocale() {
        return secondRequest.getLocale();
    }

    @Override
    public String getRemoteAddr() {
        return secondRequest.getRemoteAddr();
    }

    @Override
    public boolean isSecure() {
        return secondRequest.isSecure();
    }

    @Override
    public String getHeader(String name) {
        return secondRequest.getHeader(name);
    }

    @Override
    public DeploymentConfiguration getDeploymentConfiguration() {
        return secondRequest.getDeploymentConfiguration();
    }
}
