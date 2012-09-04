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

import java.io.File;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import com.vaadin.Application;

/**
 * <code>ApplicationContext</code> provides information about the running
 * context of the application. Each context is shared by all applications that
 * are open for one user. In a web-environment this corresponds to a
 * HttpSession.
 * <p>
 * Base class for web application contexts (including portlet contexts) that
 * handles the common tasks.
 * 
 * @author Vaadin Ltd.
 * @since 3.1
 */
public abstract class ApplicationContext implements HttpSessionBindingListener,
        Serializable {

    /**
     * Interface for listening to transaction events. Implement this interface
     * to listen to all transactions between the client and the application.
     * 
     */
    public static interface TransactionListener extends Serializable {

        /**
         * Invoked at the beginning of every transaction.
         * 
         * The transaction is linked to the context, not the application so if
         * you have multiple applications running in the same context you need
         * to check that the request is associated with the application you are
         * interested in. This can be done looking at the application parameter.
         * 
         * @param application
         *            the Application object.
         * @param transactionData
         *            the Data identifying the transaction.
         */
        public void transactionStart(Application application,
                Object transactionData);

        /**
         * Invoked at the end of every transaction.
         * 
         * The transaction is linked to the context, not the application so if
         * you have multiple applications running in the same context you need
         * to check that the request is associated with the application you are
         * interested in. This can be done looking at the application parameter.
         * 
         * @param applcation
         *            the Application object.
         * @param transactionData
         *            the Data identifying the transaction.
         */
        public void transactionEnd(Application application,
                Object transactionData);

    }

    protected Collection<ApplicationContext.TransactionListener> listeners = Collections
            .synchronizedList(new LinkedList<ApplicationContext.TransactionListener>());

    protected final HashSet<Application> applications = new HashSet<Application>();

    protected WebBrowser browser = new WebBrowser();

    protected HashMap<Application, AbstractCommunicationManager> applicationToAjaxAppMgrMap = new HashMap<Application, AbstractCommunicationManager>();

    private long totalSessionTime = 0;

    private long lastRequestTime = -1;

    private transient WrappedSession session;

    /**
     * Adds a transaction listener to this context. The transaction listener is
     * called before and after each each request related to this session except
     * when serving static resources.
     * 
     * The transaction listener must not be null.
     * 
     * @see com.vaadin.service.ApplicationContext#addTransactionListener(com.vaadin.service.ApplicationContext.TransactionListener)
     */
    public void addTransactionListener(
            ApplicationContext.TransactionListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    /**
     * Removes a transaction listener from this context.
     * 
     * @param listener
     *            the listener to be removed.
     * @see ApplicationContext.TransactionListener
     */
    public void removeTransactionListener(
            ApplicationContext.TransactionListener listener) {
        listeners.remove(listener);
    }

    /**
     * Sends a notification that a transaction is starting.
     * 
     * @param application
     *            The application associated with the transaction.
     * @param request
     *            the HTTP or portlet request that triggered the transaction.
     */
    protected void startTransaction(Application application, Object request) {
        ArrayList<ApplicationContext.TransactionListener> currentListeners;
        synchronized (listeners) {
            currentListeners = new ArrayList<ApplicationContext.TransactionListener>(
                    listeners);
        }
        for (ApplicationContext.TransactionListener listener : currentListeners) {
            listener.transactionStart(application, request);
        }
    }

    /**
     * Sends a notification that a transaction has ended.
     * 
     * @param application
     *            The application associated with the transaction.
     * @param request
     *            the HTTP or portlet request that triggered the transaction.
     */
    protected void endTransaction(Application application, Object request) {
        LinkedList<Exception> exceptions = null;

        ArrayList<ApplicationContext.TransactionListener> currentListeners;
        synchronized (listeners) {
            currentListeners = new ArrayList<ApplicationContext.TransactionListener>(
                    listeners);
        }

        for (ApplicationContext.TransactionListener listener : currentListeners) {
            try {
                listener.transactionEnd(application, request);
            } catch (final RuntimeException t) {
                if (exceptions == null) {
                    exceptions = new LinkedList<Exception>();
                }
                exceptions.add(t);
            }
        }

        // If any runtime exceptions occurred, throw a combined exception
        if (exceptions != null) {
            final StringBuffer msg = new StringBuffer();
            for (Exception e : exceptions) {
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

    /**
     * @see javax.servlet.http.HttpSessionBindingListener#valueBound(HttpSessionBindingEvent)
     */
    @Override
    public void valueBound(HttpSessionBindingEvent arg0) {
        // We are not interested in bindings
    }

    /**
     * @see javax.servlet.http.HttpSessionBindingListener#valueUnbound(HttpSessionBindingEvent)
     */
    @Override
    public void valueUnbound(HttpSessionBindingEvent event) {
        // If we are going to be unbound from the session, the session must be
        // closing
        try {
            while (!applications.isEmpty()) {
                final Application app = applications.iterator().next();
                app.close();
                removeApplication(app);
            }
        } catch (Exception e) {
            // This should never happen but is possible with rare
            // configurations (e.g. robustness tests). If you have one
            // thread doing HTTP socket write and another thread trying to
            // remove same application here. Possible if you got e.g. session
            // lifetime 1 min but socket write may take longer than 1 min.
            // FIXME: Handle exception
            getLogger().log(Level.SEVERE,
                    "Could not remove application, leaking memory.", e);
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

    /**
     * Returns a collection of all the applications in this context.
     * 
     * Each application context contains all active applications for one user.
     * 
     * @return A collection containing all the applications in this context.
     */
    public Collection<Application> getApplications() {
        return Collections.unmodifiableCollection(applications);
    }

    protected void removeApplication(Application application) {
        applications.remove(application);
        applicationToAjaxAppMgrMap.remove(application);
    }

    /**
     * @return The total time spent servicing requests in this session.
     */
    public long getTotalSessionTime() {
        return totalSessionTime;
    }

    /**
     * Sets the time spent servicing the last request in the session and updates
     * the total time spent servicing requests in this session.
     * 
     * @param time
     *            the time spent in the last request.
     */
    public void setLastRequestTime(long time) {
        lastRequestTime = time;
        totalSessionTime += time;
    }

    /**
     * @return the time spent servicing the last request in this session.
     */
    public long getLastRequestTime() {
        return lastRequestTime;
    }

    private Logger getLogger() {
        return Logger.getLogger(ApplicationContext.class.getName());
    }

    /**
     * Returns application context base directory.
     * 
     * Typically an application is deployed in a such way that is has an
     * application directory. For web applications this directory is the root
     * directory of the web applications. In some cases applications might not
     * have an application directory (for example web applications running
     * inside a war).
     * 
     * @return The application base directory or null if the application has no
     *         base directory.
     */
    public abstract File getBaseDirectory();

    /**
     * Gets the session to which this application context is currently
     * associated.
     * 
     * @return the wrapped session for this context
     */
    public WrappedSession getSession() {
        return session;
    }

    /**
     * Sets the session to which this application context is currently
     * associated.
     * 
     * @param session
     *            the wrapped session for this context
     */
    public void setSession(WrappedSession session) {
        this.session = session;
    }

}