/*
@ITMillApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.server;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import com.vaadin.Application;
import com.vaadin.service.ApplicationContext;
import com.vaadin.terminal.ApplicationResource;

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

    public void addTransactionListener(TransactionListener listener) {
        listeners.add(listener);
    }

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
        synchronized (listeners) {
            for (TransactionListener listener : listeners) {
                listener.transactionStart(application, request);
            }
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

        synchronized (listeners) {
            for (TransactionListener listener : listeners) {
                try {
                    listener.transactionEnd(application, request);
                } catch (final RuntimeException t) {
                    if (exceptions == null) {
                        exceptions = new LinkedList<Exception>();
                    }
                    exceptions.add(t);
                }
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
    public void valueBound(HttpSessionBindingEvent arg0) {
        // We are not interested in bindings
    }

    /**
     * @see javax.servlet.http.HttpSessionBindingListener#valueUnbound(HttpSessionBindingEvent)
     */
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
            System.err.println("Could not remove application, leaking memory.");
            e.printStackTrace();
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

    public Collection<Application> getApplications() {
        return Collections.unmodifiableCollection(applications);
    }

    protected void removeApplication(Application application) {
        applications.remove(application);
        applicationToAjaxAppMgrMap.remove(application);
    }

    public String generateApplicationResourceURL(ApplicationResource resource,
            String mapKey) {

        final String filename = resource.getFilename();
        if (filename == null) {
            return "APP/" + mapKey + "/";
        } else {
            return "APP/" + mapKey + "/" + filename;
        }

    }

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

    public String getURLKey(URL context, String relativeUri) {
        final int index = relativeUri.indexOf('/');
        final int next = relativeUri.indexOf('/', index + 1);
        if (next < 0) {
            return null;
        }
        return relativeUri.substring(index + 1, next);
    }

}