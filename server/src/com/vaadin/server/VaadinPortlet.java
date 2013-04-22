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

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import com.liferay.portal.kernel.util.PortalClassInvoker;
import com.liferay.portal.kernel.util.PropsUtil;
import com.vaadin.server.communication.PortletDummyRequestHandler;
import com.vaadin.server.communication.PortletUIInitHandler;
import com.vaadin.util.CurrentInstance;

/**
 * Portlet 2.0 base class. This replaces the servlet in servlet/portlet 1.0
 * deployments and handles various portlet requests from the browser.
 * 
 * TODO Document me!
 * 
 * @author peholmst
 */
public class VaadinPortlet extends GenericPortlet implements Constants,
        Serializable {

    /**
     * @deprecated As of 7.0. Will likely change or be removed in a future
     *             version
     */
    @Deprecated
    public static final String RESOURCE_URL_ID = "APP";

    public static class VaadinHttpAndPortletRequest extends
            VaadinPortletRequest {

        public VaadinHttpAndPortletRequest(PortletRequest request,
                HttpServletRequest originalRequest,
                VaadinPortletService vaadinService) {
            super(request, vaadinService);
            this.originalRequest = originalRequest;
        }

        private final HttpServletRequest originalRequest;

        @Override
        public String getParameter(String name) {
            String parameter = super.getParameter(name);
            if (parameter == null) {
                parameter = originalRequest.getParameter(name);
            }
            return parameter;
        }

        @Override
        public String getRemoteAddr() {
            return originalRequest.getRemoteAddr();
        }

        @Override
        public String getRemoteHost() {
            return originalRequest.getRemoteHost();
        }

        @Override
        public int getRemotePort() {
            return originalRequest.getRemotePort();
        }

        @Override
        public String getHeader(String name) {
            String header = super.getHeader(name);
            if (header == null) {
                header = originalRequest.getHeader(name);
            }
            return header;
        }

        @Override
        public Enumeration<String> getHeaderNames() {
            Enumeration<String> headerNames = super.getHeaderNames();
            if (headerNames == null) {
                headerNames = originalRequest.getHeaderNames();
            }
            return headerNames;
        }

        @Override
        public Enumeration<String> getHeaders(String name) {
            Enumeration<String> headers = super.getHeaders(name);
            if (headers == null) {
                headers = originalRequest.getHeaders(name);
            }
            return headers;
        }

        @Override
        public Map<String, String[]> getParameterMap() {
            Map<String, String[]> parameterMap = super.getParameterMap();
            if (parameterMap == null) {
                parameterMap = originalRequest.getParameterMap();
            }
            return parameterMap;
        }
    }

    public static class VaadinGateinRequest extends VaadinHttpAndPortletRequest {
        public VaadinGateinRequest(PortletRequest request,
                VaadinPortletService vaadinService) {
            super(request, getOriginalRequest(request), vaadinService);
        }

        private static final HttpServletRequest getOriginalRequest(
                PortletRequest request) {
            try {
                Method getRealReq = request.getClass().getMethod(
                        "getRealRequest");
                HttpServletRequestWrapper origRequest = (HttpServletRequestWrapper) getRealReq
                        .invoke(request);
                return origRequest;
            } catch (Exception e) {
                throw new IllegalStateException("GateIn request not detected",
                        e);
            }
        }
    }

    public static class VaadinLiferayRequest extends
            VaadinHttpAndPortletRequest {

        public VaadinLiferayRequest(PortletRequest request,
                VaadinPortletService vaadinService) {
            super(request, getOriginalRequest(request), vaadinService);
        }

        @Override
        public String getPortalProperty(String name) {
            return PropsUtil.get(name);
        }

        private static HttpServletRequest getOriginalRequest(
                PortletRequest request) {
            try {
                // httpRequest = PortalUtil.getHttpServletRequest(request);
                HttpServletRequest httpRequest = (HttpServletRequest) PortalClassInvoker
                        .invoke("com.liferay.portal.util.PortalUtil",
                                "getHttpServletRequest", request);

                // httpRequest =
                // PortalUtil.getOriginalServletRequest(httpRequest);
                httpRequest = (HttpServletRequest) PortalClassInvoker.invoke(
                        "com.liferay.portal.util.PortalUtil",
                        "getOriginalServletRequest", httpRequest);
                return httpRequest;
            } catch (Exception e) {
                throw new IllegalStateException("Liferay request not detected",
                        e);
            }
        }

    }

    /**
     * This portlet parameter is used to add styles to the main element. E.g
     * "height:500px" generates a style="height:500px" to the main element.
     * 
     * @deprecated As of 7.0. Will likely change or be removed in a future
     *             version
     */
    @Deprecated
    public static final String PORTLET_PARAMETER_STYLE = "style";

    /**
     * This portal parameter is used to define the name of the Vaadin theme that
     * is used for all Vaadin applications in the portal.
     * 
     * @deprecated As of 7.0. Will likely change or be removed in a future
     *             version
     */
    @Deprecated
    public static final String PORTAL_PARAMETER_VAADIN_THEME = "vaadin.theme";

    /**
     * @deprecated As of 7.0. Will likely change or be removed in a future
     *             version
     */
    @Deprecated
    public static final String WRITE_AJAX_PAGE_SCRIPT_WIDGETSET_SHOULD_WRITE = "writeAjaxPageScriptWidgetsetShouldWrite";

    // TODO some parts could be shared with AbstractApplicationServlet

    // TODO Can we close the application when the portlet is removed? Do we know
    // when the portlet is removed?

    private VaadinPortletService vaadinService;

    @Override
    public void init(PortletConfig config) throws PortletException {
        CurrentInstance.clearAll();
        setCurrent(this);
        super.init(config);
        Properties initParameters = new Properties();

        // Read default parameters from the context
        final PortletContext context = config.getPortletContext();
        for (final Enumeration<String> e = context.getInitParameterNames(); e
                .hasMoreElements();) {
            final String name = e.nextElement();
            initParameters.setProperty(name, context.getInitParameter(name));
        }

        // Override with application settings from portlet.xml
        for (final Enumeration<String> e = config.getInitParameterNames(); e
                .hasMoreElements();) {
            final String name = e.nextElement();
            initParameters.setProperty(name, config.getInitParameter(name));
        }

        DeploymentConfiguration deploymentConfiguration = createDeploymentConfiguration(initParameters);
        vaadinService = createPortletService(deploymentConfiguration);
        // Sets current service even though there are no request and response
        vaadinService.setCurrentInstances(null, null);

        portletInitialized();

        CurrentInstance.clearAll();
    }

    protected void portletInitialized() throws PortletException {

    }

    @Override
    public void destroy() {
    	// perform vaadin portlet-specific destroy actions...
    	
    	// allow subclass to get into the destroy...
    	portletDestroyed();
    }
    
    protected void portletDestroyed() {
    	
    }
    
    protected DeploymentConfiguration createDeploymentConfiguration(
            Properties initParameters) {
        return new DefaultDeploymentConfiguration(getClass(), initParameters);
    }

    protected VaadinPortletService createPortletService(
            DeploymentConfiguration deploymentConfiguration) {
        return new VaadinPortletService(this, deploymentConfiguration);
    }

    /**
     * @author Vaadin Ltd
     * 
     * @deprecated As of 7.0. This is no longer used and only provided for
     *             backwards compatibility. Each {@link RequestHandler} can
     *             individually decide whether it wants to handle a request or
     *             not.
     */
    @Deprecated
    protected enum RequestType {
        FILE_UPLOAD, UIDL, RENDER, STATIC_FILE, APP, DUMMY, EVENT, ACTION, UNKNOWN, BROWSER_DETAILS, PUBLISHED_FILE, HEARTBEAT;
    }

    /**
     * @param vaadinRequest
     * @return
     * 
     * @deprecated As of 7.0. This is no longer used and only provided for
     *             backwards compatibility. Each {@link RequestHandler} can
     *             individually decide whether it wants to handle a request or
     *             not.
     */
    @Deprecated
    protected RequestType getRequestType(VaadinPortletRequest vaadinRequest) {
        PortletRequest request = vaadinRequest.getPortletRequest();
        if (request instanceof RenderRequest) {
            return RequestType.RENDER;
        } else if (request instanceof ResourceRequest) {
            ResourceRequest resourceRequest = (ResourceRequest) request;
            if (ServletPortletHelper.isUIDLRequest(vaadinRequest)) {
                return RequestType.UIDL;
            } else if (PortletUIInitHandler.isUIInitRequest(vaadinRequest)) {
                return RequestType.BROWSER_DETAILS;
            } else if (ServletPortletHelper.isFileUploadRequest(vaadinRequest)) {
                return RequestType.FILE_UPLOAD;
            } else if (ServletPortletHelper
                    .isPublishedFileRequest(vaadinRequest)) {
                return RequestType.PUBLISHED_FILE;
            } else if (ServletPortletHelper.isAppRequest(vaadinRequest)) {
                return RequestType.APP;
            } else if (ServletPortletHelper.isHeartbeatRequest(vaadinRequest)) {
                return RequestType.HEARTBEAT;
            } else if (PortletDummyRequestHandler.isDummyRequest(vaadinRequest)) {
                return RequestType.DUMMY;
            } else {
                return RequestType.STATIC_FILE;
            }
        } else if (request instanceof ActionRequest) {
            return RequestType.ACTION;
        } else if (request instanceof EventRequest) {
            return RequestType.EVENT;
        }
        return RequestType.UNKNOWN;
    }

    /**
     * @param request
     * @param response
     * @throws PortletException
     * @throws IOException
     * 
     * @deprecated As of 7.0. Will likely change or be removed in a future
     *             version
     */
    @Deprecated
    protected void handleRequest(PortletRequest request,
            PortletResponse response) throws PortletException, IOException {

        CurrentInstance.clearAll();
        setCurrent(this);
        try {
            getService().handleRequest(createVaadinRequest(request),
                    createVaadinResponse(response));
        } catch (ServiceException e) {
            throw new PortletException(e);
        }
    }

    /**
     * Wraps the request in a (possibly portal specific) Vaadin portlet request.
     * 
     * @param request
     *            The original PortletRequest
     * @return A wrapped version of the PorletRequest
     */
    protected VaadinPortletRequest createVaadinRequest(PortletRequest request) {
        String portalInfo = request.getPortalContext().getPortalInfo()
                .toLowerCase();
        if (portalInfo.contains("liferay")) {
            return new VaadinLiferayRequest(request, getService());
        } else if (portalInfo.contains("gatein")) {
            return new VaadinGateinRequest(request, getService());
        } else {
            return new VaadinPortletRequest(request, getService());
        }

    }

    private VaadinPortletResponse createVaadinResponse(PortletResponse response) {
        return new VaadinPortletResponse(response, getService());
    }

    protected VaadinPortletService getService() {
        return vaadinService;
    }

    @Override
    public void processEvent(EventRequest request, EventResponse response)
            throws PortletException, IOException {
        handleRequest(request, response);
    }

    @Override
    public void processAction(ActionRequest request, ActionResponse response)
            throws PortletException, IOException {
        handleRequest(request, response);
    }

    @Override
    protected void doDispatch(RenderRequest request, RenderResponse response)
            throws PortletException, IOException {
        try {
            // try to let super handle - it'll call methods annotated for
            // handling, the default doXYZ(), or throw if a handler for the mode
            // is not found
            super.doDispatch(request, response);

        } catch (PortletException e) {
            if (e.getCause() == null) {
                // No cause interpreted as 'unknown mode' - pass that trough
                // so that the application can handle
                handleRequest(request, response);

            } else {
                // Something else failed, pass on
                throw e;
            }
        }
    }

    @Override
    public void serveResource(ResourceRequest request, ResourceResponse response)
            throws PortletException, IOException {
        handleRequest(request, response);
    }

    private static final Logger getLogger() {
        return Logger.getLogger(VaadinPortlet.class.getName());
    }

    /**
     * Gets the currently used Vaadin portlet. The current portlet is
     * automatically defined when initializing the portlet and when processing
     * requests to the server and in threads started at a point when the current
     * portlet is defined (see {@link InheritableThreadLocal}). In other cases,
     * (e.g. from background threads started in some other way), the current
     * portlet is not automatically defined.
     * 
     * @return the current vaadin portlet instance if available, otherwise
     *         <code>null</code>
     * 
     * @see #setCurrent(VaadinPortlet)
     * 
     * @since 7.0
     */
    public static VaadinPortlet getCurrent() {
        return CurrentInstance.get(VaadinPortlet.class);
    }

    /**
     * Sets the current Vaadin portlet. This method is used by the framework to
     * set the current portlet whenever a new request is processed and it is
     * cleared when the request has been processed.
     * <p>
     * The application developer can also use this method to define the current
     * portlet outside the normal request handling, e.g. when initiating custom
     * background threads.
     * </p>
     * 
     * @param portlet
     *            the Vaadin portlet to register as the current portlet
     * 
     * @see #getCurrent()
     * @see InheritableThreadLocal
     */
    public static void setCurrent(VaadinPortlet portlet) {
        CurrentInstance.setInheritable(VaadinPortlet.class, portlet);
    }

}
