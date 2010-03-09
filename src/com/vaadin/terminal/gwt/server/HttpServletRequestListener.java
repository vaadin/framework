/*
@ITMillApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.server;

import java.io.Serializable;

import javax.servlet.Filter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.vaadin.Application;
import com.vaadin.service.ApplicationContext.TransactionListener;
import com.vaadin.terminal.Terminal;

/**
 * {@link Application} that implements this interface gets notified of request
 * start and end by terminal.
 * <p>
 * Interface can be used for several helper tasks including:
 * <ul>
 * <li>Opening and closing database connections
 * <li>Implementing {@link ThreadLocal}
 * <li>Setting/Getting {@link Cookie}
 * </ul>
 * <p>
 * Alternatives for implementing similar features are are Servlet {@link Filter}
 * s and {@link TransactionListener}s in Vaadin.
 * 
 * @since 6.2
 * @see PortletRequestListener
 */
public interface HttpServletRequestListener extends Serializable {

    /**
     * This method is called before {@link Terminal} applies the request to
     * Application.
     * 
     * @param request
     * @param response
     */
    public void onRequestStart(HttpServletRequest request,
            HttpServletResponse response);

    /**
     * This method is called at the end of each request.
     * 
     * @param request
     * @param response
     */
    public void onRequestEnd(HttpServletRequest request,
            HttpServletResponse response);
}