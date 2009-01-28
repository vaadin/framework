package com.itmill.toolkit.ui;

import java.util.ArrayList;
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
    private boolean popupVisible;
    private boolean hideOnMouseOut;
    private final ArrayList<Component> componentList;

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
        popupVisible = false;
        hideOnMouseOut = true;
        componentList = new ArrayList<Component>(1);
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
    public void setPopupVisibility(boolean visible) {
        popupVisible = visible;
        requestRepaint();
    }

    /**
     * @deprecated Use {@link #isPopupVisible()} instead.
     */
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
        setPopupVisible(visible);
    }

    /**
     * Return whether the popup is visible.
     * 
     * @return true if the popup is showing
     */
    public boolean isPopupVisible() {
        return popupVisible;
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
        return componentList.iterator();

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
        if (popupVisible) {
            Component c = componentList.get(0);

            if (c == null) {
                c = content.getPopupComponent();
                if (c != null) {
                    componentList.add(c);
                    super.addComponent(c);
                }
            }

            if (c == null) {
                throw new PaintException(
                        "Received null when trying to paint popup component");
            }

            target.startTag("popupComponent");
            c.paint(target);
            target.endTag("popupComponent");
        }

        target.addVariable(this, "popupVisibility", popupVisible);
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

            popupVisible = ((Boolean) variables.get("popupVisibility"))
                    .booleanValue();

            if (popupVisible) {
                if (componentList.isEmpty()) {
                    Component c = content.getPopupComponent();
                    if (c != null) {
                        componentList.add(c);
                        super.addComponent(c);
                    } else {
                        popupVisible = false;
                    }
                }
            } else if (!componentList.isEmpty()) {
                super.removeComponent(componentList.get(0));
                componentList.clear();
            }
            requestRepaint();
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
}
