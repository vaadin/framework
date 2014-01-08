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
import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.FastStringSet;
import com.vaadin.client.HasComponentsConnector;
import com.vaadin.client.Util;
import com.vaadin.client.metadata.NoDataException;
import com.vaadin.client.metadata.Property;
import com.vaadin.client.metadata.TypeDataStore;
import com.vaadin.client.ui.AbstractConnector;
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

    /**
     * Internal container/descriptor for search predicates
     * 
     * @author Vaadin Ltd
     */
    private static final class Predicate {
        private String name = "";
        private String value = "";
        private boolean wildcard = false;
        private int index = -1;
    }

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

    private boolean isNotificationExpression(String path) {
        String[] starts = { "//", "/" };

        String[] frags = { "com.vaadin.ui.Notification.class",
                "com.vaadin.ui.Notification", "VNotification.class",
                "VNotification", "Notification.class", "Notification" };

        String[] ends = { "/", "[" };

        for (String s : starts) {
            for (String f : frags) {
                if (path.equals(s + f)) {
                    return true;
                }

                for (String e : ends) {
                    if (path.startsWith(s + f + e)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Element> getElementsByPath(String path) {

        if (isNotificationExpression(path)) {
            List<Element> elements = new ArrayList<Element>();

            for (VNotification n : findNotificationsByPath(path)) {
                elements.add(n.getElement());
            }

            return elements;
        }

        List<Element> elems = eliminateDuplicates(getElementsByPathStartingAtConnector(
                path, client.getUIConnector()));

        return elems;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Element getElementByPath(String path) {
        if (isNotificationExpression(path)) {
            return findNotificationsByPath(path).get(0).getElement();
        }
        return getElementByPathStartingAtConnector(path,
                client.getUIConnector());
    }

    /**
     * Generate a list of predicates from a single predicate string
     * 
     * @param str
     *            a comma separated string of predicates
     * @return a List of Predicate objects
     */
    private List<Predicate> extractPredicates(String path) {
        List<Predicate> predicates = new ArrayList<Predicate>();

        String str = extractPredicateString(path);
        if (null == str || str.length() == 0) {
            return predicates;
        }

        // Extract input strings
        List<String> input = new ArrayList<String>();
        {
            int idx = indexOfIgnoringQuotes(str, ',', 0), p = 0;
            if (idx == -1) {
                input.add(str);
            } else {
                do {
                    input.add(str.substring(p, idx));
                    p = idx + 1;
                    idx = indexOfIgnoringQuotes(str, ',', p);
                } while (idx > -1);
                input.add(str.substring(p));
            }
        }

        // Process each predicate into proper predicate descriptor
        for (String s : input) {
            Predicate p = new Predicate();
            s = s.trim();

            try {
                // If we can parse out the predicate as a pure index argument,
                // stop processing here.
                p.index = Integer.parseInt(s);
                predicates.add(p);

                continue;
            } catch (Exception e) {
                p.index = -1;
            }

            int idx = indexOfIgnoringQuotes(s, '=');
            if (idx < 0) {
                continue;
            }
            p.name = s.substring(0, idx);
            p.value = s.substring(idx + 1);

            if (p.value.equals("?")) {
                p.wildcard = true;
                p.value = null;
            } else {
                // Only unquote predicate value once we're sure it's a proper
                // value...

                p.value = unquote(p.value);
            }

            predicates.add(p);
        }

        // Move any (and all) index predicates to last place in the list.
        for (int i = 0, l = predicates.size(); i < l - 1; ++i) {
            if (predicates.get(i).index > -1) {
                predicates.add(predicates.remove(i));
                --i;
                --l;
            }
        }

        return predicates;
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
    private List<VNotification> findNotificationsByPath(String path) {

        List<VNotification> notifications = new ArrayList<VNotification>();
        for (Widget w : RootPanel.get()) {
            if (w instanceof VNotification) {
                notifications.add((VNotification) w);
            }
        }

        List<Predicate> predicates = extractPredicates(path);
        for (Predicate p : predicates) {

            if (p.index > -1) {
                VNotification n = notifications.get(p.index);
                notifications.clear();
                if (n != null) {
                    notifications.add(n);
                }
            }

        }

        return eliminateDuplicates(notifications);
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
     * {@inheritDoc}
     */
    @Override
    public List<Element> getElementsByPathStartingAt(String path, Element root) {
        List<Element> elements = getElementsByPathStartingAtConnector(path,
                Util.findPaintable(client, root));
        return elements;
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
        ComponentConnector connector;
        if (pathComponents[0].length() > 0) {
            connector = findConnectorByPath(pathComponents[0], root);
        } else {
            connector = root;
        }
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
     * Finds a list of elements by the specified path, starting traversal of the
     * connector hierarchy from the specified root.
     * 
     * @param path
     *            the locator path
     * @param root
     *            the root connector
     * @return the list of elements identified by path or empty list if not
     *         found.
     */
    private List<Element> getElementsByPathStartingAtConnector(String path,
            ComponentConnector root) {
        String[] pathComponents = path.split(SUBPART_SEPARATOR);
        List<ComponentConnector> connectors;
        if (pathComponents[0].length() > 0) {
            connectors = findConnectorsByPath(pathComponents[0],
                    Arrays.asList(root));
        } else {
            connectors = Arrays.asList(root);
        }

        List<Element> output = new ArrayList<Element>();
        if (null != connectors && !connectors.isEmpty()) {
            if (pathComponents.length > 1) {
                // We have subparts
                for (ComponentConnector connector : connectors) {
                    if (connector.getWidget() instanceof SubPartAware) {
                        output.add(((SubPartAware) connector.getWidget())
                                .getSubPartElement(pathComponents[1]));
                    }
                }
            } else {
                for (ComponentConnector connector : connectors) {
                    output.add(connector.getWidget().getElement());
                }
            }
        }
        return eliminateDuplicates(output);
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
     * @return The connector identified by {@code path} or null if no such
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

        List<ComponentConnector> connectors = filterMatches(potentialMatches,
                extractPredicates(fragments[0]));

        if (!connectors.isEmpty()) {
            if (fragments.length > 1) {
                return findConnectorByPath(fragments[1], connectors.get(0));
            } else {
                return connectors.get(0);
            }
        }
        return null;
    }

    /**
     * Recursively finds connectors for the elements identified by the provided
     * path by traversing the connector hierarchy starting from {@code parents}
     * connectors.
     * 
     * @param path
     *            The path identifying elements.
     * @param parents
     *            The list of connectors to start traversing from.
     * @return The list of connectors identified by {@code path} or empty list
     *         if no such connectors could be found.
     */
    private List<ComponentConnector> findConnectorsByPath(String path,
            List<ComponentConnector> parents) {
        boolean findRecursively = path.startsWith("//");
        // Strip away the one or two slashes from the beginning of the path
        path = path.substring(findRecursively ? 2 : 1);

        String[] fragments = splitFirstFragmentFromTheRest(path);

        List<ComponentConnector> connectors = new ArrayList<ComponentConnector>();
        for (ComponentConnector parent : parents) {
            connectors.addAll(filterMatches(
                    collectPotentialMatches(parent, fragments[0],
                            findRecursively), extractPredicates(fragments[0])));
        }

        if (!connectors.isEmpty() && fragments.length > 1) {
            return (findConnectorsByPath(fragments[1], connectors));
        }
        return eliminateDuplicates(connectors);
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
     * @return The predicate string for the path fragment or empty string if not
     *         found.
     */
    private String extractPredicateString(String pathFragment) {
        int ixOpenBracket = indexOfIgnoringQuotes(pathFragment, '[');
        if (ixOpenBracket >= 0) {
            int ixCloseBracket = indexOfIgnoringQuotes(pathFragment, ']',
                    ixOpenBracket);
            return pathFragment.substring(ixOpenBracket + 1, ixCloseBracket);
        }
        return "";
    }

    /**
     * Go through a list of potentially matching components, modifying that list
     * until all elements that remain in that list match the complete list of
     * predicates.
     * 
     * @param potentialMatches
     *            a list of component connectors. Will be changed.
     * @param predicates
     *            an immutable list of predicates
     * @return filtered list of component connectors.
     */
    private List<ComponentConnector> filterMatches(
            List<ComponentConnector> potentialMatches,
            List<Predicate> predicates) {

        for (Predicate p : predicates) {

            if (p.index > -1) {
                try {
                    ComponentConnector v = potentialMatches.get(p.index);
                    potentialMatches.clear();
                    potentialMatches.add(v);
                } catch (IndexOutOfBoundsException e) {
                    potentialMatches.clear();
                }

                continue;
            }

            for (int i = 0, l = potentialMatches.size(); i < l; ++i) {

                ComponentConnector c = potentialMatches.get(i);
                Property property = AbstractConnector.getStateType(c)
                        .getProperty(p.name);

                Object propData;
                try {
                    propData = property.getValue(c.getState());
                } catch (NoDataException e) {
                    propData = null;
                }

                if ((p.wildcard && propData == null)
                        || (!p.wildcard && !valueEqualsPropertyValue(p.value,
                                property, c.getState()))) {
                    potentialMatches.remove(i);
                    --l;
                    --i;
                }
            }

        }

        return eliminateDuplicates(potentialMatches);
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
        } catch (Exception e) {
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
        if (parent instanceof HasComponentsConnector) {
            List<ComponentConnector> children = ((HasComponentsConnector) parent)
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
        return eliminateDuplicates(potentialMatches);
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
        Class<?> connectorClass = connector.getClass();
        List<String> ids = new ArrayList<String>();

        FastStringSet identifiers = TypeDataStore.get().findIdentifiersFor(
                connectorClass);
        JsArrayString str = identifiers.dump();

        for (int j = 0; j < str.length(); ++j) {
            ids.add(str.get(j));
        }

        Integer[] widgetTags = client.getConfiguration()
                .getTagsForServerSideClassName(getFullClassName(widgetName));
        if (widgetTags.length == 0) {
            widgetTags = client.getConfiguration()
                    .getTagsForServerSideClassName(
                            getFullClassName("com.vaadin.ui." + widgetName));
        }

        for (int i = 0, l = ids.size(); i < l; ++i) {

            // Fuzz the connector name, so that the client can provide (for
            // example: /Button, /Button.class, /com.vaadin.ui.Button,
            // /com.vaadin.ui.Button.class, etc)

            String name = ids.get(i);
            final String simpleName = getSimpleClassName(name);
            final String fullName = getFullClassName(name);

            if (widgetTags.length > 0) {
                Integer[] foundTags = client.getConfiguration()
                        .getTagsForServerSideClassName(fullName);
                for (int tag : foundTags) {
                    if (tagsMatch(widgetTags, tag)) {
                        return true;
                    }
                }
            }

            // Fallback if something failed before.
            if (widgetName.equals(fullName + ".class")
                    || widgetName.equals(fullName)
                    || widgetName.equals(simpleName + ".class")
                    || widgetName.equals(simpleName) || widgetName.equals(name)) {
                return true;
            }
        }

        // If the server-side class name didn't match, fall back to testing for
        // the explicit widget name
        String widget = Util.getSimpleName(connector.getWidget());
        return widgetName.equals(widget)
                || widgetName.equals(widget + ".class");

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
        int ixOfSlash = indexOfIgnoringQuotes(path, '/');
        if (ixOfSlash > 0) {
            return new String[] { path.substring(0, ixOfSlash),
                    path.substring(ixOfSlash) };
        }
        return new String[] { path };
    }

    private int indexOfIgnoringQuotes(String str, char find) {
        return indexOfIgnoringQuotes(str, find, 0);
    }

    private int indexOfIgnoringQuotes(String str, char find, int startingAt) {
        boolean quote = false;
        String quoteChars = "'\"";
        char currentQuote = '"';
        for (int i = startingAt; i < str.length(); ++i) {
            char cur = str.charAt(i);
            if (quote) {
                if (cur == currentQuote) {
                    quote = !quote;
                }
                continue;
            } else if (cur == find) {
                return i;
            } else {
                if (quoteChars.indexOf(cur) >= 0) {
                    currentQuote = cur;
                    quote = !quote;
                }
            }
        }
        return -1;
    }

    private String getSimpleClassName(String s) {
        String[] parts = s.split("\\.");
        if (s.endsWith(".class")) {
            return parts[parts.length - 2];
        }
        return parts.length > 0 ? parts[parts.length - 1] : s;
    }

    private String getFullClassName(String s) {
        if (s.endsWith(".class")) {
            return s.substring(0, s.lastIndexOf(".class"));
        }
        return s;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.client.componentlocator.LocatorStrategy#validatePath(java.
     * lang.String)
     */
    @Override
    public boolean validatePath(String path) {
        // This syntax is so difficult to regexp properly, that we'll just try
        // to find something with it regardless of the correctness of the
        // syntax...
        return true;
    }

    /**
     * Go through a list, removing all duplicate elements from it. This method
     * is used to avoid accumulation of duplicate entries in result lists
     * resulting from low-context recursion.
     * 
     * Preserves first entry in list, removes others. Preserves list order.
     * 
     * @return list passed as parameter, after modification
     */
    private final <T> List<T> eliminateDuplicates(List<T> list) {

        int l = list.size();
        for (int j = 0; j < l; ++j) {
            T ref = list.get(j);

            for (int i = j + 1; i < l; ++i) {
                if (list.get(i) == ref) {
                    list.remove(i);
                    --i;
                    --l;
                }
            }
        }

        return list;
    }

    private boolean tagsMatch(Integer[] targets, Integer tag) {
        for (int i = 0; i < targets.length; ++i) {
            if (targets[i].equals(tag)) {
                return true;
            }
        }

        try {
            return tagsMatch(targets,
                    client.getConfiguration().getParentTag(tag));
        } catch (Exception e) {
            return false;
        }
    }
}
