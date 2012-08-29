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

package com.vaadin.ui;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.event.ActionManager;
import com.vaadin.event.MouseEvents.ClickEvent;
import com.vaadin.event.MouseEvents.ClickListener;
import com.vaadin.server.PaintException;
import com.vaadin.server.PaintTarget;
import com.vaadin.server.Scrollable;
import com.vaadin.server.LegacyComponent;
import com.vaadin.shared.EventId;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.ui.panel.PanelServerRpc;
import com.vaadin.shared.ui.panel.PanelState;
import com.vaadin.ui.Component.Focusable;

/**
 * Panel - a simple single component container.
 * 
 * @author Vaadin Ltd.
 * @since 3.0
 */
@SuppressWarnings("serial")
public class Panel extends AbstractComponentContainer implements Scrollable,
        ComponentContainer.ComponentAttachListener,
        ComponentContainer.ComponentDetachListener, Action.Notifier, Focusable,
        LegacyComponent {

    /**
     * Content of the panel.
     */
    private ComponentContainer content;

    /**
     * Keeps track of the Actions added to this component, and manages the
     * painting and handling as well.
     */
    protected ActionManager actionManager;

    private PanelServerRpc rpc = new PanelServerRpc() {
        @Override
        public void click(MouseEventDetails mouseDetails) {
            fireEvent(new ClickEvent(Panel.this, mouseDetails));
        }
    };

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
        registerRpc(rpc);
        setContent(content);
        setWidth(100, Unit.PERCENTAGE);
        getState().setTabIndex(-1);
    }

    /**
     * Creates a new empty panel with caption. Default layout is used.
     * 
     * @param caption
     *            the caption used in the panel (HTML/XHTML).
     */
    public Panel(String caption) {
        this(caption, null);
    }

    /**
     * Creates a new empty panel with the given caption and content.
     * 
     * @param caption
     *            the caption of the panel (HTML/XHTML).
     * @param content
     *            the content used in the panel.
     */
    public Panel(String caption, ComponentContainer content) {
        this(content);
        setCaption(caption);
    }

    /**
     * Sets the caption of the panel.
     * 
     * Note that the caption is interpreted as HTML/XHTML and therefore care
     * should be taken not to enable HTML injection and XSS attacks using panel
     * captions. This behavior may change in future versions.
     * 
     * @see AbstractComponent#setCaption(String)
     */
    @Override
    public void setCaption(String caption) {
        super.setCaption(caption);
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
        markAsDirty();
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
     * @see com.vaadin.server.LegacyComponent#paintContent(com.vaadin.server
     * .PaintTarget)
     */
    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        if (actionManager != null) {
            actionManager.paintActions(null, target);
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
     *            The component to be removed.
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
    @Override
    public Iterator<Component> getComponentIterator() {
        return Collections.singleton((Component) content).iterator();
    }

    /**
     * Called when one or more variables handled by the implementing class are
     * changed.
     * 
     * @see com.vaadin.server.VariableOwner#changeVariables(Object, Map)
     */
    @Override
    @SuppressWarnings("unchecked")
    public void changeVariables(Object source, Map<String, Object> variables) {
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
            getState().setScrollLeft(newScrollX.intValue());
        }
        if (newScrollY != null && newScrollY.intValue() != getScrollTop()) {
            // set internally, not to fire request repaint
            getState().setScrollTop(newScrollY.intValue());
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
     * @see com.vaadin.server.Scrollable#setScrollable(boolean)
     */
    @Override
    public int getScrollLeft() {
        return getState().getScrollLeft();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.server.Scrollable#setScrollable(boolean)
     */
    @Override
    public int getScrollTop() {
        return getState().getScrollTop();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.server.Scrollable#setScrollLeft(int)
     */
    @Override
    public void setScrollLeft(int scrollLeft) {
        if (scrollLeft < 0) {
            throw new IllegalArgumentException(
                    "Scroll offset must be at least 0");
        }
        getState().setScrollLeft(scrollLeft);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.server.Scrollable#setScrollTop(int)
     */
    @Override
    public void setScrollTop(int scrollTop) {
        if (scrollTop < 0) {
            throw new IllegalArgumentException(
                    "Scroll offset must be at least 0");
        }
        getState().setScrollTop(scrollTop);
    }

    /* Documented in superclass */
    @Override
    public void replaceComponent(Component oldComponent, Component newComponent) {

        content.replaceComponent(oldComponent, newComponent);
    }

    /**
     * A new component is attached to container.
     * 
     * @see com.vaadin.ui.ComponentContainer.ComponentAttachListener#componentAttachedToContainer(com.vaadin.ui.ComponentContainer.ComponentAttachEvent)
     */
    @Override
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
    @Override
    public void componentDetachedFromContainer(ComponentDetachEvent event) {
        if (event.getContainer() == content) {
            fireComponentDetachEvent(event.getDetachedComponent());
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
    @Override
    protected ActionManager getActionManager() {
        if (actionManager == null) {
            actionManager = new ActionManager(this);
        }
        return actionManager;
    }

    @Override
    public <T extends Action & com.vaadin.event.Action.Listener> void addAction(
            T action) {
        getActionManager().addAction(action);
    }

    @Override
    public <T extends Action & com.vaadin.event.Action.Listener> void removeAction(
            T action) {
        if (actionManager != null) {
            actionManager.removeAction(action);
        }
    }

    @Override
    public void addActionHandler(Handler actionHandler) {
        getActionManager().addActionHandler(actionHandler);
    }

    @Override
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
        addListener(EventId.CLICK_EVENT_IDENTIFIER, ClickEvent.class, listener,
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
        removeListener(EventId.CLICK_EVENT_IDENTIFIER, ClickEvent.class,
                listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getTabIndex() {
        return getState().getTabIndex();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTabIndex(int tabIndex) {
        getState().setTabIndex(tabIndex);
    }

    /**
     * Moves keyboard focus to the component. {@see Focusable#focus()}
     * 
     */
    @Override
    public void focus() {
        super.focus();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.ComponentContainer#getComponentCount()
     */
    @Override
    public int getComponentCount() {
        // This is so wrong... (#2924)
        return content.getComponentCount();
    }

    @Override
    protected PanelState getState() {
        return (PanelState) super.getState();
    }

}
