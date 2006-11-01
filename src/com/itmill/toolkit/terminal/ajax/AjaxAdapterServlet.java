/* *************************************************************************
 
                               IT Mill Toolkit 

               Development of Browser User Intarfaces Made Easy

                    Copyright (C) 2000-2006 IT Mill Ltd
                     
   *************************************************************************

   This product is distributed under commercial license that can be found
   from the product package on license/license.txt. Use of this product might 
   require purchasing a commercial license from IT Mill Ltd. For guidelines 
   on usage, see license/licensing-guidelines.html

   *************************************************************************
   
   For more information, contact:
   
   IT Mill Ltd                           phone: +358 2 4802 7180
   Ruukinkatu 2-4                        fax:   +358 2 4802 7181
   20540, Turku                          email:  info@itmill.com
   Finland                               company www: www.itmill.com
   
   Primary source for information and releases: www.itmill.com

   ********************************************************************** */

package com.itmill.toolkit.terminal.ajax;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import com.itmill.toolkit.Application;

/**
 * Servlet implementing connection between a web-browser and Millstone
 * Application.
 * 
 * (stage drafting and planning)
 * 
 * <h1>Paintable update format</h1>
 * 
 * <changes><change pid="271987">... UIDL XML FRAGMENT HERE ... </change>
 * <change pid="271987">... UIDL XML FRAGMENT HERE ... </change> <change
 * pid="271987">... UIDL XML FRAGMENT HERE ... </change> <change format="HTML"
 * paintableId="271987">. <![CDATA[... SERVER SIDE TRANSFORMED HTML HERE ...]]>
 * </change> </changes>
 * 
 * In the first phase, no server side-transforms are supported, so the format
 * attribute is omitted and only UIDL supported.
 * 
 * <h1>Request parameters</h1>
 * 
 * TBD
 * 
 * @author IT Mill Ltd, Joonas Lehtinen, Sami Ekblad
 * @version @VERSION@
 * @since 3.1
 */
public class AjaxAdapterServlet extends HttpServlet {

    /**
     * Session attribute, where to find servlet context to
     * AjaxApplicationContext mapping
     */
    private static String SESSION_ATTR_APPLICATION_CONTEXT = "org.millstone.ajaxadapter.AjaxApplicationContext";

	private static String SESSION_BINDING_LISTENER = "bindinglistener";
    
    private Class applicationClass;

    private Properties applicationProperties;
    


    /**
     * Called by the servlet container to indicate to a servlet that the servlet
     * is being placed into service.
     * 
     * @param servletConfig
     *            object containing the servlet's configuration and
     *            initialization parameters
     * @throws ServletException
     *             if an exception has occurred that interferes with the
     *             servlet's normal operation.
     */
    public void init(javax.servlet.ServletConfig servletConfig)
            throws javax.servlet.ServletException {
        super.init(servletConfig);

        // Get the application class name
        String applicationClassName = servletConfig
                .getInitParameter("application");
        if (applicationClassName == null) {
            Log.error("Application not specified in servlet parameters");
        }

        // Store the application parameters into Properties object
        this.applicationProperties = new Properties();
        for (Enumeration e = servletConfig.getInitParameterNames(); e
                .hasMoreElements();) {
            String name = (String) e.nextElement();
            this.applicationProperties.setProperty(name, servletConfig
                    .getInitParameter(name));
        }

        // Override with server.xml parameters
        ServletContext context = servletConfig.getServletContext();
        for (Enumeration e = context.getInitParameterNames(); e
                .hasMoreElements();) {
            String name = (String) e.nextElement();
            this.applicationProperties.setProperty(name, context
                    .getInitParameter(name));
        }

        // Load the application class using the same class loader
        // as the servlet itself
        ClassLoader loader = this.getClass().getClassLoader();
        try {
            this.applicationClass = loader.loadClass(applicationClassName);
        } catch (ClassNotFoundException e) {
            throw new ServletException("Failed to load application class: "
                    + applicationClassName);
        }
    }

    private AjaxApplicationContext getAjaxApplicationContext(
            HttpServletRequest request) {

        // Get the session
        HttpSession session = request.getSession(true);

        // Get the Map
        Map acmap = (Map) session
                .getAttribute(SESSION_ATTR_APPLICATION_CONTEXT);
        if (acmap == null) {
            acmap = new HashMap();
            session.setAttribute(SESSION_ATTR_APPLICATION_CONTEXT, acmap);
    		HttpSessionBindingListener sessionBindingListener = new SessionBindingListener(
    				acmap);
    		session.setAttribute(SESSION_BINDING_LISTENER,
    				sessionBindingListener);

        }

        // Get the application context
        AjaxApplicationContext ac = (AjaxApplicationContext) acmap
                .get(getServletContext());
        if (ac == null) {
            ac = new AjaxApplicationContext(getServletContext());
            acmap.put(getServletContext(), ac);
        }

        return ac;
    }

    protected void service(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

        // Get the context
        AjaxApplicationContext context = getAjaxApplicationContext(request);

        try {

            // Get the application
            Application application = context.getApplication(
                    getApplicationUrl(request), request.getContextPath()
                            + request.getServletPath());

            // Create application if it doesn't exist
            if (application == null)
                application = context.createApplication(applicationClass,
                        getApplicationUrl(request), request.getLocale(),
                        applicationProperties);

            // Notify transaction start
			if (context != null) {
				context.startTransaction(application, request);
			}
			
            // Uidl
            context.getApplicationManager(application).handleXmlHttpRequest(
                    request, response);

            // Notify transaction end
			if (context != null) {
				context.endTransaction(application, request);
			}
            
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /** Get the current application URL from request */
    private URL getApplicationUrl(HttpServletRequest request) {

        URL applicationUrl;
        try {
            URL reqURL = new URL((request.isSecure() ? "https://" : "http://")
                    + request.getServerName() + ":" + request.getServerPort()
                    + request.getRequestURI());
            String servletPath = request.getContextPath()
                    + request.getServletPath();
            if (servletPath.length() == 0
                    || servletPath.charAt(servletPath.length() - 1) != '/')
                servletPath = servletPath + "/";
            applicationUrl = new URL(reqURL, servletPath);
        } catch (MalformedURLException e) {
            Log.error("Error constructing application url "
                    + request.getRequestURI() + " (" + e + ")");
            throw new RuntimeException("Error constructing application url", e);
        }

        return applicationUrl;
    }
    
    
	private class SessionBindingListener implements HttpSessionBindingListener {
		private Map acmap;
		protected SessionBindingListener(Map acmap) {
			this.acmap = acmap;
		}

		/**
		 * @see javax.servlet.http.HttpSessionBindingListener#valueBound(HttpSessionBindingEvent)
		 */
		public void valueBound(HttpSessionBindingEvent arg0) {
			// We are not interested in bindings
		}

		/**
		 * @see javax.servlet.http.HttpSessionBindingListener#valueUnbound(HttpSessionBindingEvent)
		 */
		public void valueUnbound(HttpSessionBindingEvent event) {

			// If the binding listener is unbound from the session, the
			// session must be closing
			if (event.getName().equals(SESSION_BINDING_LISTENER)) {

				// Close all applications
				for (Iterator i = acmap.values().iterator(); i.hasNext();) {
					AjaxApplicationContext actx = (AjaxApplicationContext) i.next();
					actx.close();					
				}
			}
		}

	}

}
