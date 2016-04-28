/*
 * Copyright 2000-2014 Vaadin Ltd.
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
package com.vaadin.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Widget;

public class ConnectorMap {

    public static ConnectorMap get(ApplicationConnection applicationConnection) {
        return applicationConnection.getConnectorMap();
    }

    @Deprecated
    private final ComponentDetailMap idToComponentDetail = ComponentDetailMap
            .create();

    /**
     * Returns a {@link ServerConnector} by its id
     * 
     * @param id
     *            The connector id
     * @return A connector or null if a connector with the given id has not been
     *         registered
     */
    public ServerConnector getConnector(String connectorId) {
        ComponentDetail componentDetail = idToComponentDetail.get(connectorId);
        if (componentDetail == null) {
            return null;
        } else {
            return componentDetail.getConnector();
        }
    }

    /**
     * Returns a {@link ComponentConnector} element by its root element.
     * 
     * @param element
     *            Root element of the {@link ComponentConnector}
     * @return A connector or null if a connector with the given id has not been
     *         registered
     */
    public ComponentConnector getConnector(Element element) {
        ServerConnector connector = getConnector(getConnectorId(element));
        if (!(connector instanceof ComponentConnector)) {
            // This can happen at least if element is not part of this
            // application but is part of another application and the connector
            // id happens to map to e.g. an extension in this application
            return null;
        }

        // Ensure this connector is really connected to the element. We cannot
        // be sure of this otherwise as the id comes from the DOM and could be
        // part of another application.
        ComponentConnector cc = (ComponentConnector) connector;
        if (cc.getWidget() == null || cc.getWidget().getElement() != element) {
            return null;
        }

        return cc;
    }

    /**
     * FIXME: What does this even do and why?
     * 
     * @param pid
     * @return
     */
    public boolean isDragAndDropPaintable(String pid) {
        return (pid.startsWith("DD"));
    }

    /**
     * Checks if a connector with the given id has been registered.
     * 
     * @param connectorId
     *            The id to check for
     * @return true if a connector has been registered with the given id, false
     *         otherwise
     */
    public boolean hasConnector(String connectorId) {
        return idToComponentDetail.containsKey(connectorId);
    }

    /**
     * Removes all registered connectors
     */
    public void clear() {
        idToComponentDetail.clear();
    }

    /**
     * Retrieves the connector whose widget matches the parameter.
     * 
     * @param widget
     *            The widget
     * @return A connector with {@literal widget} as its root widget or null if
     *         no connector was found
     */
    public ComponentConnector getConnector(Widget widget) {
        return widget == null ? null : getConnector(widget.getElement());
    }

    public void registerConnector(String id, ServerConnector connector) {
        Profiler.enter("ConnectorMap.registerConnector");
        ComponentDetail componentDetail = GWT.create(ComponentDetail.class);
        idToComponentDetail.put(id, componentDetail);
        componentDetail.setConnector(connector);
        if (connector instanceof ComponentConnector) {
            ComponentConnector pw = (ComponentConnector) connector;
            Widget widget = pw.getWidget();
            Profiler.enter("ConnectorMap.setConnectorId");
            setConnectorId(widget.getElement(), id);
            Profiler.leave("ConnectorMap.setConnectorId");
        }
        Profiler.leave("ConnectorMap.registerConnector");
    }

    private static native void setConnectorId(Element el, String id)
    /*-{
        el.tkPid = id;
    }-*/;

    /**
     * Gets the connector id using a DOM element - the element should be the
     * root element for a connector, otherwise no id will be found. Use
     * {@link #getConnectorId(ServerConnector)} instead whenever possible.
     * 
     * @see #getConnectorId(ServerConnector)
     * @param el
     *            element of the connector whose id is desired
     * @return the id of the element's connector, if it's a connector
     */
    native static final String getConnectorId(Element el)
    /*-{
        return el.tkPid;
    }-*/;

    /**
     * Gets the main element for the connector with the given id. The reverse of
     * {@link #getConnectorId(Element)}.
     * 
     * @param connectorId
     *            the id of the widget whose element is desired
     * @return the element for the connector corresponding to the id
     */
    public Element getElement(String connectorId) {
        ServerConnector p = getConnector(connectorId);
        if (p instanceof ComponentConnector) {
            return ((ComponentConnector) p).getWidget().getElement();
        }

        return null;
    }

    /**
     * Unregisters the given connector; always use after removing a connector.
     * This method does not remove the connector from the DOM, but marks the
     * connector so that ApplicationConnection may clean up its references to
     * it. Removing the widget from DOM is component containers responsibility.
     * 
     * @param connector
     *            the connector to remove
     */
    public void unregisterConnector(ServerConnector connector) {
        if (connector == null) {
            getLogger().severe("Trying to unregister null connector");
            return;
        }

        String connectorId = connector.getConnectorId();

        idToComponentDetail.remove(connectorId);
        connector.onUnregister();

        for (ServerConnector child : connector.getChildren()) {
            if (child.getParent() == connector) {
                /*
                 * Only unregister children that are actually connected to this
                 * parent. For instance when moving connectors from one layout
                 * to another and removing the first layout it will still
                 * contain references to its old children, which are now
                 * attached to another connector.
                 */
                unregisterConnector(child);
            }
        }
    }

    /**
     * Gets all registered {@link ComponentConnector} instances
     * 
     * @return An array of all registered {@link ComponentConnector} instances
     * 
     * @deprecated As of 7.0.1, use {@link #getComponentConnectorsAsJsArray()}
     *             for better performance.
     */
    @Deprecated
    public ComponentConnector[] getComponentConnectors() {
        ArrayList<ComponentConnector> result = new ArrayList<ComponentConnector>();

        JsArrayObject<ServerConnector> connectors = getConnectorsAsJsArray();
        int size = connectors.size();

        for (int i = 0; i < size; i++) {
            ServerConnector connector = connectors.get(i);
            if (connector instanceof ComponentConnector) {
                result.add((ComponentConnector) connector);
            }
        }

        return result.toArray(new ComponentConnector[result.size()]);
    }

    public JsArrayObject<ComponentConnector> getComponentConnectorsAsJsArray() {
        JsArrayObject<ComponentConnector> result = JavaScriptObject
                .createArray().cast();

        JsArrayObject<ServerConnector> connectors = getConnectorsAsJsArray();
        int size = connectors.size();
        for (int i = 0; i < size; i++) {
            ServerConnector connector = connectors.get(i);
            if (connector instanceof ComponentConnector) {
                result.add((ComponentConnector) connector);
            }
        }

        return result;
    }

    @Deprecated
    private ComponentDetail getComponentDetail(
            ComponentConnector componentConnector) {
        return idToComponentDetail.get(componentConnector.getConnectorId());
    }

    public int size() {
        return idToComponentDetail.size();
    }

    /**
     * @return
     * 
     * @deprecated As of 7.0.1, use {@link #getConnectorsAsJsArray()} for
     *             improved performance.
     */
    @Deprecated
    public Collection<? extends ServerConnector> getConnectors() {
        Collection<ComponentDetail> values = idToComponentDetail.values();
        ArrayList<ServerConnector> arrayList = new ArrayList<ServerConnector>(
                values.size());
        for (ComponentDetail componentDetail : values) {
            arrayList.add(componentDetail.getConnector());
        }
        return arrayList;
    }

    public JsArrayObject<ServerConnector> getConnectorsAsJsArray() {
        JsArrayObject<ComponentDetail> componentDetails = idToComponentDetail
                .valuesAsJsArray();
        JsArrayObject<ServerConnector> connectors = JavaScriptObject
                .createArray().cast();

        int size = componentDetails.size();
        for (int i = 0; i < size; i++) {
            connectors.add(componentDetails.get(i).getConnector());
        }

        return connectors;
    }

    /**
     * Tests if the widget is the root widget of a {@link ComponentConnector}.
     * 
     * @param widget
     *            The widget to test
     * @return true if the widget is the root widget of a
     *         {@link ComponentConnector}, false otherwise
     */
    public boolean isConnector(Widget w) {
        return getConnectorId(w.getElement()) != null;
    }

    private static Logger getLogger() {
        return Logger.getLogger(ConnectorMap.class.getName());
    }
}
