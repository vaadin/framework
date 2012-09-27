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
import java.util.Locale;
import java.util.Map;

import javax.portlet.ClientDataRequest;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.ResourceRequest;

import com.vaadin.shared.ApplicationConstants;

/**
 * Wrapper for {@link PortletRequest} and its subclasses.
 * 
 * @author Vaadin Ltd.
 * @since 7.0
 * 
 * @see VaadinRequest
 * @see VaadinPortletResponse
 */
public class VaadinPortletRequest implements VaadinRequest {

    private final PortletRequest request;
    private final VaadinPortletService vaadinService;

    /**
     * Wraps a portlet request and an associated vaadin service
     * 
     * @param request
     *            the portlet request to wrap
     * @param vaadinService
     *            the associated vaadin service
     */
    public VaadinPortletRequest(PortletRequest request,
            VaadinPortletService vaadinService) {
        this.request = request;
        this.vaadinService = vaadinService;
    }

    @Override
    public Object getAttribute(String name) {
        return request.getAttribute(name);
    }

    @Override
    public int getContentLength() {
        try {
            return ((ClientDataRequest) request).getContentLength();
        } catch (ClassCastException e) {
            throw new IllegalStateException(
                    "Content lenght only available for ClientDataRequests");
        }
    }

    @Override
    public InputStream getInputStream() throws IOException {
        try {
            return ((ClientDataRequest) request).getPortletInputStream();
        } catch (ClassCastException e) {
            throw new IllegalStateException(
                    "Input data only available for ClientDataRequests");
        }
    }

    @Override
    public String getParameter(String name) {
        return request.getParameter(name);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return request.getParameterMap();
    }

    @Override
    public void setAttribute(String name, Object o) {
        request.setAttribute(name, o);
    }

    @Override
    public String getRequestPathInfo() {
        if (request instanceof ResourceRequest) {
            ResourceRequest resourceRequest = (ResourceRequest) request;
            String resourceID = resourceRequest.getResourceID();
            if (VaadinPortlet.RESOURCE_URL_ID.equals(resourceID)) {
                String resourcePath = resourceRequest
                        .getParameter(ApplicationConstants.V_RESOURCE_PATH);
                return resourcePath;
            }
            return resourceID;
        } else {
            return null;
        }
    }

    @Override
    public WrappedSession getWrappedSession() {
        return getWrappedSession(true);
    }

    @Override
    public WrappedSession getWrappedSession(boolean allowSessionCreation) {
        PortletSession session = request
                .getPortletSession(allowSessionCreation);
        if (session != null) {
            return new WrappedPortletSession(session);
        } else {
            return null;
        }
    }

    /**
     * Gets the original, unwrapped portlet request.
     * 
     * @return the unwrapped portlet request
     */
    public PortletRequest getPortletRequest() {
        return request;
    }

    @Override
    public String getContentType() {
        try {
            return ((ResourceRequest) request).getContentType();
        } catch (ClassCastException e) {
            throw new IllegalStateException(
                    "Content type only available for ResourceRequests");
        }
    }

    @Override
    public BrowserDetails getBrowserDetails() {
        return new BrowserDetails() {
            @Override
            public String getUriFragment() {
                return null;
            }

            @Override
            public String getWindowName() {
                return null;
            }

            @Override
            public WebBrowser getWebBrowser() {
                VaadinPortletSession context = (VaadinPortletSession) VaadinServiceSession
                        .getCurrent();
                return context.getBrowser();
            }
        };
    }

    @Override
    public Locale getLocale() {
        return request.getLocale();
    }

    @Override
    public String getRemoteAddr() {
        return null;
    }

    @Override
    public boolean isSecure() {
        return request.isSecure();
    }

    @Override
    public String getHeader(String string) {
        return null;
    }

    /**
     * Reads a portal property from the portal context of the Vaadin request.
     * 
     * @param name
     *            a string with the name of the portal property to get
     * @return a string with the value of the property, or <code>null</code> if
     *         the property is not defined
     */
    public String getPortalProperty(String name) {
        return request.getPortalContext().getProperty(name);
    }

    @Override
    public VaadinPortletService getService() {
        return vaadinService;
    }

    /**
     * Helper method to get a {@link VaadinPortletRequest} from a
     * {@link VaadinRequest}. Aside from casting, this method also takes care of
     * situations where there's another level of wrapping.
     * 
     * @param request
     *            a Vaadin request
     * @return a Vaadin portlet request
     * @throws ClassCastException
     *             if the Vaadin request doesn't wrap a portlet request
     */
    public static VaadinPortletRequest cast(VaadinRequest request) {
        if (request instanceof CombinedRequest) {
            CombinedRequest combinedRequest = (CombinedRequest) request;
            request = combinedRequest.getSecondRequest();
        }
        return (VaadinPortletRequest) request;
    }

    @Override
    public String getContextPath() {
        return request.getContextPath();
    }
}
