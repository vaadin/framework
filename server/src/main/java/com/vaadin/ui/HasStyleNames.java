/*
 * Copyright 2000-2021 Vaadin Ltd.
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
package com.vaadin.ui;

import java.io.Serializable;

/**
 * Implemented by components which support style names.
 *
 * <p>
 * Each style name will occur only once as specified and it is not prefixed with
 * the style name of the component.
 * </p>
 *
 * @since 8.7
 */
public interface HasStyleNames extends Serializable {

    /**
     * Gets all user-defined CSS style names of a component. If the component
     * has multiple style names defined, the return string is a space-separated
     * list of style names. Built-in style names defined in Vaadin or GWT are
     * not returned.
     *
     * <p>
     * The style names are returned only in the basic form in which they were
     * added.
     * </p>
     *
     * @since 8.7
     * @return the style name or a space-separated list of user-defined style
     *         names of the component
     * @see #setStyleName(String)
     * @see #addStyleName(String)
     * @see #removeStyleName(String)
     */
    String getStyleName();

    /**
     * Sets one or more user-defined style names of the component, replacing any
     * previous user-defined styles. Multiple styles can be specified as a
     * space-separated list of style names. The style names must be valid CSS
     * class names.
     *
     * <p>
     * It is normally a good practice to use {@link #addStyleName(String)
     * addStyleName()} rather than this setter, as different software
     * abstraction layers can then add their own styles without accidentally
     * removing those defined in other layers.
     * </p>
     *
     * @since 8.7
     * @param style
     *            the new style or styles of the component as a space-separated
     *            list
     * @see #getStyleName()
     * @see #addStyleName(String)
     * @see #removeStyleName(String)
     */
    void setStyleName(String style);

    /**
     * Adds or removes a style name. Multiple styles can be specified as a
     * space-separated list of style names.
     *
     * If the {@code add} parameter is true, the style name is added to the
     * component. If the {@code add} parameter is false, the style name is
     * removed from the component.
     * <p>
     * Functionally this is equivalent to using {@link #addStyleName(String)} or
     * {@link #removeStyleName(String)}
     *
     * @since 8.7
     * @param style
     *            the style name to be added or removed
     * @param add
     *            <code>true</code> to add the given style, <code>false</code>
     *            to remove it
     * @see #addStyleName(String)
     * @see #removeStyleName(String)
     */
    default void setStyleName(String style, boolean add) {
        if (add) {
            addStyleName(style);
        } else {
            removeStyleName(style);
        }
    }

    /**
     * Adds one or more style names to this component. Multiple styles can be
     * specified as a space-separated list of style names. The style name will
     * be rendered as a HTML class name, which can be used in a CSS definition.
     *
     *
     * @since 8.7
     * @param style
     *            the new style to be added to the component
     * @see #getStyleName()
     * @see #setStyleName(String)
     * @see #removeStyleName(String)
     */
    void addStyleName(String style);

    /**
     * Adds one or more style names to this component by using one or multiple
     * parameters.
     *
     * @since 8.7
     * @param styles
     *            the style name or style names to be added to the component
     * @see #addStyleName(String)
     * @see #setStyleName(String)
     * @see #removeStyleName(String)
     */
    default void addStyleNames(String... styles) {
        for (String style : styles) {
            addStyleName(style);
        }
    }

    /**
     * Removes one or more style names from component. Multiple styles can be
     * specified as a space-separated list of style names.
     *
     * <p>
     * The parameter must be a valid CSS style name. Only user-defined style
     * names added with {@link #addStyleName(String) addStyleName()} or
     * {@link #setStyleName(String) setStyleName()} can be removed; built-in
     * style names defined in Vaadin or GWT can not be removed.
     * </p>
     *
     * @since 8.7
     * @param style
     *            the style name or style names to be removed
     * @see #getStyleName()
     * @see #setStyleName(String)
     * @see #addStyleName(String)
     */
    void removeStyleName(String style);

    /**
     * Removes one or more style names from component. Multiple styles can be
     * specified by using multiple parameters.
     *
     * <p>
     * The parameter must be a valid CSS style name. Only user-defined style
     * names added with {@link #addStyleName(String) addStyleName()} or
     * {@link #setStyleName(String) setStyleName()} can be removed; built-in
     * style names defined in Vaadin or GWT can not be removed.
     * </p>
     *
     * @since 8.7
     * @param styles
     *            the style name or style names to be removed
     * @see #removeStyleName(String)
     * @see #setStyleName(String)
     * @see #addStyleName(String)
     */
    default void removeStyleNames(String... styles) {
        for (String style : styles) {
            removeStyleName(style);
        }
    }

}
