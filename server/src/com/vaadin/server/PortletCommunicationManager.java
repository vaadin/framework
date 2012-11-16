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

import java.io.IOException;
import java.io.InputStream;

import javax.portlet.MimeResponse;
import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceURL;

import org.json.JSONException;
import org.json.JSONObject;

import com.vaadin.shared.ApplicationConstants;
import com.vaadin.ui.UI;

/**
 * TODO document me!
 * 
 * @author peholmst
 * 
 * 
 * @deprecated might be refactored or removed before 7.0.0
 */
@Deprecated
@SuppressWarnings("serial")
public class PortletCommunicationManager extends AbstractCommunicationManager {

    public PortletCommunicationManager(VaadinSession session) {
        super(session);
    }

    @Override
    protected BootstrapHandler createBootstrapHandler() {
        return new BootstrapHandler() {
            @Override
            public boolean handleRequest(VaadinSession session,
                    VaadinRequest request, VaadinResponse response)
                    throws IOException {
                PortletRequest portletRequest = ((VaadinPortletRequest) request)
                        .getPortletRequest();
                if (portletRequest instanceof RenderRequest) {
                    return super.handleRequest(session, request, response);
                } else {
                    return false;
                }
            }

            @Override
            protected String getServiceUrl(BootstrapContext context) {
                ResourceURL portletResourceUrl = getRenderResponse(context)
                        .createResourceURL();
                portletResourceUrl.setResourceID(VaadinPortlet.RESOURCE_URL_ID);
                return portletResourceUrl.toString();
            }

            private RenderResponse getRenderResponse(BootstrapContext context) {
                PortletResponse response = ((VaadinPortletResponse) context
                        .getResponse()).getPortletResponse();

                RenderResponse renderResponse = (RenderResponse) response;
                return renderResponse;
            }

            @Override
            protected void appendMainScriptTagContents(
                    BootstrapContext context, StringBuilder builder)
                    throws JSONException, IOException {
                // fixed base theme to use - all portal pages with Vaadin
                // applications will load this exactly once
                String portalTheme = ((VaadinPortletRequest) context
                        .getRequest())
                        .getPortalProperty(VaadinPortlet.PORTAL_PARAMETER_VAADIN_THEME);
                if (portalTheme != null
                        && !portalTheme.equals(context.getThemeName())) {
                    String portalThemeUri = getThemeUri(context, portalTheme);
                    // XSS safe - originates from portal properties
                    builder.append("vaadin.loadTheme('" + portalThemeUri
                            + "');");
                }

                super.appendMainScriptTagContents(context, builder);
            }

            @Override
            protected String getMainDivStyle(BootstrapContext context) {
                VaadinService vaadinService = context.getRequest().getService();
                return vaadinService.getDeploymentConfiguration()
                        .getApplicationOrSystemProperty(
                                VaadinPortlet.PORTLET_PARAMETER_STYLE, null);
            }

            @Override
            protected JSONObject getApplicationParameters(
                    BootstrapContext context) throws JSONException,
                    PaintException {
                JSONObject parameters = super.getApplicationParameters(context);
                VaadinPortletResponse response = (VaadinPortletResponse) context
                        .getResponse();
                MimeResponse portletResponse = (MimeResponse) response
                        .getPortletResponse();
                ResourceURL resourceURL = portletResponse.createResourceURL();
                resourceURL.setResourceID("browserDetails");
                parameters.put("browserDetailsUrl", resourceURL.toString());

                // Always send path info as a query parameter
                parameters.put(
                        ApplicationConstants.SERVICE_URL_PATH_AS_PARAMETER,
                        true);

                return parameters;
            }

        };

    }

    @Override
    protected InputStream getThemeResourceAsStream(UI uI, String themeName,
            String resource) {
        VaadinPortletSession session = (VaadinPortletSession) uI.getSession();
        PortletContext portletContext = session.getPortletSession()
                .getPortletContext();
        return portletContext.getResourceAsStream("/"
                + VaadinPortlet.THEME_DIRECTORY_PATH + themeName + "/"
                + resource);
    }

}
