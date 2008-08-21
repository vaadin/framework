package com.itmill.toolkit.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import com.itmill.toolkit.terminal.PaintException;
import com.itmill.toolkit.terminal.PaintTarget;

public class PopupView extends AbstractComponentContainer {

    Content itsContent;
    boolean popupVisible;
    ArrayList componentList;

    /* Constructors */

    /**
     * A simple way to create a PopupPanel. Note that the minimal representation
     * may not be dynamically updated, to achieve this create your own Content
     * object.
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
    public PopupView(final PopupView.Content content) {
        super();
        itsContent = content;
        popupVisible = false;
        componentList = new ArrayList(1);
    }

    /**
     * This method will replace the current content of the panel with a new one.
     * Give null to remove current content.
     * 
     * @param newContent
     *            PopupView.Content object containing new information for the
     *            PopupView
     * 
     */
    public void setContent(PopupView.Content newContent) {
        itsContent = newContent;
        requestRepaint();
    }

    /**
     * Returns the content-package for this PopupView. Returns null if the
     * PopupView has no content.
     * 
     * @return the PopupView.Content for this object or null
     */
    public PopupView.Content getContent() {
        return itsContent;
    }

    /**
     * Return whether the popup is visible.
     * 
     * @return true if the popup is showing
     */
    public boolean getPopupVisibility() {
        return popupVisible;
    }

    /*
     * Methods inherited from AbstractComponentContainer. These are unnecessary
     * (but mandatory). They are not supported in this implementation.
     */

    /**
     * Not supported in this implementation. Will return an empty iterator.
     * 
     * @see com.itmill.toolkit.ui.ComponentContainer#getComponentIterator()
     */
    public Iterator getComponentIterator() {
        return componentList.iterator();

    }

    /**
     * Not supported in this implementation.
     * 
     * @see com.itmill.toolkit.ui.AbstractComponentContainer#removeAllComponents()
     * @throws UnsupportedOperationException
     */
    public void removeAllComponents() {
        throw new UnsupportedOperationException();
    }

    /**
     * In this implementation, moveComponents is not supported. It always throws
     * UnsupportedOperationException.
     * 
     * @see com.itmill.toolkit.ui.AbstractComponentContainer#moveComponentsFrom(com.itmill.toolkit.ui.ComponentContainer)
     * @throws UnsupportedOperationException
     */
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
    public void addComponent(Component c) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();

    }

    /**
     * Not supported in this implementation. Always throws
     * UnsupportedOperationException.
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
    public java.lang.String getTag() {
        return "popupview";
    }

    /**
     * Paint (serialize) the component for the client.
     * 
     * @see com.itmill.toolkit.ui.AbstractComponent#paintContent(com.itmill.toolkit.terminal.PaintTarget)
     */
    public void paintContent(PaintTarget target) throws PaintException {
        // Superclass writes any common attributes in the paint target.
        super.paintContent(target);

        target.addAttribute("html", itsContent.getMinimizedValueAsHTML());
        target.addAttribute("popupVisible", popupVisible);

        if (popupVisible) {
            Component c = itsContent.getPopupComponent();

            target.startTag("popupComponent");
            c.paint(target);
            target.endTag("popupComponent");
        }

    }

    /**
     * Deserialize changes received from client.
     * 
     * @see com.itmill.toolkit.ui.AbstractComponent#changeVariables(java.lang.Object,
     *      java.util.Map)
     */
    public void changeVariables(Object source, Map variables) {
        if (variables.containsKey("popupVisibility")) {
            popupVisible = ((Boolean) variables.get("popupVisibility"))
                    .booleanValue();

            if (popupVisible) {
                componentList.add(itsContent.getPopupComponent());
            } else {
                componentList.clear();
            }
            requestRepaint();
        }
    }

    /**
     * Used to deliver customized content-packages to the PopupView. These are
     * dynamically loaded when they are redrawn.
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
