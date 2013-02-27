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
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.vaadin.server.communication.PushRequestHandler;
import com.vaadin.server.communication.ServletBootstrapHandler;
import com.vaadin.server.communication.ServletUIInitHandler;
import com.vaadin.shared.communication.PushMode;
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

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.server.LegacyCommunicationManager.Callback#criticalNotification
     * (com.vaadin.server.VaadinRequest, com.vaadin.server.VaadinResponse,
     * java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Deprecated
    @Override
    public void criticalNotification(VaadinRequest request,
            VaadinResponse response, String cap, String msg, String details,
            String url) throws IOException {
        getServlet().criticalNotification((VaadinServletRequest) request,
                (VaadinServletResponse) response, cap, msg, details, url);
    }

    @Override
    protected List<RequestHandler> createRequestHandlers() {
        List<RequestHandler> handlers = super.createRequestHandlers();
        handlers.add(0, new ServletBootstrapHandler());
        handlers.add(new ServletUIInitHandler());
        if (getDeploymentConfiguration().getPushMode() != PushMode.DISABLED) {
            handlers.add(new PushRequestHandler(this));
        }
        return handlers;
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
        if (ServletUIInitHandler.isUIInitRequest(request)) {
            // This is the first request if you are embedding by writing the
            // embedding code yourself
            return true;
        } else if (isOtherRequest(request)) {
            /*
             * I.e URIs that are not RPC calls or static (theme) files.
             */
            return true;
        }

        return false;
    }

    private boolean isOtherRequest(VaadinRequest request) {
        // TODO This should be refactored in some way. It should not be
        // necessary to check all these types.
        return (!ServletPortletHelper.isAppRequest(request)
                && !ServletUIInitHandler.isUIInitRequest(request)
                && !ServletPortletHelper.isFileUploadRequest(request)
                && !ServletPortletHelper.isHeartbeatRequest(request)
                && !ServletPortletHelper.isPublishedFileRequest(request) && !ServletPortletHelper
                    .isUIDLRequest(request));
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

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.server.VaadinService#handleSessionExpired(com.vaadin.server
     * .VaadinRequest, com.vaadin.server.VaadinResponse)
     */
    @Override
    protected void handleSessionExpired(VaadinRequest request,
            VaadinResponse response) throws ServiceException {
        if (!(request instanceof VaadinServletRequest)) {
            throw new ServiceException(new IllegalArgumentException(
                    "handleSessionExpired called with a non-VaadinServletRequest: "
                            + request.getClass().getName()));
        }

        VaadinServletRequest servletRequest = (VaadinServletRequest) request;
        VaadinServletResponse servletResponse = (VaadinServletResponse) response;

        try {
            SystemMessages ci = getSystemMessages(
                    ServletPortletHelper.findLocale(null, null, request),
                    request);
            if (ServletPortletHelper.isUIDLRequest(request)) {
                /*
                 * Invalidate session (weird to have session if we're saying
                 * that it's expired)
                 * 
                 * Session must be invalidated before criticalNotification as it
                 * commits the response.
                 */
                servletRequest.getSession().invalidate();

                // send uidl redirect
                criticalNotification(request, response,
                        ci.getSessionExpiredCaption(),
                        ci.getSessionExpiredMessage(), null,
                        ci.getSessionExpiredURL());

            } else if (ServletPortletHelper.isHeartbeatRequest(request)) {
                response.sendError(HttpServletResponse.SC_GONE,
                        "Session expired");
            } else {
                // 'plain' http req - e.g. browser reload;
                // just go ahead redirect the browser
                String sessionExpiredURL = ci.getSessionExpiredURL();
                if (sessionExpiredURL != null) {
                    servletResponse.sendRedirect(sessionExpiredURL);
                } else {
                    /*
                     * Session expired as a result of a standard http request
                     * and we have nowhere to redirect. Reloading would likely
                     * cause an endless loop. This can at least happen if
                     * refreshing a resource when the session has expired.
                     */
                    response.sendError(HttpServletResponse.SC_GONE,
                            "Session expired");
                }
            }
        } catch (IOException e) {
            throw new ServiceException(e);
        }

    }

    private static final Logger getLogger() {
        return Logger.getLogger(VaadinServletService.class.getName());
    }

}
