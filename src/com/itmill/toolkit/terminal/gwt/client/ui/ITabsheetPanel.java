/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal.gwt.client.ui;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.Util;

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
    @Override
    public void add(Widget w) {
        Element el = createContainerElement();
        DOM.appendChild(getElement(), el);
        super.add(w, el);
    }

    private Element createContainerElement() {
        Element el = DOM.createDiv();
        DOM.setStyleAttribute(el, "position", "absolute");
        DOM.setStyleAttribute(el, "overflow", "auto");
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

    @Override
    public boolean remove(Widget w) {
        final int index = getWidgetIndex(w);
        final boolean removed = super.remove(w);
        if (removed) {
            if (visibleWidget == w) {
                visibleWidget = null;
            }
            Element child = DOM.getChild(getElement(), index);
            DOM.removeChild(getElement(), child);
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
        DOM.setStyleAttribute(e, "visibility", "hidden");
        DOM.setStyleAttribute(e, "top", "-100000px");
        DOM.setStyleAttribute(e, "left", "-100000px");
    }

    private void unHide(Element e) {
        DOM.setStyleAttribute(e, "top", "0px");
        DOM.setStyleAttribute(e, "left", "0px");
        DOM.setStyleAttribute(e, "visibility", "");
    }

    public void fixVisibleTabSize(int width, int height) {
        if (visibleWidget == null) {
            return;
        }

        if (height < 0) {
            height = visibleWidget.getOffsetHeight();
        }
        if (width < 0) {
            width = visibleWidget.getOffsetWidth();
        }

        // i-tabsheet-tabsheetpanel height
        getElement().getStyle().setPropertyPx("height", height);
        getElement().getStyle().setPropertyPx("width", width);

        // widget wrapper height
        Element wrapperDiv = DOM.getParent(visibleWidget.getElement());
        wrapperDiv.getStyle().setPropertyPx("height", height);
        wrapperDiv.getStyle().setPropertyPx("width", width);
    }

    public void runWebkitOverflowAutoFix() {
        if (visibleWidget != null) {
            Util.runWebkitOverflowAutoFix(DOM.getParent(visibleWidget
                    .getElement()));
        }

    }

    public void replaceComponent(Widget oldComponent, Widget newComponent) {
        boolean isVisible = (visibleWidget == oldComponent);
        int widgetIndex = getWidgetIndex(oldComponent);
        remove(oldComponent);
        insert(newComponent, widgetIndex);
        if (isVisible) {
            showWidget(widgetIndex);
        }
    }
}
