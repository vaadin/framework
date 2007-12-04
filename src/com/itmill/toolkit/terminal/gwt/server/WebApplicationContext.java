/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal.gwt.server;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.WeakHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.service.ApplicationContext;
import com.itmill.toolkit.ui.Window;

/**
 * Web application context for the IT Mill Toolkit applications.
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.1
 */
public class WebApplicationContext implements ApplicationContext,
        HttpSessionBindingListener {

    private List listeners;

    private final HttpSession session;

    private final WeakHashMap formActions = new WeakHashMap();

    private final HashSet applications = new HashSet();

    private final WebBrowser browser = new WebBrowser();

    /**
     * Creates a new Web Application Context.
     * 
     * @param session
     *                the HTTP session.
     */
    WebApplicationContext(HttpSession session) {
        this.session = session;
    }

    /**
     * Gets the form action for given window.
     * <p>
     * By default, this action is "", which preserves the current url. Commonly
     * this is wanted to be set to <code>application.getUrl.toString</code> or
     * <code>window.getUrl.toString</code> in order to clean any local links
     * or parameters set from the action.
     * </p>
     * 
     * @param window
     *                the Window for which the action is queried.
     * @return the Action to be set into Form action attribute.
     */
    public String getWindowFormAction(Window window) {
        final String action = (String) formActions.get(window);
        return action == null ? "" : action;
    }

    /**
     * Sets the form action for given window.
     * <p>
     * By default, this action is "", which preserves the current url. Commonly
     * this is wanted to be set to <code>application.getUrl.toString</code> or
     * <code>window.getUrl.toString</code> in order to clean any local links
     * or parameters set from the action.
     * </p>
     * 
     * @param window
     *                the Window for which the action is set.
     * @param action
     *                the New action for the window.
     */
    public void setWindowFormAction(Window window, String action) {
        if (action == null || action == "") {
            formActions.remove(window);
        } else {
            formActions.put(window, action);
        }
    }

    /**
     * Gets the application context base directory.
     * 
     * @see com.itmill.toolkit.service.ApplicationContext#getBaseDirectory()
     */
    public File getBaseDirectory() {
        final String realPath = ApplicationServlet.getResourcePath(session
                .getServletContext(), "/");
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
     * Gets the applications in this context.
     * 
     * @see com.itmill.toolkit.service.ApplicationContext#getApplications()
     */
    public Collection getApplications() {

        return Collections.unmodifiableCollection(applications);
    }

    /**
     * Gets the application context for HttpSession.
     * 
     * @param session
     *                the HTTP session.
     * @return the application context for HttpSession.
     */
    static public WebApplicationContext getApplicationContext(
            HttpSession session) {
        WebApplicationContext cx = (WebApplicationContext) session
                .getAttribute(WebApplicationContext.class.getName());
        if (cx == null) {
            cx = new WebApplicationContext(session);
            session.setAttribute(WebApplicationContext.class.getName(), cx);
        }
        return cx;
    }

    /**
     * Returns <code>true</code> if and only if the argument is not
     * <code>null</code> and is a Boolean object that represents the same
     * boolean value as this object.
     * 
     * @param obj
     *                the object to compare with.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        return session.equals(obj);
    }

    /**
     * Returns the hash code value .
     * 
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return session.hashCode();
    }

    /**
     * Adds the transaction listener to this context.
     * 
     * @see com.itmill.toolkit.service.ApplicationContext#addTransactionListener(com.itmill.toolkit.service.ApplicationContext.TransactionListener)
     */
    public void addTransactionListener(TransactionListener listener) {
        if (listeners == null) {
            listeners = new LinkedList();
        }
        listeners.add(listener);
    }

    /**
     * Removes the transaction listener from this context.
     * 
     * @see com.itmill.toolkit.service.ApplicationContext#removeTransactionListener(com.itmill.toolkit.service.ApplicationContext.TransactionListener)
     */
    public void removeTransactionListener(TransactionListener listener) {
        if (listeners != null) {
            listeners.remove(listener);
        }

    }

    /**
     * Notifies the transaction start.
     * 
     * @param application
     * @param request
     *                the HTTP request.
     */
    protected void startTransaction(Application application,
            HttpServletRequest request) {
        if (listeners == null) {
            return;
        }
        for (final Iterator i = listeners.iterator(); i.hasNext();) {
            ((ApplicationContext.TransactionListener) i.next())
                    .transactionStart(application, request);
        }
    }

    /**
     * Notifies the transaction end.
     * 
     * @param application
     * @param request
     *                the HTTP request.
     */
    protected void endTransaction(Application application,
            HttpServletRequest request) {
        if (listeners == null) {
            return;
        }

        LinkedList exceptions = null;
        for (final Iterator i = listeners.iterator(); i.hasNext();) {
            try {
                ((ApplicationContext.TransactionListener) i.next())
                        .transactionEnd(application, request);
            } catch (final RuntimeException t) {
                if (exceptions == null) {
                    exceptions = new LinkedList();
                }
                exceptions.add(t);
            }
        }

        // If any runtime exceptions occurred, throw a combined exception
        if (exceptions != null) {
            final StringBuffer msg = new StringBuffer();
            for (final Iterator i = listeners.iterator(); i.hasNext();) {
                final RuntimeException e = (RuntimeException) i.next();
                if (msg.length() == 0) {
                    msg.append("\n\n--------------------------\n\n");
                }
                msg.append(e.getMessage() + "\n");
                final StringWriter trace = new StringWriter();
                e.printStackTrace(new PrintWriter(trace, true));
                msg.append(trace.toString());
            }
            throw new RuntimeException(msg.toString());
        }
    }

    protected void removeApplication(Application application) {
        applications.remove(application);
    }

    protected void addApplication(Application application) {
        applications.add(application);
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
        // If we are going to be unbound from the session, the session must be
        // closing

        while (!applications.isEmpty()) {
            final Application app = (Application) applications.iterator()
                    .next();
            app.close();
            removeApplication(app);
        }
    }

    /**
     * Get the web browser associated with this application context.
     * 
     * Because application context is related to the http session and server
     * maintains one session per browser-instance, each context has exactly one
     * web browser associated with it.
     * 
     * @return
     */
    public WebBrowser getBrowser() {
        return browser;
    }
}
