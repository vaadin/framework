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
package com.vaadin.client.componentlocator;

import java.util.List;

import com.google.gwt.dom.client.Element;

/**
 * This interface should be implemented by all locator strategies. A locator
 * strategy is responsible for generating and decoding a string that identifies
 * an element in the DOM. A strategy can implement its own syntax for the
 * locator string, which may be completely different from any other strategy's
 * syntax.
 * 
 * @since 7.2
 * @author Vaadin Ltd
 */
public interface LocatorStrategy {

    /**
     * Test the given input path for formatting errors. If a given path can not
     * be validated, the locator strategy will not be attempted.
     * 
     * @param path
     *            a locator path expression
     * @return true, if the implementing class can process the given path,
     *         otherwise false
     */
    boolean validatePath(String path);

    /**
     * Generates a String locator which uniquely identifies the target element.
     * The {@link #getElementByPath(String)} method can be used for the inverse
     * operation, i.e. locating an element based on the return value from this
     * method.
     * <p>
     * Note that getElementByPath(getPathForElement(element)) == element is not
     * always true as #getPathForElement(Element) can return a path to another
     * element if the widget determines an action on the other element will give
     * the same result as the action on the target element.
     * </p>
     * 
     * @param targetElement
     *            The element to generate a path for.
     * @return A String locator that identifies the target element or null if a
     *         String locator could not be created.
     */
    String getPathForElement(Element targetElement);

    /**
     * Locates an element using a String locator (path) which identifies a DOM
     * element. The {@link #getPathForElement(Element)} method can be used for
     * the inverse operation, i.e. generating a string expression for a DOM
     * element.
     * 
     * @param path
     *            The String locator which identifies the target element.
     * @return The DOM element identified by {@code path} or null if the element
     *         could not be located.
     */
    Element getElementByPath(String path);

    /**
     * Locates an element using a String locator (path) which identifies a DOM
     * element. The path starts from the specified root element.
     * 
     * @see #getElementByPath(String)
     * 
     * @param path
     *            The String locator which identifies the target element.
     * @param root
     *            The element that is at the root of the path.
     * @return The DOM element identified by {@code path} or null if the element
     *         could not be located.
     */
    Element getElementByPathStartingAt(String path, Element root);

    /**
     * Locates all elements that match a String locator (path) which identifies
     * DOM elements.
     * 
     * This functionality is limited in {@link LegacyLocatorStrategy}.
     * 
     * @param path
     *            The String locator which identifies target elements.
     * @return List that contains all matched elements. Empty list if none
     *         found.
     */
    List<Element> getElementsByPath(String path);

    /**
     * Locates all elements that match a String locator (path) which identifies
     * DOM elements. The path starts from the specified root element.
     * 
     * This functionality is limited in {@link LegacyLocatorStrategy}.
     * 
     * @see #getElementsByPath(String)
     * 
     * @param path
     *            The String locator which identifies target elements.
     * @param root
     *            The element that is at the root of the path.
     * @return List that contains all matched elements. Empty list if none
     *         found.
     */

    List<Element> getElementsByPathStartingAt(String path, Element root);
}
