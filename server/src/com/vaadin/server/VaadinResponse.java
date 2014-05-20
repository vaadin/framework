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

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;

import javax.portlet.MimeResponse;
import javax.portlet.PortletResponse;
import javax.portlet.ResourceResponse;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * A generic response from the server, wrapping a more specific response type,
 * e.g. HttpServletResponse or PortletResponse.
 * 
 * @since 7.0
 */
public interface VaadinResponse extends Serializable {

    /**
     * Sets the (http) status code for the response. If you want to include an
     * error message along the status code, use {@link #sendError(int, String)}
     * instead.
     * 
     * @param statusCode
     *            the status code to set
     * @see HttpServletResponse#setStatus(int)
     * 
     * @see ResourceResponse#HTTP_STATUS_CODE
     */
    public void setStatus(int statusCode);

    /**
     * Sets the content type of this response. If the content type including a
     * charset is set before {@link #getWriter()} is invoked, the returned
     * PrintWriter will automatically use the defined charset.
     * 
     * @param contentType
     *            a string specifying the MIME type of the content
     * 
     * @see ServletResponse#setContentType(String)
     * @see MimeResponse#setContentType(String)
     */
    public void setContentType(String contentType);

    /**
     * Sets the value of a generic response header. If the header had already
     * been set, the new value overwrites the previous one.
     * 
     * @param name
     *            the name of the header
     * @param value
     *            the header value.
     * 
     * @see HttpServletResponse#setHeader(String, String)
     * @see PortletResponse#setProperty(String, String)
     */
    public void setHeader(String name, String value);

    /**
     * Properly formats a timestamp as a date header. If the header had already
     * been set, the new value overwrites the previous one.
     * 
     * @param name
     *            the name of the header
     * @param timestamp
     *            the number of milliseconds since epoch
     * 
     * @see HttpServletResponse#setDateHeader(String, long)
     */
    public void setDateHeader(String name, long timestamp);

    /**
     * Returns a <code>OutputStream</code> for writing binary data in the
     * response.
     * <p>
     * Either this method or getWriter() may be called to write the response,
     * not both.
     * 
     * @return a <code>OutputStream</code> for writing binary data
     * @throws IOException
     *             if an input or output exception occurred
     * 
     * @see #getWriter()
     * @see ServletResponse#getOutputStream()
     * @see MimeResponse#getPortletOutputStream()
     */
    public OutputStream getOutputStream() throws IOException;

    /**
     * Returns a <code>PrintWriter</code> object that can send character text to
     * the client. The PrintWriter uses the character encoding defined using
     * setContentType.
     * <p>
     * Either this method or getOutputStream() may be called to write the
     * response, not both.
     * 
     * @return a <code>PrintWriter</code> for writing character text
     * @throws IOException
     *             if an input or output exception occurred
     * 
     * @see #getOutputStream()
     * @see ServletResponse#getWriter()
     * @see MimeResponse#getWriter()
     */
    public PrintWriter getWriter() throws IOException;

    /**
     * Sets cache time in milliseconds, -1 means no cache at all. All required
     * headers related to caching in the response are set based on the time.
     * 
     * @param milliseconds
     *            Cache time in milliseconds
     */
    public void setCacheTime(long milliseconds);

    /**
     * Sends an error response to the client using the specified status code and
     * clears the buffer. In some configurations, this can cause a predefined
     * error page to be displayed.
     * 
     * @param errorCode
     *            the HTTP status code
     * @param message
     *            a message to accompany the error
     * @throws IOException
     *             if an input or output exception occurs
     * 
     * @see HttpServletResponse#sendError(int, String)
     */
    public void sendError(int errorCode, String message) throws IOException;

    /**
     * Gets the vaadin service for the context of this response.
     * 
     * @return the vaadin service
     * 
     * @see VaadinService
     */
    public VaadinService getService();

    /**
     * Adds the specified cookie to the response. This method can be called
     * multiple times to set more than one cookie.
     * 
     * @param cookie
     *            the Cookie to return to the client
     * 
     * @see HttpServletResponse#addCookie(Cookie)
     * @see PortletResponse#addProperty(Cookie)
     */
    public void addCookie(Cookie cookie);
}
