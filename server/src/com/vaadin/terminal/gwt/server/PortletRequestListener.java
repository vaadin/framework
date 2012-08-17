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
package com.vaadin.terminal.gwt.server;

import java.io.Serializable;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.servlet.Filter;

import com.vaadin.Application;
import com.vaadin.service.ApplicationContext.TransactionListener;
import com.vaadin.terminal.Terminal;

/**
 * An {@link Application} that implements this interface gets notified of
 * request start and end by the terminal. It is quite similar to the
 * {@link HttpServletRequestListener}, but the parameters are Portlet specific.
 * If an Application is deployed as both a Servlet and a Portlet, one most
 * likely needs to implement both.
 * <p>
 * Only JSR 286 style Portlets are supported.
 * <p>
 * The interface can be used for several helper tasks including:
 * <ul>
 * <li>Opening and closing database connections
 * <li>Implementing {@link ThreadLocal}
 * <li>Inter-portlet communication
 * </ul>
 * <p>
 * Alternatives for implementing similar features are are Servlet {@link Filter}
 * s and {@link TransactionListener}s in Vaadin.
 * 
 * @since 6.2
 * @see HttpServletRequestListener
 */
public interface PortletRequestListener extends Serializable {

    /**
     * This method is called before {@link Terminal} applies the request to
     * Application.
     * 
     * @param requestData
     *            the {@link PortletRequest} about to change Application state
     */
    public void onRequestStart(PortletRequest request, PortletResponse response);

    /**
     * This method is called at the end of each request.
     * 
     * @param requestData
     *            the {@link PortletRequest}
     */
    public void onRequestEnd(PortletRequest request, PortletResponse response);
}