/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal.gwt.client.ui;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A panel that displays all of its child widgets in a 'deck', where only one
 * can be visible at a time. It is used by
 * {@link com.itmill.toolkit.terminal.gwt.client.ui.ITabsheetPanel}.
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
     *                the widget to be added
     */
    public void add(Widget w) {
        super.add(w, getElement());
        initChildWidget(w);
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
     *                the widget to be inserted
     * @param beforeIndex
     *                the index before which it will be inserted
     * @throws IndexOutOfBoundsException
     *                 if <code>beforeIndex</code> is out of range
     */
    public void insert(Widget w, int beforeIndex) {
        super.insert(w, getElement(), beforeIndex, true);
        initChildWidget(w);
    }

    public boolean remove(Widget w) {
        final boolean removed = super.remove(w);
        if (removed) {
            resetChildWidget(w);

            if (visibleWidget == w) {
                visibleWidget = null;
            }
        }
        return removed;
    }

    /**
     * Shows the widget at the specified index. This causes the currently-
     * visible widget to be hidden.
     * 
     * @param index
     *                the index of the widget to be shown
     */
    public void showWidget(int index) {
        checkIndexBoundsForAccess(index);

        if (visibleWidget != null) {
            visibleWidget.setVisible(false);
        }
        visibleWidget = getWidget(index);
        visibleWidget.setVisible(true);
    }

    /**
     * Make the widget invisible, and set its width and height to full.
     */
    private void initChildWidget(Widget w) {
        w.setVisible(false);
    }

    /**
     * Make the widget visible, and clear the widget's width and height
     * attributes. This is done so that any changes to the visibility, height,
     * or width of the widget that were done by the panel are undone.
     */
    private void resetChildWidget(Widget w) {
        w.setVisible(true);
    }
}
