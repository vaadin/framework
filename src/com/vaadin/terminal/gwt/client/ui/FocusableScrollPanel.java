package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.event.dom.client.HasScrollHandlers;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.ScrollPanel;

/**
 * A scrollhandlers similar to {@link ScrollPanel}.
 * 
 */
public class FocusableScrollPanel extends SimpleFocusablePanel implements
        HasScrollHandlers {

    public FocusableScrollPanel() {
        // Prevent IE standard mode bug when a AbsolutePanel is contained.
        Style style = getElement().getStyle();
        style.setOverflow(Overflow.AUTO);
        style.setProperty("zoom", "1");
        style.setPosition(Position.RELATIVE);
    }

    public HandlerRegistration addScrollHandler(ScrollHandler handler) {
        return addDomHandler(handler, ScrollEvent.getType());
    }

    /**
     * Gets the horizontal scroll position.
     * 
     * @return the horizontal scroll position, in pixels
     */
    public int getHorizontalScrollPosition() {
        return getElement().getScrollLeft();
    }

    /**
     * Gets the vertical scroll position.
     * 
     * @return the vertical scroll position, in pixels
     */
    public int getScrollPosition() {
        return getElement().getScrollTop();
    }

    /**
     * Sets the horizontal scroll position.
     * 
     * @param position
     *            the new horizontal scroll position, in pixels
     */
    public void setHorizontalScrollPosition(int position) {
        getElement().setScrollLeft(position);
    }

    /**
     * Sets the vertical scroll position.
     * 
     * @param position
     *            the new vertical scroll position, in pixels
     */
    public void setScrollPosition(int position) {
        getElement().setScrollTop(position);
    }

}
