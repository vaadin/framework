package com.vaadin.terminal.gwt.server;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.CacheControl;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.PortletMode;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.ResourceURL;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;

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

    protected Map<Application, Set<PortletListener>> portletListeners = new HashMap<Application, Set<PortletListener>>();

    protected transient PortletSession session;

    protected final HashSet<Application> applications = new HashSet<Application>();

    protected WebBrowser browser = new WebBrowser();

    protected HashMap<Application, PortletCommunicationManager> applicationToAjaxAppMgrMap = new HashMap<Application, PortletCommunicationManager>();

    public void addTransactionListener(TransactionListener listener) {
        if (listeners == null) {
            listeners = new LinkedList<TransactionListener>();
        }
        listeners.add(listener);
    }

    public Collection<Application> getApplications() {
        return Collections.unmodifiableCollection(applications);
    }

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
            mgr = new PortletCommunicationManager(application);
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

    public void addPortletListener(Application app, PortletListener listener) {
        Set<PortletListener> l = portletListeners.get(app);
        if (l == null) {
            l = new LinkedHashSet<PortletListener>();
            portletListeners.put(app, l);
        }
        l.add(listener);
    }

    public void removePortletListener(Application app, PortletListener listener) {
        Set<PortletListener> l = portletListeners.get(app);
        if (l != null) {
            l.remove(listener);
        }
    }

    public void firePortletRenderRequest(Application app,
            RenderRequest request, RenderResponse response) {
        Set<PortletListener> listeners = portletListeners.get(app);
        if (listeners != null) {
            for (PortletListener l : listeners) {
                l.handleRenderRequest(request, new RestrictedRenderResponse(response));
            }
        }
    }

    public void firePortletActionRequest(Application app,
            ActionRequest request, ActionResponse response) {
        Set<PortletListener> listeners = portletListeners.get(app);
        if (listeners != null) {
            for (PortletListener l : listeners) {
                l.handleActionRequest(request, response);
            }
        }
    }

    public void firePortletEventRequest(Application app, EventRequest request,
            EventResponse response) {
        Set<PortletListener> listeners = portletListeners.get(app);
        if (listeners != null) {
            for (PortletListener l : listeners) {
                l.handleEventRequest(request, response);
            }
        }
    }

    public void firePortletResourceRequest(Application app,
            ResourceRequest request, ResourceResponse response) {
        Set<PortletListener> listeners = portletListeners.get(app);
        if (listeners != null) {
            for (PortletListener l : listeners) {
                l.handleResourceRequest(request, response);
            }
        }
    }

    public interface PortletListener extends Serializable {

        public void handleRenderRequest(RenderRequest request,
                RenderResponse response);

        public void handleActionRequest(ActionRequest request,
                ActionResponse response);

        public void handleEventRequest(EventRequest request,
                EventResponse response);

        public void handleResourceRequest(ResourceRequest request,
                ResourceResponse response);
    }

    private class RestrictedRenderResponse implements RenderResponse,
            Serializable {

        private RenderResponse response;

        private RestrictedRenderResponse(RenderResponse response) {
            this.response = response;
        }

        public void addProperty(String key, String value) {
            response.addProperty(key, value);
        }

        public PortletURL createActionURL() {
            return response.createActionURL();
        }

        public PortletURL createRenderURL() {
            return response.createRenderURL();
        }

        public String encodeURL(String path) {
            return response.encodeURL(path);
        }

        public void flushBuffer() throws IOException {
            // NOP
            // TODO throw?
        }

        public int getBufferSize() {
            return response.getBufferSize();
        }

        public String getCharacterEncoding() {
            return response.getCharacterEncoding();
        }

        public String getContentType() {
            return response.getContentType();
        }

        public Locale getLocale() {
            return response.getLocale();
        }

        public String getNamespace() {
            return response.getNamespace();
        }

        public OutputStream getPortletOutputStream() throws IOException {
            // write forbidden
            return null;
        }

        public PrintWriter getWriter() throws IOException {
            // write forbidden
            return null;
        }

        public boolean isCommitted() {
            return response.isCommitted();
        }

        public void reset() {
            // NOP
            // TODO throw?
        }

        public void resetBuffer() {
            // NOP
            // TODO throw?
        }

        public void setBufferSize(int size) {
            // NOP
            // TODO throw?
        }

        public void setContentType(String type) {
            // NOP
            // TODO throw?
        }

        public void setProperty(String key, String value) {
            response.setProperty(key, value);
        }

        public void setTitle(String title) {
            response.setTitle(title);
        }

        public void setNextPossiblePortletModes(
                Collection<PortletMode> portletModes) {
            // NOP
            // TODO throw?
        }

        public ResourceURL createResourceURL() {
            return response.createResourceURL();
        }

        public CacheControl getCacheControl() {
            return response.getCacheControl();
        }

        public void addProperty(Cookie cookie) {
            // NOP
            // TODO throw?
        }

        public void addProperty(String key, Element element) {
            // NOP
            // TODO throw?
        }

        public Element createElement(String tagName) throws DOMException {
            // NOP
            return null;
        }
    }

}
