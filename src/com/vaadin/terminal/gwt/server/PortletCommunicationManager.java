/*
@ITMillApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.ResourceURL;
import javax.servlet.ServletException;

import com.vaadin.Application;
import com.vaadin.terminal.Paintable;
import com.vaadin.terminal.StreamVariable;
import com.vaadin.terminal.VariableOwner;
import com.vaadin.terminal.WrappedRequest;
import com.vaadin.terminal.WrappedResponse;
import com.vaadin.ui.Component;
import com.vaadin.ui.Root;

/**
 * TODO document me!
 * 
 * @author peholmst
 * 
 */
@SuppressWarnings("serial")
public class PortletCommunicationManager extends AbstractCommunicationManager {

    private transient ResourceResponse currentUidlResponse;

    private static class AbstractApplicationPortletWrapper implements Callback {

        private final AbstractApplicationPortlet portlet;

        public AbstractApplicationPortletWrapper(
                AbstractApplicationPortlet portlet) {
            this.portlet = portlet;
        }

        public void criticalNotification(WrappedRequest request,
                WrappedResponse response, String cap, String msg,
                String details, String outOfSyncURL) throws IOException {
            portlet.criticalNotification(
                    ((WrappedPortletRequest) request).getPortletRequest(),
                    ((WrappedPortletResponse) response).getPortletResponse(),
                    cap, msg, details, outOfSyncURL);
        }

        public String getRequestPathInfo(WrappedRequest request) {
            return request.getRequestPathInfo();
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

    public void handleFileUpload(ResourceRequest request,
            ResourceResponse response) throws IOException {
        String contentType = request.getContentType();
        String name = request.getParameter("name");
        String ownerId = request.getParameter("rec-owner");
        VariableOwner variableOwner = getVariableOwner(ownerId);
        StreamVariable streamVariable = ownerToNameToStreamVariable.get(
                variableOwner).get(name);

        if (contentType.contains("boundary")) {
            doHandleSimpleMultipartFileUpload(
                    new WrappedPortletRequest(request),
                    new WrappedPortletResponse(response), streamVariable, name,
                    variableOwner, contentType.split("boundary=")[1]);
        } else {
            doHandleXhrFilePost(new WrappedPortletRequest(request),
                    new WrappedPortletResponse(response), streamVariable, name,
                    variableOwner, request.getContentLength());
        }

    }

    @Override
    protected void unregisterPaintable(Component p) {
        super.unregisterPaintable(p);
        if (ownerToNameToStreamVariable != null) {
            ownerToNameToStreamVariable.remove(p);
        }
    }

    public void handleUidlRequest(ResourceRequest request,
            ResourceResponse response,
            AbstractApplicationPortlet applicationPortlet, Root root)
            throws InvalidUIDLSecurityKeyException, IOException {
        currentUidlResponse = response;
        doHandleUidlRequest(new WrappedPortletRequest(request),
                new WrappedPortletResponse(response),
                new AbstractApplicationPortletWrapper(applicationPortlet), root);
        currentUidlResponse = null;
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
     * @param assumedRoot
     *            if the window has been already resolved once, this parameter
     *            must contain the window.
     * @return Window matching the given URI or null if not found.
     * @throws ServletException
     *             if an exception has occurred that interferes with the
     *             servlet's normal operation.
     */
    Root getApplicationRoot(PortletRequest request,
            AbstractApplicationPortlet applicationPortlet,
            Application application, Root assumedRoot) {

        return doGetApplicationWindow(new WrappedPortletRequest(request),
                new AbstractApplicationPortletWrapper(applicationPortlet),
                application, assumedRoot);
    }

    private Map<VariableOwner, Map<String, StreamVariable>> ownerToNameToStreamVariable;

    @Override
    String getStreamVariableTargetUrl(VariableOwner owner, String name,
            StreamVariable value) {
        if (ownerToNameToStreamVariable == null) {
            ownerToNameToStreamVariable = new HashMap<VariableOwner, Map<String, StreamVariable>>();
        }
        Map<String, StreamVariable> nameToReceiver = ownerToNameToStreamVariable
                .get(owner);
        if (nameToReceiver == null) {
            nameToReceiver = new HashMap<String, StreamVariable>();
            ownerToNameToStreamVariable.put(owner, nameToReceiver);
        }
        nameToReceiver.put(name, value);
        ResourceURL resurl = currentUidlResponse.createResourceURL();
        resurl.setResourceID("UPLOAD");
        resurl.setParameter("name", name);
        resurl.setParameter("rec-owner", getPaintableId((Paintable) owner));
        resurl.setProperty("name", name);
        resurl.setProperty("rec-owner", getPaintableId((Paintable) owner));
        return resurl.toString();
    }

    @Override
    protected void cleanStreamVariable(VariableOwner owner, String name) {
        Map<String, StreamVariable> map = ownerToNameToStreamVariable
                .get(owner);
        map.remove(name);
        if (map.isEmpty()) {
            ownerToNameToStreamVariable.remove(owner);
        }
    }

    public boolean handleApplicationRequest(PortletRequest request,
            PortletResponse response) throws IOException {
        return handleApplicationRequest(new WrappedPortletRequest(request),
                new WrappedPortletResponse(response));
    }

}
