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

import java.util.Set;

import javax.portlet.PortletSession;
import javax.servlet.http.HttpSession;

/**
 * A generic session, wrapping a more specific session implementation, e.g.
 * {@link HttpSession} or {@link PortletSession}.
 * 
 * 
 * @author Vaadin Ltd
 * @version @VERSION@
 * @since 7.0.0
 */
public interface WrappedSession {
    /**
     * Returns the maximum time interval, in seconds, that this session will be
     * kept open between client accesses.
     * 
     * @return an integer specifying the number of seconds this session remains
     *         open between client requests
     * 
     * @see javax.servlet.http.HttpSession#getMaxInactiveInterval()
     * @see javax.portlet.PortletSession#getMaxInactiveInterval()
     */
    public int getMaxInactiveInterval();

    /**
     * Gets an attribute from this session.
     * 
     * @param name
     *            the name of the attribute
     * @return the attribute value, or <code>null</code> if the attribute is not
     *         defined in the session
     * 
     * @see javax.servlet.http.HttpSession#getAttribute(String)
     * @see javax.portlet.PortletSession#getAttribute(String)
     */
    public Object getAttribute(String name);

    /**
     * Saves an attribute value in this session.
     * 
     * @param name
     *            the name of the attribute
     * @param value
     *            the attribute value
     * 
     * @see javax.servlet.http.HttpSession#setAttribute(String, Object)
     * @see javax.portlet.PortletSession#setAttribute(String, Object)
     */
    public void setAttribute(String name, Object value);

    /**
     * Gets the current set of attribute names stored in this session.
     * 
     * @return an unmodifiable set of the current attribute names
     * 
     * @see HttpSession#getAttributeNames()
     * @see PortletSession#getAttributeNames()
     */
    public Set<String> getAttributeNames();

    /**
     * Invalidates this session then unbinds any objects bound to it.
     * 
     * @see HttpSession#invalidate()
     * @see PortletSession#invalidate()
     */
    public void invalidate();
}
