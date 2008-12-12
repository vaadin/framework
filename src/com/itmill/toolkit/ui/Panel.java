/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.ui;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import com.itmill.toolkit.event.Action;
import com.itmill.toolkit.event.ShortcutAction;
import com.itmill.toolkit.event.Action.Handler;
import com.itmill.toolkit.terminal.KeyMapper;
import com.itmill.toolkit.terminal.PaintException;
import com.itmill.toolkit.terminal.PaintTarget;
import com.itmill.toolkit.terminal.Scrollable;

/**
 * Panel - a simple single component container.
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.0
 */
public class Panel extends AbstractComponentContainer implements Scrollable,
        ComponentContainer.ComponentAttachListener,
        ComponentContainer.ComponentDetachListener, Action.Container {

    public static final String STYLE_LIGHT = "light";

    /**
     * Layout of the panel.
     */
    private Layout layout;

    /**
     * Scroll X position.
     */
    private int scrollOffsetX = 0;

    /**
     * Scroll Y position.
     */
    private int scrollOffsetY = 0;

    /**
     * Scrolling mode.
     */
    private boolean scrollable = false;

    /** List of action handlers */
    private LinkedList actionHandlers = null;

    /** Action mapper */
    private KeyMapper actionMapper = null;

    /**
     * Creates a new empty panel. Ordered layout is used.
     */
    public Panel() {
        this((Layout) null);
    }

    /**
     * Creates a new empty panel with given layout. Layout must be non-null.
     * 
     * @param layout
     *            the layout used in the panel.
     */
    public Panel(Layout layout) {
        setWidth(100, UNITS_PERCENTAGE);
        setLayout(layout);
    }

    /**
     * Creates a new empty panel with caption. Default layout is used.
     * 
     * @param caption
     *            the caption used in the panel.
     */
    public Panel(String caption) {
        this(caption, null);
    }

    /**
     * Creates a new empty panel with caption.
     * 
     * @param caption
     *            the caption of the panel.
     * @param layout
     *            the layout used in the panel.
     */
    public Panel(String caption, Layout layout) {
        this(layout);
        setCaption(caption);
    }

    /**
     * Gets the current layout of the panel.
     * 
     * @return the Current layout of the panel.
     */
    public Layout getLayout() {
        return layout;
    }

    /**
     * Sets the layout of the panel.
     * 
     * If given layout is null, a VerticalLayout with margins set is used as a
     * default.
     * 
     * Components from old layout are not moved to new layout by default
     * (changed in 5.2.2). Use function in Layout interface manually.
     * 
     * @param newLayout
     *            the New layout of the panel.
     */
    public void setLayout(Layout newLayout) {

        // Only allow non-null layouts
        if (newLayout == null) {
            newLayout = new VerticalLayout();
            // Force margins by default
            newLayout.setMargin(true);
        }

        if (newLayout == layout) {
            // don't set the same layout twice
            return;
        }

        // detach old layout if present
        if (layout != null) {
            layout.setParent(null);
            layout
                    .removeListener((ComponentContainer.ComponentAttachListener) this);
            layout
                    .removeListener((ComponentContainer.ComponentDetachListener) this);
        }

        // Sets the panel to be parent for the layout
        newLayout.setParent(this);

        // Sets the new layout
        layout = newLayout;

        // Adds the event listeners for new layout
        newLayout
                .addListener((ComponentContainer.ComponentAttachListener) this);
        newLayout
                .addListener((ComponentContainer.ComponentDetachListener) this);
    }

    /**
     * Paints the content of this component.
     * 
     * @param target
     *            the Paint Event.
     * @throws PaintException
     *             if the paint operation failed.
     */
    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        layout.paint(target);

        if (isScrollable()) {
            target.addVariable(this, "scrollLeft", getScrollLeft());
            target.addVariable(this, "scrollTop", getScrollTop());
        }

        if (actionHandlers != null && !actionHandlers.isEmpty()) {
            target.addVariable(this, "action", "");
            target.startTag("actions");

            for (final Iterator ahi = actionHandlers.iterator(); ahi.hasNext();) {
                final Action[] aa = ((Action.Handler) ahi.next()).getActions(
                        null, this);
                if (aa != null) {
                    for (int ai = 0; ai < aa.length; ai++) {
                        final Action a = aa[ai];
                        target.startTag("action");
                        final String akey = actionMapper.key(aa[ai]);
                        target.addAttribute("key", akey);
                        if (a.getCaption() != null) {
                            target.addAttribute("caption", a.getCaption());
                        }
                        if (a.getIcon() != null) {
                            target.addAttribute("icon", a.getIcon());
                        }
                        if (a instanceof ShortcutAction) {
                            final ShortcutAction sa = (ShortcutAction) a;
                            target.addAttribute("kc", sa.getKeyCode());
                            final int[] modifiers = sa.getModifiers();
                            if (modifiers != null) {
                                final String[] smodifiers = new String[modifiers.length];
                                for (int i = 0; i < modifiers.length; i++) {
                                    smodifiers[i] = String
                                            .valueOf(modifiers[i]);
                                }
                                target.addAttribute("mk", smodifiers);
                            }
                        }
                        target.endTag("action");
                    }
                }
            }
            target.endTag("actions");
        }
    }

    @Override
    public void requestRepaintAll() {
        // Panel has odd structure, delegate to layout
        requestRepaint();
        getLayout().requestRepaintAll();
    }

    /**
     * Gets the component UIDL tag.
     * 
     * @return the Component UIDL tag as string.
     */
    @Override
    public String getTag() {
        return "panel";
    }

    /**
     * Adds the component into this container.
     * 
     * @param c
     *            the component to be added.
     * @see com.itmill.toolkit.ui.AbstractComponentContainer#addComponent(com.itmill.toolkit.ui.Component)
     */
    @Override
    public void addComponent(Component c) {
        layout.addComponent(c);
        // No repaint request is made as we except the underlying container to
        // request repaints
    }

    /**
     * Removes the component from this container.
     * 
     * @param c
     *            The component to be added.
     * @see com.itmill.toolkit.ui.AbstractComponentContainer#removeComponent(com.itmill.toolkit.ui.Component)
     */
    @Override
    public void removeComponent(Component c) {
        layout.removeComponent(c);
        // No repaint request is made as we except the underlying container to
        // request repaints
    }

    /**
     * Gets the component container iterator for going trough all the components
     * in the container.
     * 
     * @return the Iterator of the components inside the container.
     * @see com.itmill.toolkit.ui.ComponentContainer#getComponentIterator()
     */
    public Iterator getComponentIterator() {
        return layout.getComponentIterator();
    }

    /**
     * Called when one or more variables handled by the implementing class are
     * changed.
     * 
     * @see com.itmill.toolkit.terminal.VariableOwner#changeVariables(Object,
     *      Map)
     */
    @Override
    public void changeVariables(Object source, Map variables) {
        super.changeVariables(source, variables);

        // Get new size
        final Integer newWidth = (Integer) variables.get("width");
        final Integer newHeight = (Integer) variables.get("height");
        if (newWidth != null && newWidth.intValue() != getWidth()) {
            setWidth(newWidth.intValue(), UNITS_PIXELS);
        }
        if (newHeight != null && newHeight.intValue() != getHeight()) {
            setHeight(newHeight.intValue(), UNITS_PIXELS);
        }

        // Scrolling
        final Integer newScrollX = (Integer) variables.get("scrollLeft");
        final Integer newScrollY = (Integer) variables.get("scrollTop");
        if (newScrollX != null && newScrollX.intValue() != getScrollLeft()) {
            // set internally, not to fire request repaint
            scrollOffsetX = newScrollX.intValue();
        }
        if (newScrollY != null && newScrollY.intValue() != getScrollTop()) {
            // set internally, not to fire request repaint
            scrollOffsetY = newScrollY.intValue();
        }

        // Actions
        if (variables.containsKey("action")) {
            final String key = (String) variables.get("action");
            final Action action = (Action) actionMapper.get(key);
            if (action != null && actionHandlers != null) {
                for (final Iterator i = actionHandlers.iterator(); i.hasNext();) {
                    ((Action.Handler) i.next())
                            .handleAction(action, this, this);
                }
            }
        }

    }

    /* Scrolling functionality */

    /* Documented in interface */
    public int getScrollLeft() {
        return scrollOffsetX;
    }

    /**
     * @deprecated use getScrollLeft() instead
     */
    @Deprecated
    public int getScrollOffsetX() {
        return getScrollLeft();
    }

    /* Documented in interface */
    public int getScrollTop() {
        return scrollOffsetY;
    }

    /**
     * @deprecated use getScrollTop() instead
     */
    @Deprecated
    public int getScrollOffsetY() {
        return getScrollTop();
    }

    /* Documented in interface */
    public boolean isScrollable() {
        return scrollable;
    }

    /* Documented in interface */
    public void setScrollable(boolean isScrollingEnabled) {
        if (scrollable != isScrollingEnabled) {
            scrollable = isScrollingEnabled;
            requestRepaint();
        }
    }

    /* Documented in interface */
    public void setScrollLeft(int pixelsScrolled) {
        if (pixelsScrolled < 0) {
            throw new IllegalArgumentException(
                    "Scroll offset must be at least 0");
        }
        if (scrollOffsetX != pixelsScrolled) {
            scrollOffsetX = pixelsScrolled;
            requestRepaint();
        }
    }

    /**
     * @deprecated use setScrollLeft() method instead
     */
    @Deprecated
    public void setScrollOffsetX(int pixels) {
        setScrollLeft(pixels);
    }

    /* Documented in interface */
    public void setScrollTop(int pixelsScrolledDown) {
        if (pixelsScrolledDown < 0) {
            throw new IllegalArgumentException(
                    "Scroll offset must be at least 0");
        }
        if (scrollOffsetY != pixelsScrolledDown) {
            scrollOffsetY = pixelsScrolledDown;
            requestRepaint();
        }
    }

    /**
     * @deprecated use setScrollTop() method instead
     */
    @Deprecated
    public void setScrollOffsetY(int pixels) {
        setScrollTop(pixels);
    }

    /* Documented in superclass */
    public void replaceComponent(Component oldComponent, Component newComponent) {

        layout.replaceComponent(oldComponent, newComponent);
    }

    /**
     * A new component is attached to container.
     * 
     * @see com.itmill.toolkit.ui.ComponentContainer.ComponentAttachListener#componentAttachedToContainer(com.itmill.toolkit.ui.ComponentContainer.ComponentAttachEvent)
     */
    public void componentAttachedToContainer(ComponentAttachEvent event) {
        if (event.getContainer() == layout) {
            fireComponentAttachEvent(event.getAttachedComponent());
        }
    }

    /**
     * A component has been detached from container.
     * 
     * @see com.itmill.toolkit.ui.ComponentContainer.ComponentDetachListener#componentDetachedFromContainer(com.itmill.toolkit.ui.ComponentContainer.ComponentDetachEvent)
     */
    public void componentDetachedFromContainer(ComponentDetachEvent event) {
        if (event.getContainer() == layout) {
            fireComponentDetachEvent(event.getDetachedComponent());
        }
    }

    /**
     * Notifies the component that it is connected to an application.
     * 
     * @see com.itmill.toolkit.ui.Component#attach()
     */
    @Override
    public void attach() {
        // can't call parent here as this is Panels hierarchy is a hack
        requestRepaint();
        if (layout != null) {
            layout.attach();
        }
    }

    /**
     * Notifies the component that it is detached from the application.
     * 
     * @see com.itmill.toolkit.ui.Component#detach()
     */
    @Override
    public void detach() {
        // can't call parent here as this is Panels hierarchy is a hack
        if (layout != null) {
            layout.detach();
        }
    }

    /**
     * Removes all components from this container.
     * 
     * @see com.itmill.toolkit.ui.ComponentContainer#removeAllComponents()
     */
    @Override
    public void removeAllComponents() {
        layout.removeAllComponents();
    }

    public void addActionHandler(Handler actionHandler) {
        if (actionHandler != null) {

            if (actionHandlers == null) {
                actionHandlers = new LinkedList();
                actionMapper = new KeyMapper();
            }

            if (!actionHandlers.contains(actionHandler)) {
                actionHandlers.add(actionHandler);
                requestRepaint();
            }
        }

    }

    /**
     * Removes an action handler.
     * 
     * @see com.itmill.toolkit.event.Action.Container#removeActionHandler(Action.Handler)
     */
    public void removeActionHandler(Action.Handler actionHandler) {

        if (actionHandlers != null && actionHandlers.contains(actionHandler)) {

            actionHandlers.remove(actionHandler);

            if (actionHandlers.isEmpty()) {
                actionHandlers = null;
                actionMapper = null;
            }

            requestRepaint();
        }
    }
}
