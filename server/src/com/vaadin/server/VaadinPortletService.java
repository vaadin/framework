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
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;

import com.vaadin.server.VaadinPortlet.RequestType;
import com.vaadin.ui.UI;

public class VaadinPortletService extends VaadinService {
    private final VaadinPortlet portlet;

    public VaadinPortletService(VaadinPortlet portlet,
            DeploymentConfiguration deploymentConfiguration) {
        super(deploymentConfiguration);
        this.portlet = portlet;

        // Set default class loader if not already set
        if (getClassLoader() == null) {
            /*
             * The servlet is most likely to be loaded with a class loader
             * specific to the application instead of some generic system class
             * loader that loads the Vaadin classes.
             */
            setClassLoader(portlet.getClass().getClassLoader());
        }
    }

    protected VaadinPortlet getPortlet() {
        return portlet;
    }

    private static String getPortalProperty(VaadinRequest request,
            String portalParameterVaadinWidgetset) {
        return ((VaadinPortletRequest) request)
                .getPortalProperty(portalParameterVaadinWidgetset);
    }

    @Override
    public String getConfiguredWidgetset(VaadinRequest request) {

        String widgetset = getDeploymentConfiguration()
                .getApplicationOrSystemProperty(
                        VaadinPortlet.PARAMETER_WIDGETSET, null);

        if (widgetset == null) {
            // If no widgetset defined for the application, check the
            // portal property
            widgetset = getPortalProperty(request,
                    VaadinPortlet.PORTAL_PARAMETER_VAADIN_WIDGETSET);
            if ("com.vaadin.portal.gwt.PortalDefaultWidgetSet"
                    .equals(widgetset)) {
                // For backwards compatibility - automatically map old portal
                // default widget set to default widget set
                widgetset = VaadinPortlet.DEFAULT_WIDGETSET;

            }
        }

        if (widgetset == null) {
            // If no widgetset defined for the portal, use the default
            widgetset = VaadinPortlet.DEFAULT_WIDGETSET;
        }

        return widgetset;
    }

    @Override
    public String getConfiguredTheme(VaadinRequest request) {

        // is the default theme defined by the portal?
        String themeName = getPortalProperty(request,
                Constants.PORTAL_PARAMETER_VAADIN_THEME);

        if (themeName == null) {
            // no, using the default theme defined by Vaadin
            themeName = VaadinPortlet.DEFAULT_THEME_NAME;
        }

        return themeName;
    }

    @Override
    public boolean isStandalone(VaadinRequest request) {
        return false;
    }

    @Override
    public String getStaticFileLocation(VaadinRequest request) {
        String staticFileLocation = getPortalProperty(request,
                Constants.PORTAL_PARAMETER_VAADIN_RESOURCE_PATH);
        if (staticFileLocation != null) {
            // remove trailing slash if any
            while (staticFileLocation.endsWith(".")) {
                staticFileLocation = staticFileLocation.substring(0,
                        staticFileLocation.length() - 1);
            }
            return staticFileLocation;
        } else {
            // default for Liferay
            return "/html";
        }
    }

    @Override
    public String getMimeType(String resourceName) {
        return getPortlet().getPortletContext().getMimeType(resourceName);
    }

    @Override
    public File getBaseDirectory() {
        PortletContext context = getPortlet().getPortletContext();
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
        RequestType requestType = getRequestType(request);
        if (requestType == RequestType.RENDER) {
            // In most cases the first request is a render request that
            // renders the HTML fragment. This should create a Vaadin
            // session unless there is already one.
            return true;
        } else if (requestType == RequestType.EVENT) {
            // A portlet can also be sent an event even though it has not
            // been rendered, e.g. portlet on one page sends an event to a
            // portlet on another page and then moves the user to that page.
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
     * @deprecated will likely change or be removed in a future version
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

    @Override
    protected AbstractCommunicationManager createCommunicationManager(
            VaadinSession session) {
        return new PortletCommunicationManager(session);
    }

    public static PortletRequest getCurrentPortletRequest() {
        VaadinRequest currentRequest = VaadinService.getCurrentRequest();
        if (currentRequest instanceof VaadinPortletRequest) {
            return ((VaadinPortletRequest) currentRequest).getPortletRequest();
        } else {
            return null;
        }
    }

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
}
