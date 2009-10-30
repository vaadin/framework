package com.vaadin.terminal.gwt.server;

import java.io.File;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import com.vaadin.Application;
import com.vaadin.service.ApplicationContext;

/**
 * TODO Write documentation, fix JavaDoc tags.
 * 
 * @author peholmst
 */
@SuppressWarnings("serial")
public class PortletApplicationContext2 implements ApplicationContext,
        HttpSessionBindingListener, Serializable {

    protected LinkedList<TransactionListener> listeners;

    protected transient PortletSession session;

    protected final HashSet<Application> applications = new HashSet<Application>();

    protected WebBrowser browser = new WebBrowser();

    protected HashMap<Application, PortletCommunicationManager> applicationToAjaxAppMgrMap = new HashMap<Application, PortletCommunicationManager>();

    @Override
    public void addTransactionListener(TransactionListener listener) {
        if (listeners == null) {
            listeners = new LinkedList<TransactionListener>();
        }
        listeners.add(listener);
    }

    @Override
    public Collection<Application> getApplications() {
        return Collections.unmodifiableCollection(applications);
    }

    @Override
    public File getBaseDirectory() {
        String resultPath = session.getPortletContext().getRealPath("/");
        if (resultPath != null) {
            return new File(resultPath);
        } else {
            try {
                final URL url = session.getPortletContext().getResource("/");
                return new File(url.getFile());
            } catch (final Exception e) {
                // FIXME: Handle exception
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public void removeTransactionListener(TransactionListener listener) {
        if (listeners != null) {
            listeners.remove(listener);
        }
    }

    protected PortletCommunicationManager getApplicationManager(
            Application application) {
        PortletCommunicationManager mgr = applicationToAjaxAppMgrMap
                .get(application);

        if (mgr == null) {
            // Creates a new manager
            // TODO Use a factory instead
            mgr = new PortletCommunicationManagerImpl();
            applicationToAjaxAppMgrMap.put(application, mgr);
        }
        return mgr;
    }

    public static PortletApplicationContext2 getApplicationContext(
            PortletSession session) {
        PortletApplicationContext2 cx = (PortletApplicationContext2) session
                .getAttribute(PortletApplicationContext2.class.getName());
        if (cx == null) {
            cx = new PortletApplicationContext2();
            session
                    .setAttribute(PortletApplicationContext2.class.getName(),
                            cx);
        }
        if (cx.session == null) {
            cx.session = session;
        }
        return cx;
    }

    public WebBrowser getBrowser() {
        return browser;
    }

    @SuppressWarnings("unchecked")
    protected void startTransaction(Application application,
            PortletRequest request) {
        if (listeners == null) {
            return;
        }
        for (TransactionListener listener : (LinkedList<TransactionListener>) listeners
                .clone()) {
            listener.transactionStart(application, request);
        }
    }

    @SuppressWarnings("unchecked")
    protected void endTransaction(Application application,
            PortletRequest request) {
        if (listeners == null) {
            return;
        }

        LinkedList<Exception> exceptions = null;
        for (TransactionListener listener : (LinkedList<TransactionListener>) listeners
                .clone()) {
            try {
                listener.transactionEnd(application, request);
            } catch (final RuntimeException e) {
                if (exceptions == null) {
                    exceptions = new LinkedList<Exception>();
                }
                exceptions.add(e);
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

    protected void removeApplication(Application application) {
        applications.remove(application);
    }

    protected void addApplication(Application application) {
        applications.add(application);
    }

    public PortletSession getPortletSession() {
        return session;
    }

    @Override
    public void valueBound(HttpSessionBindingEvent event) {
        // We are not interested in bindings
    }

    public void valueUnbound(HttpSessionBindingEvent event) {
        // If we are going to be unbound from the session, the session must be
        // closing
        try {
            while (!applications.isEmpty()) {
                final Application app = applications.iterator().next();
                app.close();
                applicationToAjaxAppMgrMap.remove(app);
                removeApplication(app);
            }
        } catch (Exception e) {
            // FIXME: Handle exception
            System.err.println("Could not remove application, leaking memory.");
            e.printStackTrace();
        }
    }
}
