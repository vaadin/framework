/*
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.vaadin.Application;
import com.vaadin.external.org.apache.commons.fileupload.FileItemIterator;
import com.vaadin.external.org.apache.commons.fileupload.FileUpload;
import com.vaadin.external.org.apache.commons.fileupload.FileUploadException;
import com.vaadin.external.org.apache.commons.fileupload.servlet.ServletFileUpload;
import com.vaadin.terminal.ApplicationResource;
import com.vaadin.terminal.DownloadStream;
import com.vaadin.ui.Window;

/**
 * Application manager processes changes and paints for single application
 * instance.
 * 
 * This class handles applications running as servlets.
 * 
 * @see AbstractCommunicationManager
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 5.0
 */
@SuppressWarnings("serial")
public class CommunicationManager extends AbstractCommunicationManager {

    /**
     * Concrete wrapper class for {@link HttpServletRequest}.
     * 
     * @see Request
     */
    private static class HttpServletRequestWrapper implements Request {

        private final HttpServletRequest request;

        public HttpServletRequestWrapper(HttpServletRequest request) {
            this.request = request;
        }

        public Object getAttribute(String name) {
            return request.getAttribute(name);
        }

        public int getContentLength() {
            return request.getContentLength();
        }

        public InputStream getInputStream() throws IOException {
            return request.getInputStream();
        }

        public String getParameter(String name) {
            return request.getParameter(name);
        }

        public String getRequestID() {
            return "RequestURL:" + request.getRequestURI();
        }

        public Session getSession() {
            return new HttpSessionWrapper(request.getSession());
        }

        public Object getWrappedRequest() {
            return request;
        }

        public boolean isRunningInPortlet() {
            return false;
        }

        public void setAttribute(String name, Object o) {
            request.setAttribute(name, o);
        }
    }

    /**
     * Concrete wrapper class for {@link HttpServletResponse}.
     * 
     * @see Response
     */
    private static class HttpServletResponseWrapper implements Response {

        private final HttpServletResponse response;

        public HttpServletResponseWrapper(HttpServletResponse response) {
            this.response = response;
        }

        public OutputStream getOutputStream() throws IOException {
            return response.getOutputStream();
        }

        public Object getWrappedResponse() {
            return response;
        }

        public void setContentType(String type) {
            response.setContentType(type);
        }

    }

    /**
     * Concrete wrapper class for {@link HttpSession}.
     * 
     * @see Session
     */
    private static class HttpSessionWrapper implements Session {

        private final HttpSession session;

        public HttpSessionWrapper(HttpSession session) {
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

    private static class AbstractApplicationServletWrapper implements Callback {

        private final AbstractApplicationServlet servlet;

        public AbstractApplicationServletWrapper(
                AbstractApplicationServlet servlet) {
            this.servlet = servlet;
        }

        public void criticalNotification(Request request, Response response,
                String cap, String msg, String details, String outOfSyncURL)
                throws IOException {
            servlet.criticalNotification((HttpServletRequest) request
                    .getWrappedRequest(), (HttpServletResponse) response
                    .getWrappedResponse(), cap, msg, details, outOfSyncURL);
        }

        public String getRequestPathInfo(Request request) {
            return servlet.getRequestPathInfo((HttpServletRequest) request
                    .getWrappedRequest());
        }

        public InputStream getThemeResourceAsStream(String themeName,
                String resource) throws IOException {
            return servlet.getServletContext().getResourceAsStream(
                    "/" + AbstractApplicationServlet.THEME_DIRECTORY_PATH
                            + themeName + "/" + resource);
        }

    }

    /**
     * @deprecated use {@link #CommunicationManager(Application)} instead
     * @param application
     * @param applicationServlet
     */
    @Deprecated
    public CommunicationManager(Application application,
            AbstractApplicationServlet applicationServlet) {
        super(application);
    }

    /**
     * TODO New constructor - document me!
     * 
     * @param application
     */
    public CommunicationManager(Application application) {
        super(application);
    }

    @Override
    protected FileUpload createFileUpload() {
        return new ServletFileUpload();
    }

    @Override
    protected FileItemIterator getUploadItemIterator(FileUpload upload,
            Request request) throws IOException, FileUploadException {
        return ((ServletFileUpload) upload)
                .getItemIterator((HttpServletRequest) request
                        .getWrappedRequest());
    }

    /**
     * Handles file upload request submitted via Upload component.
     * 
     * TODO document
     * 
     * @param request
     * @param response
     * @throws IOException
     * @throws FileUploadException
     */
    public void handleFileUpload(HttpServletRequest request,
            HttpServletResponse response) throws IOException,
            FileUploadException {
        doHandleFileUpload(new HttpServletRequestWrapper(request),
                new HttpServletResponseWrapper(response));
    }

    /**
     * Handles UIDL request
     * 
     * TODO document
     * 
     * @param request
     * @param response
     * @throws IOException
     * @throws ServletException
     */
    public void handleUidlRequest(HttpServletRequest request,
            HttpServletResponse response,
            AbstractApplicationServlet applicationServlet) throws IOException,
            ServletException, InvalidUIDLSecurityKeyException {
        doHandleUidlRequest(new HttpServletRequestWrapper(request),
                new HttpServletResponseWrapper(response),
                new AbstractApplicationServletWrapper(applicationServlet));
    }

    /**
     * Gets the existing application or creates a new one. Get a window within
     * an application based on the requested URI.
     * 
     * @param request
     *            the HTTP Request.
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
    Window getApplicationWindow(HttpServletRequest request,
            AbstractApplicationServlet applicationServlet,
            Application application, Window assumedWindow)
            throws ServletException {
        return doGetApplicationWindow(new HttpServletRequestWrapper(request),
                new AbstractApplicationServletWrapper(applicationServlet),
                application, assumedWindow);
    }

    /**
     * Calls the Window URI handler for a request and returns the
     * {@link DownloadStream} returned by the handler.
     * 
     * If the window is the main window of an application, the deprecated
     * {@link Application#handleURI(java.net.URL, String)} is called first to
     * handle {@link ApplicationResource}s and the window handler is only called
     * if it returns null.
     * 
     * @see AbstractCommunicationManager#handleURI(Window, Request, Response,
     *      Callback)
     * 
     * @param window
     * @param request
     * @param response
     * @param applicationServlet
     * @return
     */
    DownloadStream handleURI(Window window, HttpServletRequest request,
            HttpServletResponse response,
            AbstractApplicationServlet applicationServlet) {
        return handleURI(window, new HttpServletRequestWrapper(request),
                new HttpServletResponseWrapper(response),
                new AbstractApplicationServletWrapper(applicationServlet));
    }

}
