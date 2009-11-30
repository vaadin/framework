package com.vaadin.terminal.gwt.server;

import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.MimeResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.ResourceURL;
import javax.servlet.http.HttpSessionBindingListener;

import com.vaadin.Application;
import com.vaadin.terminal.ApplicationResource;

/**
 * TODO Write documentation, fix JavaDoc tags.
 *
 * This is automatically registered as a {@link HttpSessionBindingListener} when
 * {@link PortletSession#setAttribute()} is called with the context as value.
 *
 * @author peholmst
 */
@SuppressWarnings("serial")
public class PortletApplicationContext2 extends AbstractWebApplicationContext {

    protected Map<Application, Set<PortletListener>> portletListeners = new HashMap<Application, Set<PortletListener>>();

    protected transient PortletSession session;

    protected HashMap<String, Application> portletWindowIdToApplicationMap = new HashMap<String, Application>();

    private MimeResponse mimeResponse;

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

    protected PortletCommunicationManager getApplicationManager(
            Application application) {
        PortletCommunicationManager mgr = (PortletCommunicationManager) applicationToAjaxAppMgrMap
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

    @Override
    protected void removeApplication(Application application) {
        super.removeApplication(application);
        // values() is backed by map, removes the key-value pair from the map
        portletWindowIdToApplicationMap.values().remove(application);
    }

    protected void addApplication(Application application,
            String portletWindowId) {
        applications.add(application);
        portletWindowIdToApplicationMap.put(portletWindowId, application);
    }

    public Application getApplicationForWindowId(String portletWindowId) {
        return portletWindowIdToApplicationMap.get(portletWindowId);
    }

    public PortletSession getPortletSession() {
        return session;
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

    /**
     * This is for use by {@link AbstractApplicationPortlet} only.
     *
     * TODO cleaner implementation, now "semi-static"!
     *
     * @param mimeResponse
     */
    void setMimeResponse(MimeResponse mimeResponse) {
        this.mimeResponse = mimeResponse;
    }

    @Override
    public String generateApplicationResourceURL(
            ApplicationResource resource,
            String mapKey) {
        ResourceURL resourceURL = mimeResponse.createResourceURL();
        final String filename = resource.getFilename();
        if (filename == null) {
            resourceURL.setResourceID("APP/" + mapKey + "/");
        } else {
            resourceURL.setResourceID("APP/" + mapKey + "/" + filename);
        }
        return resourceURL.toString();
    }

}
