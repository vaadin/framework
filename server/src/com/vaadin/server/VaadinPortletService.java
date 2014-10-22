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

import static com.vaadin.shared.util.SharedUtil.trimTrailingSlashes;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.portlet.EventRequest;
import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;

import com.vaadin.server.VaadinPortlet.RequestType;
import com.vaadin.server.communication.PortletBootstrapHandler;
import com.vaadin.server.communication.PortletDummyRequestHandler;
import com.vaadin.server.communication.PortletListenerNotifier;
import com.vaadin.server.communication.PortletStateAwareRequestHandler;
import com.vaadin.server.communication.PortletUIInitHandler;
import com.vaadin.ui.UI;

public class VaadinPortletService extends VaadinService {
    private final VaadinPortlet portlet;

    public VaadinPortletService(VaadinPortlet portlet,
            DeploymentConfiguration deploymentConfiguration)
            throws ServiceException {
        super(deploymentConfiguration);
        this.portlet = portlet;
    }

    @Override
    protected List<RequestHandler> createRequestHandlers()
            throws ServiceException {
        List<RequestHandler> handlers = super.createRequestHandlers();

        handlers.add(new PortletUIInitHandler());
        handlers.add(new PortletListenerNotifier());
        handlers.add(0, new PortletDummyRequestHandler());
        handlers.add(0, new PortletBootstrapHandler());
        handlers.add(0, new PortletStateAwareRequestHandler());

        return handlers;
    }

    /**
     * Retrieves a reference to the portlet associated with this service.
     * 
     * @return A reference to the VaadinPortlet this service is using
     */
    public VaadinPortlet getPortlet() {
        return portlet;
    }

    private String getPortalProperty(VaadinRequest request, String propertyName) {
        return ((VaadinPortletRequest) request).getPortalProperty(propertyName);
    }

    private String getParameter(VaadinRequest request, String name,
            String defaultValue) {
        VaadinPortletRequest portletRequest = (VaadinPortletRequest) request;

        String preference = portletRequest.getPortletPreference(name);
        if (preference != null) {
            return preference;
        }

        String appOrSystemProperty = getAppOrSystemProperty(name, null);
        if (appOrSystemProperty != null) {
            return appOrSystemProperty;
        }

        String portalProperty = portletRequest.getPortalProperty(name);
        if (portalProperty != null) {

            // For backwards compatibility - automatically map old portal
            // default widget set to default widget set
            if (name.equals(Constants.PORTAL_PARAMETER_VAADIN_WIDGETSET)) {
                return mapDefaultWidgetset(portalProperty);
            }

            return portalProperty;
        }

        return defaultValue;
    }

    private String getAppOrSystemProperty(String name, String defaultValue) {
        DeploymentConfiguration deploymentConfiguration = getDeploymentConfiguration();

        return deploymentConfiguration.getApplicationOrSystemProperty(name,
                defaultValue);
    }

    @Override
    public String getConfiguredWidgetset(VaadinRequest request) {

        String widgetset = getDeploymentConfiguration()
                .getApplicationOrSystemProperty(
                        VaadinPortlet.PARAMETER_WIDGETSET, null);

        if (widgetset == null) {
            widgetset = getParameter(request,
                    Constants.PORTAL_PARAMETER_VAADIN_WIDGETSET,
                    Constants.DEFAULT_WIDGETSET);
        }

        return widgetset;
    }

    private String mapDefaultWidgetset(String widgetset) {
        if ("com.vaadin.portal.gwt.PortalDefaultWidgetSet".equals(widgetset)) {
            return Constants.DEFAULT_WIDGETSET;
        }

        return widgetset;
    }

    @Override
    public String getConfiguredTheme(VaadinRequest request) {
        return getParameter(request, Constants.PORTAL_PARAMETER_VAADIN_THEME,
                Constants.DEFAULT_THEME_NAME);
    }

    @Override
    public boolean isStandalone(VaadinRequest request) {
        return false;
    }

    @Override
    public String getStaticFileLocation(VaadinRequest request) {
        // /html is default for Liferay
        String staticFileLocation = getParameter(request,
                Constants.PORTAL_PARAMETER_VAADIN_RESOURCE_PATH, "/html");

        if (Constants.PORTLET_CONTEXT.equals(staticFileLocation)) {
            return request.getContextPath();
        } else {
            return trimTrailingSlashes(staticFileLocation);
        }
    }

    private PortletContext getPortletContext() {
        return getPortlet().getPortletContext();
    }

    @Override
    public String getMimeType(String resourceName) {
        return getPortletContext().getMimeType(resourceName);
    }

    @Override
    public File getBaseDirectory() {
        PortletContext context = getPortletContext();
        String resultPath = context.getRealPath("/");
        if (resultPath != null) {
            return new File(resultPath);
        } else {
            try {
                final URL url = context.getResource("/");
                return new File(url.getFile());
            } catch (final Exception e) {
                // FIXME: Handle exception
                getLogger()
                        .log(Level.INFO,
                                "Cannot access base directory, possible security issue "
                                        + "with Application Server or Servlet Container",
                                e);
            }
        }
        return null;
    }

    private static final Logger getLogger() {
        return Logger.getLogger(VaadinPortletService.class.getName());
    }

    @Override
    protected boolean requestCanCreateSession(VaadinRequest request) {
        if (!(request instanceof VaadinPortletRequest)) {
            throw new IllegalArgumentException(
                    "Request is not a VaadinPortletRequest");
        }

        PortletRequest portletRequest = ((VaadinPortletRequest) request)
                .getPortletRequest();
        if (portletRequest instanceof RenderRequest) {
            // In most cases the first request is a render request that
            // renders the HTML fragment. This should create a Vaadin
            // session unless there is already one.
            return true;
        } else if (portletRequest instanceof EventRequest) {
            // A portlet can also be sent an event even though it has not
            // been rendered, e.g. portlet on one page sends an event to a
            // portlet on another page and then moves the user to that page.
            return true;
        } else if (PortletUIInitHandler.isUIInitRequest(request)) {
            // In some cases, the RenderRequest seems to be cached, causing the
            // first request be the one triggered by vaadinBootstrap.js.
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
            type = getPortlet().getRequestType((VaadinPortletRequest) request);
            request.setAttribute(RequestType.class.getName(), type);
        }
        return type;
    }

    /**
     * Gets the currently processed portlet request. The current portlet request
     * is automatically defined when the request is started. The current portlet
     * request can not be used in e.g. background threads because of the way
     * server implementations reuse request instances.
     * 
     * @return the current portlet request instance if available, otherwise
     *         <code>null</code>
     * 
     */
    public static PortletRequest getCurrentPortletRequest() {
        VaadinPortletRequest currentRequest = getCurrentRequest();
        if (currentRequest != null) {
            return currentRequest.getPortletRequest();
        } else {
            return null;
        }
    }

    /**
     * Gets the currently processed Vaadin portlet request. The current request
     * is automatically defined when the request is started. The current request
     * can not be used in e.g. background threads because of the way server
     * implementations reuse request instances.
     * 
     * @return the current Vaadin portlet request instance if available,
     *         otherwise <code>null</code>
     * 
     */
    public static VaadinPortletRequest getCurrentRequest() {
        return (VaadinPortletRequest) VaadinService.getCurrentRequest();
    }

    /**
     * Gets the currently processed Vaadin portlet response. The current
     * response is automatically defined when the request is started. The
     * current response can not be used in e.g. background threads because of
     * the way server implementations reuse response instances.
     * 
     * @return the current Vaadin portlet response instance if available,
     *         otherwise <code>null</code>
     * 
     */
    public static VaadinPortletResponse getCurrentResponse() {
        return (VaadinPortletResponse) VaadinService.getCurrentResponse();
    }

    @Override
    protected VaadinSession createVaadinSession(VaadinRequest request)
            throws ServiceException {
        return new VaadinPortletSession(this);
    }

    @Override
    public String getServiceName() {
        return getPortlet().getPortletName();
    }

    /**
     * Always preserve UIs in portlets to make portlet actions work.
     */
    @Override
    public boolean preserveUIOnRefresh(UIProvider provider, UICreateEvent event) {
        return true;
    }

    @Override
    public InputStream getThemeResourceAsStream(UI uI, String themeName,
            String resource) {
        VaadinPortletSession session = (VaadinPortletSession) uI.getSession();
        PortletContext portletContext = session.getPortletSession()
                .getPortletContext();
        return portletContext.getResourceAsStream("/"
                + VaadinPortlet.THEME_DIR_PATH + '/' + themeName + "/"
                + resource);
    }

    @Override
    public String getMainDivId(VaadinSession session, VaadinRequest request,
            Class<? extends UI> uiClass) {
        PortletRequest portletRequest = ((VaadinPortletRequest) request)
                .getPortletRequest();
        /*
         * We need to generate a unique ID because some portals already create a
         * DIV with the portlet's Window ID as the DOM ID.
         */
        return "v-" + portletRequest.getWindowID();
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
            VaadinResponse response) {
        // TODO Figure out a better way to deal with
        // SessionExpiredExceptions
        getLogger().finest("A user session has expired");
    }

}
