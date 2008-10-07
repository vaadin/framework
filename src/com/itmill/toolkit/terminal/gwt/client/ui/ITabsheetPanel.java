/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal.gwt.client.ui;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A panel that displays all of its child widgets in a 'deck', where only one
 * can be visible at a time. It is used by
 * {@link com.itmill.toolkit.terminal.gwt.client.ui.ITabsheet}.
 * 
 * This class has the same basic functionality as the GWT DeckPanel
 * {@link com.google.gwt.user.client.ui.DeckPanel}, with the exception that it
 * doesn't manipulate the child widgets' width and height attributes.
 */
public class ITabsheetPanel extends ComplexPanel {

    private Widget visibleWidget;
    private int contentHeight;

    /**
     * Creates an empty tabsheet panel.
     */
    public ITabsheetPanel() {
        setElement(DOM.createDiv());
    }

    /**
     * Adds the specified widget to the deck.
     * 
     * @param w
     *            the widget to be added
     */
    public void add(Widget w) {
        Element el = createContainerElement();
        DOM.appendChild(getElement(), el);
        super.add(w, el);
    }

    private Element createContainerElement() {
        Element el = DOM.createDiv();
        hide(el);
        return el;
    }

    /**
     * Gets the index of the currently-visible widget.
     * 
     * @return the visible widget's index
     */
    public int getVisibleWidget() {
        return getWidgetIndex(visibleWidget);
    }

    /**
     * Inserts a widget before the specified index.
     * 
     * @param w
     *            the widget to be inserted
     * @param beforeIndex
     *            the index before which it will be inserted
     * @throws IndexOutOfBoundsException
     *             if <code>beforeIndex</code> is out of range
     */
    public void insert(Widget w, int beforeIndex) {
        Element el = createContainerElement();
        DOM.insertChild(getElement(), el, beforeIndex);
        super.insert(w, el, beforeIndex, false);
    }

    public boolean remove(Widget w) {
        final int index = getWidgetIndex(w);
        final boolean removed = super.remove(w);
        if (removed) {
            if (visibleWidget == w) {
                visibleWidget = null;
            }
            Element child = DOM.getChild(getElement(), index);
            DOM.removeChild(getElement(), child);
            unHide(child);
        }
        return removed;
    }

    /**
     * Shows the widget at the specified index. This causes the currently-
     * visible widget to be hidden.
     * 
     * @param index
     *            the index of the widget to be shown
     */
    public void showWidget(int index) {
        checkIndexBoundsForAccess(index);
        Widget newVisible = getWidget(index);
        if (visibleWidget != newVisible) {
            if (visibleWidget != null) {
                hide(DOM.getParent(visibleWidget.getElement()));
            }
            visibleWidget = newVisible;
            unHide(DOM.getParent(visibleWidget.getElement()));
        }
    }

    private void hide(Element e) {
        DOM.setStyleAttribute(e, "width", "0px");
        DOM.setStyleAttribute(e, "height", "0px");
        DOM.setStyleAttribute(e, "overflow", "hidden");
        DOM.setStyleAttribute(e, "visibility", "hidden");
        DOM.setStyleAttribute(e, "position", "absolute");
        DOM.setStyleAttribute(e, "top", "0px");
        DOM.setStyleAttribute(e, "left", "0px");
    }

    private void unHide(Element e) {
        DOM.setStyleAttribute(e, "marginLeft", "0px");
        DOM.setStyleAttribute(e, "marginTop", "0px");
        DOM.setStyleAttribute(e, "position", "");
        DOM.setStyleAttribute(e, "top", "");
        DOM.setStyleAttribute(e, "left", "");
        DOM.setStyleAttribute(e, "visibility", "");
        DOM.setStyleAttribute(e, "width", "");
        DOM.setStyleAttribute(e, "height", "");
        DOM.setStyleAttribute(e, "overflow", "");

    }

}
