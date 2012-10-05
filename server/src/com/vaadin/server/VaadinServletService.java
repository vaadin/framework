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

package com.vaadin.server;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;

import com.vaadin.server.VaadinServlet.RequestType;

public class VaadinServletService extends VaadinService {
    private final VaadinServlet servlet;

    public VaadinServletService(VaadinServlet servlet,
            DeploymentConfiguration deploymentConfiguration) {
        super(deploymentConfiguration);
        this.servlet = servlet;
    }

    protected VaadinServlet getServlet() {
        return servlet;
    }

    @Override
    public String getStaticFileLocation(VaadinRequest request) {
        HttpServletRequest servletRequest = VaadinServletRequest.cast(request);
        String staticFileLocation;
        // if property is defined in configurations, use that
        staticFileLocation = getDeploymentConfiguration()
                .getApplicationOrSystemProperty(
                        VaadinServlet.PARAMETER_VAADIN_RESOURCES, null);
        if (staticFileLocation != null) {
            return staticFileLocation;
        }

        // the last (but most common) option is to generate default location
        // from request

        // if context is specified add it to widgetsetUrl
        String ctxPath = servletRequest.getContextPath();

        // FIXME: ctxPath.length() == 0 condition is probably unnecessary
        // and
        // might even be wrong.

        if (ctxPath.length() == 0
                && request.getAttribute("javax.servlet.include.context_path") != null) {
            // include request (e.g portlet), get context path from
            // attribute
            ctxPath = (String) request
                    .getAttribute("javax.servlet.include.context_path");
        }

        // Remove heading and trailing slashes from the context path
        ctxPath = VaadinServlet.removeHeadingOrTrailing(ctxPath, "/");

        if (ctxPath.equals("")) {
            return "";
        } else {
            return "/" + ctxPath;
        }
    }

    @Override
    public String getConfiguredWidgetset(VaadinRequest request) {
        return getDeploymentConfiguration().getApplicationOrSystemProperty(
                VaadinServlet.PARAMETER_WIDGETSET,
                VaadinServlet.DEFAULT_WIDGETSET);
    }

    @Override
    public String getConfiguredTheme(VaadinRequest request) {
        // Use the default
        return VaadinServlet.getDefaultTheme();
    }

    @Override
    public boolean isStandalone(VaadinRequest request) {
        return true;
    }

    @Override
    public String getMimeType(String resourceName) {
        return getServlet().getServletContext().getMimeType(resourceName);
    }

    @Override
    public File getBaseDirectory() {
        final String realPath = VaadinServlet.getResourcePath(
                servlet.getServletContext(), "/");
        if (realPath == null) {
            return null;
        }
        return new File(realPath);
    }

    @Override
    protected boolean requestCanCreateSession(VaadinRequest request) {
        RequestType requestType = getRequestType(request);
        if (requestType == RequestType.BROWSER_DETAILS) {
            // This is the first request if you are embedding by writing the
            // embedding code yourself
            return true;
        } else if (requestType == RequestType.OTHER) {
            /*
             * I.e URIs that are not RPC calls or static (theme) files.
             */
            return true;
        }

        return false;
    }

    /**
     * Gets the request type for the request.
     * 
     * @param request
     *            the request to get a request type for
     * @return the request type
     * 
     * @deprecated might be refactored or removed before 7.0.0
     */
    @Deprecated
    protected RequestType getRequestType(VaadinRequest request) {
        RequestType type = (RequestType) request.getAttribute(RequestType.class
                .getName());
        if (type == null) {
            type = getServlet().getRequestType(
                    VaadinServletRequest.cast(request));
            request.setAttribute(RequestType.class.getName(), type);
        }
        return type;
    }

    @Override
    protected URL getApplicationUrl(VaadinRequest request)
            throws MalformedURLException {
        return getServlet().getApplicationUrl(
                VaadinServletRequest.cast(request));
    }

    @Override
    protected AbstractCommunicationManager createCommunicationManager(
            VaadinServiceSession session) {
        return new CommunicationManager(session);
    }

    public static HttpServletRequest getCurrentServletRequest() {
        VaadinRequest currentRequest = VaadinService.getCurrentRequest();
        try {
            VaadinServletRequest request = VaadinServletRequest
                    .cast(currentRequest);
            if (request != null) {
                return request.getHttpServletRequest();
            } else {
                return null;
            }
        } catch (ClassCastException e) {
            return null;
        }
    }

    public static VaadinServletResponse getCurrentResponse() {
        return (VaadinServletResponse) VaadinService.getCurrentResponse();
    }

    @Override
    public String getServiceName() {
        return getServlet().getServletName();
    }
}