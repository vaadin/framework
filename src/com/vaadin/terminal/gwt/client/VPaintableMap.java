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
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.Paintable;
import com.vaadin.terminal.gwt.client.RenderInformation.Size;

public class VPaintableMap {

    private Map<String, VPaintable> idToPaintable = new HashMap<String, VPaintable>();
    private Map<VPaintable, String> paintableToId = new HashMap<VPaintable, String>();

    public static VPaintableMap get(ApplicationConnection applicationConnection) {
        return applicationConnection.getPaintableMap();
    }

    @Deprecated
    private final ComponentDetailMap idToComponentDetail = ComponentDetailMap
            .create();

    private Set<String> unregistryBag = new HashSet<String>();

    /**
     * Returns a Paintable by its paintable id
     * 
     * @param id
     *            The Paintable id
     */
    public VPaintable getPaintable(String pid) {
        return idToPaintable.get(pid);
    }

    /**
     * Returns a Paintable element by its root element
     * 
     * @param element
     *            Root element of the paintable
     */
    public VPaintableWidget getPaintable(Element element) {
        return (VPaintableWidget) getPaintable(getPid(element));
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
     * Checks if a paintable with the given paintable id has been registered.
     * 
     * @param pid
     *            The paintable id to check for
     * @return true if a paintable has been registered with the given paintable
     *         id, false otherwise
     */
    public boolean hasPaintable(String pid) {
        return idToPaintable.containsKey(pid);
    }

    /**
     * Removes all registered paintable ids
     */
    public void clear() {
        idToPaintable.clear();
        paintableToId.clear();
        idToComponentDetail.clear();
    }

    @Deprecated
    public Widget getWidget(VPaintableWidget paintable) {
        return paintable.getWidgetForPaintable();
    }

    @Deprecated
    public VPaintableWidget getPaintable(Widget widget) {
        return getPaintable(widget.getElement());
    }

    public void registerPaintable(String pid, VPaintable paintable) {
        ComponentDetail componentDetail = GWT.create(ComponentDetail.class);
        idToComponentDetail.put(pid, componentDetail);
        idToPaintable.put(pid, paintable);
        paintableToId.put(paintable, pid);
        if (paintable instanceof VPaintableWidget) {
            VPaintableWidget pw = (VPaintableWidget) paintable;
            setPid(pw.getWidgetForPaintable().getElement(), pid);
        }
    }

    private native void setPid(Element el, String pid)
    /*-{
        el.tkPid = pid;
    }-*/;

    /**
     * Gets the paintableId for a specific paintable.
     * <p>
     * The paintableId is used in the UIDL to identify a specific widget
     * instance, effectively linking the widget with it's server side Component.
     * </p>
     * 
     * @param paintable
     *            the paintable who's id is needed
     * @return the id for the given paintable or null if the paintable could not
     *         be found
     */
    public String getPid(VPaintable paintable) {
        if (paintable == null) {
            return null;
        }
        return paintableToId.get(paintable);
    }

    @Deprecated
    public String getPid(Widget widget) {
        return getPid(widget.getElement());
    }

    /**
     * Gets the paintableId using a DOM element - the element should be the main
     * element for a paintable otherwise no id will be found. Use
     * {@link #getPid(Paintable)} instead whenever possible.
     * 
     * @see #getPid(Paintable)
     * @param el
     *            element of the paintable whose pid is desired
     * @return the pid of the element's paintable, if it's a paintable
     */
    native String getPid(Element el)
    /*-{
        return el.tkPid;
    }-*/;

    /**
     * Gets the main element for the paintable with the given id. The revers of
     * {@link #getPid(Element)}.
     * 
     * @param pid
     *            the pid of the widget whose element is desired
     * @return the element for the paintable corresponding to the pid
     */
    public Element getElement(String pid) {
        VPaintable p = getPaintable(pid);
        if (p instanceof VPaintableWidget) {
            return ((VPaintableWidget) p).getWidgetForPaintable().getElement();
        }

        return null;
    }

    /**
     * Unregisters the given paintable; always use after removing a paintable.
     * This method does not remove the paintable from the DOM, but marks the
     * paintable so that ApplicationConnection may clean up its references to
     * it. Removing the widget from DOM is component containers responsibility.
     * 
     * @param p
     *            the paintable to remove
     */
    public void unregisterPaintable(VPaintable p) {

        // add to unregistry que

        if (p == null) {
            VConsole.error("WARN: Trying to unregister null paintable");
            return;
        }
        String id = getPid(p);
        Widget widget = null;
        if (p instanceof VPaintableWidget) {
            widget = ((VPaintableWidget) p).getWidgetForPaintable();
        }

        if (id == null) {
            /*
             * Uncomment the following to debug unregistring components. No
             * paintables with null id should end here. At least one exception
             * is our VScrollTableRow, that is hacked to fake it self as a
             * Paintable to build support for sizing easier.
             */
            // if (!(p instanceof VScrollTableRow)) {
            // VConsole.log("Trying to unregister Paintable not created by Application Connection.");
            // }
        } else {
            unregistryBag.add(id);
        }
        if (widget != null && widget instanceof HasWidgets) {
            unregisterChildPaintables((HasWidgets) widget);
        }

    }

    public VPaintableWidget[] getRegisteredPaintableWidgets() {
        ArrayList<VPaintableWidget> result = new ArrayList<VPaintableWidget>();

        for (VPaintable paintable : getPaintables()) {
            if (paintable instanceof VPaintableWidget) {
                VPaintableWidget paintableWidget = (VPaintableWidget) paintable;
                if (!unregistryBag.contains(getPid(paintable))) {
                    result.add(paintableWidget);
                }
            }
        }

        return result.toArray(new VPaintableWidget[result.size()]);
    }

    void purgeUnregistryBag(boolean unregisterPaintables) {
        if (unregisterPaintables) {
            for (String pid : unregistryBag) {
                VPaintable paintable = getPaintable(pid);
                if (paintable == null) {
                    /*
                     * this should never happen, but it does :-( See e.g.
                     * com.vaadin.tests.components.accordion.RemoveTabs (with
                     * test script)
                     */
                    VConsole.error("Tried to unregister component (id="
                            + pid
                            + ") that is never registered (or already unregistered)");
                    continue;
                }
                Widget widget = null;
                if (paintable instanceof VPaintableWidget) {
                    widget = ((VPaintableWidget) paintable)
                            .getWidgetForPaintable();
                }

                // check if can be cleaned
                if (widget == null || !widget.isAttached()) {
                    // clean reference to paintable
                    idToComponentDetail.remove(pid);
                    idToPaintable.remove(pid);
                    paintableToId.remove(paintable);
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
     * Unregisters a paintable and all it's child paintables recursively. Use
     * when after removing a paintable that contains other paintables. Does not
     * unregister the given container itself. Does not actually remove the
     * paintable from the DOM.
     * 
     * @see #unregisterPaintable(Paintable)
     * @param container
     */
    public void unregisterChildPaintables(HasWidgets container) {
        // FIXME: This should be based on the paintable hierarchy
        final Iterator<Widget> it = container.iterator();
        while (it.hasNext()) {
            final Widget w = it.next();
            VPaintableWidget p = getPaintable(w);
            if (p != null) {
                // This will unregister the paintable and all its children
                unregisterPaintable(p);
            } else if (w instanceof HasWidgets) {
                // For normal widget containers, unregister the children
                unregisterChildPaintables((HasWidgets) w);
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
    public Size getOffsetSize(VPaintableWidget paintable) {
        return getComponentDetail(paintable).getOffsetSize();
    }

    /**
     * FIXME: Should not be here
     * 
     * @param paintable
     * @return
     */
    @Deprecated
    public void setOffsetSize(VPaintableWidget paintable, Size newSize) {
        getComponentDetail(paintable).setOffsetSize(newSize);
    }

    private ComponentDetail getComponentDetail(VPaintableWidget paintable) {
        return idToComponentDetail.get(getPid(paintable));
    }

    public int size() {
        return idToPaintable.size();
    }

    /**
     * FIXME: Should be moved to VAbstractPaintableWidget
     * 
     * @param paintable
     * @return
     */
    @Deprecated
    public TooltipInfo getTooltipInfo(VPaintableWidget paintable, Object key) {
        return getComponentDetail(paintable).getTooltipInfo(key);
    }

    @Deprecated
    public TooltipInfo getWidgetTooltipInfo(Widget widget, Object key) {
        return getTooltipInfo(getPaintable(widget), key);
    }

    public Collection<? extends VPaintable> getPaintables() {
        return Collections.unmodifiableCollection(paintableToId.keySet());
    }

    /**
     * FIXME: Should not be here
     * 
     * @param paintable
     * @return
     */
    @Deprecated
    public void registerTooltip(VPaintableWidget paintable, Object key,
            TooltipInfo tooltip) {
        getComponentDetail(paintable).putAdditionalTooltip(key, tooltip);

    }

    /**
     * FIXME: Should not be here
     * 
     * @param paintable
     * @return
     */
    @Deprecated
    public boolean hasEventListeners(VPaintableWidget paintable,
            String eventIdentifier) {
        return getComponentDetail(paintable).hasEventListeners(eventIdentifier);
    }

    /**
     * Tests if the widget is the root widget of a VPaintableWidget.
     * 
     * @param widget
     *            The widget to test
     * @return true if the widget is the root widget of a VPaintableWidget,
     *         false otherwise
     */
    public boolean isPaintable(Widget w) {
        return getPid(w) != null;
    }

}