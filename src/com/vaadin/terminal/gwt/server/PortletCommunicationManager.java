/*
@ITMillApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.ClientDataRequest;
import javax.portlet.MimeResponse;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequestWrapper;

import com.vaadin.Application;
import com.vaadin.external.org.apache.commons.fileupload.FileItemIterator;
import com.vaadin.external.org.apache.commons.fileupload.FileUpload;
import com.vaadin.external.org.apache.commons.fileupload.FileUploadException;
import com.vaadin.external.org.apache.commons.fileupload.portlet.PortletFileUpload;
import com.vaadin.terminal.DownloadStream;
import com.vaadin.ui.Window;

/**
 * TODO document me!
 * 
 * @author peholmst
 * 
 */
@SuppressWarnings("serial")
public class PortletCommunicationManager extends AbstractCommunicationManager {

    protected String dummyURL;

    private static class PortletRequestWrapper implements Request {

        private final PortletRequest request;

        public PortletRequestWrapper(PortletRequest request) {
            this.request = request;
        }

        public Object getAttribute(String name) {
            return request.getAttribute(name);
        }

        public int getContentLength() {
            return ((ClientDataRequest) request).getContentLength();
        }

        public InputStream getInputStream() throws IOException {
            return ((ClientDataRequest) request).getPortletInputStream();
        }

        public String getParameter(String name) {
            String value = request.getParameter(name);
            if (value == null) {
                // for GateIn portlet container simple-portal
                try {
                    Method getRealReq = request.getClass().getMethod(
                            "getRealRequest");
                    HttpServletRequestWrapper origRequest = (HttpServletRequestWrapper) getRealReq
                            .invoke(request);
                    value = origRequest.getParameter(name);
                } catch (Exception e) {
                    // do nothing - not on GateIn simple-portal
                }
            }
            return value;
        }

        public String getRequestID() {
            return "WindowID:" + request.getWindowID();
        }

        public Session getSession() {
            return new PortletSessionWrapper(request.getPortletSession());
        }

        public Object getWrappedRequest() {
            return request;
        }

        public boolean isRunningInPortlet() {
            return true;
        }

        public void setAttribute(String name, Object o) {
            request.setAttribute(name, o);
        }

    }

    private static class PortletResponseWrapper implements Response {

        private final PortletResponse response;

        public PortletResponseWrapper(PortletResponse response) {
            this.response = response;
        }

        public OutputStream getOutputStream() throws IOException {
            return ((MimeResponse) response).getPortletOutputStream();
        }

        public Object getWrappedResponse() {
            return response;
        }

        public void setContentType(String type) {
            ((MimeResponse) response).setContentType(type);
        }
    }

    private static class PortletSessionWrapper implements Session {

        private final PortletSession session;

        public PortletSessionWrapper(PortletSession session) {
            this.session = session;
        }

        public Object getAttribute(String name) {
            return session.getAttribute(name);
        }

        public int getMaxInactiveInterval() {
            return session.getMaxInactiveInterval();
        }

        public Object getWrappedSession() {
            return session;
        }

        public boolean isNew() {
            return session.isNew();
        }

        public void setAttribute(String name, Object o) {
            session.setAttribute(name, o);
        }

    }

    private static class AbstractApplicationPortletWrapper implements Callback {

        private final AbstractApplicationPortlet portlet;

        public AbstractApplicationPortletWrapper(
                AbstractApplicationPortlet portlet) {
            this.portlet = portlet;
        }

        public void criticalNotification(Request request, Response response,
                String cap, String msg, String details, String outOfSyncURL)
                throws IOException {
            portlet.criticalNotification((PortletRequest) request
                    .getWrappedRequest(), (MimeResponse) response
                    .getWrappedResponse(), cap, msg, details, outOfSyncURL);
        }

        public String getRequestPathInfo(Request request) {
            if (request.getWrappedRequest() instanceof ResourceRequest) {
                return ((ResourceRequest) request.getWrappedRequest())
                        .getResourceID();
            } else {
                // We do not use paths in portlet mode
                throw new UnsupportedOperationException(
                        "PathInfo only available when using ResourceRequests");
            }
        }

        public InputStream getThemeResourceAsStream(String themeName,
                String resource) throws IOException {
            return portlet.getPortletContext().getResourceAsStream(
                    "/" + AbstractApplicationPortlet.THEME_DIRECTORY_PATH
                            + themeName + "/" + resource);
        }

    }

    public PortletCommunicationManager(Application application) {
        super(application);
    }

    @Override
    protected FileUpload createFileUpload() {
        return new PortletFileUpload();
    }

    @Override
    protected FileItemIterator getUploadItemIterator(FileUpload upload,
            Request request) throws IOException, FileUploadException {
        return ((PortletFileUpload) upload)
                .getItemIterator((ActionRequest) request.getWrappedRequest());
    }

    public void handleFileUpload(ActionRequest request, ActionResponse response)
            throws FileUploadException, IOException {
        doHandleFileUpload(new PortletRequestWrapper(request),
                new PortletResponseWrapper(response));
    }

    @Override
    protected void sendUploadResponse(Request request, Response response)
            throws IOException {
        if (response.getWrappedResponse() instanceof ActionResponse) {
            /*
             * If we do not redirect to some other page, the entire portal page
             * will be re-printed into the target of the upload request (an
             * IFRAME), which in turn will cause very strange side effects.
             */
            System.out.println("Redirecting to dummyURL: " + dummyURL);
            ((ActionResponse) response.getWrappedResponse())
                    .sendRedirect(dummyURL == null ? "http://www.google.com"
                            : dummyURL);
        } else {
            super.sendUploadResponse(request, response);
        }
    }

    public void handleUidlRequest(ResourceRequest request,
            ResourceResponse response,
            AbstractApplicationPortlet applicationPortlet, Window window)
            throws InvalidUIDLSecurityKeyException, IOException {
        doHandleUidlRequest(new PortletRequestWrapper(request),
                new PortletResponseWrapper(response),
                new AbstractApplicationPortletWrapper(applicationPortlet),
                window);
    }

    DownloadStream handleURI(Window window, ResourceRequest request,
            ResourceResponse response,
            AbstractApplicationPortlet applicationPortlet) {
        return handleURI(window, new PortletRequestWrapper(request),
                new PortletResponseWrapper(response),
                new AbstractApplicationPortletWrapper(applicationPortlet));
    }

    /**
     * Gets the existing application or creates a new one. Get a window within
     * an application based on the requested URI.
     * 
     * @param request
     *            the portlet Request.
     * @param applicationPortlet
     * @param application
     *            the Application to query for window.
     * @param assumedWindow
     *            if the window has been already resolved once, this parameter
     *            must contain the window.
     * @return Window matching the given URI or null if not found.
     * @throws ServletException
     *             if an exception has occurred that interferes with the
     *             servlet's normal operation.
     */
    Window getApplicationWindow(PortletRequest request,
            AbstractApplicationPortlet applicationPortlet,
            Application application, Window assumedWindow) {
        return doGetApplicationWindow(new PortletRequestWrapper(request),
                new AbstractApplicationPortletWrapper(applicationPortlet),
                application, assumedWindow);
    }

}
