/*
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.ui;

import java.util.Iterator;
import java.util.Map;

import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.event.ActionManager;
import com.vaadin.event.MouseEvents.ClickEvent;
import com.vaadin.event.MouseEvents.ClickListener;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.terminal.Scrollable;
import com.vaadin.terminal.gwt.client.MouseEventDetails;
import com.vaadin.terminal.gwt.client.ui.VPanel;
import com.vaadin.ui.Component.Focusable;
import com.vaadin.ui.themes.Reindeer;
import com.vaadin.ui.themes.Runo;

/**
 * Panel - a simple single component container.
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.0
 */
@SuppressWarnings("serial")
@ClientWidget(VPanel.class)
public class Panel extends AbstractComponentContainer implements Scrollable,
        ComponentContainer.ComponentAttachListener,
        ComponentContainer.ComponentDetachListener, Action.Notifier, Focusable {

    private static final String CLICK_EVENT = VPanel.CLICK_EVENT_IDENTIFIER;

    /**
     * Removes extra decorations from the Panel.
     * 
     * @deprecated this style is no longer part of the core framework and this
     *             component, even though most built-in themes implement this
     *             style. Use the constant specified in the theme class file
     *             that you're using, if it provides one, e.g.
     *             {@link Reindeer#PANEL_LIGHT} or {@link Runo#PANEL_LIGHT} .
     */
    @Deprecated
    public static final String STYLE_LIGHT = "light";

    /**
     * Content of the panel.
     */
    private ComponentContainer content;

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

    /**
     * Keeps track of the Actions added to this component, and manages the
     * painting and handling as well.
     */
    protected ActionManager actionManager;

    /**
     * By default the Panel is not in the normal document focus flow and can
     * only be focused by using the focus()-method. Change this to 0 if you want
     * to have the Panel in the normal focus flow.
     */
    private int tabIndex = -1;

    /**
     * Creates a new empty panel. A VerticalLayout is used as content.
     */
    public Panel() {
        this((ComponentContainer) null);
    }

    /**
     * Creates a new empty panel which contains the given content. The content
     * cannot be null.
     * 
     * @param content
     *            the content for the panel.
     */
    public Panel(ComponentContainer content) {
        setContent(content);
        setWidth(100, UNITS_PERCENTAGE);
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
     * Creates a new empty panel with the given caption and content.
     * 
     * @param caption
     *            the caption of the panel.
     * @param content
     *            the content used in the panel.
     */
    public Panel(String caption, ComponentContainer content) {
        this(content);
        setCaption(caption);
    }

    /**
     * Gets the current layout of the panel.
     * 
     * @return the Current layout of the panel.
     * @deprecated A Panel can now contain a ComponentContainer which is not
     *             necessarily a Layout. Use {@link #getContent()} instead.
     */
    @Deprecated
    public Layout getLayout() {
        if (content instanceof Layout) {
            return (Layout) content;
        } else if (content == null) {
            return null;
        }

        throw new IllegalStateException(
                "Panel does not contain a Layout. Use getContent() instead of getLayout().");
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
     * @deprecated A Panel can now contain a ComponentContainer which is not
     *             necessarily a Layout. Use
     *             {@link #setContent(ComponentContainer)} instead.
     */
    @Deprecated
    public void setLayout(Layout newLayout) {
        setContent(newLayout);
    }

    /**
     * Returns the content of the Panel.
     * 
     * @return
     */
    public ComponentContainer getContent() {
        return content;
    }

    /**
     * 
     * Set the content of the Panel. If null is given as the new content then a
     * layout is automatically created and set as the content.
     * 
     * @param content
     *            The new content
     */
    public void setContent(ComponentContainer newContent) {

        // If the content is null we create the default content
        if (newContent == null) {
            newContent = createDefaultContent();
        }

        // if (newContent == null) {
        // throw new IllegalArgumentException("Content cannot be null");
        // }

        if (newContent == content) {
            // don't set the same content twice
            return;
        }

        // detach old content if present
        if (content != null) {
            content.setParent(null);
            content.removeListener((ComponentContainer.ComponentAttachListener) this);
            content.removeListener((ComponentContainer.ComponentDetachListener) this);
        }

        // Sets the panel to be parent for the content
        newContent.setParent(this);

        // Sets the new content
        content = newContent;

        // Adds the event listeners for new content
        newContent
                .addListener((ComponentContainer.ComponentAttachListener) this);
        newContent
                .addListener((ComponentContainer.ComponentDetachListener) this);

        content = newContent;
    }

    /**
     * Create a ComponentContainer which is added by default to the Panel if
     * user does not specify any content.
     * 
     * @return
     */
    private ComponentContainer createDefaultContent() {
        VerticalLayout layout = new VerticalLayout();
        // Force margins by default
        layout.setMargin(true);
        return layout;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.ui.AbstractComponent#paintContent(com.vaadin.terminal.PaintTarget
     * )
     */
    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        content.paint(target);

        target.addVariable(this, "tabindex", getTabIndex());

        if (isScrollable()) {
            target.addVariable(this, "scrollLeft", getScrollLeft());
            target.addVariable(this, "scrollTop", getScrollTop());
        }

        if (actionManager != null) {
            actionManager.paintActions(null, target);
        }
    }

    @Override
    public void requestRepaintAll() {
        // Panel has odd structure, delegate to layout
        requestRepaint();
        if (getContent() != null) {
            getContent().requestRepaintAll();
        }
    }

    /**
     * Adds the component into this container.
     * 
     * @param c
     *            the component to be added.
     * @see com.vaadin.ui.AbstractComponentContainer#addComponent(com.vaadin.ui.Component)
     */
    @Override
    public void addComponent(Component c) {
        content.addComponent(c);
        // No repaint request is made as we except the underlying container to
        // request repaints
    }

    /**
     * Removes the component from this container.
     * 
     * @param c
     *            The component to be added.
     * @see com.vaadin.ui.AbstractComponentContainer#removeComponent(com.vaadin.ui.Component)
     */
    @Override
    public void removeComponent(Component c) {
        content.removeComponent(c);
        // No repaint request is made as we except the underlying container to
        // request repaints
    }

    /**
     * Gets the component container iterator for going through all the
     * components in the container.
     * 
     * @return the Iterator of the components inside the container.
     * @see com.vaadin.ui.ComponentContainer#getComponentIterator()
     */
    public Iterator<Component> getComponentIterator() {
        return content.getComponentIterator();
    }

    /**
     * Called when one or more variables handled by the implementing class are
     * changed.
     * 
     * @see com.vaadin.terminal.VariableOwner#changeVariables(Object, Map)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void changeVariables(Object source, Map<String, Object> variables) {
        super.changeVariables(source, variables);

        if (variables.containsKey(CLICK_EVENT)) {
            fireClick((Map<String, Object>) variables.get(CLICK_EVENT));
        }

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
        if (actionManager != null) {
            actionManager.handleActions(variables, this);
        }

    }

    /* Scrolling functionality */

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.terminal.Scrollable#setScrollable(boolean)
     */
    public int getScrollLeft() {
        return scrollOffsetX;
    }

    /**
     * @deprecated use {@link #getScrollLeft()} instead
     */
    @Deprecated
    public int getScrollOffsetX() {
        return getScrollLeft();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.terminal.Scrollable#setScrollable(boolean)
     */
    public int getScrollTop() {
        return scrollOffsetY;
    }

    /**
     * @deprecated use {@link #getScrollTop()} instead
     */
    @Deprecated
    public int getScrollOffsetY() {
        return getScrollTop();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.terminal.Scrollable#setScrollable(boolean)
     */
    public boolean isScrollable() {
        return scrollable;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.terminal.Scrollable#setScrollable(boolean)
     */
    public void setScrollable(boolean isScrollingEnabled) {
        if (scrollable != isScrollingEnabled) {
            scrollable = isScrollingEnabled;
            requestRepaint();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.terminal.Scrollable#setScrollLeft(int)
     */
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

        content.replaceComponent(oldComponent, newComponent);
    }

    /**
     * A new component is attached to container.
     * 
     * @see com.vaadin.ui.ComponentContainer.ComponentAttachListener#componentAttachedToContainer(com.vaadin.ui.ComponentContainer.ComponentAttachEvent)
     */
    public void componentAttachedToContainer(ComponentAttachEvent event) {
        if (event.getContainer() == content) {
            fireComponentAttachEvent(event.getAttachedComponent());
        }
    }

    /**
     * A component has been detached from container.
     * 
     * @see com.vaadin.ui.ComponentContainer.ComponentDetachListener#componentDetachedFromContainer(com.vaadin.ui.ComponentContainer.ComponentDetachEvent)
     */
    public void componentDetachedFromContainer(ComponentDetachEvent event) {
        if (event.getContainer() == content) {
            fireComponentDetachEvent(event.getDetachedComponent());
        }
    }

    /**
     * Notifies the component that it is connected to an application.
     * 
     * @see com.vaadin.ui.Component#attach()
     */
    @Override
    public void attach() {
        // can't call parent here as this is Panels hierarchy is a hack
        requestRepaint();
        if (content != null) {
            content.attach();
        }
    }

    /**
     * Notifies the component that it is detached from the application.
     * 
     * @see com.vaadin.ui.Component#detach()
     */
    @Override
    public void detach() {
        // can't call parent here as this is Panels hierarchy is a hack
        if (content != null) {
            content.detach();
        }
    }

    /**
     * Removes all components from this container.
     * 
     * @see com.vaadin.ui.ComponentContainer#removeAllComponents()
     */
    @Override
    public void removeAllComponents() {
        content.removeAllComponents();
    }

    /*
     * ACTIONS
     */
    protected ActionManager getActionManager() {
        if (actionManager == null) {
            actionManager = new ActionManager(this);
        }
        return actionManager;
    }

    public <T extends Action & com.vaadin.event.Action.Listener> void addAction(
            T action) {
        getActionManager().addAction(action);
    }

    public <T extends Action & com.vaadin.event.Action.Listener> void removeAction(
            T action) {
        if (actionManager != null) {
            actionManager.removeAction(action);
        }
    }

    public void addActionHandler(Handler actionHandler) {
        getActionManager().addActionHandler(actionHandler);
    }

    public void removeActionHandler(Handler actionHandler) {
        if (actionManager != null) {
            actionManager.removeActionHandler(actionHandler);
        }
    }

    /**
     * Removes all action handlers
     */
    public void removeAllActionHandlers() {
        if (actionManager != null) {
            actionManager.removeAllActionHandlers();
        }
    }

    /**
     * Add a click listener to the Panel. The listener is called whenever the
     * user clicks inside the Panel. Also when the click targets a component
     * inside the Panel, provided the targeted component does not prevent the
     * click event from propagating.
     * 
     * Use {@link #removeListener(ClickListener)} to remove the listener.
     * 
     * @param listener
     *            The listener to add
     */
    public void addListener(ClickListener listener) {
        addListener(CLICK_EVENT, ClickEvent.class, listener,
                ClickListener.clickMethod);
    }

    /**
     * Remove a click listener from the Panel. The listener should earlier have
     * been added using {@link #addListener(ClickListener)}.
     * 
     * @param listener
     *            The listener to remove
     */
    public void removeListener(ClickListener listener) {
        removeListener(CLICK_EVENT, ClickEvent.class, listener);
    }

    /**
     * Fire a click event to all click listeners.
     * 
     * @param object
     *            The raw "value" of the variable change from the client side.
     */
    private void fireClick(Map<String, Object> parameters) {
        MouseEventDetails mouseDetails = MouseEventDetails
                .deSerialize((String) parameters.get("mouseDetails"));
        fireEvent(new ClickEvent(this, mouseDetails));
    }

    
    /**
     * {@inheritDoc}
     */
    public int getTabIndex() {
        return tabIndex;
    }

    /**
     * {@inheritDoc}
     */
    public void setTabIndex(int tabIndex) {
        this.tabIndex = tabIndex;
        requestRepaint();
    }

    /**
     * Moves keyboard focus to the component. {@see Focusable#focus()}
     * 
     */
    @Override
    public void focus() {
        super.focus();
    }

}
