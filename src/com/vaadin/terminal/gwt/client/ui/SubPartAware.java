/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ComponentLocator;

/**
 * Interface implemented by {@link Widget}s which can provide identifiers for at
 * least one element inside the component. Used by {@link ComponentLocator}.
 * 
 */
public interface SubPartAware {

    /**
     * Locates an element inside a component using the identifier provided in
     * {@code subPart}. The {@code subPart} identifier is component specific and
     * may be any string of characters, numbers, space characters and brackets.
     * 
     * @param subPart
     *            The identifier for the element inside the component
     * @return The element identified by subPart or null if the element could
     *         not be found.
     */
    Element getSubPartElement(String subPart);

    /**
     * Provides an identifier that identifies the element within the component.
     * The {@code subElement} is a part of the component and must never be null.
     * <p>
     * <b>Note!</b>
     * {@code getSubPartElement(getSubPartName(element)) == element} is <i>not
     * always</i> true. A component can choose to provide a more generic
     * identifier for any given element if the results of all interactions with
     * {@code subElement} are the same as interactions with the element
     * identified by the return value. For example a button can return an
     * identifier for the root element even though a DIV inside the button was
     * passed as {@code subElement} because interactions with the DIV and the
     * root button element produce the same result.
     * 
     * @param subElement
     *            The element the identifier string should uniquely identify
     * @return An identifier that uniquely identifies {@code subElement} or null
     *         if no identifier could be provided.
     */
    String getSubPartName(Element subElement);

}