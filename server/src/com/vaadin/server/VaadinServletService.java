/*
 * Copyright 2000-2014 Vaadin Ltd.
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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.atmosphere.util.Version;

import com.vaadin.server.communication.PushRequestHandler;
import com.vaadin.server.communication.ServletBootstrapHandler;
import com.vaadin.server.communication.ServletUIInitHandler;
import com.vaadin.ui.UI;

public class VaadinServletService extends VaadinService {
    private final VaadinServlet servlet;

    private boolean atmosphereAvailable = checkAtmosphereSupport();

    /**
     * Keeps track of whether a warning about missing push support has already
     * been logged. This is used to avoid spamming the log with the same message
     * every time a new UI is bootstrapped.
     */
    private boolean pushWarningLogged = false;

    public VaadinServletService(VaadinServlet servlet,
            DeploymentConfiguration deploymentConfiguration)
            throws ServiceException {
        super(deploymentConfiguration);
        this.servlet = servlet;
    }

    private static boolean checkAtmosphereSupport() {
        try {
            String rawVersion = Version.getRawVersion();
            if (!Constants.REQUIRED_ATMOSPHERE_RUNTIME_VERSION
                    .equals(rawVersion)) {
                getLogger().log(
                        Level.WARNING,
                        Constants.INVALID_ATMOSPHERE_VERSION_WARNING,
                        new Object[] {
                                Constants.REQUIRED_ATMOSPHERE_RUNTIME_VERSION,
                                rawVersion });
            }
            return true;
        } catch (NoClassDefFoundError e) {
            return false;
        }
    }

    @Override
    protected List<RequestHandler> createRequestHandlers()
            throws ServiceException {
        List<RequestHandler> handlers = super.createRequestHandlers();
        handlers.add(0, new ServletBootstrapHandler());
        handlers.add(new ServletUIInitHandler());
        if (atmosphereAvailable) {
            try {
                handlers.add(new PushRequestHandler(this));
            } catch (ServiceException e) {
                // Atmosphere init failed. Push won't work but we don't throw a
                // service exception as we don't want to prevent non-push
                // applications from working
                getLogger()
                        .log(Level.WARNING,
                                "Error initializing Atmosphere. Push will not work.",
                                e);
                atmosphereAvailable = false;
            }
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
                && !ServletPortletHelper.isPublishedFileRequest(request)
                && !ServletPortletHelper.isUIDLRequest(request) && !ServletPortletHelper
                    .isPushRequest(request));
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

    private static final Logger getLogger() {
        return Logger.getLogger(VaadinServletService.class.getName());
    }

    @Override
    public boolean ensurePushAvailable() {
        if (atmosphereAvailable) {
            return true;
        } else {
            if (!pushWarningLogged) {
                pushWarningLogged = true;
                getLogger().log(Level.WARNING,
                        Constants.ATMOSPHERE_MISSING_ERROR);
            }
            return false;
        }
    }
}
