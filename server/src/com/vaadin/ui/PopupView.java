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

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;

import com.vaadin.server.LegacyPaint;
import com.vaadin.server.PaintException;
import com.vaadin.server.PaintTarget;

/**
 * 
 * A component for displaying a two different views to data. The minimized view
 * is normally used to render the component, and when it is clicked the full
 * view is displayed on a popup. The inner class {@link PopupView.Content} is
 * used to deliver contents to this component.
 * 
 * @author Vaadin Ltd.
 */
@SuppressWarnings("serial")
public class PopupView extends AbstractComponentContainer implements
        LegacyComponent {

    private Content content;
    private boolean hideOnMouseOut;
    private Component visibleComponent;

    private static final Method POPUP_VISIBILITY_METHOD;
    static {
        try {
            POPUP_VISIBILITY_METHOD = PopupVisibilityListener.class
                    .getDeclaredMethod("popupVisibilityChange",
                            new Class[] { PopupVisibilityEvent.class });
        } catch (final java.lang.NoSuchMethodException e) {
            // This should never happen
            throw new java.lang.RuntimeException(
                    "Internal error finding methods in PopupView");
        }
    }

    /**
     * Iterator for the visible components (zero or one components), used by
     * {@link PopupView#getComponentIterator()}.
     */
    private static class SingleComponentIterator implements
            Iterator<Component>, Serializable {

        private final Component component;
        private boolean first;

        public SingleComponentIterator(Component component) {
            this.component = component;
            first = (component == null);
        }

        @Override
        public boolean hasNext() {
            return !first;
        }

        @Override
        public Component next() {
            if (!first) {
                first = true;
                return component;
            } else {
                return null;
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    /* Constructors */

    /**
     * A simple way to create a PopupPanel. Note that the minimal representation
     * may not be dynamically updated, in order to achieve this create your own
     * Content object and use {@link PopupView#PopupView(Content)}.
     * 
     * @param small
     *            the minimal textual representation as HTML
     * @param large
     *            the full, Component-type representation
     */
    public PopupView(final java.lang.String small, final Component large) {
        this(new PopupView.Content() {
            @Override
            public java.lang.String getMinimizedValueAsHTML() {
                return small;
            }

            @Override
            public Component getPopupComponent() {
                return large;
            }
        });

    }

    /**
     * Creates a PopupView through the PopupView.Content interface. This allows
     * the creator to dynamically change the contents of the PopupView.
     * 
     * @param content
     *            the PopupView.Content that contains the information for this
     */
    public PopupView(PopupView.Content content) {
        super();
        hideOnMouseOut = true;
        setContent(content);
    }

    /**
     * This method will replace the current content of the panel with a new one.
     * 
     * @param newContent
     *            PopupView.Content object containing new information for the
     *            PopupView
     * @throws IllegalArgumentException
     *             if the method is passed a null value, or if one of the
     *             content methods returns null
     */
    public void setContent(PopupView.Content newContent)
            throws IllegalArgumentException {
        if (newContent == null) {
            throw new IllegalArgumentException("Content must not be null");
        }
        content = newContent;
        markAsDirty();
    }

    /**
     * Returns the content-package for this PopupView.
     * 
     * @return the PopupView.Content for this object or null
     */
    public PopupView.Content getContent() {
        return content;
    }

    /**
     * Set the visibility of the popup. Does not hide the minimal
     * representation.
     * 
     * @param visible
     */
    public void setPopupVisible(boolean visible) {
        if (isPopupVisible() != visible) {
            if (visible) {
                visibleComponent = content.getPopupComponent();
                if (visibleComponent == null) {
                    throw new java.lang.IllegalStateException(
                            "PopupView.Content did not return Component to set visible");
                }
                super.addComponent(visibleComponent);
            } else {
                super.removeComponent(visibleComponent);
                visibleComponent = null;
            }
            fireEvent(new PopupVisibilityEvent(this));
            markAsDirty();
        }
    }

    /**
     * Return whether the popup is visible.
     * 
     * @return true if the popup is showing
     */
    public boolean isPopupVisible() {
        return visibleComponent != null;
    }

    /**
     * Check if this popup will be hidden when the user takes the mouse cursor
     * out of the popup area.
     * 
     * @return true if the popup is hidden on mouse out, false otherwise
     */
    public boolean isHideOnMouseOut() {
        return hideOnMouseOut;
    }

    /**
     * Should the popup automatically hide when the user takes the mouse cursor
     * out of the popup area? If this is false, the user must click outside the
     * popup to close it. The default is true.
     * 
     * @param hideOnMouseOut
     * 
     */
    public void setHideOnMouseOut(boolean hideOnMouseOut) {
        this.hideOnMouseOut = hideOnMouseOut;
    }

    /*
     * Methods inherited from AbstractComponentContainer. These are unnecessary
     * (but mandatory). Most of them are not supported in this implementation.
     */

    /**
     * This class only contains other components when the popup is showing.
     * 
     * @see com.vaadin.ui.ComponentContainer#getComponentIterator()
     */
    @Override
    public Iterator<Component> getComponentIterator() {
        return new SingleComponentIterator(visibleComponent);
    }

    /**
     * Gets the number of contained components. Consistent with the iterator
     * returned by {@link #getComponentIterator()}.
     * 
     * @return the number of contained components (zero or one)
     */
    @Override
    public int getComponentCount() {
        return (visibleComponent != null ? 1 : 0);
    }

    /**
     * Not supported in this implementation.
     * 
     * @see com.vaadin.ui.AbstractComponentContainer#removeAllComponents()
     * @throws UnsupportedOperationException
     */
    @Override
    public void removeAllComponents() {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported in this implementation.
     * 
     * @see com.vaadin.ui.AbstractComponentContainer#moveComponentsFrom(com.vaadin.ui.ComponentContainer)
     * @throws UnsupportedOperationException
     */
    @Override
    public void moveComponentsFrom(ComponentContainer source)
            throws UnsupportedOperationException {

        throw new UnsupportedOperationException();
    }

    /**
     * Not supported in this implementation.
     * 
     * @see com.vaadin.ui.AbstractComponentContainer#addComponent(com.vaadin.ui.Component)
     * @throws UnsupportedOperationException
     */
    @Override
    public void addComponent(Component c) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();

    }

    /**
     * Not supported in this implementation.
     * 
     * @see com.vaadin.ui.ComponentContainer#replaceComponent(com.vaadin.ui.Component,
     *      com.vaadin.ui.Component)
     * @throws UnsupportedOperationException
     */
    @Override
    public void replaceComponent(Component oldComponent, Component newComponent)
            throws UnsupportedOperationException {

        throw new UnsupportedOperationException();
    }

    /**
     * Not supported in this implementation
     * 
     * @see com.vaadin.ui.AbstractComponentContainer#removeComponent(com.vaadin.ui.Component)
     */
    @Override
    public void removeComponent(Component c)
            throws UnsupportedOperationException {
        throw new UnsupportedOperationException();

    }

    /*
     * Methods for server-client communications.
     */

    /**
     * Paint (serialize) the component for the client.
     * 
     * @see com.vaadin.ui.AbstractComponent#paintContent(com.vaadin.server.PaintTarget)
     */
    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        String html = content.getMinimizedValueAsHTML();
        if (html == null) {
            html = "";
        }
        target.addAttribute("html", html);
        target.addAttribute("hideOnMouseOut", hideOnMouseOut);

        // Only paint component to client if we know that the popup is showing
        if (isPopupVisible()) {
            target.startTag("popupComponent");
            LegacyPaint.paint(visibleComponent, target);
            target.endTag("popupComponent");
        }

        target.addVariable(this, "popupVisibility", isPopupVisible());
    }

    /**
     * Deserialize changes received from client.
     * 
     * @see com.vaadin.ui.AbstractComponent#changeVariables(java.lang.Object,
     *      java.util.Map)
     */
    @Override
    public void changeVariables(Object source, Map<String, Object> variables) {
        if (variables.containsKey("popupVisibility")) {
            setPopupVisible(((Boolean) variables.get("popupVisibility"))
                    .booleanValue());
        }
    }

    /**
     * Used to deliver customized content-packages to the PopupView. These are
     * dynamically loaded when they are redrawn. The user must take care that
     * neither of these methods ever return null.
     */
    public interface Content extends Serializable {

        /**
         * This should return a small view of the full data.
         * 
         * @return value in HTML format
         */
        public String getMinimizedValueAsHTML();

        /**
         * This should return the full Component representing the data
         * 
         * @return a Component for the value
         */
        public Component getPopupComponent();
    }

    /**
     * Add a listener that is called whenever the visibility of the popup is
     * changed.
     * 
     * @param listener
     *            the listener to add
     * @see PopupVisibilityListener
     * @see PopupVisibilityEvent
     * @see #removeListener(PopupVisibilityListener)
     * 
     */
    public void addPopupVisibilityListener(PopupVisibilityListener listener) {
        addListener(PopupVisibilityEvent.class, listener,
                POPUP_VISIBILITY_METHOD);
    }

    /**
     * @deprecated Since 7.0, replaced by
     *             {@link #addPopupVisibilityListener(PopupVisibilityListener)}
     **/
    @Deprecated
    public void addListener(PopupVisibilityListener listener) {
        addPopupVisibilityListener(listener);
    }

    /**
     * Removes a previously added listener, so that it no longer receives events
     * when the visibility of the popup changes.
     * 
     * @param listener
     *            the listener to remove
     * @see PopupVisibilityListener
     * @see #addListener(PopupVisibilityListener)
     */
    public void removePopupVisibilityListener(PopupVisibilityListener listener) {
        removeListener(PopupVisibilityEvent.class, listener,
                POPUP_VISIBILITY_METHOD);
    }

    /**
     * @deprecated Since 7.0, replaced by
     *             {@link #removePopupVisibilityListener(PopupVisibilityListener)}
     **/
    @Deprecated
    public void removeListener(PopupVisibilityListener listener) {
        removePopupVisibilityListener(listener);
    }

    /**
     * This event is received by the PopupVisibilityListeners when the
     * visibility of the popup changes. You can get the new visibility directly
     * with {@link #isPopupVisible()}, or get the PopupView that produced the
     * event with {@link #getPopupView()}.
     * 
     */
    public static class PopupVisibilityEvent extends Event {

        public PopupVisibilityEvent(PopupView source) {
            super(source);
        }

        /**
         * Get the PopupView instance that is the source of this event.
         * 
         * @return the source PopupView
         */
        public PopupView getPopupView() {
            return (PopupView) getSource();
        }

        /**
         * Returns the current visibility of the popup.
         * 
         * @return true if the popup is visible
         */
        public boolean isPopupVisible() {
            return getPopupView().isPopupVisible();
        }
    }

    /**
     * Defines a listener that can receive a PopupVisibilityEvent when the
     * visibility of the popup changes.
     * 
     */
    public interface PopupVisibilityListener extends Serializable {
        /**
         * Pass to {@link PopupView#PopupVisibilityEvent} to start listening for
         * popup visibility changes.
         * 
         * @param event
         *            the event
         * 
         * @see {@link PopupVisibilityEvent}
         * @see {@link PopupView#addListener(PopupVisibilityListener)}
         */
        public void popupVisibilityChange(PopupVisibilityEvent event);
    }
}
