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

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
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
import com.vaadin.service.ApplicationContext;
import com.vaadin.shared.ApplicationConstants;

/**
 * Base class for web application contexts (including portlet contexts) that
 * handles the common tasks.
 */
public abstract class AbstractWebApplicationContext implements
        ApplicationContext, HttpSessionBindingListener, Serializable {

    protected Collection<TransactionListener> listeners = Collections
            .synchronizedList(new LinkedList<TransactionListener>());

    protected final HashSet<Application> applications = new HashSet<Application>();

    protected WebBrowser browser = new WebBrowser();

    protected HashMap<Application, AbstractCommunicationManager> applicationToAjaxAppMgrMap = new HashMap<Application, AbstractCommunicationManager>();

    private long totalSessionTime = 0;

    private long lastRequestTime = -1;

    @Override
    public void addTransactionListener(TransactionListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    @Override
    public void removeTransactionListener(TransactionListener listener) {
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
        ArrayList<TransactionListener> currentListeners;
        synchronized (listeners) {
            currentListeners = new ArrayList<TransactionListener>(listeners);
        }
        for (TransactionListener listener : currentListeners) {
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

        ArrayList<TransactionListener> currentListeners;
        synchronized (listeners) {
            currentListeners = new ArrayList<TransactionListener>(listeners);
        }

        for (TransactionListener listener : currentListeners) {
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

    @Override
    public Collection<Application> getApplications() {
        return Collections.unmodifiableCollection(applications);
    }

    protected void removeApplication(Application application) {
        applications.remove(application);
        applicationToAjaxAppMgrMap.remove(application);
    }

    @Override
    public String generateApplicationResourceURL(ApplicationResource resource,
            String mapKey) {

        final String filename = resource.getFilename();
        if (filename == null) {
            return ApplicationConstants.APP_PROTOCOL_PREFIX
                    + ApplicationConstants.APP_REQUEST_PATH + mapKey + "/";
        } else {
            // #7738 At least Tomcat and JBoss refuses requests containing
            // encoded slashes or backslashes in URLs. Application resource URLs
            // should really be passed in another way than as part of the path
            // in the future.
            String encodedFileName = urlEncode(filename).replace("%2F", "/")
                    .replace("%5C", "\\");
            return ApplicationConstants.APP_PROTOCOL_PREFIX
                    + ApplicationConstants.APP_REQUEST_PATH + mapKey + "/"
                    + encodedFileName;
        }

    }

    static String urlEncode(String filename) {
        try {
            return URLEncoder.encode(filename, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(
                    "UTF-8 charset not available (\"this should never happen\")",
                    e);
        }
    }

    @Override
    public boolean isApplicationResourceURL(URL context, String relativeUri) {
        // If the relative uri is null, we are ready
        if (relativeUri == null) {
            return false;
        }

        // Resolves the prefix
        String prefix = relativeUri;
        final int index = relativeUri.indexOf('/');
        if (index >= 0) {
            prefix = relativeUri.substring(0, index);
        }

        // Handles the resource requests
        return (prefix.equals("APP"));
    }

    @Override
    public String getURLKey(URL context, String relativeUri) {
        final int index = relativeUri.indexOf('/');
        final int next = relativeUri.indexOf('/', index + 1);
        if (next < 0) {
            return null;
        }
        return relativeUri.substring(index + 1, next);
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
        return Logger.getLogger(AbstractWebApplicationContext.class.getName());
    }

}