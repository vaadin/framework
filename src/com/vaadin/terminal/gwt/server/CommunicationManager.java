/*
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.vaadin.Application;
import com.vaadin.terminal.ApplicationResource;
import com.vaadin.terminal.DownloadStream;
import com.vaadin.terminal.Paintable;
import com.vaadin.terminal.StreamVariable;
import com.vaadin.terminal.VariableOwner;
import com.vaadin.ui.Component;
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
            servlet.criticalNotification(
                    (HttpServletRequest) request.getWrappedRequest(),
                    (HttpServletResponse) response.getWrappedResponse(), cap,
                    msg, details, outOfSyncURL);
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

    /**
     * Handles file upload request submitted via Upload component.
     * 
     * @see #getStreamVariableTargetUrl(ReceiverOwner, String, StreamVariable)
     * 
     * @param request
     * @param response
     * @throws IOException
     * @throws InvalidUIDLSecurityKeyException
     */
    public void handleFileUpload(HttpServletRequest request,
            HttpServletResponse response) throws IOException,
            InvalidUIDLSecurityKeyException {

        /*
         * URI pattern: APP/UPLOAD/[PID]/[NAME]/[SECKEY] See #createReceiverUrl
         */

        String pathInfo = request.getPathInfo();
        // strip away part until the data we are interested starts
        int startOfData = pathInfo
                .indexOf(AbstractApplicationServlet.UPLOAD_URL_PREFIX)
                + AbstractApplicationServlet.UPLOAD_URL_PREFIX.length();
        String uppUri = pathInfo.substring(startOfData);
        String[] parts = uppUri.split("/", 3); // 0 = pid, 1= name, 2 = sec key
        String variableName = parts[1];
        String paintableId = parts[0];

        StreamVariable streamVariable = pidToNameToStreamVariable.get(
                paintableId).get(variableName);
        String secKey = streamVariableToSeckey.get(streamVariable);
        if (secKey.equals(parts[2])) {

            VariableOwner source = getVariableOwner(paintableId);
            String contentType = request.getContentType();
            if (request.getContentType().contains("boundary")) {
                // Multipart requests contain boundary string
                doHandleSimpleMultipartFileUpload(
                        new HttpServletRequestWrapper(request),
                        new HttpServletResponseWrapper(response),
                        streamVariable, variableName, source,
                        contentType.split("boundary=")[1]);
            } else {
                // if boundary string does not exist, the posted file is from
                // XHR2.post(File)
                doHandleXhrFilePost(new HttpServletRequestWrapper(request),
                        new HttpServletResponseWrapper(response),
                        streamVariable, variableName, source,
                        request.getContentLength());
            }
        } else {
            throw new InvalidUIDLSecurityKeyException(
                    "Security key in upload post did not match!");
        }

    }

    /**
     * Handles UIDL request
     * 
     * TODO document
     * 
     * @param request
     * @param response
     * @param applicationServlet
     * @param window
     *            target window of the UIDL request, can be null if window not
     *            found
     * @throws IOException
     * @throws ServletException
     */
    public void handleUidlRequest(HttpServletRequest request,
            HttpServletResponse response,
            AbstractApplicationServlet applicationServlet, Window window)
            throws IOException, ServletException,
            InvalidUIDLSecurityKeyException {
        doHandleUidlRequest(new HttpServletRequestWrapper(request),
                new HttpServletResponseWrapper(response),
                new AbstractApplicationServletWrapper(applicationServlet),
                window);
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

    @Override
    protected void unregisterPaintable(Component p) {
        /* Cleanup possible receivers */
        if (pidToNameToStreamVariable != null) {
            Map<String, StreamVariable> removed = pidToNameToStreamVariable
                    .remove(getPaintableId(p));
            if (removed != null) {
                for (String key : removed.keySet()) {
                    streamVariableToSeckey.remove(removed.get(key));
                }
            }
        }
        super.unregisterPaintable(p);

    }

    private Map<String, Map<String, StreamVariable>> pidToNameToStreamVariable;

    private Map<StreamVariable, String> streamVariableToSeckey;

    @Override
    String getStreamVariableTargetUrl(VariableOwner owner, String name,
            StreamVariable value) {
        /*
         * We will use the same APP/* URI space as ApplicationResources but
         * prefix url with UPLOAD
         * 
         * eg. APP/UPLOAD/[PID]/[NAME]/[SECKEY]
         * 
         * SECKEY is created on each paint to make URL's unpredictable (to
         * prevent CSRF attacks).
         * 
         * NAME and PID from URI forms a key to fetch StreamVariable when
         * handling post
         */
        String paintableId = getPaintableId((Paintable) owner);
        String key = paintableId + "/" + name;

        if (pidToNameToStreamVariable == null) {
            pidToNameToStreamVariable = new HashMap<String, Map<String, StreamVariable>>();
        }
        Map<String, StreamVariable> nameToStreamVariable = pidToNameToStreamVariable
                .get(paintableId);
        if (nameToStreamVariable == null) {
            nameToStreamVariable = new HashMap<String, StreamVariable>();
            pidToNameToStreamVariable.put(paintableId, nameToStreamVariable);
        }
        nameToStreamVariable.put(name, value);

        if (streamVariableToSeckey == null) {
            streamVariableToSeckey = new HashMap<StreamVariable, String>();
        }
        String seckey = streamVariableToSeckey.get(value);
        if (seckey == null) {
            seckey = UUID.randomUUID().toString();
            streamVariableToSeckey.put(value, seckey);
        }

        return "app://" + AbstractApplicationServlet.UPLOAD_URL_PREFIX + key
                + "/" + seckey;

    }

    @Override
    protected void cleanStreamVariable(VariableOwner owner, String name) {
        Map<String, StreamVariable> nameToStreamVar = pidToNameToStreamVariable
                .get(getPaintableId((Paintable) owner));
        nameToStreamVar.remove("name");
        if (nameToStreamVar.isEmpty()) {
            pidToNameToStreamVariable.remove(getPaintableId((Paintable) owner));
        }
    }

}
