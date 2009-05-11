/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.server;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import com.vaadin.Application;

/**
 * This servlet connects IT Mill Toolkit Application to Web.
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 5.0
 */

@SuppressWarnings("serial")
public class ApplicationServlet extends AbstractApplicationServlet {

    // Private fields
    private Class<? extends Application> applicationClass;

    /**
     * Called by the servlet container to indicate to a servlet that the servlet
     * is being placed into service.
     * 
     * @param servletConfig
     *            the object containing the servlet's configuration and
     *            initialization parameters
     * @throws javax.servlet.ServletException
     *             if an exception has occurred that interferes with the
     *             servlet's normal operation.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void init(javax.servlet.ServletConfig servletConfig)
            throws javax.servlet.ServletException {
        super.init(servletConfig);

        // Loads the application class using the same class loader
        // as the servlet itself

        // Gets the application class name
        final String applicationClassName = servletConfig
                .getInitParameter("application");
        if (applicationClassName == null) {
            throw new ServletException(
                    "Application not specified in servlet parameters");
        }

        try {
            applicationClass = (Class<? extends Application>) getClassLoader()
                    .loadClass(applicationClassName);
        } catch (final ClassNotFoundException e) {
            throw new ServletException("Failed to load application class: "
                    + applicationClassName);
        }
    }

    @Override
    protected Application getNewApplication(HttpServletRequest request)
            throws ServletException {

        // Creates a new application instance
        try {
            final Application application = getApplicationClass().newInstance();

            return application;
        } catch (final IllegalAccessException e) {
            throw new ServletException("getNewApplication failed", e);
        } catch (final InstantiationException e) {
            throw new ServletException("getNewApplication failed", e);
        }
    }

    @Override
    protected Class<? extends Application> getApplicationClass() {
        return applicationClass;
    }
}