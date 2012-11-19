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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.security.GeneralSecurityException;
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
import javax.servlet.http.HttpServletResponse;

import com.liferay.portal.kernel.util.PortalClassInvoker;
import com.liferay.portal.kernel.util.PropsUtil;
import com.vaadin.server.AbstractCommunicationManager.Callback;
import com.vaadin.ui.UI;
import com.vaadin.util.CurrentInstance;

/**
 * Portlet 2.0 base class. This replaces the servlet in servlet/portlet 1.0
 * deployments and handles various portlet requests from the browser.
 * 
 * TODO Document me!
 * 
 * @author peholmst
 */
public class VaadinPortlet extends GenericPortlet implements Constants {

    /**
     * @deprecated might be refactored or removed before 7.0.0
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
        public String getHeader(String name) {
            String header = super.getHeader(name);
            if (header == null) {
                header = originalRequest.getHeader(name);
            }
            return header;
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

    public static class AbstractApplicationPortletWrapper implements Callback {

        private final VaadinPortlet portlet;

        public AbstractApplicationPortletWrapper(VaadinPortlet portlet) {
            this.portlet = portlet;
        }

        @Override
        public void criticalNotification(VaadinRequest request,
                VaadinResponse response, String cap, String msg,
                String details, String outOfSyncURL) throws IOException {
            portlet.criticalNotification((VaadinPortletRequest) request,
                    (VaadinPortletResponse) response, cap, msg, details,
                    outOfSyncURL);
        }
    }

    /**
     * This portlet parameter is used to add styles to the main element. E.g
     * "height:500px" generates a style="height:500px" to the main element.
     * 
     * @deprecated might be refactored or removed before 7.0.0
     */
    @Deprecated
    public static final String PORTLET_PARAMETER_STYLE = "style";

    /**
     * This portal parameter is used to define the name of the Vaadin theme that
     * is used for all Vaadin applications in the portal.
     * 
     * @deprecated might be refactored or removed before 7.0.0
     */
    @Deprecated
    public static final String PORTAL_PARAMETER_VAADIN_THEME = "vaadin.theme";

    /**
     * @deprecated might be refactored or removed before 7.0.0
     */
    @Deprecated
    public static final String WRITE_AJAX_PAGE_SCRIPT_WIDGETSET_SHOULD_WRITE = "writeAjaxPageScriptWidgetsetShouldWrite";

    // TODO some parts could be shared with AbstractApplicationServlet

    // TODO Can we close the application when the portlet is removed? Do we know
    // when the portlet is removed?

    private VaadinPortletService vaadinService;
    private AddonContext addonContext;

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

        addonContext = new AddonContext(vaadinService);
        addonContext.init();

        portletInitialized();
        CurrentInstance.clearAll();
    }

    protected void portletInitialized() throws PortletException {

    }

    protected DeploymentConfiguration createDeploymentConfiguration(
            Properties initParameters) {
        return new DefaultDeploymentConfiguration(getClass(), initParameters);
    }

    protected VaadinPortletService createPortletService(
            DeploymentConfiguration deploymentConfiguration) {
        return new VaadinPortletService(this, deploymentConfiguration);
    }

    @Override
    public void destroy() {
        super.destroy();

        addonContext.destroy();
    }

    /**
     * @author Vaadin Ltd
     * 
     * @deprecated might be refactored or removed before 7.0.0
     */
    @Deprecated
    protected enum RequestType {
        FILE_UPLOAD, UIDL, RENDER, STATIC_FILE, APP, DUMMY, EVENT, ACTION, UNKNOWN, BROWSER_DETAILS, DEPENDENCY_RESOURCE, HEARTBEAT;
    }

    /**
     * @param vaadinRequest
     * @return
     * 
     * @deprecated might be refactored or removed before 7.0.0
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
            } else if (isBrowserDetailsRequest(resourceRequest)) {
                return RequestType.BROWSER_DETAILS;
            } else if (ServletPortletHelper.isFileUploadRequest(vaadinRequest)) {
                return RequestType.FILE_UPLOAD;
            } else if (ServletPortletHelper
                    .isDependencyResourceRequest(vaadinRequest)) {
                return RequestType.DEPENDENCY_RESOURCE;
            } else if (ServletPortletHelper.isAppRequest(vaadinRequest)) {
                return RequestType.APP;
            } else if (ServletPortletHelper.isHeartbeatRequest(vaadinRequest)) {
                return RequestType.HEARTBEAT;
            } else if (isDummyRequest(resourceRequest)) {
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

    private boolean isBrowserDetailsRequest(ResourceRequest request) {
        return request.getResourceID() != null
                && request.getResourceID().equals("browserDetails");
    }

    private boolean isDummyRequest(ResourceRequest request) {
        return request.getResourceID() != null
                && request.getResourceID().equals("DUMMY");
    }

    /**
     * @param request
     * @param response
     * @throws PortletException
     * @throws IOException
     * 
     * @deprecated might be refactored or removed before 7.0.0
     */
    @Deprecated
    protected void handleRequest(PortletRequest request,
            PortletResponse response) throws PortletException, IOException {
        RequestTimer requestTimer = new RequestTimer();
        requestTimer.start();

        CurrentInstance.clearAll();
        setCurrent(this);

        try {
            AbstractApplicationPortletWrapper portletWrapper = new AbstractApplicationPortletWrapper(
                    this);

            VaadinPortletRequest vaadinRequest = createVaadinRequest(request);

            VaadinPortletResponse vaadinResponse = new VaadinPortletResponse(
                    response, getService());

            getService().setCurrentInstances(vaadinRequest, vaadinResponse);

            RequestType requestType = getRequestType(vaadinRequest);

            if (requestType == RequestType.UNKNOWN) {
                handleUnknownRequest(request, response);
            } else if (requestType == RequestType.DUMMY) {
                /*
                 * This dummy page is used by action responses to redirect to,
                 * in order to prevent the boot strap code from being rendered
                 * into strange places such as iframes.
                 */
                ((ResourceResponse) response).setContentType("text/html");
                final OutputStream out = ((ResourceResponse) response)
                        .getPortletOutputStream();
                final PrintWriter outWriter = new PrintWriter(
                        new BufferedWriter(new OutputStreamWriter(out, "UTF-8")));
                outWriter.print("<html><body>dummy page</body></html>");
                outWriter.close();
            } else if (requestType == RequestType.STATIC_FILE) {
                serveStaticResources((ResourceRequest) request,
                        (ResourceResponse) response);
            } else {
                VaadinPortletSession vaadinSession = null;

                try {
                    // TODO What about PARAM_UNLOADBURST &
                    // redirectToApplication??

                    vaadinSession = (VaadinPortletSession) getService()
                            .findVaadinSession(vaadinRequest);
                    if (vaadinSession == null) {
                        return;
                    }

                    PortletCommunicationManager communicationManager = (PortletCommunicationManager) vaadinSession
                            .getCommunicationManager();

                    if (requestType == RequestType.DEPENDENCY_RESOURCE) {
                        communicationManager.serveDependencyResource(
                                vaadinRequest, vaadinResponse);
                        return;
                    } else if (requestType == RequestType.HEARTBEAT) {
                        communicationManager.handleHeartbeatRequest(
                                vaadinRequest, vaadinResponse, vaadinSession);
                        return;
                    }

                    /* Update browser information from request */
                    vaadinSession.getBrowser().updateRequestDetails(
                            vaadinRequest);

                    /* Notify listeners */

                    // Finds the right UI
                    UI uI = null;
                    if (requestType == RequestType.UIDL) {
                        uI = getService().findUI(vaadinRequest);
                    }

                    // TODO Should this happen before or after the transaction
                    // starts?
                    if (request instanceof RenderRequest) {
                        vaadinSession.firePortletRenderRequest(uI,
                                (RenderRequest) request,
                                (RenderResponse) response);
                    } else if (request instanceof ActionRequest) {
                        vaadinSession.firePortletActionRequest(uI,
                                (ActionRequest) request,
                                (ActionResponse) response);
                    } else if (request instanceof EventRequest) {
                        vaadinSession.firePortletEventRequest(uI,
                                (EventRequest) request,
                                (EventResponse) response);
                    } else if (request instanceof ResourceRequest) {
                        vaadinSession.firePortletResourceRequest(uI,
                                (ResourceRequest) request,
                                (ResourceResponse) response);
                    }

                    /* Handle the request */
                    if (requestType == RequestType.FILE_UPLOAD) {
                        // UI is resolved in handleFileUpload by
                        // PortletCommunicationManager
                        communicationManager.handleFileUpload(vaadinSession,
                                vaadinRequest, vaadinResponse);
                        return;
                    } else if (requestType == RequestType.BROWSER_DETAILS) {
                        communicationManager.handleBrowserDetailsRequest(
                                vaadinRequest, vaadinResponse, vaadinSession);
                        return;
                    } else if (requestType == RequestType.UIDL) {
                        // Handles AJAX UIDL requests
                        communicationManager.handleUidlRequest(vaadinRequest,
                                vaadinResponse, portletWrapper, uI);
                        return;
                    } else {
                        handleOtherRequest(vaadinRequest, vaadinResponse,
                                requestType, vaadinSession,
                                communicationManager);
                    }
                } catch (final SessionExpiredException e) {
                    // TODO Figure out a better way to deal with
                    // SessionExpiredExceptions
                    getLogger().finest("A user session has expired");
                } catch (final GeneralSecurityException e) {
                    // TODO Figure out a better way to deal with
                    // GeneralSecurityExceptions
                    getLogger()
                            .fine("General security exception, the security key was probably incorrect.");
                } catch (final Throwable e) {
                    handleServiceException(vaadinRequest, vaadinResponse,
                            vaadinSession, e);
                } finally {
                    if (vaadinSession != null) {
                        vaadinSession.cleanupInactiveUIs();
                        requestTimer.stop(vaadinSession);
                    }
                }
            }
        } finally {
            CurrentInstance.clearAll();
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

    protected VaadinPortletService getService() {
        return vaadinService;
    }

    private void handleUnknownRequest(PortletRequest request,
            PortletResponse response) {
        getLogger().warning("Unknown request type");
    }

    /**
     * Handle a portlet request that is not for static files, UIDL or upload.
     * Also render requests are handled here.
     * 
     * This method is called after starting the application and calling portlet
     * and transaction listeners.
     * 
     * @param request
     * @param response
     * @param requestType
     * @param vaadinSession
     * @param vaadinSession
     * @param communicationManager
     * @throws PortletException
     * @throws IOException
     * @throws MalformedURLException
     */
    private void handleOtherRequest(VaadinPortletRequest request,
            VaadinResponse response, RequestType requestType,
            VaadinSession vaadinSession,
            PortletCommunicationManager communicationManager)
            throws PortletException, IOException, MalformedURLException {
        if (requestType == RequestType.APP || requestType == RequestType.RENDER) {
            if (!communicationManager.handleOtherRequest(request, response)) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND,
                        "Not found");
            }
        } else if (requestType == RequestType.EVENT) {
            // nothing to do, listeners do all the work
        } else if (requestType == RequestType.ACTION) {
            // nothing to do, listeners do all the work
        } else {
            throw new IllegalStateException(
                    "handleRequest() without anything to do - should never happen!");
        }
    }

    @Override
    public void processEvent(EventRequest request, EventResponse response)
            throws PortletException, IOException {
        handleRequest(request, response);
    }

    private void serveStaticResources(ResourceRequest request,
            ResourceResponse response) throws IOException, PortletException {
        final String resourceID = request.getResourceID();
        final PortletContext pc = getPortletContext();

        InputStream is = pc.getResourceAsStream(resourceID);
        if (is != null) {
            final String mimetype = pc.getMimeType(resourceID);
            if (mimetype != null) {
                response.setContentType(mimetype);
            }
            final OutputStream os = response.getPortletOutputStream();
            final byte buffer[] = new byte[DEFAULT_BUFFER_SIZE];
            int bytes;
            while ((bytes = is.read(buffer)) >= 0) {
                os.write(buffer, 0, bytes);
            }
        } else {
            getLogger().info(
                    "Requested resource [" + resourceID
                            + "] could not be found");
            response.setProperty(ResourceResponse.HTTP_STATUS_CODE,
                    Integer.toString(HttpServletResponse.SC_NOT_FOUND));
        }
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

    private void handleServiceException(VaadinPortletRequest request,
            VaadinPortletResponse response, VaadinSession vaadinSession,
            Throwable e) throws IOException, PortletException {
        // TODO Check that this error handler is working when running inside a
        // portlet

        // if this was an UIDL request, response UIDL back to client
        ErrorHandler errorHandler = ErrorEvent.findErrorHandler(vaadinSession);
        if (getRequestType(request) == RequestType.UIDL) {
            SystemMessages ci = getService().getSystemMessages(
                    ServletPortletHelper.findLocale(null, vaadinSession,
                            request));
            criticalNotification(request, response,
                    ci.getInternalErrorCaption(), ci.getInternalErrorMessage(),
                    null, ci.getInternalErrorURL());
            if (errorHandler != null) {
                errorHandler.error(new ErrorEvent(e));
            }
        } else {
            if (errorHandler != null) {
                errorHandler.error(new ErrorEvent(e));
            } else {
                // Re-throw other exceptions
                throw new PortletException(e);
            }
        }
    }

    /**
     * Send notification to client's application. Used to notify client of
     * critical errors and session expiration due to long inactivity. Server has
     * no knowledge of what application client refers to.
     * 
     * @param request
     *            the Portlet request instance.
     * @param response
     *            the Portlet response to write to.
     * @param caption
     *            for the notification
     * @param message
     *            for the notification
     * @param details
     *            a detail message to show in addition to the passed message.
     *            Currently shown directly but could be hidden behind a details
     *            drop down.
     * @param url
     *            url to load after message, null for current page
     * @throws IOException
     *             if the writing failed due to input/output error.
     * 
     * @deprecated might be refactored or removed before 7.0.0
     */
    @Deprecated
    void criticalNotification(VaadinPortletRequest request,
            VaadinPortletResponse response, String caption, String message,
            String details, String url) throws IOException {

        // clients JS app is still running, but server application either
        // no longer exists or it might fail to perform reasonably.
        // send a notification to client's application and link how
        // to "restart" application.

        if (caption != null) {
            caption = "\"" + caption + "\"";
        }
        if (details != null) {
            if (message == null) {
                message = details;
            } else {
                message += "<br/><br/>" + details;
            }
        }
        if (message != null) {
            message = "\"" + message + "\"";
        }
        if (url != null) {
            url = "\"" + url + "\"";
        }

        // Set the response type
        response.setContentType("application/json; charset=UTF-8");
        final OutputStream out = response.getOutputStream();
        final PrintWriter outWriter = new PrintWriter(new BufferedWriter(
                new OutputStreamWriter(out, "UTF-8")));
        outWriter.print("for(;;);[{\"changes\":[], \"meta\" : {"
                + "\"appError\": {" + "\"caption\":" + caption + ","
                + "\"message\" : " + message + "," + "\"url\" : " + url
                + "}}, \"resources\": {}, \"locales\":[]}]");
        outWriter.close();
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
