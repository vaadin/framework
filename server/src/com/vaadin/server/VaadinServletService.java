/*
 * Copyright 2000-2013 Vaadin Ltd.
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
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import com.vaadin.server.VaadinServlet.RequestType;
import com.vaadin.server.communication.ServletBootstrapHandler;
import com.vaadin.ui.UI;

public class VaadinServletService extends VaadinService {
    private final VaadinServlet servlet;

    public VaadinServletService(VaadinServlet servlet,
            DeploymentConfiguration deploymentConfiguration) {
        super(deploymentConfiguration);
        this.servlet = servlet;

        // Set default class loader if not already set
        if (getClassLoader() == null) {
            /*
             * The servlet is most likely to be loaded with a class loader
             * specific to the application instead of some generic system class
             * loader that loads the Vaadin classes.
             */
            setClassLoader(servlet.getClass().getClassLoader());
        }
    }

    /**
     * Retrieves a reference to the servlet associated with this service.
     * 
     * @return A reference to the VaadinServlet this service is using
     */
    public VaadinServlet getServlet() {
        return servlet;
    }

    @Override
    public String getStaticFileLocation(VaadinRequest request) {
        VaadinServletRequest servletRequest = (VaadinServletRequest) request;
        String staticFileLocation;
        // if property is defined in configurations, use that
        staticFileLocation = getDeploymentConfiguration()
                .getApplicationOrSystemProperty(
                        VaadinServlet.PARAMETER_VAADIN_RESOURCES, null);
        if (staticFileLocation != null) {
            return staticFileLocation;
        }

        // the last (but most common) option is to generate default location
        // from request by finding how many "../" should be added to the
        // requested path before we get to the context root

        String requestedPath = servletRequest.getServletPath();
        String pathInfo = servletRequest.getPathInfo();
        if (pathInfo != null) {
            requestedPath += pathInfo;
        }

        return getCancelingRelativePath(requestedPath);
    }

    /**
     * Gets a relative path that cancels the provided path. This essentially
     * adds one .. for each part of the path to cancel.
     * 
     * @param pathToCancel
     *            the path that should be canceled
     * @return a relative path that cancels out the provided path segment
     */
    public static String getCancelingRelativePath(String pathToCancel) {
        StringBuilder sb = new StringBuilder(".");
        // Start from i = 1 to ignore first slash
        for (int i = 1; i < pathToCancel.length(); i++) {
            if (pathToCancel.charAt(i) == '/') {
                sb.append("/..");
            }
        }
        return sb.toString();
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
     * @deprecated As of 7.0. Will likely change or be removed in a future
     *             version
     */
    @Deprecated
    protected RequestType getRequestType(VaadinRequest request) {
        RequestType type = (RequestType) request.getAttribute(RequestType.class
                .getName());
        if (type == null) {
            type = getServlet().getRequestType((VaadinServletRequest) request);
            request.setAttribute(RequestType.class.getName(), type);
        }
        return type;
    }

    @Override
    protected URL getApplicationUrl(VaadinRequest request)
            throws MalformedURLException {
        return getServlet().getApplicationUrl((VaadinServletRequest) request);
    }

    public static HttpServletRequest getCurrentServletRequest() {
        VaadinRequest currentRequest = VaadinService.getCurrentRequest();
        if (currentRequest instanceof VaadinServletRequest) {
            return (VaadinServletRequest) currentRequest;
        } else {
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

    @Override
    public InputStream getThemeResourceAsStream(UI uI, String themeName,
            String resource) {
        VaadinServletService service = (VaadinServletService) uI.getSession()
                .getService();
        ServletContext servletContext = service.getServlet()
                .getServletContext();
        return servletContext.getResourceAsStream("/"
                + VaadinServlet.THEME_DIR_PATH + '/' + themeName + "/"
                + resource);
    }

    @Override
    public String getMainDivId(VaadinSession session, VaadinRequest request,
            Class<? extends UI> uiClass) {
        String appId = null;
        try {
            URL appUrl = getServlet().getApplicationUrl(
                    (VaadinServletRequest) request);
            appId = appUrl.getPath();
        } catch (MalformedURLException e) {
            // Just ignore problem here
        }

        if (appId == null || "".equals(appId) || "/".equals(appId)) {
            appId = "ROOT";
        }
        appId = appId.replaceAll("[^a-zA-Z0-9]", "");
        // Add hashCode to the end, so that it is still (sort of)
        // predictable, but indicates that it should not be used in CSS
        // and
        // such:
        int hashCode = appId.hashCode();
        if (hashCode < 0) {
            hashCode = -hashCode;
        }
        appId = appId + "-" + hashCode;
        return appId;
    }

    @Override
    protected BootstrapHandler createBootstrapHandler(VaadinSession session) {
        return new ServletBootstrapHandler();
    }
}
