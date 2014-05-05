/*
 * Copyright 2000-2014 Vaadin Ltd.
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

package com.vaadin.client.ui.aria;

import com.google.gwt.aria.client.Id;
import com.google.gwt.aria.client.InvalidValue;
import com.google.gwt.aria.client.Roles;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
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
     *            Widget, that should be bound to the caption
     * @param captionElements
     *            Element with of caption to bind
     */
    public static void bindCaption(Widget widget, Element captionElement) {
        assert widget != null : "Valid Widget required";

        if (widget instanceof HandlesAriaCaption) {
            // Let the widget handle special cases itself
            if (captionElement == null) {
                ((HandlesAriaCaption) widget).bindAriaCaption(null);
            } else {
                ensureHasId(captionElement);
                ((HandlesAriaCaption) widget).bindAriaCaption(DOM
                        .asOld(captionElement));
            }
        } else if (captionElement != null) {
            // Handle the default case
            ensureHasId(captionElement);
            String ownerId = ensureHasId(widget.getElement());
            captionElement.setAttribute("for", ownerId);

            Roles.getTextboxRole().setAriaLabelledbyProperty(
                    widget.getElement(), Id.of(captionElement));
        } else {
            clearCaption(widget);
        }
    }

    /**
     * Removes a binding to a caption added with bindCaption() from the provided
     * Widget.
     * 
     * @param widget
     *            Widget, that was bound to a caption before
     */
    private static void clearCaption(Widget widget) {
        Roles.getTextboxRole()
                .removeAriaLabelledbyProperty(widget.getElement());
    }

    /**
     * Handles the required actions depending of the input Widget being required
     * or not.
     * 
     * @param widget
     *            Widget, typically an input Widget like TextField
     * @param required
     *            boolean, true when the element is required
     */
    public static void handleInputRequired(Widget widget, boolean required) {
        assert widget != null : "Valid Widget required";

        if (widget instanceof HandlesAriaRequired) {
            ((HandlesAriaRequired) widget).setAriaRequired(required);
        } else {
            handleInputRequired(widget.getElement(), required);
        }
    }

    /**
     * Handles the required actions depending of the input element being
     * required or not.
     * 
     * @param element
     *            Element, typically from an input Widget like TextField
     * @param required
     *            boolean, true when the element is required
     */
    public static void handleInputRequired(Element element, boolean required) {
        if (required) {
            Roles.getTextboxRole().setAriaRequiredProperty(element, required);
        } else {
            Roles.getTextboxRole().removeAriaRequiredProperty(element);
        }
    }

    /**
     * Handles the required actions depending of the input Widget contains
     * unaccepted input.
     * 
     * @param widget
     *            Widget, typically an input Widget like TextField
     * @param invalid
     *            boolean, true when the Widget input has an error
     */
    public static void handleInputInvalid(Widget widget, boolean invalid) {
        assert widget != null : "Valid Widget required";

        if (widget instanceof HandlesAriaInvalid) {
            ((HandlesAriaInvalid) widget).setAriaInvalid(invalid);
        } else {
            handleInputInvalid(widget.getElement(), invalid);
        }
    }

    /**
     * Handles the required actions depending of the input element contains
     * unaccepted input.
     * 
     * @param element
     *            Element, typically an input Widget like TextField
     * @param invalid
     *            boolean, true when the element input has an error
     */
    public static void handleInputInvalid(Element element, boolean invalid) {
        if (invalid) {
            Roles.getTextboxRole().setAriaInvalidState(element,
                    InvalidValue.TRUE);
        } else {
            Roles.getTextboxRole().removeAriaInvalidState(element);
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
    public static String ensureHasId(Element element) {
        assert element != null : "Valid Element required";

        String id = element.getId();
        if (null == id || id.isEmpty()) {
            id = DOM.createUniqueId();
            element.setId(id);
        }
        return id;
    }

    /**
     * Allows to move an element out of the visible area of the browser window.
     * 
     * This makes it possible to have additional information for an assistive
     * device, that is not in the way for visual users.
     * 
     * @param element
     *            Element to move out of sight
     * @param boolean assistiveOnly true when element should only be visible for
     *        assistive devices, false to make the element visible for all
     */
    public static void setVisibleForAssistiveDevicesOnly(Element element,
            boolean assistiveOnly) {
        if (assistiveOnly) {
            element.addClassName(ASSISTIVE_DEVICE_ONLY_STYLE);
        } else {
            element.removeClassName(ASSISTIVE_DEVICE_ONLY_STYLE);
        }
    }
}
