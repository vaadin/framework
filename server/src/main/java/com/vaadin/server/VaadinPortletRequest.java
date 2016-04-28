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

package com.vaadin.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Enumeration;

import javax.portlet.ClientDataRequest;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.ResourceRequest;
import javax.portlet.filter.PortletRequestWrapper;

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
public class VaadinPortletRequest extends PortletRequestWrapper implements
        VaadinRequest {

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
        super(request);
        this.vaadinService = vaadinService;
    }

    @Override
    public int getContentLength() {
        try {
            return ((ClientDataRequest) getRequest()).getContentLength();
        } catch (ClassCastException e) {
            throw new IllegalStateException(
                    "Content lenght only available for ClientDataRequests");
        }
    }

    @Override
    public InputStream getInputStream() throws IOException {
        try {
            return ((ClientDataRequest) getRequest()).getPortletInputStream();
        } catch (ClassCastException e) {
            throw new IllegalStateException(
                    "Input data only available for ClientDataRequests");
        }
    }

    @Override
    public BufferedReader getReader() throws IOException {
        try {
            return ((ClientDataRequest) getRequest()).getReader();
        } catch (ClassCastException e) {
            throw new IllegalStateException(
                    "Reader only available for ClientDataRequests");
        }
    }

    @Override
    public String getPathInfo() {
        PortletRequest request = getRequest();
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
        PortletSession session = getPortletSession(allowSessionCreation);
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
        return getRequest();
    }

    @Override
    public String getContentType() {
        try {
            return ((ResourceRequest) getRequest()).getContentType();
        } catch (ClassCastException e) {
            throw new IllegalStateException(
                    "Content type only available for ResourceRequests");
        }
    }

    @Override
    public String getCharacterEncoding() {
        try {
            return ((ClientDataRequest) getRequest()).getCharacterEncoding();
        } catch (ClassCastException e) {
            throw new IllegalStateException(
                    "Character encoding only available for ClientDataRequest");
        }
    }

    @Override
    public String getMethod() {
        try {
            return ((ClientDataRequest) getRequest()).getMethod();
        } catch (ClassCastException e) {
            throw new IllegalStateException(
                    "Method only available for ClientDataRequest");
        }
    }

    @Override
    public String getRemoteAddr() {
        return null;
    }

    @Override
    public String getRemoteHost() {
        return null;
    }

    @Override
    public int getRemotePort() {
        return -1;
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
        return getRequest().getPortalContext().getProperty(name);
    }

    /**
     * Reads a portlet preference from the portlet of the request.
     * 
     * @param name
     *            The name of the portlet preference. Cannot be
     *            <code>null</code>.
     * 
     * @return The value of the portlet preference, <code>null</code> if the
     *         preference is not defined.
     */
    public String getPortletPreference(String name) {
        PortletRequest request = getRequest();
        PortletPreferences preferences = request.getPreferences();

        return preferences.getValue(name, null);
    }

    @Override
    public VaadinPortletService getService() {
        return vaadinService;
    }

    @Override
    public long getDateHeader(String name) {
        String header = getHeader(name);
        if (header == null) {
            return -1;
        } else {
            try {
                return VaadinPortletResponse.HTTP_DATE_FORMAT.parse(header)
                        .getTime();
            } catch (ParseException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return null;
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        return null;
    }

    /**
     * Gets the currently processed portlet request. The current portlet request
     * is automatically defined when the request is started. The current portlet
     * request can not be used in e.g. background threads because of the way
     * server implementations reuse request instances.
     * 
     * @return the current portlet request instance if available, otherwise
     *         <code>null</code>
     * @since 7.3
     */
    public static PortletRequest getCurrentPortletRequest() {
        return VaadinPortletService.getCurrentPortletRequest();

    }

    /**
     * Gets the currently processed Vaadin portlet request. The current request
     * is automatically defined when the request is started. The current request
     * can not be used in e.g. background threads because of the way server
     * implementations reuse request instances.
     * 
     * @return the current Vaadin portlet request instance if available,
     *         otherwise <code>null</code>
     * @since 7.3
     */
    public static VaadinPortletRequest getCurrent() {
        return VaadinPortletService.getCurrentRequest();
    }
}
