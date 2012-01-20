package com.vaadin.terminal.gwt.client;

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
import com.vaadin.terminal.gwt.client.RenderInformation.FloatSize;
import com.vaadin.terminal.gwt.client.RenderInformation.Size;

public class PaintableMap {

    private Map<String, Paintable> idToPaintable = new HashMap<String, Paintable>();

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
    public Paintable getPaintable(String pid) {
        return idToPaintable.get(pid);
    }

    /**
     * Returns a Paintable element by its root element
     * 
     * @param element
     *            Root element of the paintable
     */
    public Paintable getPaintable(Element element) {
        return getPaintable(getPid(element));
    }

    public static PaintableMap get(ApplicationConnection applicationConnection) {
        return applicationConnection.getPaintableMap();
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
        idToComponentDetail.clear();
    }

    @Deprecated
    public Widget getWidget(Paintable paintable) {
        return (Widget) paintable;
    }

    @Deprecated
    public Paintable getPaintable(Widget widget) {
        return (Paintable) widget;
    }

    public void registerPaintable(String pid, Paintable paintable) {
        ComponentDetail componentDetail = GWT.create(ComponentDetail.class);
        idToComponentDetail.put(pid, componentDetail);
        idToPaintable.put(pid, paintable);
        setPid(((Widget) paintable).getElement(), pid);
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
    public String getPid(Paintable paintable) {
        return getPid(getWidget(paintable));
    }

    @Deprecated
    public String getPid(Widget widget) {
        if (widget == null) {
            return null;
        }
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
        return ((Widget) getPaintable(pid)).getElement();
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
    public void unregisterPaintable(Paintable p) {

        // add to unregistry que

        if (p == null) {
            VConsole.error("WARN: Trying to unregister null paintable");
            return;
        }
        String id = getPid(p);
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
            if (p instanceof HasWidgets) {
                unregisterChildPaintables((HasWidgets) p);
            }
        } else {
            unregistryBag.add(id);
            if (p instanceof HasWidgets) {
                unregisterChildPaintables((HasWidgets) p);
            }
        }
    }

    void purgeUnregistryBag(boolean unregisterPaintables) {
        if (unregisterPaintables) {
            for (String pid : unregistryBag) {
                Paintable paintable = getPaintable(pid);
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
                // check if can be cleaned
                Widget component = getWidget(paintable);
                if (!component.isAttached()) {
                    // clean reference to paintable
                    idToComponentDetail.remove(pid);
                    idToPaintable.remove(pid);
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
        final Iterator<Widget> it = container.iterator();
        while (it.hasNext()) {
            final Widget w = it.next();
            if (w instanceof Paintable) {
                unregisterPaintable((Paintable) w);
            } else if (w instanceof HasWidgets) {
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
    public Size getOffsetSize(Paintable paintable) {
        return getComponentDetail(paintable).getOffsetSize();
    }

    /**
     * FIXME: Should not be here
     * 
     * @param paintable
     * @return
     */
    @Deprecated
    public FloatSize getRelativeSize(Paintable paintable) {
        return getComponentDetail(paintable).getRelativeSize();
    }

    /**
     * FIXME: Should not be here
     * 
     * @param paintable
     * @return
     */
    @Deprecated
    public void setOffsetSize(Paintable paintable, Size newSize) {
        getComponentDetail(paintable).setOffsetSize(newSize);
    }

    /**
     * FIXME: Should not be here
     * 
     * @param paintable
     * @return
     */
    @Deprecated
    public void setRelativeSize(Paintable paintable, FloatSize relativeSize) {
        getComponentDetail(paintable).setRelativeSize(relativeSize);

    }

    private ComponentDetail getComponentDetail(Paintable paintable) {
        return idToComponentDetail.get(getPid(paintable));
    }

    public int size() {
        return idToPaintable.size();
    }

    /**
     * FIXME: Should not be here
     * 
     * @param paintable
     * @return
     */
    @Deprecated
    public TooltipInfo getTooltipInfo(Paintable paintable, Object key) {
        return getComponentDetail(paintable).getTooltipInfo(key);
    }

    public Collection<? extends Paintable> getPaintables() {
        return Collections.unmodifiableCollection(idToPaintable.values());
    }

    /**
     * FIXME: Should not be here
     * 
     * @param paintable
     * @return
     */
    @Deprecated
    public void registerTooltip(Paintable paintable, Object key,
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
    public boolean hasEventListeners(Paintable paintable, String eventIdentifier) {
        return getComponentDetail(paintable).hasEventListeners(eventIdentifier);
    }

}
