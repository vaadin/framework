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

import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.vaadin.client.ApplicationConnection;

/**
 * ComponentLocator provides methods for generating a String locator for a given
 * DOM element and for locating a DOM element using a String locator.
 * <p>
 * The main use for this class is locating components for automated testing
 * purposes.
 * 
 * @since 7.2, moved from {@link com.vaadin.client.ComponentLocator}
 */
public class ComponentLocator {

    private final List<LocatorStrategy> locatorStrategies;
    private final LocatorStrategy legacyLocatorStrategy;

    /**
     * Reference to ApplicationConnection instance.
     */

    private final ApplicationConnection client;

    /**
     * Construct a ComponentLocator for the given ApplicationConnection.
     * 
     * @param client
     *            ApplicationConnection instance for the application.
     */
    public ComponentLocator(ApplicationConnection client) {
        this.client = client;
        legacyLocatorStrategy = new LegacyLocatorStrategy(client);
        locatorStrategies = Arrays.asList(new VaadinFinderLocatorStrategy(
                client), legacyLocatorStrategy);
    }

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
     * @since 5.4
     * @param targetElement
     *            The element to generate a path for.
     * @return A String locator that identifies the target element or null if a
     *         String locator could not be created.
     * @deprecated As of 7.2, call and override
     *             {@link #getPathForElement(Element)} instead
     */
    @Deprecated
    public String getPathForElement(
            com.google.gwt.user.client.Element targetElement) {
        for (LocatorStrategy strategy : locatorStrategies) {
            String path = strategy.getPathForElement(targetElement);
            if (null != path) {
                return path;
            }
        }
        return null;
    }

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
     * @since 7.2
     * @param targetElement
     *            The element to generate a path for.
     * @return A String locator that identifies the target element or null if a
     *         String locator could not be created.
     */
    public String getPathForElement(Element targetElement) {
        if (targetElement != null) {
            return getPathForElement(DOM.asOld(targetElement));
        }
        return null;
    }

    /**
     * Returns a String locator which uniquely identifies the target element.
     * The returned locator is in a legacy format that is suitable for Vaadin
     * TestBench Recorder. For non-legacy format, use
     * {@link #getPathForElement(com.google.gwt.user.client.Element)} instead.
     * 
     * 
     * @since
     * @param targetElement
     *            The element to generate a path for.
     * @return A String locator that identifies the target element or null if a
     *         String locator could not be created.
     */
    public String getLegacyPathForElement(Element targetElement) {
        return legacyLocatorStrategy
                .getPathForElement(DOM.asOld(targetElement));
    }

    /**
     * Locates an element using a String locator (path) which identifies a DOM
     * element. The {@link #getPathForElement(Element)} method can be used for
     * the inverse operation, i.e. generating a string expression for a DOM
     * element.
     * 
     * @since 5.4
     * @param path
     *            The String locator which identifies the target element.
     * @return The DOM element identified by {@code path} or null if the element
     *         could not be located.
     */
    public com.google.gwt.user.client.Element getElementByPath(String path) {
        for (LocatorStrategy strategy : locatorStrategies) {
            if (strategy.validatePath(path)) {
                Element element = strategy.getElementByPath(path);
                if (null != element) {
                    return DOM.asOld(element);
                }
            }
        }
        return null;
    }

    /**
     * Locates elements using a String locator (path) which identifies DOM
     * elements.
     * 
     * @since 7.2
     * @param path
     *            The String locator which identifies target elements.
     * @return The JavaScriptArray of DOM elements identified by {@code path} or
     *         empty array if elements could not be located.
     */
    public JsArray<Element> getElementsByPath(String path) {
        JsArray<Element> jsElements = JavaScriptObject.createArray().cast();
        for (LocatorStrategy strategy : locatorStrategies) {
            if (strategy.validatePath(path)) {
                List<Element> elements = strategy.getElementsByPath(path);
                if (elements.size() > 0) {
                    for (Element e : elements) {
                        jsElements.push(e);
                    }
                    return jsElements;
                }
            }
        }
        return jsElements;
    }

    /**
     * Locates elements using a String locator (path) which identifies DOM
     * elements. The path starts from the specified root element.
     * 
     * @see #getElementByPath(String)
     * 
     * @since 7.2
     * @param path
     *            The path of elements to be found
     * @param root
     *            The root element where the path is anchored
     * @return The JavaScriptArray of DOM elements identified by {@code path} or
     *         empty array if elements could not be located.
     */
    public JsArray<Element> getElementsByPathStartingAt(String path,
            Element root) {
        JsArray<Element> jsElements = JavaScriptObject.createArray().cast();
        for (LocatorStrategy strategy : locatorStrategies) {
            if (strategy.validatePath(path)) {
                List<Element> elements = strategy.getElementsByPathStartingAt(
                        path, root);
                if (elements.size() > 0) {
                    for (Element e : elements) {
                        jsElements.push(e);
                    }
                    return jsElements;
                }
            }
        }
        return jsElements;
    }

    /**
     * Locates an element using a String locator (path) which identifies a DOM
     * element. The path starts from the specified root element.
     * 
     * @see #getElementByPath(String)
     * 
     * @since 7.2
     * 
     * @param path
     *            The path of the element to be found
     * @param root
     *            The root element where the path is anchored
     * @return The DOM element identified by {@code path} or null if the element
     *         could not be located.
     */
    public com.google.gwt.user.client.Element getElementByPathStartingAt(
            String path, Element root) {
        for (LocatorStrategy strategy : locatorStrategies) {
            if (strategy.validatePath(path)) {
                Element element = strategy.getElementByPathStartingAt(path,
                        root);
                if (null != element) {
                    return DOM.asOld(element);
                }
            }
        }
        return null;
    }

    /**
     * Returns the {@link ApplicationConnection} used by this locator.
     * <p>
     * This method is primarily for internal use by the framework.
     * 
     * @return the application connection
     */
    public ApplicationConnection getClient() {
        return client;
    }

    /**
     * Check if a given selector is valid for LegacyLocatorStrategy.
     * 
     * @param path
     *            Vaadin selector path
     * @return true if passes path validation with LegacyLocatorStrategy
     */
    public boolean isValidForLegacyLocator(String path) {
        for (LocatorStrategy ls : locatorStrategies) {
            if (ls instanceof LegacyLocatorStrategy) {
                return ls.validatePath(path);
            }
        }
        return false;
    }

}
