/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Widget;

public class ConnectorMap {

    private Map<String, ServerConnector> idToConnector = new HashMap<String, ServerConnector>();

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
        return idToConnector.get(connectorId);
    }

    /**
     * Returns a {@link ComponentConnector} element by its root element
     * 
     * @param element
     *            Root element of the {@link ComponentConnector}
     * @return A connector or null if a connector with the given id has not been
     *         registered
     */
    public ComponentConnector getConnector(Element element) {
        return (ComponentConnector) getConnector(getConnectorId(element));
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
        return idToConnector.containsKey(connectorId);
    }

    /**
     * Removes all registered connectors
     */
    public void clear() {
        idToConnector.clear();
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
        return getConnector(widget.getElement());
    }

    public void registerConnector(String id, ServerConnector connector) {
        ComponentDetail componentDetail = GWT.create(ComponentDetail.class);
        idToComponentDetail.put(id, componentDetail);
        idToConnector.put(id, connector);
        if (connector instanceof ComponentConnector) {
            ComponentConnector pw = (ComponentConnector) connector;
            setConnectorId(pw.getWidget().getElement(), id);
        }
    }

    private native void setConnectorId(Element el, String id)
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
    native String getConnectorId(Element el)
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
            VConsole.error("Trying to unregister null connector");
            return;
        }

        String connectorId = connector.getConnectorId();

        idToComponentDetail.remove(connectorId);
        idToConnector.remove(connectorId);
        connector.onUnregister();

        if (connector instanceof ComponentContainerConnector) {
            for (ComponentConnector child : ((ComponentContainerConnector) connector)
                    .getChildren()) {
                if (child.getParent() == connector) {
                    // Only unregister children that are actually connected to
                    // this parent. For instance when moving connectors from one
                    // layout to another and removing the first layout it will
                    // still contain references to its old children, which are
                    // now attached to another connector.
                    unregisterConnector(child);
                }
            }
        }
    }

    /**
     * Gets all registered {@link ComponentConnector} instances
     * 
     * @return An array of all registered {@link ComponentConnector} instances
     */
    public ComponentConnector[] getComponentConnectors() {
        ArrayList<ComponentConnector> result = new ArrayList<ComponentConnector>();

        for (ServerConnector connector : getConnectors()) {
            if (connector instanceof ComponentConnector) {
                result.add((ComponentConnector) connector);
            }
        }

        return result.toArray(new ComponentConnector[result.size()]);
    }

    @Deprecated
    private ComponentDetail getComponentDetail(
            ComponentConnector componentConnector) {
        return idToComponentDetail.get(componentConnector.getConnectorId());
    }

    public int size() {
        return idToConnector.size();
    }

    /**
     * FIXME: Should be moved to VAbstractPaintableWidget
     * 
     * @param paintable
     * @return
     */
    @Deprecated
    public TooltipInfo getTooltipInfo(ComponentConnector paintable, Object key) {
        return getComponentDetail(paintable).getTooltipInfo(key);
    }

    @Deprecated
    public TooltipInfo getWidgetTooltipInfo(Widget widget, Object key) {
        return getTooltipInfo(getConnector(widget), key);
    }

    public Collection<? extends ServerConnector> getConnectors() {
        return Collections.unmodifiableCollection(idToConnector.values());
    }

    /**
     * FIXME: Should not be here
     * 
     * @param componentConnector
     * @return
     */
    @Deprecated
    public void registerTooltip(ComponentConnector componentConnector,
            Object key, TooltipInfo tooltip) {
        getComponentDetail(componentConnector).putAdditionalTooltip(key,
                tooltip);

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

}
