package com.vaadin.client.ui;

import com.google.gwt.aria.client.Id;
import com.google.gwt.aria.client.InvalidValue;
import com.google.gwt.aria.client.Roles;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;

/**
 * Helper class that helps to implement the WAI-ARIA functionality.
 */
public class AriaHelper {

    /**
     * Binds a caption (label in HTML speak) to the form element as required by
     * WAI-ARIA specification.
     * 
     * @param widget
     *            Element, that should be bound to the caption
     * @param captionElement
     *            Element of the caption
     */
    public static void bindCaption(Widget widget, Element captionElement) {
        assert widget != null : "Valid Widget required";

        if (null != captionElement) {
            ensureUniqueId(captionElement);

            if (widget instanceof HandlesAriaCaption) {
                ((HandlesAriaCaption) widget).handleAriaCaption(captionElement);
            } else {
                String ownerId = ensureUniqueId(widget.getElement());
                captionElement.setAttribute("for", ownerId);

                Roles.getTextboxRole().setAriaLabelledbyProperty(
                        widget.getElement(), Id.of(captionElement));
            }
        } else {
            Roles.getTextboxRole().removeAriaLabelledbyProperty(
                    widget.getElement());
        }
    }

    /**
     * Handles the required actions depending of the input element being
     * required or not.
     * 
     * @param inputElement
     *            Element, typically an input element
     * @param required
     *            boolean, true when the element is required
     */
    public static void handleInputRequired(Element inputElement,
            boolean required) {
        if (required) {
            Roles.getTextboxRole().setAriaRequiredProperty(inputElement, true);
        } else {
            Roles.getTextboxRole().removeAriaRequiredProperty(inputElement);
        }
    }

    /**
     * Handles the required actions depending of the input element contains
     * unaccepted input
     * 
     * @param inputElement
     *            Element, typically an input element
     * @param showError
     *            boolean, true when the element input has an error
     */
    public static void handleInputError(Element inputElement, boolean showError) {
        if (showError) {
            Roles.getTextboxRole().setAriaInvalidState(inputElement,
                    InvalidValue.TRUE);
        } else {
            Roles.getTextboxRole().removeAriaInvalidState(inputElement);
        }
    }

    /**
     * Makes sure that the provided element has an id attribute. Adds a new
     * unique id if not.
     * 
     * @param element
     *            Element to check
     * @return String with the id of the element
     */
    private static String ensureUniqueId(Element element) {
        String id = element.getId();
        if (null == id || id.isEmpty()) {
            id = DOM.createUniqueId();
            element.setId(id);
        }
        return id;
    }
}
