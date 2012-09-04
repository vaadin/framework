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
import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.MimeResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletMode;
import javax.portlet.PortletModeException;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.StateAwareResponse;
import javax.servlet.http.HttpSessionBindingListener;
import javax.xml.namespace.QName;

import com.vaadin.Application;
import com.vaadin.ui.UI;
import com.vaadin.util.CurrentInstance;

/**
 * TODO Write documentation, fix JavaDoc tags.
 * 
 * This is automatically registered as a {@link HttpSessionBindingListener} when
 * {@link PortletSession#setAttribute()} is called with the context as value.
 * 
 * @author peholmst
 */
@SuppressWarnings("serial")
public class PortletApplicationContext2 extends ApplicationContext {

    protected Map<Application, Set<PortletListener>> portletListeners = new HashMap<Application, Set<PortletListener>>();

    protected HashMap<String, Application> portletWindowIdToApplicationMap = new HashMap<String, Application>();

    private final Map<String, QName> eventActionDestinationMap = new HashMap<String, QName>();
    private final Map<String, Serializable> eventActionValueMap = new HashMap<String, Serializable>();

    private final Map<String, String> sharedParameterActionNameMap = new HashMap<String, String>();
    private final Map<String, String> sharedParameterActionValueMap = new HashMap<String, String>();

    @Override
    public File getBaseDirectory() {
        PortletSession session = getPortletSession();
        String resultPath = session.getPortletContext().getRealPath("/");
        if (resultPath != null) {
            return new File(resultPath);
        } else {
            try {
                final URL url = session.getPortletContext().getResource("/");
                return new File(url.getFile());
            } catch (final Exception e) {
                // FIXME: Handle exception
                getLogger()
                        .log(Level.INFO,
                                "Cannot access base directory, possible security issue "
                                        + "with Application Server or Servlet Container",
                                e);
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
            mgr = createPortletCommunicationManager(application);
            applicationToAjaxAppMgrMap.put(application, mgr);
        }
        return mgr;
    }

    protected PortletCommunicationManager createPortletCommunicationManager(
            Application application) {
        return new PortletCommunicationManager(application);
    }

    public static PortletApplicationContext2 getApplicationContext(
            PortletSession session) {
        Object cxattr = session.getAttribute(PortletApplicationContext2.class
                .getName());
        PortletApplicationContext2 cx = null;
        // can be false also e.g. if old context comes from another
        // classloader when using
        // <private-session-attributes>false</private-session-attributes>
        // and redeploying the portlet - see #7461
        if (cxattr instanceof PortletApplicationContext2) {
            cx = (PortletApplicationContext2) cxattr;
        }
        if (cx == null) {
            cx = new PortletApplicationContext2();
            session.setAttribute(PortletApplicationContext2.class.getName(), cx);
        }
        cx.setSession(new WrappedPortletSession(session));
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
        WrappedSession wrappedSession = getSession();
        PortletSession session = ((WrappedPortletSession) wrappedSession)
                .getPortletSession();
        return session;
    }

    private PortletResponse getCurrentResponse() {
        WrappedPortletResponse currentResponse = (WrappedPortletResponse) CurrentInstance
                .get(WrappedResponse.class);

        if (currentResponse != null) {
            return currentResponse.getPortletResponse();
        } else {
            return null;
        }
    }

    public PortletConfig getPortletConfig() {
        WrappedPortletResponse response = (WrappedPortletResponse) CurrentInstance
                .get(WrappedResponse.class);
        return response.getDeploymentConfiguration().getPortlet()
                .getPortletConfig();
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

    public void firePortletRenderRequest(Application app, UI uI,
            RenderRequest request, RenderResponse response) {
        Set<PortletListener> listeners = portletListeners.get(app);
        if (listeners != null) {
            for (PortletListener l : listeners) {
                l.handleRenderRequest(request, new RestrictedRenderResponse(
                        response), uI);
            }
        }
    }

    public void firePortletActionRequest(Application app, UI uI,
            ActionRequest request, ActionResponse response) {
        String key = request.getParameter(ActionRequest.ACTION_NAME);
        if (eventActionDestinationMap.containsKey(key)) {
            // this action request is only to send queued portlet events
            response.setEvent(eventActionDestinationMap.get(key),
                    eventActionValueMap.get(key));
            // cleanup
            eventActionDestinationMap.remove(key);
            eventActionValueMap.remove(key);
        } else if (sharedParameterActionNameMap.containsKey(key)) {
            // this action request is only to set shared render parameters
            response.setRenderParameter(sharedParameterActionNameMap.get(key),
                    sharedParameterActionValueMap.get(key));
            // cleanup
            sharedParameterActionNameMap.remove(key);
            sharedParameterActionValueMap.remove(key);
        } else {
            // normal action request, notify listeners
            Set<PortletListener> listeners = portletListeners.get(app);
            if (listeners != null) {
                for (PortletListener l : listeners) {
                    l.handleActionRequest(request, response, uI);
                }
            }
        }
    }

    public void firePortletEventRequest(Application app, UI uI,
            EventRequest request, EventResponse response) {
        Set<PortletListener> listeners = portletListeners.get(app);
        if (listeners != null) {
            for (PortletListener l : listeners) {
                l.handleEventRequest(request, response, uI);
            }
        }
    }

    public void firePortletResourceRequest(Application app, UI uI,
            ResourceRequest request, ResourceResponse response) {
        Set<PortletListener> listeners = portletListeners.get(app);
        if (listeners != null) {
            for (PortletListener l : listeners) {
                l.handleResourceRequest(request, response, uI);
            }
        }
    }

    public interface PortletListener extends Serializable {

        public void handleRenderRequest(RenderRequest request,
                RenderResponse response, UI uI);

        public void handleActionRequest(ActionRequest request,
                ActionResponse response, UI uI);

        public void handleEventRequest(EventRequest request,
                EventResponse response, UI uI);

        public void handleResourceRequest(ResourceRequest request,
                ResourceResponse response, UI uI);
    }

    /**
     * Creates a new action URL.
     * 
     * @param action
     * @return action URL or null if called outside a MimeRequest (outside a
     *         UIDL request or similar)
     */
    public PortletURL generateActionURL(String action) {
        PortletURL url = null;
        PortletResponse response = getCurrentResponse();
        if (response instanceof MimeResponse) {
            url = ((MimeResponse) response).createActionURL();
            url.setParameter("javax.portlet.action", action);
        } else {
            return null;
        }
        return url;
    }

    /**
     * Sends a portlet event to the indicated destination.
     * 
     * Internally, an action may be created and opened, as an event cannot be
     * sent directly from all types of requests.
     * 
     * The event destinations and values need to be kept in the context until
     * sent. Any memory leaks if the action fails are limited to the session.
     * 
     * Event names for events sent and received by a portlet need to be declared
     * in portlet.xml .
     * 
     * @param uI
     *            a window in which a temporary action URL can be opened if
     *            necessary
     * @param name
     *            event name
     * @param value
     *            event value object that is Serializable and, if appropriate,
     *            has a valid JAXB annotation
     */
    public void sendPortletEvent(UI uI, QName name, Serializable value)
            throws IllegalStateException {
        PortletResponse response = getCurrentResponse();
        if (response instanceof MimeResponse) {
            String actionKey = "" + System.currentTimeMillis();
            while (eventActionDestinationMap.containsKey(actionKey)) {
                actionKey = actionKey + ".";
            }
            PortletURL actionUrl = generateActionURL(actionKey);
            if (actionUrl != null) {
                eventActionDestinationMap.put(actionKey, name);
                eventActionValueMap.put(actionKey, value);
                uI.getPage().open(new ExternalResource(actionUrl.toString()));
            } else {
                // this should never happen as we already know the response is a
                // MimeResponse
                throw new IllegalStateException(
                        "Portlet events can only be sent from a portlet request");
            }
        } else if (response instanceof StateAwareResponse) {
            ((StateAwareResponse) response).setEvent(name, value);
        } else {
            throw new IllegalStateException(
                    "Portlet events can only be sent from a portlet request");
        }
    }

    /**
     * Sets a shared portlet parameter.
     * 
     * Internally, an action may be created and opened, as shared parameters
     * cannot be set directly from all types of requests.
     * 
     * The parameters and values need to be kept in the context until sent. Any
     * memory leaks if the action fails are limited to the session.
     * 
     * Shared parameters set or read by a portlet need to be declared in
     * portlet.xml .
     * 
     * @param uI
     *            a window in which a temporary action URL can be opened if
     *            necessary
     * @param name
     *            parameter identifier
     * @param value
     *            parameter value
     */
    public void setSharedRenderParameter(UI uI, String name, String value)
            throws IllegalStateException {
        PortletResponse response = getCurrentResponse();
        if (response instanceof MimeResponse) {
            String actionKey = "" + System.currentTimeMillis();
            while (sharedParameterActionNameMap.containsKey(actionKey)) {
                actionKey = actionKey + ".";
            }
            PortletURL actionUrl = generateActionURL(actionKey);
            if (actionUrl != null) {
                sharedParameterActionNameMap.put(actionKey, name);
                sharedParameterActionValueMap.put(actionKey, value);
                uI.getPage().open(new ExternalResource(actionUrl.toString()));
            } else {
                // this should never happen as we already know the response is a
                // MimeResponse
                throw new IllegalStateException(
                        "Shared parameters can only be set from a portlet request");
            }
        } else if (response instanceof StateAwareResponse) {
            ((StateAwareResponse) response).setRenderParameter(name, value);
        } else {
            throw new IllegalStateException(
                    "Shared parameters can only be set from a portlet request");
        }
    }

    /**
     * Sets the portlet mode. This may trigger a new render request.
     * 
     * Portlet modes used by a portlet need to be declared in portlet.xml .
     * 
     * @param uI
     *            a window in which the render URL can be opened if necessary
     * @param portletMode
     *            the portlet mode to switch to
     * @throws PortletModeException
     *             if the portlet mode is not allowed for some reason
     *             (configuration, permissions etc.)
     */
    public void setPortletMode(UI uI, PortletMode portletMode)
            throws IllegalStateException, PortletModeException {
        PortletResponse response = getCurrentResponse();
        if (response instanceof MimeResponse) {
            PortletURL url = ((MimeResponse) response).createRenderURL();
            url.setPortletMode(portletMode);
            throw new RuntimeException("UI.open has not yet been implemented");
            // UI.open(new ExternalResource(url.toString()));
        } else if (response instanceof StateAwareResponse) {
            ((StateAwareResponse) response).setPortletMode(portletMode);
        } else {
            throw new IllegalStateException(
                    "Portlet mode can only be changed from a portlet request");
        }
    }

    private Logger getLogger() {
        return Logger.getLogger(PortletApplicationContext2.class.getName());
    }
}
