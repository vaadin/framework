/*
 * Copyright 2000-2014 Vaadin Ltd.
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

package com.vaadin.server.communication;

import java.io.IOException;

import javax.portlet.MimeResponse;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceURL;

import org.json.JSONException;
import org.json.JSONObject;

import com.vaadin.server.BootstrapHandler;
import com.vaadin.server.PaintException;
import com.vaadin.server.VaadinPortlet;
import com.vaadin.server.VaadinPortlet.VaadinLiferayRequest;
import com.vaadin.server.VaadinPortletRequest;
import com.vaadin.server.VaadinPortletResponse;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ApplicationConstants;

public class PortletBootstrapHandler extends BootstrapHandler {
    @Override
    public boolean handleRequest(VaadinSession session, VaadinRequest request,
            VaadinResponse response) throws IOException {
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
    protected void appendMainScriptTagContents(BootstrapContext context,
            StringBuilder builder) throws JSONException, IOException {
        // fixed base theme to use - all portal pages with Vaadin
        // applications will load this exactly once
        String portalTheme = ((VaadinPortletRequest) context.getRequest())
                .getPortalProperty(VaadinPortlet.PORTAL_PARAMETER_VAADIN_THEME);
        if (portalTheme != null && !portalTheme.equals(context.getThemeName())) {
            String portalThemeUri = getThemeUri(context, portalTheme);
            // XSS safe - originates from portal properties
            builder.append("vaadin.loadTheme('" + portalThemeUri + "');");
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
    protected JSONObject getApplicationParameters(BootstrapContext context)
            throws JSONException, PaintException {
        JSONObject parameters = super.getApplicationParameters(context);
        VaadinPortletResponse response = (VaadinPortletResponse) context
                .getResponse();
        VaadinPortletRequest request = (VaadinPortletRequest) context
                .getRequest();
        MimeResponse portletResponse = (MimeResponse) response
                .getPortletResponse();
        ResourceURL resourceURL = portletResponse.createResourceURL();
        resourceURL.setResourceID("v-browserDetails");
        parameters.put("browserDetailsUrl", resourceURL.toString());

        // Always send path info as a query parameter
        parameters
                .put(ApplicationConstants.SERVICE_URL_PATH_AS_PARAMETER, true);

        // If we are running in Liferay then we need to prefix all parameters
        // with the portlet namespace
        if (request instanceof VaadinLiferayRequest) {
            parameters.put(
                    ApplicationConstants.SERVICE_URL_PARAMETER_NAMESPACE,
                    response.getPortletResponse().getNamespace());
        }

        return parameters;
    }
}
