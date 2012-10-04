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
import java.io.Serializable;
import java.net.URI;
import java.util.Locale;
import java.util.Map;

import javax.portlet.PortletRequest;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import com.vaadin.ui.UI;

/**
 * A generic request to the server, wrapping a more specific request type, e.g.
 * HttpServletReqest or PortletRequest.
 * 
 * @author Vaadin Ltd
 * @since 7.0.0
 */
public interface VaadinRequest extends Serializable {

    /**
     * Detailed information extracted from the browser.
     * 
     * @see VaadinRequest#getBrowserDetails()
     * @deprecated might be refactored or removed before 7.0.0
     */
    @Deprecated
    public interface BrowserDetails extends Serializable {
        /**
         * Gets the URI in the browser address bar, including the fragment
         * (Javascript window.location)
         * 
         * @return the browser location URI
         */
        public URI getLocation();

        /**
         * Gets the value of window.name from the browser. This can be used to
         * keep track of the specific window between browser reloads.
         * 
         * @return the string value of window.name in the browser
         */
        public String getWindowName();

        /**
         * Gets a reference to the {@link WebBrowser} object containing
         * additional information, e.g. screen size and the time zone offset.
         * 
         * @return the web browser object
         */
        public WebBrowser getWebBrowser();
    }

    /**
     * Gets the named request parameter This is typically a HTTP GET or POST
     * parameter, though other request types might have other ways of
     * representing parameters.
     * 
     * @see javax.servlet.ServletRequest#getParameter(String)
     * @see javax.portlet.PortletRequest#getParameter(String)
     * 
     * @param parameter
     *            the name of the parameter
     * @return The paramter value, or <code>null</code> if no parameter with the
     *         given name is present
     */
    public String getParameter(String parameter);

    /**
     * Gets all the parameters of the request.
     * 
     * @see #getParameter(String)
     * 
     * @see javax.servlet.ServletRequest#getParameterMap()
     * @see javax.portlet.PortletRequest#getParameter(String)
     * 
     * @return A mapping of parameter names to arrays of parameter values
     */
    public Map<String, String[]> getParameterMap();

    /**
     * Returns the length of the request content that can be read from the input
     * stream returned by {@link #getInputStream()}.
     * 
     * @see javax.servlet.ServletRequest#getContentLength()
     * @see javax.portlet.ClientDataRequest#getContentLength()
     * 
     * @return content length in bytes
     */
    public int getContentLength();

    /**
     * Returns an input stream from which the request content can be read. The
     * request content length can be obtained with {@link #getContentLength()}
     * without reading the full stream contents.
     * 
     * @see javax.servlet.ServletRequest#getInputStream()
     * @see javax.portlet.ClientDataRequest#getPortletInputStream()
     * 
     * @return the input stream from which the contents of the request can be
     *         read
     * @throws IOException
     *             if the input stream can not be opened
     */
    public InputStream getInputStream() throws IOException;

    /**
     * Gets a request attribute.
     * 
     * @param name
     *            the name of the attribute
     * @return the value of the attribute, or <code>null</code> if there is no
     *         attribute with the given name
     * 
     * @see javax.servlet.ServletRequest#getAttribute(String)
     * @see javax.portlet.PortletRequest#getAttribute(String)
     */
    public Object getAttribute(String name);

    /**
     * Defines a request attribute.
     * 
     * @param name
     *            the name of the attribute
     * @param value
     *            the attribute value
     * 
     * @see javax.servlet.ServletRequest#setAttribute(String, Object)
     * @see javax.portlet.PortletRequest#setAttribute(String, Object)
     */
    public void setAttribute(String name, Object value);

    /**
     * Gets the path of the requested resource relative to the application. The
     * path be <code>null</code> if no path information is available. Does
     * always start with / if the path isn't <code>null</code>.
     * 
     * @return a string with the path relative to the application.
     * 
     * @see javax.servlet.http.HttpServletRequest#getPathInfo()
     */
    public String getRequestPathInfo();

    /**
     * Returns the portion of the request URI that indicates the context of the
     * request. The context path always comes first in a request URI.
     * 
     * @see HttpServletRequest#getContextPath()
     * @see PortletRequest#getContextPath()
     * 
     * @return a String specifying the portion of the request URI that indicates
     *         the context of the request
     */
    public String getContextPath();

    /**
     * Gets the session associated with this request, creating a new if there is
     * no session.
     * 
     * @see WrappedSession
     * @see HttpServletRequest#getSession()
     * @see PortletRequest#getPortletSession()
     * 
     * @return the wrapped session for this request
     */
    public WrappedSession getWrappedSession();

    /**
     * Gets the session associated with this request, optionally creating a new
     * if there is no session.
     * 
     * @param allowSessionCreation
     *            <code>true</code> to create a new session for this request if
     *            necessary; <code>false</code> to return <code>null</code> if
     *            there's no current session
     * 
     * @see WrappedSession
     * @see HttpServletRequest#getSession(boolean)
     * @see PortletRequest#getPortletSession(boolean)
     * 
     * @return the wrapped session for this request
     */
    public WrappedSession getWrappedSession(boolean allowSessionCreation);

    /**
     * Returns the MIME type of the body of the request, or null if the type is
     * not known.
     * 
     * @return a string containing the name of the MIME type of the request, or
     *         null if the type is not known
     * 
     * @see javax.servlet.ServletRequest#getContentType()
     * @see javax.portlet.ResourceRequest#getContentType()
     * 
     */
    public String getContentType();

    /**
     * Gets detailed information about the browser from which the request
     * originated. This consists of information that is not available from
     * normal HTTP requests, but requires additional information to be extracted
     * for instance using javascript in the browser.
     * 
     * This information is only guaranteed to be available in some special
     * cases, for instance in {@link UI#init(VaadinRequest)}.
     * 
     * @return the browser details, or <code>null</code> if details are not
     *         available
     * 
     * @see BrowserDetails
     * @deprecated might be refactored or removed before 7.0.0
     */
    @Deprecated
    public BrowserDetails getBrowserDetails();

    /**
     * Gets locale information from the query, e.g. using the Accept-Language
     * header.
     * 
     * @return the preferred Locale
     * 
     * @see ServletRequest#getLocale()
     * @see PortletRequest#getLocale()
     */
    public Locale getLocale();

    /**
     * Returns the IP address from which the request came. This might also be
     * the address of a proxy between the server and the original requester.
     * 
     * @return a string containing the IP address, or <code>null</code> if the
     *         address is not available
     * 
     * @see ServletRequest#getRemoteAddr()
     */
    public String getRemoteAddr();

    /**
     * Checks whether the request was made using a secure channel, e.g. using
     * https.
     * 
     * @return a boolean indicating if the request is secure
     * 
     * @see ServletRequest#isSecure()
     * @see PortletRequest#isSecure()
     */
    public boolean isSecure();

    /**
     * Gets the value of a request header, e.g. a http header for a
     * {@link HttpServletRequest}.
     * 
     * @param headerName
     *            the name of the header
     * @return the header value, or <code>null</code> if the header is not
     *         present in the request
     * 
     * @see HttpServletRequest#getHeader(String)
     */
    public String getHeader(String headerName);

    /**
     * Gets the vaadin service for the context of this request.
     * 
     * @return the vaadin service
     * 
     * @see VaadinService
     */
    public VaadinService getService();

}
