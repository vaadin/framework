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

package com.vaadin.client.debug.internal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.dom.client.Element;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.componentlocator.ComponentLocator;
import com.vaadin.client.componentlocator.SelectorPredicate;

/**
 * A single segment of a selector path pointing to an Element.
 * <p>
 * This class should be considered internal to the framework and may change at
 * any time.
 * <p>
 * 
 * @since 7.1.x
 */
public class SelectorPath {
    private final String path;
    private final Element element;
    private final ComponentLocator locator;
    private static Map<String, Integer> counter = new HashMap<String, Integer>();
    private static Map<String, String> legacyNames = new HashMap<String, String>();

    static {
        legacyNames.put("FilterSelect", "ComboBox");
        legacyNames.put("ScrollTable", "Table");
    }

    protected SelectorPath(ServerConnector c, Element e) {
        element = e;
        locator = new ComponentLocator(c.getConnection());
        path = locator.getPathForElement(e);
    }

    public String getPath() {
        return path;
    }

    public Element getElement() {
        return element;
    }

    public ComponentLocator getLocator() {
        return locator;
    }

    /**
     * Generate ElementQuery code for Java. Fallback to By.vaadin(path) if
     * dealing with LegacyLocator
     * 
     * @return String containing Java code for finding the element described by
     *         path
     */
    public String getElementQuery() {
        if (path.isEmpty() || locator.isValidForLegacyLocator(path)) {
            return getLegacyLocatorQuery();
        }

        String[] fragments;
        String tmpPath = path;
        List<SelectorPredicate> postFilters = SelectorPredicate
                .extractPostFilterPredicates(path);
        if (postFilters.size() > 0) {
            tmpPath = tmpPath.substring(1, tmpPath.lastIndexOf(')'));
            if (tmpPath.contains("#")) {
                // FIXME: SubParts should be handled.
                tmpPath = tmpPath.split("#")[0];
            }
        }

        // Generate an ElementQuery
        fragments = tmpPath.split("/");
        String elementQueryString = "";
        int index = 0;
        for (SelectorPredicate p : postFilters) {
            if (p.getIndex() > 0) {
                index = p.getIndex();
            }
        }

        for (int i = 1; i < fragments.length; ++i) {
            if (fragments[i].isEmpty()) {
                // Recursive searches cause empty fragments
                continue;
            }

            // if i == 1 or previous fragment was empty, search is recursive
            boolean recursive = (i > 1 ? fragments[i - 1].isEmpty() : false);

            // if elementQueryString is not empty, join the next query with .
            String queryFragment = (!elementQueryString.isEmpty() ? "." : "");
            // if search is not recursive, add another $ in front of fragment
            queryFragment += (!recursive ? "$" : "")
                    + generateFragment(fragments[i]);

            elementQueryString += queryFragment;
        }

        if (!hasId(fragments[fragments.length - 1])) {
            if (index == 0) {
                elementQueryString += ".first()";
            } else {
                elementQueryString += ".get(" + index + ")";
            }
        }

        // Return full Java variable assignment and eQuery
        return generateJavaVariable(fragments[fragments.length - 1])
                + elementQueryString + ";";
    }

    /**
     * Finds out if the given query fragment has a defined id
     * 
     * @param fragment
     *            Query fragment
     * @return true if has id
     */
    private boolean hasId(String fragment) {
        for (SelectorPredicate p : SelectorPredicate
                .extractPredicates(fragment)) {
            if (p.getName().equals("id")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Generates a recursive ElementQuery for given path fragment
     * 
     * @param fragment
     *            Query fragment
     * @return ElementQuery java code as a String
     */
    private String generateFragment(String fragment) {
        // Get Element.class -name
        String elementClass = getComponentName(fragment) + "Element.class";

        String queryFragment = "$(" + elementClass + ")";

        for (SelectorPredicate p : SelectorPredicate
                .extractPredicates(fragment)) {
            // Add in predicates like .caption and .id
            queryFragment += "." + p.getName() + "(\"" + p.getValue() + "\")";
        }
        return queryFragment;
    }

    /**
     * Returns the name of the component described by given query fragment
     * 
     * @param fragment
     *            Query fragment
     * @return Class part of fragment
     */
    protected String getComponentName(String fragment) {
        return fragment.split("\\[")[0];
    }

    /**
     * Generates a legacy locator for SelectorPath.
     * 
     * @return String containing Java code for element search and assignment
     */
    private String getLegacyLocatorQuery() {
        String name;
        if (!path.isEmpty()) {
            String[] frags = path.split("/");
            name = getComponentName(frags[frags.length - 1]).substring(1);
        } else {
            name = "root";
        }

        if (legacyNames.containsKey(name)) {
            name = legacyNames.get(name);
        }

        name = getNameWithCount(name);

        // Use direct path and elementX naming style.
        return "WebElement " + name.substring(0, 1).toLowerCase()
                + name.substring(1) + " = getDriver().findElement(By.vaadin(\""
                + path + "\"));";
    }

    /**
     * Get variable name with counter for given component name.
     * 
     * @param name
     *            Component name
     * @return name followed by count
     */
    protected String getNameWithCount(String name) {
        if (!counter.containsKey(name)) {
            counter.put(name, 0);
        }
        counter.put(name, counter.get(name) + 1);
        name += counter.get(name);
        return name;
    }

    /**
     * Generate Java variable assignment from given selector fragment
     * 
     * @param pathFragment
     *            Selector fragment
     * @return piece of java code
     */
    private String generateJavaVariable(String pathFragment) {
        // Get element type and predicates from fragment
        List<SelectorPredicate> predicates = SelectorPredicate
                .extractPredicates(pathFragment);
        String elementType = pathFragment.split("\\[")[0];
        String name = getNameFromPredicates(predicates, elementType);

        if (name.equals(elementType)) {
            name = getNameWithCount(name);
        }

        // Replace unusable characters
        name = name.replaceAll("\\W", "");

        // Lowercase the first character of name
        return elementType + "Element " + name.substring(0, 1).toLowerCase()
                + name.substring(1) + " = ";
    }

    /**
     * Get variable name based on predicates. Fallback to elementType
     * 
     * @param predicates
     *            Predicates related to element
     * @param elementType
     *            Element type
     * @return name for Variable
     */
    private String getNameFromPredicates(List<SelectorPredicate> predicates,
            String elementType) {
        String name = elementType;
        for (SelectorPredicate p : predicates) {
            if ("caption".equals(p.getName())) {
                // Caption + elementType is a suitable name
                name = p.getValue() + elementType;
            } else if ("id".equals(p.getName())) {
                // Just id. This is unique, use it.
                return p.getValue();
            }
        }
        return name;
    }
}
