package com.vaadin.terminal.gwt.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.ClientDataRequest;
import javax.portlet.MimeResponse;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import com.vaadin.Application;
import com.vaadin.external.org.apache.commons.fileupload.FileItemIterator;
import com.vaadin.external.org.apache.commons.fileupload.FileUpload;
import com.vaadin.external.org.apache.commons.fileupload.FileUploadException;
import com.vaadin.external.org.apache.commons.fileupload.portlet.PortletFileUpload;

/**
 * TODO document me!
 * 
 * @author peholmst
 * 
 */
@SuppressWarnings("serial")
public class PortletCommunicationManager extends AbstractCommunicationManager {

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
            return request.getParameter(name);
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
            // TODO Implement me!
        }

        public String getRequestPathInfo(Request request) {
            // We do not use paths in portlet mode
            throw new UnsupportedOperationException(
                    "PathInfo not available when running in Portlet mode");
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
    protected FileItemIterator getItemIterator(FileUpload upload,
            Request request) throws IOException, FileUploadException {
        return ((PortletFileUpload) upload)
                .getItemIterator((ActionRequest) request.getWrappedRequest());
    }

    public void handleFileUpload(ActionRequest request, ActionResponse response)
            throws FileUploadException, IOException {
        doHandleFileUpload(new PortletRequestWrapper(request),
                new PortletResponseWrapper(response));
    }

    public void handleUidlRequest(ResourceRequest request,
            ResourceResponse response,
            AbstractApplicationPortlet applicationPortlet)
            throws InvalidUIDLSecurityKeyException, IOException {
        doHandleUidlRequest(new PortletRequestWrapper(request),
                new PortletResponseWrapper(response),
                new AbstractApplicationPortletWrapper(applicationPortlet));
    }

}
