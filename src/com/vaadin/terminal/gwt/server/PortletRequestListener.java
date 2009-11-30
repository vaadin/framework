package com.vaadin.terminal.gwt.server;

import java.io.Serializable;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.servlet.Filter;

import com.vaadin.Application;
import com.vaadin.service.ApplicationContext.TransactionListener;
import com.vaadin.terminal.Terminal;

/**
 * {@link Application} that implements this interface gets notified of request
 * start and end by terminal. It is quite similar to
 * {@link HttpServletRequestListener}, but the parameters are Portlet specific.
 * If Application is used deployed as both Servlet and Portlet, one most likely
 * needs to implement both.
 * <p>
 * Only JSR 268 style Portlets are supported.
 * <p>
 * Interface can be used for several helper tasks including:
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
     * This method is called at the end if each request.
     * 
     * @param requestData
     *            the {@link PortletRequest}
     */
    public void onRequestEnd(PortletRequest request, PortletResponse response);
}