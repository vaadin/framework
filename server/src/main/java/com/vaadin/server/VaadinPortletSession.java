/*
 * Copyright 2000-2016 Vaadin Ltd.
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

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

import com.vaadin.server.communication.PortletListenerNotifier;
import com.vaadin.shared.Registration;
import com.vaadin.ui.UI;
import com.vaadin.util.CurrentInstance;

/**
 * An implementation of {@link VaadinSession} for JSR-286 portlet environments.
 *
 * This is automatically registered as a {@link HttpSessionBindingListener} when
 * {@link PortletSession#setAttribute()} is called with the context as value.
 *
 * Only the documented parts of this class should be considered as stable public
 * API.
 *
 * Note also that some methods and/or nested interfaces might move to
 * {@link VaadinPortletService} in future minor or major versions of Vaadin. In
 * these cases, a deprecated redirection for backwards compatibility will be
 * used in VaadinPortletSession for a transition period.
 *
 * @since 7.0
 */
@SuppressWarnings("serial")
public class VaadinPortletSession extends VaadinSession {

    private final Set<PortletListener> portletListeners = new LinkedHashSet<>();

    private final Map<String, QName> eventActionDestinationMap = new HashMap<>();
    private final Map<String, Serializable> eventActionValueMap = new HashMap<>();

    private final Map<String, String> sharedParameterActionNameMap = new HashMap<>();
    private final Map<String, String> sharedParameterActionValueMap = new HashMap<>();

    /**
     * Create a portlet service session for the given portlet service
     *
     * @param service
     *            the portlet service to which the new session belongs
     */
    public VaadinPortletSession(VaadinPortletService service) {
        super(service);
    }

    /**
     * Returns the underlying portlet session.
     *
     * @return portlet session
     */
    public PortletSession getPortletSession() {
        WrappedSession wrappedSession = getSession();
        PortletSession session = ((WrappedPortletSession) wrappedSession)
                .getPortletSession();
        return session;
    }

    private PortletResponse getCurrentResponse() {
        VaadinPortletResponse currentResponse = (VaadinPortletResponse) CurrentInstance
                .get(VaadinResponse.class);

        if (currentResponse != null) {
            return currentResponse.getPortletResponse();
        } else {
            return null;
        }
    }

    /**
     * Returns the JSR-286 portlet configuration that provides access to the
     * portlet context and init parameters.
     *
     * @return portlet configuration
     */
    public PortletConfig getPortletConfig() {
        VaadinPortletResponse response = (VaadinPortletResponse) CurrentInstance
                .get(VaadinResponse.class);
        return response.getService().getPortlet().getPortletConfig();
    }

    /**
     * Adds a listener for various types of portlet requests.
     *
     * @param listener
     *            to add
     * @since 8.0
     */
    public Registration addPortletListener(PortletListener listener) {
        portletListeners.add(listener);
        return () -> portletListeners.remove(listener);
    }

    /**
     * Removes a portlet request listener registered with
     * {@link #addPortletListener(PortletListener)}.
     *
     * @param listener
     *            to remove
     * @deprecated Use a {@link Registration} object returned by
     *             {@link #addPortletListener(PortletListener)} to remove a
     *             listener
     */
    @Deprecated
    public void removePortletListener(PortletListener listener) {
        portletListeners.remove(listener);
    }

    /**
     * For internal use by the framework only - API subject to change.
     */
    public void firePortletRenderRequest(UI uI, RenderRequest request,
            RenderResponse response) {
        for (PortletListener l : new ArrayList<>(portletListeners)) {
            l.handleRenderRequest(request,
                    new RestrictedRenderResponse(response), uI);
        }
    }

    /**
     * For internal use by the framework only - API subject to change.
     */
    public void firePortletActionRequest(UI uI, ActionRequest request,
            ActionResponse response) {
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
            for (PortletListener l : new ArrayList<>(portletListeners)) {
                l.handleActionRequest(request, response, uI);
            }
        }
    }

    /**
     * For internal use by the framework only - API subject to change.
     */
    public void firePortletEventRequest(UI uI, EventRequest request,
            EventResponse response) {
        for (PortletListener l : new ArrayList<>(portletListeners)) {
            l.handleEventRequest(request, response, uI);
        }
    }

    /**
     * For internal use by the framework only - API subject to change.
     */
    public void firePortletResourceRequest(UI uI, ResourceRequest request,
            ResourceResponse response) {
        for (PortletListener l : new ArrayList<>(portletListeners)) {
            l.handleResourceRequest(request, response, uI);
        }
    }

    /**
     * Listener interface for the various types of JSR-286 portlet requests. The
     * listener methods are called by the request handler
     * {@link PortletListenerNotifier} after the session is locked and the
     * corresponding UI has been found (if already created) but before other
     * request processing takes place.
     *
     * Direct rendering of output is not possible in a portlet listener and the
     * JSR-286 limitations on allowed operations in each phase or portlet
     * request processing must be respected by the listeners.
     *
     * Note that internal action requests used by the framework to trigger
     * events or set shared parameters do not call the action request listener
     * but will result in a later event or render request that will trigger the
     * corresponding listener.
     */
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
     * Creating an action URL is only supported when processing a suitable
     * request (render or resource request, including normal Vaadin UIDL
     * processing) and will return null if not processing a suitable request.
     *
     * @param action
     *            the action parameter (javax.portlet.action parameter value in
     *            JSR-286)
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
     * Sending portlet events from background threads is not supported.
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
                actionKey += ".";
            }
            PortletURL actionUrl = generateActionURL(actionKey);
            if (actionUrl != null) {
                eventActionDestinationMap.put(actionKey, name);
                eventActionValueMap.put(actionKey, value);
                uI.getPage().setLocation(actionUrl.toString());
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
     * Setting shared render parameters from background threads is not
     * supported.
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
                actionKey += ".";
            }
            PortletURL actionUrl = generateActionURL(actionKey);
            if (actionUrl != null) {
                sharedParameterActionNameMap.put(actionKey, name);
                sharedParameterActionValueMap.put(actionKey, value);
                uI.getPage().setLocation(actionUrl.toString());
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
     * Currently, this is only supported when working with a
     * {@link StateAwareResponse} (an action request or an event request).
     * Portlet mode change in background threads is not supported.
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
     * @throws IllegalStateException
     *             if not processing a request of the correct type
     */
    public void setPortletMode(UI uI, PortletMode portletMode)
            throws IllegalStateException, PortletModeException {
        PortletResponse response = getCurrentResponse();
        if (response instanceof MimeResponse) {
            PortletURL url = ((MimeResponse) response).createRenderURL();
            url.setPortletMode(portletMode);
            throw new IllegalStateException(
                    "Portlet mode change is currently only supported when processing event and action requests");
            // UI.open(new ExternalResource(url.toString()));
        } else if (response instanceof StateAwareResponse) {
            ((StateAwareResponse) response).setPortletMode(portletMode);
        } else {
            throw new IllegalStateException(
                    "Portlet mode can only be changed from a portlet request");
        }
    }

}
