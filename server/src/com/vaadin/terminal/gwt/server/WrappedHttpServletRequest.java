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

    @Override
    public String getRequestPathInfo() {
        return getPathInfo();
    }

    @Override
    public int getSessionMaxInactiveInterval() {
        return getSession().getMaxInactiveInterval();
    }

    @Override
    public Object getSessionAttribute(String name) {
        return getSession().getAttribute(name);
    }

    @Override
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

    @Override
    public DeploymentConfiguration getDeploymentConfiguration() {
        return deploymentConfiguration;
    }

    @Override
    public BrowserDetails getBrowserDetails() {
        return new BrowserDetails() {
            @Override
            public String getUriFragment() {
                return null;
            }

            @Override
            public String getWindowName() {
                return null;
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