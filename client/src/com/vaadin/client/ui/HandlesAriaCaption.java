package com.vaadin.client.ui;

import com.google.gwt.user.client.Element;

/**
 * Some Widgets need to handle the caption handling for WAI-ARIA themselfs, as
 * for example the required ids need to be set in a specific way. In such a
 * case, the Widget needs to implement this interface.
 */
public interface HandlesAriaCaption {

    /**
     * Called to bind the provided caption (label in HTML speak) element to the
     * main input element of the Widget.
     * 
     * @param captionElement
     *            Element of the caption
     */
    void handleAriaCaption(Element captionElement);
}
