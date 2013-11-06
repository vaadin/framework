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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.Util;
import com.vaadin.client.metadata.NoDataException;
import com.vaadin.client.metadata.Property;
import com.vaadin.client.ui.AbstractConnector;
import com.vaadin.client.ui.AbstractHasComponentsConnector;
import com.vaadin.client.ui.SubPartAware;
import com.vaadin.client.ui.VNotification;
import com.vaadin.shared.AbstractComponentState;

/**
 * The VaadinFinder locator strategy implements an XPath-like syntax for
 * locating elements in Vaadin applications. This is used in the new
 * VaadinFinder API in TestBench 4.
 * 
 * Examples of the supported syntax:
 * <ul>
 * <li>Find the third text field in the DOM: {@code //VTextField[2]}</li>
 * <li>Find the second button inside the first vertical layout:
 * {@code //VVerticalLayout/VButton[1]}</li>
 * <li>Find the first column on the third row of the "Accounts" table:
 * {@code //VScrollTable[caption="Accounts"]#row[2]/col[0]}</li>
 * </ul>
 * 
 * @since 7.2
 * @author Vaadin Ltd
 */
public class VaadinFinderLocatorStrategy implements LocatorStrategy {

    public static final String SUBPART_SEPARATOR = "#";

    private final ApplicationConnection client;

    public VaadinFinderLocatorStrategy(ApplicationConnection clientConnection) {
        client = clientConnection;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPathForElement(Element targetElement) {
        // Path generation functionality is not yet implemented as there is no
        // current need for it. This might be implemented in the future if the
        // need arises. Until then, all locator generation is handled by
        // LegacyLocatorStrategy.
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Element getElementByPath(String path) {
        if (path.startsWith("//VNotification")) {
            return findNotificationByPath(path);
        }
        return getElementByPathStartingAtConnector(path,
                client.getUIConnector());
    }

    /**
     * Special case for finding notifications as they have no connectors and are
     * directly attached to {@link RootPanel}.
     * 
     * @param path
     *            The path of the notification, should be
     *            {@code "//VNotification"} optionally followed by an index in
     *            brackets.
     * @return the notification element or null if not found.
     */
    private Element findNotificationByPath(String path) {
        ArrayList<VNotification> notifications = new ArrayList<VNotification>();
        for (Widget w : RootPanel.get()) {
            if (w instanceof VNotification) {
                notifications.add((VNotification) w);
            }
        }
        String indexStr = extractPredicateString(path);
        int index = indexStr == null ? 0 : Integer.parseInt(indexStr);
        if (index >= 0 && index < notifications.size()) {
            return notifications.get(index).getElement();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Element getElementByPathStartingAt(String path, Element root) {
        return getElementByPathStartingAtConnector(path,
                Util.findPaintable(client, root));
    }

    /**
     * Finds an element by the specified path, starting traversal of the
     * connector hierarchy from the specified root.
     * 
     * @param path
     *            the locator path
     * @param root
     *            the root connector
     * @return the element identified by path or null if not found.
     */
    private Element getElementByPathStartingAtConnector(String path,
            ComponentConnector root) {
        String[] pathComponents = path.split(SUBPART_SEPARATOR);
        ComponentConnector connector = findConnectorByPath(pathComponents[0],
                root);
        if (connector != null) {
            if (pathComponents.length > 1) {
                // We have subparts
                if (connector.getWidget() instanceof SubPartAware) {
                    return ((SubPartAware) connector.getWidget())
                            .getSubPartElement(pathComponents[1]);
                } else {
                    return null;
                }
            }
            return connector.getWidget().getElement();
        }
        return null;
    }

    /**
     * Recursively finds a connector for the element identified by the provided
     * path by traversing the connector hierarchy starting from the
     * {@code parent} connector.
     * 
     * @param path
     *            The path identifying an element.
     * @param parent
     *            The connector to start traversing from.
     * @return The connector identified by {@code path} or null if it no such
     *         connector could be found.
     */
    private ComponentConnector findConnectorByPath(String path,
            ComponentConnector parent) {
        boolean findRecursively = path.startsWith("//");
        // Strip away the one or two slashes from the beginning of the path
        path = path.substring(findRecursively ? 2 : 1);

        String[] fragments = splitFirstFragmentFromTheRest(path);
        List<ComponentConnector> potentialMatches = collectPotentialMatches(
                parent, fragments[0], findRecursively);
        ComponentConnector connector = filterPotentialMatches(potentialMatches,
                extractPredicateString(fragments[0]));
        if (connector != null) {
            if (fragments.length > 1) {
                return findConnectorByPath(fragments[1], connector);
            } else {
                return connector;
            }
        }
        return null;
    }

    /**
     * Returns the predicate string, i.e. the string between the brackets in a
     * path fragment. Examples: <code>
     * VTextField[0] => 0
     * VTextField[caption='foo'] => caption='foo'
     * </code>
     * 
     * @param pathFragment
     *            The path fragment from which to extract the predicate string.
     * @return The predicate string for the path fragment or null if none.
     */
    private String extractPredicateString(String pathFragment) {
        int ixOpenBracket = pathFragment.indexOf('[');
        if (ixOpenBracket >= 0) {
            int ixCloseBracket = pathFragment.indexOf(']', ixOpenBracket);
            return pathFragment.substring(ixOpenBracket + 1, ixCloseBracket);
        }
        return null;
    }

    /**
     * Returns the first ComponentConnector that matches the predicate string
     * from a list of potential matches. If {@code predicateString} is null, the
     * first element in the {@code potentialMatches} list is returned.
     * 
     * @param potentialMatches
     *            A list of potential matches to check.
     * @param predicateString
     *            The predicate that should match. Can be an index or a property
     *            name, value pair or null.
     * @return A {@link ComponentConnector} from the {@code potentialMatches}
     *         list, which matches the {@code predicateString} or null if no
     *         matches are found.
     */
    private ComponentConnector filterPotentialMatches(
            List<ComponentConnector> potentialMatches, String predicateString) {

        if (potentialMatches.isEmpty()) {
            return null;
        }

        if (predicateString != null) {

            int split_idx = predicateString.indexOf('=');

            if (split_idx != -1) {

                String propertyName = predicateString.substring(0, split_idx)
                        .trim();
                String value = unquote(predicateString.substring(split_idx + 1)
                        .trim());

                for (ComponentConnector connector : potentialMatches) {
                    Property property = AbstractConnector.getStateType(
                            connector).getProperty(propertyName);
                    if (valueEqualsPropertyValue(value, property,
                            connector.getState())) {
                        return connector;
                    }
                }

                return null;

            } else {
                int index = Integer.valueOf(predicateString);
                return index < potentialMatches.size() ? potentialMatches
                        .get(index) : null;
            }
        }

        return potentialMatches.get(0);
    }

    /**
     * Returns true if the value matches the value of the property in the state
     * object.
     * 
     * @param value
     *            The value to compare against.
     * @param property
     *            The property, whose value to check.
     * @param state
     *            The connector, whose state object contains the property.
     * @return true if the values match.
     */
    private boolean valueEqualsPropertyValue(String value, Property property,
            AbstractComponentState state) {
        try {
            return value.equals(property.getValue(state));
        } catch (NoDataException e) {
            // The property doesn't exist in the state object, so they aren't
            // equal.
            return false;
        }
    }

    /**
     * Removes the surrounding quotes from a string if it is quoted.
     * 
     * @param str
     *            the possibly quoted string
     * @return an unquoted version of str
     */
    private String unquote(String str) {
        if ((str.startsWith("\"") && str.endsWith("\""))
                || (str.startsWith("'") && str.endsWith("'"))) {
            return str.substring(1, str.length() - 1);
        }
        return str;
    }

    /**
     * Collects all connectors that match the widget class name of the path
     * fragment. If the {@code collectRecursively} parameter is true, a
     * depth-first search of the connector hierarchy is performed.
     * 
     * Searching depth-first ensure that we can return the matches in correct
     * order for selecting based on index predicates.
     * 
     * @param parent
     *            The {@link ComponentConnector} to start the search from.
     * @param pathFragment
     *            The path fragment identifying which type of widget to search
     *            for.
     * @param collectRecursively
     *            If true, all matches from all levels below {@code parent} will
     *            be collected. If false only direct children will be collected.
     * @return A list of {@link ComponentConnector}s matching the widget type
     *         specified in the {@code pathFragment}.
     */
    private List<ComponentConnector> collectPotentialMatches(
            ComponentConnector parent, String pathFragment,
            boolean collectRecursively) {
        ArrayList<ComponentConnector> potentialMatches = new ArrayList<ComponentConnector>();
        if (parent instanceof AbstractHasComponentsConnector) {
            List<ComponentConnector> children = ((AbstractHasComponentsConnector) parent)
                    .getChildComponents();
            for (ComponentConnector child : children) {
                String widgetName = getWidgetName(pathFragment);
                if (connectorMatchesPathFragment(child, widgetName)) {
                    potentialMatches.add(child);
                }
                if (collectRecursively) {
                    potentialMatches.addAll(collectPotentialMatches(child,
                            pathFragment, collectRecursively));
                }
            }
        }
        return potentialMatches;
    }

    /**
     * Determines whether a connector matches a path fragment. This is done by
     * comparing the path fragment to the name of the widget type of the
     * connector.
     * 
     * @param connector
     *            The connector to compare.
     * @param widgetName
     *            The name of the widget class.
     * @return true if the widget type of the connector equals the widget type
     *         identified by the path fragment.
     */
    private boolean connectorMatchesPathFragment(ComponentConnector connector,
            String widgetName) {
        return widgetName.equals(Util.getSimpleName(connector.getWidget()));
    }

    /**
     * Extracts the name of the widget class from a path fragment
     * 
     * @param pathFragment
     *            the path fragment
     * @return the name of the widget class.
     */
    private String getWidgetName(String pathFragment) {
        String widgetName = pathFragment;
        int ixBracket = pathFragment.indexOf('[');
        if (ixBracket >= 0) {
            widgetName = pathFragment.substring(0, ixBracket);
        }
        return widgetName;
    }

    /**
     * Splits off the first path fragment from a path and returns an array of
     * two elements, where the first element is the first path fragment and the
     * second element is the rest of the path (all remaining path fragments
     * untouched).
     * 
     * @param path
     *            The path to split.
     * @return An array of two elements: The first path fragment and the rest of
     *         the path.
     */
    private String[] splitFirstFragmentFromTheRest(String path) {
        int ixOfSlash = path.indexOf('/');
        if (ixOfSlash > 0) {
            return new String[] { path.substring(0, ixOfSlash),
                    path.substring(ixOfSlash) };
        }
        return new String[] { path };
    }

}
