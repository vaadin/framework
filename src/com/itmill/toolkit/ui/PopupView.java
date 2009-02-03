package com.itmill.toolkit.ui;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;

import com.itmill.toolkit.terminal.PaintException;
import com.itmill.toolkit.terminal.PaintTarget;

/**
 * 
 * A component for displaying a two different views to data. The minimized view
 * is normally used to render the component, and when it is clicked the full
 * view is displayed on a popup. The inner class {@link PopupView.Content} is
 * used to deliver contents to this component.
 * 
 * @author IT Mill Ltd.
 */
public class PopupView extends AbstractComponentContainer {

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
            public java.lang.String getMinimizedValueAsHTML() {
                return small;
            }

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
        if (newContent == null || newContent.getMinimizedValueAsHTML() == null
                || newContent.getPopupComponent() == null) {
            throw new IllegalArgumentException(
                    "Content object is or contains null");
        }

        content = newContent;
        requestRepaint();
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
     * @deprecated Use {@link #setPopupVisible()} instead.
     */
    @Deprecated
    public void setPopupVisibility(boolean visible) {
        setPopupVisible(visible);
    }

    /**
     * @deprecated Use {@link #isPopupVisible()} instead.
     */
    @Deprecated
    public boolean getPopupVisibility() {
        return isPopupVisible();
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
            requestRepaint();
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
     * Should the popup automaticly hide when the user takes the mouse cursor
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
     * @see com.itmill.toolkit.ui.ComponentContainer#getComponentIterator()
     */
    public Iterator<Component> getComponentIterator() {
        return new Iterator<Component>() {

            private boolean first = visibleComponent == null;

            public boolean hasNext() {
                return !first;
            }

            public Component next() {
                if (!first) {
                    first = true;
                    return visibleComponent;
                } else {
                    return null;
                }
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };

    }

    /**
     * Not supported in this implementation.
     * 
     * @see com.itmill.toolkit.ui.AbstractComponentContainer#removeAllComponents()
     * @throws UnsupportedOperationException
     */
    @Override
    public void removeAllComponents() {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported in this implementation.
     * 
     * @see com.itmill.toolkit.ui.AbstractComponentContainer#moveComponentsFrom(com.itmill.toolkit.ui.ComponentContainer)
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
     * @see com.itmill.toolkit.ui.AbstractComponentContainer#addComponent(com.itmill.toolkit.ui.Component)
     * @throws UnsupportedOperationException
     */
    @Override
    public void addComponent(Component c) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();

    }

    /**
     * Not supported in this implementation.
     * 
     * @see com.itmill.toolkit.ui.ComponentContainer#replaceComponent(com.itmill.toolkit.ui.Component,
     *      com.itmill.toolkit.ui.Component)
     * @throws UnsupportedOperationException
     */
    public void replaceComponent(Component oldComponent, Component newComponent)
            throws UnsupportedOperationException {

        throw new UnsupportedOperationException();
    }

    /**
     * Not supported in this implementation
     * 
     * @see com.itmill.toolkit.ui.AbstractComponentContainer#removeComponent(com.itmill.toolkit.ui.Component)
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
     * @see com.itmill.toolkit.ui.AbstractComponent#getTag()
     */
    @Override
    public java.lang.String getTag() {
        return "popupview";
    }

    /**
     * Paint (serialize) the component for the client.
     * 
     * @see com.itmill.toolkit.ui.AbstractComponent#paintContent(com.itmill.toolkit.terminal.PaintTarget)
     */
    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        // Superclass writes any common attributes in the paint target.
        super.paintContent(target);

        String html = content.getMinimizedValueAsHTML();
        if (html == null) {
            throw new PaintException(
                    "Recieved null when trying to paint minimized value.");
        }
        target.addAttribute("html", content.getMinimizedValueAsHTML());
        target.addAttribute("hideOnMouseOut", hideOnMouseOut);

        // Only paint component to client if we know that the popup is showing
        if (isPopupVisible()) {
            target.startTag("popupComponent");
            visibleComponent.paint(target);
            target.endTag("popupComponent");
        }

        target.addVariable(this, "popupVisibility", isPopupVisible());
    }

    /**
     * Deserialize changes received from client.
     * 
     * @see com.itmill.toolkit.ui.AbstractComponent#changeVariables(java.lang.Object,
     *      java.util.Map)
     */
    @Override
    public void changeVariables(Object source, Map variables) {
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
    public interface Content {

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
    public void addListener(PopupVisibilityListener listener) {
        addListener(PopupVisibilityEvent.class, listener,
                POPUP_VISIBILITY_METHOD);
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
    public void removeListener(PopupVisibilityListener listener) {
        removeListener(PopupVisibilityEvent.class, listener,
                POPUP_VISIBILITY_METHOD);
    }

    /**
     * This event is received by the PopupVisibilityListeners when the
     * visibility of the popup changes. You can get the new visibility directly
     * with {@link #isPopupVisible()}, or get the PopupView that produced the
     * event with {@link #getPopupView()}.
     * 
     */
    public class PopupVisibilityEvent extends Event {
        /**
         * Serial generated by eclipse.
         */
        private static final long serialVersionUID = -130167162207143457L;

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
    public interface PopupVisibilityListener {
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
