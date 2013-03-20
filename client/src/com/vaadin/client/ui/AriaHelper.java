/*
 * Copyright 2000-2013 Vaadin Ltd.
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
    public static final String ASSISTIVE_DEVICE_ONLY_STYLE = "v-assistive-device-only";

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

        if (widget instanceof HandlesAriaCaption) {
            // Let the widget handle special cases itself
            if (captionElement == null) {
                ((HandlesAriaCaption) widget).clearAriaCaption();
            } else {
                ensureUniqueId(captionElement);
                ((HandlesAriaCaption) widget).bindAriaCaption(captionElement);
            }
        } else if (captionElement != null) {
            // Handle the default case
            ensureUniqueId(captionElement);
            String ownerId = ensureUniqueId(widget.getElement());
            captionElement.setAttribute("for", ownerId);

            Roles.getTextboxRole().setAriaLabelledbyProperty(
                    widget.getElement(), Id.of(captionElement));
        } else {
            clearCaption(widget);
        }
    }

    public static void clearCaption(Widget widget) {
        Roles.getTextboxRole()
                .removeAriaLabelledbyProperty(widget.getElement());
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
    public static String ensureUniqueId(Element element) {
        String id = element.getId();
        if (null == id || id.isEmpty()) {
            id = DOM.createUniqueId();
            element.setId(id);
        }
        return id;
    }

    /**
     * Moves an element out of sight. That way it is possible to have additional
     * information for an assistive device, that is not in the way for visual
     * users.
     * 
     * @param element
     *            Element to move out of sight
     */
    public static void visibleForAssistiveDevicesOnly(Element element) {
        element.addClassName(ASSISTIVE_DEVICE_ONLY_STYLE);
    }

    /**
     * Clears the settings that moved the element out of sight, so it is visible
     * on the page again.
     * 
     * @param element
     *            Element to clear the specific styles from
     */
    public static void visibleForAll(Element element) {
        element.removeClassName(ASSISTIVE_DEVICE_ONLY_STYLE);
    }
}
