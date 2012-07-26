/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.server;

import java.io.IOException;
import java.io.InputStream;

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
import com.vaadin.terminal.WrappedRequest;
import com.vaadin.terminal.WrappedResponse;
import com.vaadin.terminal.gwt.client.ApplicationConfiguration;
import com.vaadin.ui.Root;

/**
 * TODO document me!
 * 
 * @author peholmst
 * 
 */
@SuppressWarnings("serial")
public class PortletCommunicationManager extends AbstractCommunicationManager {

    public PortletCommunicationManager(Application application) {
        super(application);
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

                ResourceURL portletResourceUrl = getRenderResponse(context)
                        .createResourceURL();
                portletResourceUrl
                        .setResourceID(AbstractApplicationPortlet.RESOURCE_URL_ID);
                defaults.put(ApplicationConfiguration.PORTLET_RESOUCE_URL_BASE,
                        portletResourceUrl.toString());

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
                    throws PaintException, JSONException {
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
