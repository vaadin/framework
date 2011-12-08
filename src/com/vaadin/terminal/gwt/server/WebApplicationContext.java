/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.server;

import java.io.File;
import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import com.vaadin.Application;

/**
 * Web application context for Vaadin applications.
 * 
 * This is automatically added as a {@link HttpSessionBindingListener} when
 * added to a {@link HttpSession}.
 * 
 * @author Vaadin Ltd.
 * @version
 * @VERSION@
 * @since 3.1
 */
@SuppressWarnings("serial")
public class WebApplicationContext extends AbstractWebApplicationContext {

    protected transient HttpSession session;
    private transient boolean reinitializingSession = false;

    /**
     * Stores a reference to the currentRequest. Null it not inside a request.
     */
    private transient Object currentRequest = null;

    /**
     * Creates a new Web Application Context.
     * 
     */
    protected WebApplicationContext() {

    }

    @Override
    protected void startTransaction(Application application, Object request) {
        currentRequest = request;
        super.startTransaction(application, request);
    }

    @Override
    protected void endTransaction(Application application, Object request) {
        super.endTransaction(application, request);
        currentRequest = null;
    }

    @Override
    public void valueUnbound(HttpSessionBindingEvent event) {
        if (!reinitializingSession) {
            // Avoid closing the application if we are only reinitializing the
            // session. Closing the application would cause the state to be lost
            // and a new application to be created, which is not what we want.
            super.valueUnbound(event);
        }
    }

    /**
     * Discards the current session and creates a new session with the same
     * contents. The purpose of this is to introduce a new session key in order
     * to avoid session fixation attacks.
     */
    @SuppressWarnings("unchecked")
    public void reinitializeSession() {

        HttpSession oldSession = getHttpSession();

        // Stores all attributes (security key, reference to this context
        // instance) so they can be added to the new session
        HashMap<String, Object> attrs = new HashMap<String, Object>();
        for (Enumeration<String> e = oldSession.getAttributeNames(); e
                .hasMoreElements();) {
            String name = e.nextElement();
            attrs.put(name, oldSession.getAttribute(name));
        }

        // Invalidate the current session, set flag to avoid call to
        // valueUnbound
        reinitializingSession = true;
        oldSession.invalidate();
        reinitializingSession = false;

        // Create a new session
        HttpSession newSession = ((HttpServletRequest) currentRequest)
                .getSession();

        // Restores all attributes (security key, reference to this context
        // instance)
        for (String name : attrs.keySet()) {
            newSession.setAttribute(name, attrs.get(name));
        }

        // Update the "current session" variable
        session = newSession;
    }

    /**
     * Gets the application context base directory.
     * 
     * @see com.vaadin.service.ApplicationContext#getBaseDirectory()
     */
    public File getBaseDirectory() {
        final String realPath = ApplicationServlet.getResourcePath(
                session.getServletContext(), "/");
        if (realPath == null) {
            return null;
        }
        return new File(realPath);
    }

    /**
     * Gets the http-session application is running in.
     * 
     * @return HttpSession this application context resides in.
     */
    public HttpSession getHttpSession() {
        return session;
    }

    /**
     * Gets the application context for an HttpSession.
     * 
     * @param session
     *            the HTTP session.
     * @return the application context for HttpSession.
     */
    static public WebApplicationContext getApplicationContext(
            HttpSession session) {
        WebApplicationContext cx = (WebApplicationContext) session
                .getAttribute(WebApplicationContext.class.getName());
        if (cx == null) {
            cx = new WebApplicationContext();
            session.setAttribute(WebApplicationContext.class.getName(), cx);
        }
        if (cx.session == null) {
            cx.session = session;
        }
        return cx;
    }

    protected void addApplication(Application application) {
        applications.add(application);
    }

    /**
     * Gets communication manager for an application.
     * 
     * If this application has not been running before, a new manager is
     * created.
     * 
     * @param application
     * @return CommunicationManager
     */
    public CommunicationManager getApplicationManager(Application application,
            AbstractApplicationServlet servlet) {
        CommunicationManager mgr = (CommunicationManager) applicationToAjaxAppMgrMap
                .get(application);

        if (mgr == null) {
            // Creates new manager
            mgr = servlet.createCommunicationManager(application);
            applicationToAjaxAppMgrMap.put(application, mgr);
        }
        return mgr;
    }

}
