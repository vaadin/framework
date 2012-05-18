/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.portlet.MimeResponse;
import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceURL;

import com.vaadin.Application;
import com.vaadin.external.json.JSONException;
import com.vaadin.external.json.JSONObject;
import com.vaadin.terminal.DeploymentConfiguration;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.StreamVariable;
import com.vaadin.terminal.WrappedRequest;
import com.vaadin.terminal.WrappedResponse;
import com.vaadin.terminal.gwt.client.Connector;
import com.vaadin.ui.Root;

/**
 * TODO document me!
 * 
 * @author peholmst
 * 
 */
@SuppressWarnings("serial")
public class PortletCommunicationManager extends AbstractCommunicationManager {

    private transient MimeResponse currentMimeResponse;

    public PortletCommunicationManager(Application application) {
        super(application);
    }

    public void handleFileUpload(WrappedRequest request,
            WrappedResponse response) throws IOException {
        String contentType = request.getContentType();
        String name = request.getParameter("name");
        String ownerId = request.getParameter("rec-owner");
        Connector owner = getConnector(getApplication(), ownerId);
        StreamVariable streamVariable = ownerToNameToStreamVariable.get(owner)
                .get(name);

        if (contentType.contains("boundary")) {
            doHandleSimpleMultipartFileUpload(request, response,
                    streamVariable, name, owner,
                    contentType.split("boundary=")[1]);
        } else {
            doHandleXhrFilePost(request, response, streamVariable, name, owner,
                    request.getContentLength());
        }

    }

    @Override
    protected void postPaint(Root root) {
        super.postPaint(root);

        Application application = root.getApplication();
        if (ownerToNameToStreamVariable != null) {
            Iterator<Connector> iterator = ownerToNameToStreamVariable.keySet()
                    .iterator();
            while (iterator.hasNext()) {
                Connector owner = iterator.next();
                if (application.getConnector(owner.getConnectorId()) == null) {
                    // Owner is no longer attached to the application
                    iterator.remove();
                }
            }
        }
    }

    @Override
    protected boolean handleApplicationRequest(WrappedRequest request,
            WrappedResponse response) throws IOException {
        setCurrentMimeReponse(response);
        try {
            return super.handleApplicationRequest(request, response);
        } finally {
            currentMimeResponse = null;
        }
    }

    private void setCurrentMimeReponse(WrappedResponse response) {
        PortletResponse portletResponse = ((WrappedPortletResponse) response)
                .getPortletResponse();
        if (portletResponse instanceof MimeResponse) {
            currentMimeResponse = (MimeResponse) portletResponse;
        }

    }

    @Override
    public void handleUidlRequest(WrappedRequest request,
            WrappedResponse response, Callback callback, Root root)
            throws IOException, InvalidUIDLSecurityKeyException {
        setCurrentMimeReponse(response);
        super.handleUidlRequest(request, response, callback, root);
        currentMimeResponse = null;
    }

    @Override
    public void handleBrowserDetailsRequest(WrappedRequest request,
            WrappedResponse response, Application application)
            throws IOException {
        setCurrentMimeReponse(response);
        super.handleBrowserDetailsRequest(request, response, application);
        currentMimeResponse = null;

    }

    private Map<Connector, Map<String, StreamVariable>> ownerToNameToStreamVariable;

    @Override
    String getStreamVariableTargetUrl(Connector owner, String name,
            StreamVariable value) {
        if (ownerToNameToStreamVariable == null) {
            ownerToNameToStreamVariable = new HashMap<Connector, Map<String, StreamVariable>>();
        }
        Map<String, StreamVariable> nameToReceiver = ownerToNameToStreamVariable
                .get(owner);
        if (nameToReceiver == null) {
            nameToReceiver = new HashMap<String, StreamVariable>();
            ownerToNameToStreamVariable.put(owner, nameToReceiver);
        }
        nameToReceiver.put(name, value);
        ResourceURL resurl = createResourceURL();
        resurl.setResourceID("UPLOAD");
        resurl.setParameter("name", name);
        resurl.setParameter("rec-owner", owner.getConnectorId());
        resurl.setProperty("name", name);
        resurl.setProperty("rec-owner", owner.getConnectorId());
        return resurl.toString();
    }

    private ResourceURL createResourceURL() {
        if (currentMimeResponse == null) {
            throw new RuntimeException(
                    "No reponse object available. Cannot create a resource URL");
        }
        return currentMimeResponse.createResourceURL();
    }

    @Override
    protected void cleanStreamVariable(Connector owner, String name) {
        Map<String, StreamVariable> map = ownerToNameToStreamVariable
                .get(owner);
        map.remove(name);
        if (map.isEmpty()) {
            ownerToNameToStreamVariable.remove(owner);
        }
    }

    @Override
    protected BootstrapHandler createBootstrapHandler() {
        return new BootstrapHandler() {
            @Override
            public boolean handleRequest(Application application,
                    WrappedRequest request, WrappedResponse response)
                    throws IOException {
                PortletRequest portletRequest = WrappedPortletRequest.cast(
                        request).getPortletRequest();
                if (portletRequest instanceof RenderRequest) {
                    return super.handleRequest(application, request, response);
                } else {
                    return false;
                }
            }

            @Override
            protected String getApplicationId(BootstrapContext context) {
                PortletRequest portletRequest = WrappedPortletRequest.cast(
                        context.getRequest()).getPortletRequest();
                /*
                 * We need to generate a unique ID because some portals already
                 * create a DIV with the portlet's Window ID as the DOM ID.
                 */
                return "v-" + portletRequest.getWindowID();
            }

            @Override
            protected String getAppUri(BootstrapContext context) {
                return getRenderResponse(context).createActionURL().toString();
            }

            private RenderResponse getRenderResponse(BootstrapContext context) {
                PortletResponse response = ((WrappedPortletResponse) context
                        .getResponse()).getPortletResponse();

                RenderResponse renderResponse = (RenderResponse) response;
                return renderResponse;
            }

            @Override
            protected JSONObject getDefaultParameters(BootstrapContext context)
                    throws JSONException {
                /*
                 * We need this in order to get uploads to work. TODO this is
                 * not needed for uploads anymore, check if this is needed for
                 * some other things
                 */
                JSONObject defaults = super.getDefaultParameters(context);
                defaults.put("usePortletURLs", true);

                ResourceURL uidlUrlBase = getRenderResponse(context)
                        .createResourceURL();
                uidlUrlBase.setResourceID("UIDL");
                defaults.put("portletUidlURLBase", uidlUrlBase.toString());
                defaults.put("pathInfo", "");

                return defaults;
            }

            @Override
            protected void writeMainScriptTagContents(BootstrapContext context)
                    throws JSONException, IOException {
                // fixed base theme to use - all portal pages with Vaadin
                // applications will load this exactly once
                String portalTheme = WrappedPortletRequest
                        .cast(context.getRequest())
                        .getPortalProperty(
                                AbstractApplicationPortlet.PORTAL_PARAMETER_VAADIN_THEME);
                if (portalTheme != null
                        && !portalTheme.equals(context.getThemeName())) {
                    String portalThemeUri = getThemeUri(context, portalTheme);
                    // XSS safe - originates from portal properties
                    context.getWriter().write(
                            "vaadin.loadTheme('" + portalThemeUri + "');");
                }

                super.writeMainScriptTagContents(context);
            }

            @Override
            protected String getMainDivStyle(BootstrapContext context) {
                DeploymentConfiguration deploymentConfiguration = context
                        .getRequest().getDeploymentConfiguration();
                return deploymentConfiguration.getApplicationOrSystemProperty(
                        AbstractApplicationPortlet.PORTLET_PARAMETER_STYLE,
                        null);
            }

            @Override
            protected String getInitialUIDL(WrappedRequest request, Root root)
                    throws PaintException {
                return PortletCommunicationManager.this.getInitialUIDL(request,
                        root);
            }

            @Override
            protected JSONObject getApplicationParameters(
                    BootstrapContext context) throws JSONException,
                    PaintException {
                JSONObject parameters = super.getApplicationParameters(context);
                WrappedPortletResponse wrappedPortletResponse = (WrappedPortletResponse) context
                        .getResponse();
                MimeResponse portletResponse = (MimeResponse) wrappedPortletResponse
                        .getPortletResponse();
                ResourceURL resourceURL = portletResponse.createResourceURL();
                resourceURL.setResourceID("browserDetails");
                parameters.put("browserDetailsUrl", resourceURL.toString());
                return parameters;
            }

        };

    }

    @Override
    protected InputStream getThemeResourceAsStream(Root root, String themeName,
            String resource) {
        PortletApplicationContext2 context = (PortletApplicationContext2) root
                .getApplication().getContext();
        PortletContext portletContext = context.getPortletSession()
                .getPortletContext();
        return portletContext.getResourceAsStream("/"
                + AbstractApplicationPortlet.THEME_DIRECTORY_PATH + themeName
                + "/" + resource);
    }

}
