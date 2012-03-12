/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.Paintable;
import com.vaadin.terminal.gwt.client.RenderInformation.Size;

public class ConnectorMap {

    private Map<String, ServerConnector> idToConnector = new HashMap<String, ServerConnector>();

    public static ConnectorMap get(ApplicationConnection applicationConnection) {
        return applicationConnection.getConnectorMap();
    }

    @Deprecated
    private final ComponentDetailMap idToComponentDetail = ComponentDetailMap
            .create();

    private Set<String> unregistryBag = new HashSet<String>();

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
     * Gets the id for a specific connector.
     * <p>
     * The id is used in the UIDL to identify a specific widget instance,
     * effectively linking the widget with it's server side Component.
     * </p>
     * 
     * @param connector
     *            the connector whose id is needed
     * @return the id for the given connector or null if the connector could not
     *         be found
     * @deprecated use {@link ServerConnector#getConnectorId()} instead
     */
    @Deprecated
    public String getConnectorId(ServerConnector connector) {
        if (connector == null) {
            return null;
        }
        return connector.getConnectorId();
    }

    @Deprecated
    public String getConnectorId(Widget widget) {
        return getConnectorId(widget.getElement());
    }

    /**
     * Gets the connector id using a DOM element - the element should be the
     * root element for a connector, otherwise no id will be found. Use
     * {@link #getConnectorId(ServerConnector)} instead whenever possible.
     * 
     * @see #getConnectorId(Paintable)
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

        // add to unregistry queue

        if (connector == null) {
            VConsole.error("WARN: Trying to unregister null connector");
            return;
        }
        String id = connector.getConnectorId();
        Widget widget = null;
        if (connector instanceof ComponentConnector) {
            widget = ((ComponentConnector) connector).getWidget();
        }

        if (id == null) {
            VConsole.log("Tried to unregister a "
                    + connector.getClass().getName() + " (" + id
                    + ") which was not registered");
        } else {
            unregistryBag.add(id);
        }
        if (widget != null && widget instanceof HasWidgets) {
            unregisterChildConnectors((HasWidgets) widget);
        }

    }

    public ComponentConnector[] getRegisteredComponentConnectors() {
        ArrayList<ComponentConnector> result = new ArrayList<ComponentConnector>();

        for (ServerConnector connector : getConnectors()) {
            if (connector instanceof ComponentConnector) {
                ComponentConnector componentConnector = (ComponentConnector) connector;
                if (!unregistryBag.contains(connector.getConnectorId())) {
                    result.add(componentConnector);
                }
            }
        }

        return result.toArray(new ComponentConnector[result.size()]);
    }

    void purgeUnregistryBag(boolean unregisterConnectors) {
        if (unregisterConnectors) {
            for (String connectorId : unregistryBag) {
                // TODO purge shared state for pid
                ServerConnector connector = getConnector(connectorId);
                if (connector == null) {
                    /*
                     * this should never happen, but it does :-( See e.g.
                     * com.vaadin.tests.components.accordion.RemoveTabs (with
                     * test script)
                     */
                    VConsole.error("Tried to unregister component (id="
                            + connectorId
                            + ") that is never registered (or already unregistered)");
                    continue;
                }
                VConsole.log("Unregistering connector "
                        + connector.getClass().getName() + " " + connectorId);
                Widget widget = null;
                if (connector instanceof ComponentConnector) {
                    widget = ((ComponentConnector) connector).getWidget();
                }

                // check if can be cleaned
                if (widget == null || !widget.isAttached()) {
                    // clean reference to paintable
                    idToComponentDetail.remove(connectorId);
                    idToConnector.remove(connectorId);
                }
                /*
                 * else NOP : same component has been reattached to another
                 * parent or replaced by another component implementation.
                 */
            }
        }

        unregistryBag.clear();
    }

    /**
     * Unregisters the child connectors for the given container recursively.
     * 
     * Use when after removing a connector that contains other connectors. Does
     * not unregister the given container itself. Does not actually remove the
     * widgets from the DOM.
     * 
     * @see #unregisterConnector(ServerConnector)
     * @param container
     *            The container that contains the connectors that should be
     *            unregistered
     */
    public void unregisterChildConnectors(HasWidgets container) {
        // FIXME: This should be based on the paintable hierarchy
        final Iterator<Widget> it = container.iterator();
        while (it.hasNext()) {
            final Widget w = it.next();
            ComponentConnector p = getConnector(w);
            if (p != null) {
                // This will unregister the paintable and all its children
                unregisterConnector(p);
            } else if (w instanceof HasWidgets) {
                // For normal widget containers, unregister the children
                unregisterChildConnectors((HasWidgets) w);
            }
        }
    }

    /**
     * FIXME: Should not be here
     * 
     * @param pid
     * @param uidl
     */
    @Deprecated
    public void registerEventListenersFromUIDL(String pid, UIDL uidl) {
        ComponentDetail cd = idToComponentDetail.get(pid);
        if (cd == null) {
            throw new IllegalArgumentException("Pid must not be null");
        }

        cd.registerEventListenersFromUIDL(uidl);

    }

    /**
     * FIXME: Should not be here
     * 
     * @param paintable
     * @return
     */
    @Deprecated
    public Size getOffsetSize(ComponentConnector paintable) {
        return getComponentDetail(paintable).getOffsetSize();
    }

    /**
     * FIXME: Should not be here
     * 
     * @param componentConnector
     * @return
     */
    @Deprecated
    public void setOffsetSize(ComponentConnector componentConnector,
            Size newSize) {
        getComponentDetail(componentConnector).setOffsetSize(newSize);
    }

    private ComponentDetail getComponentDetail(
            ComponentConnector componentConnector) {
        return idToComponentDetail.get(getConnectorId(componentConnector));
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
     * FIXME: Should not be here
     * 
     * @param componentConnector
     * @return
     */
    @Deprecated
    public boolean hasEventListeners(ComponentConnector componentConnector,
            String eventIdentifier) {
        return getComponentDetail(componentConnector).hasEventListeners(
                eventIdentifier);
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
        return getConnectorId(w) != null;
    }

}
