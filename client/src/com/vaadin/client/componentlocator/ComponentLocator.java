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
package com.vaadin.client.componentlocator;

import com.google.gwt.user.client.Element;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.BrowserInfo;

/**
 * ComponentLocator provides methods for generating a String locator for a given
 * DOM element and for locating a DOM element using a String locator.
 * <p>
 * The main use for this class is locating components for automated testing purposes.
 * 
 * @since 7.2, moved from {@link com.vaadin.client.ComponentLocator}
 */
public class ComponentLocator {

    private final LegacyLocatorStrategy legacyLocatorStrategy = new LegacyLocatorStrategy(
            this);

    /**
     * Reference to ApplicationConnection instance.
     */

    private ApplicationConnection client;

    /**
     * Construct a ComponentLocator for the given ApplicationConnection.
     * 
     * @param client
     *            ApplicationConnection instance for the application.
     */
    public ComponentLocator(ApplicationConnection client) {
        this.client = client;
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
     */
    public String getPathForElement(Element targetElement) {
        return legacyLocatorStrategy
                .getPathForElement(getElement(targetElement));
    }

    /**
     * Returns the element passed to the method. Or in case of Firefox 15,
     * returns the real element that is in the DOM instead of the element passed
     * to the method (which is the same element but not ==).
     * 
     * @param targetElement
     *            the element to return
     * @return the element passed to the method
     */
    private Element getElement(Element targetElement) {
        if (targetElement == null) {
            return null;
        }

        if (!BrowserInfo.get().isFirefox()) {
            return targetElement;
        }

        if (BrowserInfo.get().getBrowserMajorVersion() != 15) {
            return targetElement;
        }

        // Firefox 15, you make me sad
        if (targetElement.getNextSibling() != null) {
            return (Element) targetElement.getNextSibling()
                    .getPreviousSibling();
        }
        if (targetElement.getPreviousSibling() != null) {
            return (Element) targetElement.getPreviousSibling()
                    .getNextSibling();
        }
        // No siblings so this is the only child
        return (Element) targetElement.getParentNode().getChild(0);
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
    public Element getElementByPath(String path) {
        return legacyLocatorStrategy.getElementByPath(path);
    }

    /**
     * @return the application connection
     */
    ApplicationConnection getClient() {
        return client;
    }
}
