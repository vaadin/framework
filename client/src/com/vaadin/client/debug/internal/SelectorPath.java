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

package com.vaadin.client.debug.internal;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.Util;
import com.vaadin.client.componentlocator.ComponentLocator;
import com.vaadin.client.componentlocator.VaadinFinderLocatorStrategy;
import com.vaadin.client.metadata.NoDataException;
import com.vaadin.client.metadata.Property;
import com.vaadin.client.ui.AbstractConnector;
import com.vaadin.client.ui.SubPartAware;

/**
 * A single segment of a selector path with optional parent.
 * <p>
 * The static method {@link #findTestBenchSelector(ServerConnector, Element)}
 * permits looking up a selector chain for an element (a selector and its
 * parents, each selector relative to its parent).
 * <p>
 * The method {@link #findElement()} can be used to locate the element
 * referenced by a {@link SelectorPath}. {@link #getJUnitSelector(String)} can
 * be used to obtain the string to add to a JUnit test to refer to the element
 * identified by the path.
 * <p>
 * This class should be considered internal to the framework and may change at
 * any time.
 * 
 * @since 7.1.x
 */
public abstract class SelectorPath {
    private final SelectorPath parent;
    private final ComponentLocator locator;

    private static final String SUBPART_SEPARATOR = VaadinFinderLocatorStrategy.SUBPART_SEPARATOR;

    /**
     * Creates a {@link SelectorPath} from the root of the UI (without a parent)
     * to identify an element.
     * <p>
     * The {@link ComponentLocator} is used to locate the corresponding
     * {@link Element} in the context of a UI. If there are multiple UIs on a
     * single page, the locator should correspond to the correct
     * {@link ApplicationConnection}.
     * 
     * @param locator
     *            {@link ComponentLocator} to use
     */
    protected SelectorPath(ComponentLocator locator) {
        this(null, locator);
    }

    /**
     * Creates a {@link SelectorPath} which is relative to another
     * {@link SelectorPath}. to identify an element.
     * <p>
     * The {@link ComponentLocator} is used to locate the corresponding
     * {@link Element} in the context of a UI. If there are multiple UIs on a
     * single page, the locator should correspond to the correct
     * {@link ApplicationConnection}.
     * 
     * @param parent
     *            parent {@link SelectorPath} or null for root paths
     * @param locator
     *            {@link ComponentLocator} to use
     */
    protected SelectorPath(SelectorPath parent, ComponentLocator locator) {
        this.parent = parent;
        this.locator = locator;
    }

    /**
     * Returns the parent {@link SelectorPath} to which this path is relative.
     * 
     * @return parent path
     */
    public SelectorPath getParent() {
        return parent;
    }

    @Override
    public String toString() {
        return "SelectorPath: " + getJUnitSelector("...");
    }

    /**
     * Returns the JUnit test fragment which can be used to refer to the element
     * in a test.
     * 
     * @param context
     *            the context to use (usually "getDriver()" or a variable name)
     * @return string to add in a JUnit test
     */
    public abstract String getJUnitSelector(String context);

    /**
     * Returns the {@link Element} that this {@link SelectorPath} points to in
     * the context of the {@link ComponentLocator} of the {@link SelectorPath}.
     * 
     * @return Element identified by the path in the current UI
     */
    public abstract Element findElement();

    /**
     * Returns the path to an element/connector, including separate intermediate
     * paths and the final path segment.
     * 
     * @param connector
     *            the connector to find
     * @param element
     *            sub-element inside connector or null to use connector root
     *            element
     * @return Vaadin locator path
     */
    public static SelectorPath findTestBenchSelector(ServerConnector connector,
            Element element) {
        // TODO there should be a better way to locate and refer to captions -
        // now using domChild in layout
        SelectorPath selectorPath = null;
        ApplicationConnection connection = connector.getConnection();
        if (connection != null) {
            if (null == element) {
                element = findConnectorRootElement(connector);
            }
            if (null != element) {
                ComponentLocator locator = new ComponentLocator(connection);
                String path = locator.getPathForElement(element);
                SelectorPath parent = null;

                if (!path.isEmpty()) {
                    selectorPath = extractIdSelectorPath(path, locator);
                    if (null == selectorPath) {
                        // parent paths first if not rooted on an ID
                        if (connector.getParent() != null) {
                            parent = findTestBenchSelector(
                                    connector.getParent(), null);
                        }

                        if (parent != null) {
                            // update path to be relative to parent
                            Element parentElement = parent.findElement();
                            if (null != parentElement) {
                                String parentPath = locator
                                        .getPathForElement(parentElement);
                                if (path.startsWith(parentPath)) {
                                    // remove path of parent to look for the
                                    // children
                                    path = path.substring(parentPath.length());
                                }
                            }
                        }

                        selectorPath = extractVaadinSelectorPath(path, parent,
                                locator);
                    }
                    if (null == selectorPath) {
                        if (path.startsWith("/V")) {
                            // fall-back: Vaadin
                            // this branch is needed for /VTabsheetPanel etc.
                            selectorPath = SelectorPath.vaadinPath(path,
                                    parent, locator);
                        } else {
                            // fall-back: XPath
                            selectorPath = SelectorPath.xpath(path, parent,
                                    locator);
                        }
                    }
                }
            }
        }
        return selectorPath;
    }

    private static SelectorPath extractIdSelectorPath(String path,
            ComponentLocator locator) {
        SelectorPath selectorPath = null;
        if (path.startsWith("PID_S")) {
            // remove internal prefix
            path = path.substring(5);

            // no parent for an ID selector
            String pid = path;
            String rest = null;
            // split at first slash that is not in the subpart (if any)
            int slashPos = path.indexOf("/");
            int subPartPos = path.indexOf(SUBPART_SEPARATOR);
            if (subPartPos >= 0 && slashPos > subPartPos) {
                // ignore slashes in subpart
                slashPos = -1;
            } else if (slashPos >= 0 && subPartPos > slashPos) {
                // ignore subpart after slashes - handled as a part of rest
                subPartPos = -1;
            }
            // split the ID part and any relative path after it
            if (slashPos > 0) {
                pid = path.substring(0, slashPos);
                rest = path.substring(slashPos);
            }

            // if there is a subpart directly after the id, need to use a Vaadin
            // selector
            SelectorPath pidSelector = null;
            if (subPartPos > 0) {
                String id = pid.substring(0, subPartPos);
                // include the subpart separator
                String subPart = pid.substring(subPartPos);
                Element element = locator.getElementByPath("PID_S" + pid);
                ComponentConnector connector = Util.findPaintable(
                        locator.getClient(), element);
                if (null != connector && null != connector.getWidget()) {
                    String type = connector.getWidget().getClass()
                            .getSimpleName();
                    pidSelector = SelectorPath.vaadinPath("//" + type
                            + "[id=\\\"" + id + "\\\"]" + subPart, null,
                            locator);
                } else {
                    // no valid connector for the subpart
                    return null;
                }
            } else {
                pidSelector = SelectorPath.id(pid, locator);
            }
            if (null != rest && !rest.isEmpty()) {
                selectorPath = extractVaadinSelectorPath(path, pidSelector,
                        locator);
                if (selectorPath == null) {
                    selectorPath = SelectorPath.xpath(rest, pidSelector,
                            locator);
                }
            } else {
                selectorPath = pidSelector;
            }
        }
        return selectorPath;
    }

    private static SelectorPath extractVaadinSelectorPath(String path,
            SelectorPath parent, ComponentLocator locator) {
        SelectorPath selectorPath = null;

        String xpathPart = null;
        int xpathPos = Math.min(path.indexOf("/div"), path.indexOf("/span"));
        if (xpathPos >= 0) {
            xpathPart = path.substring(xpathPos);
            path = path.substring(0, xpathPos);
        }

        String subPartPart = null;
        int subPartPos = path.indexOf("#");
        if (subPartPos >= 0) {
            subPartPart = path.substring(subPartPos + 1);
            path = path.substring(0, subPartPos);
        }

        String domChildPart = null;
        int domChildPos = path.indexOf("/domChild");
        if (domChildPos >= 0) {
            // include the slash
            domChildPart = path.substring(domChildPos);
            path = path.substring(0, domChildPos);
        }

        // is it something VaadinSelectorPath can handle?
        String widgetClass = null;
        // first cases in a layout slot
        RegExp widgetInSlotMatcher = RegExp
                .compile("^/(Slot\\[(\\d+)\\]/)([a-zA-Z]+)(\\[0\\])?$");
        MatchResult matchResult = widgetInSlotMatcher.exec(path);
        if (null != matchResult) {
            if (matchResult.getGroupCount() >= 3) {
                widgetClass = matchResult.getGroup(3);
            }
        }
        // handle cases without intervening slot
        if (null == widgetClass) {
            RegExp widgetDirectlyMatcher = RegExp
                    .compile("^//?([a-zA-Z]+)(\\[(\\d+)\\])?$");
            matchResult = widgetDirectlyMatcher.exec(path);
            if (null != matchResult) {
                if (matchResult.getGroupCount() >= 1) {
                    widgetClass = matchResult.getGroup(1);
                }
            }
        }
        if (null != widgetClass && !widgetClass.isEmpty()) {
            selectorPath = findVaadinSelectorInParent(path, widgetClass,
                    parent, locator);
            if (null != subPartPart
                    && selectorPath instanceof VaadinSelectorPath) {
                ((VaadinSelectorPath) selectorPath).setSubPart(subPartPart);
            } else if (null != xpathPart
                    && selectorPath instanceof VaadinSelectorPath) {
                // try to find sub-part if supported
                ComponentConnector connector = Util.findPaintable(
                        locator.getClient(), selectorPath.findElement());
                if (connector != null
                        && connector.getWidget() instanceof SubPartAware) {
                    // for SubPartAware, skip the XPath fall-back path
                    Element element = locator.getElementByPathStartingAt(path,
                            selectorPath.findElement());
                    SubPartAware subPartAware = (SubPartAware) connector
                            .getWidget();
                    String subPart = subPartAware.getSubPartName(element);
                    if (null != subPart) {
                        // type checked above
                        ((VaadinSelectorPath) selectorPath).setSubPart(subPart);
                    }
                } else {
                    // fall-back to XPath for the last part of the path
                    selectorPath = SelectorPath.xpath(xpathPart, selectorPath,
                            locator);
                }
            }

            // the whole /domChild[i]/domChild[j]... part as a single selector
            if (null != domChildPart
                    && selectorPath instanceof VaadinSelectorPath) {
                selectorPath = SelectorPath.vaadinPath(domChildPart,
                        selectorPath, locator);
            }
        } else if (null != domChildPart) {
            // cases with domChild path only (parent contains rest)
            selectorPath = SelectorPath.vaadinPath(domChildPart, parent,
                    locator);
        }
        return selectorPath;
    }

    /**
     * Find the zero-based index of the widget of type widgetClass identified by
     * path within its parent and returns the corresponding Vaadin path (if
     * any). For instance, the second button in a layout has index 1 regardless
     * of non-button components in the parent.
     * <p>
     * The approach used internally is to try to find the caption of the element
     * inside its parent and check whether it is sufficient to identify the
     * element correctly. If not, possible indices are looped through to see if
     * the component of the specified type within the specified parent
     * identifies the correct element. This is inefficient but more reliable
     * than some alternative approaches, and does not require special cases for
     * various layouts etc.
     * 
     * @param path
     *            relative path for the widget of interest
     * @param widgetClass
     *            type of the widget of interest
     * @param parent
     *            parent component to which the path is relative
     * @param locator
     *            ComponentLocator used to map paths to elements
     * @return selector path for the element, null if none found
     */
    private static SelectorPath findVaadinSelectorInParent(String path,
            String widgetClass, SelectorPath parent, ComponentLocator locator) {
        if (null == parent) {
            SelectorPath selectorPath = SelectorPath.vaadin(widgetClass, 0,
                    null, locator);
            if (selectorPath.findElement() == locator.getElementByPath(path)) {
                return selectorPath;
            } else {
                return null;
            }
        }
        // This method uses an inefficient brute-force approach but its
        // results should match what is used by the TestBench selectors.
        Element parentElement = parent.findElement();
        String parentPathString = locator.getPathForElement(parentElement);
        if (null == parentPathString) {
            parentPathString = "";
        }
        Element elementToFind = locator.getElementByPath(parentPathString
                + path);
        if (null == elementToFind) {
            return null;
        }
        // if the connector has a caption, first try if the element can be
        // located in parent with it; if that fails, use the index in parent
        String caption = getCaptionForElement(elementToFind, locator);
        if (null != caption) {
            SelectorPath testPath = SelectorPath.vaadin(widgetClass, caption,
                    parent, locator);
            Element testElement = testPath.findElement();
            // TODO in theory could also iterate upwards into parents, using
            // "//" before the caption to find the shortest matching path that
            // identifies the correct element
            if (testElement == elementToFind) {
                return testPath;
            }
        }

        // Assumes that the number of logical child elements is at most the
        // number of direct children of the DOM element - e.g. layouts have a
        // single component per slot.
        for (int i = 0; i < parentElement.getChildCount(); ++i) {
            SelectorPath testPath = SelectorPath.vaadin(widgetClass, i, parent,
                    locator);
            Element testElement = testPath.findElement();
            if (testElement == elementToFind) {
                return testPath;
            }
        }
        return null;
    }

    private static String getCaptionForElement(Element element,
            ComponentLocator locator) {
        String caption = null;
        ComponentConnector connector = Util.findPaintable(locator.getClient(),
                element);
        if (null != connector) {
            Property property = AbstractConnector.getStateType(connector)
                    .getProperty("caption");
            try {
                Object value = property.getValue(connector.getState());
                if (null != value) {
                    caption = String.valueOf(value);
                }
            } catch (NoDataException e) {
                // skip the caption based selection and use index below
            }
        }
        return caption;
    }

    private static Element findConnectorRootElement(ServerConnector connector) {
        Element element = null;
        // try to find the root element of the connector
        if (connector instanceof ComponentConnector) {
            Widget widget = ((ComponentConnector) connector).getWidget();
            if (widget != null) {
                element = widget.getElement();
            }
        }
        return element;
    }

    public ComponentLocator getLocator() {
        return locator;
    }

    @Override
    public int hashCode() {
        return getJUnitSelector("context").hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SelectorPath other = (SelectorPath) obj;
        if (parent == null) {
            if (other.parent != null) {
                return false;
            }
        } else if (!parent.equals(other.parent)) {
            return false;
        }
        if (!other.getJUnitSelector("context").equals(
                getJUnitSelector("context"))) {
            return false;
        }
        return true;
    }

    protected static SelectorPath xpath(String path, SelectorPath parent,
            ComponentLocator locator) {
        return new XPathSelectorPath(path, parent, locator);
    }

    protected static SelectorPath id(String id, ComponentLocator locator) {
        return new IdSelectorPath(id, locator);
    }

    protected static SelectorPath vaadin(String widgetClass,
            String widgetCaption, SelectorPath parent, ComponentLocator locator) {
        return new VaadinSelectorPath(widgetClass, widgetCaption, 0, parent,
                locator);
    }

    protected static SelectorPath vaadin(String widgetClass, int widgetIndex,
            SelectorPath parent, ComponentLocator locator) {
        return new VaadinSelectorPath(widgetClass, null, widgetIndex, parent,
                locator);
    }

    protected static SelectorPath vaadinPath(String vaadinPath,
            SelectorPath parent, ComponentLocator locator) {
        return new ByVaadinSelectorPath(vaadinPath, parent, locator);
    }

    /**
     * Selector path for finding an {@link Element} based on an XPath (relative
     * to the parent {@link SelectorPath}).
     */
    private static class XPathSelectorPath extends SelectorPath {
        // path segment relative to parent
        private final String path;

        /**
         * Creates a relative XPath based component selector path.
         * 
         * @param path
         *            XPath
         * @param parent
         *            {@link SelectorPath} to which the XPath is relative, null
         *            if from the root
         * @param locator
         *            ComponentLocator to use to find the element
         */
        public XPathSelectorPath(String path, SelectorPath parent,
                ComponentLocator locator) {
            super(parent, locator);
            this.path = path;
        }

        /**
         * Returns the XPath relative to the parent element.
         * 
         * @return relative path string
         */
        public String getPath() {
            return path;
        }

        @Override
        public String getJUnitSelector(String context) {
            return context + ".findElement(By.xpath(\"" + getPath() + "\"))";
        }

        @Override
        public Element findElement() {
            if (null != getParent()) {
                Element parentElement = getParent().findElement();
                if (null == parentElement) {
                    // broken path - possibly removed parent
                    return null;
                }
                Element element = getLocator().getElementByPathStartingAt(
                        getPath(), parentElement);
                return element;
            } else {
                Element element = getLocator().getElementByPath(getPath());
                return element;
            }
        }
    }

    /**
     * Element identifier based locator path.
     * <p>
     * Identifier paths never have a parent and the identifiers should be unique
     * within the context of the {@link ComponentLocator}/page.
     */
    private static class IdSelectorPath extends SelectorPath {
        private final String id;

        /**
         * Creates an identifier based {@link SelectorPath}. The identifier
         * should not contain the old "PID_S" prefix.
         * 
         * @param id
         * @param locator
         */
        public IdSelectorPath(String id, ComponentLocator locator) {
            super(locator);
            this.id = id;
        }

        /**
         * Returns the ID in the DOM used to identify the element.
         * 
         * @return Vaadin debug ID or equivalent
         */
        public String getId() {
            return id;
        }

        @Override
        public String getJUnitSelector(String context) {
            return context + ".findElement(By.id(\"" + getId() + "\"))";
        }

        @Override
        public Element findElement() {
            // this also works for IDs
            return getLocator().getElementByPath("PID_S" + getId());
        }
    }

    /**
     * Common base class for Vaadin selector paths (By.vaadin(...)).
     */
    private static abstract class AbstractVaadinSelectorPath extends
            SelectorPath {

        protected AbstractVaadinSelectorPath(SelectorPath parent,
                ComponentLocator locator) {
            super(parent, locator);
        }

        /**
         * Returns the {@link ComponentLocator} path of the element relative to
         * the parent path.
         * 
         * @return path of the element for By.vaadin(...)
         */
        protected abstract String getPath();

        @Override
        public String getJUnitSelector(String context) {
            return context + ".findElement(By.vaadin(\"" + getPath() + "\"))";
        }

        @Override
        public Element findElement() {
            if (null != getParent()) {
                Element parentElement = getParent().findElement();
                Element element = getLocator().getElementByPathStartingAt(
                        getPath(), parentElement);
                return element;
            } else {
                return getLocator().getElementByPath(getPath());
            }
        }

    }

    /**
     * TestBench selector path for Vaadin widgets. These selectors are based on
     * the widget class and either the index among the widgets of that type in
     * the parent or the widget caption.
     */
    private static class VaadinSelectorPath extends AbstractVaadinSelectorPath {
        private final String widgetClass;
        private final String widgetCaption;
        // negative for no index
        private final int widgetIndex;
        private String subPart;

        /**
         * Creates a Vaadin {@link SelectorPath}. The path identifies an element
         * of a given type under its parent based on either its caption or its
         * index (if both are given, only the caption is used). See also
         * {@link ComponentLocator} for more details.
         * 
         * @param widgetClass
         *            client-side widget class
         * @param widgetCaption
         *            caption of the widget - null to use the index instead
         * @param widgetIndex
         *            index of the widget of the type within its parent, used
         *            only if the caption is not given
         * @param parent
         *            parent {@link SelectorPath} or null
         * @param locator
         *            component locator to use to find the corresponding
         *            {@link Element}
         */
        public VaadinSelectorPath(String widgetClass, String widgetCaption,
                int widgetIndex, SelectorPath parent, ComponentLocator locator) {
            super(parent, locator);
            this.widgetClass = widgetClass;
            this.widgetCaption = widgetCaption;
            this.widgetIndex = widgetIndex;
        }

        /**
         * Returns the widget type used to identify the element.
         * 
         * @return Vaadin widget class
         */
        public String getWidgetClass() {
            return widgetClass;
        }

        /**
         * Returns the widget caption to look for or null if index is used
         * instead.
         * 
         * @return widget caption to match
         */
        public String getWidgetCaption() {
            return widgetCaption;
        }

        /**
         * Returns the index of the widget of that type within its parent - only
         * used if caption is null.
         * 
         * @return widget index
         */
        public int getWidgetIndex() {
            return widgetIndex;
        }

        /**
         * Returns the sub-part string (e.g. row and column identifiers within a
         * table) used to identify a part of a component. See
         * {@link ComponentLocator} and especially Vaadin selectors for more
         * information.
         * 
         * @return sub-part string or null if none
         */
        public String getSubPart() {
            return subPart;
        }

        /**
         * Sets the sub-part string (e.g. row and column identifiers within a
         * table) used to identify a part of a component. See
         * {@link ComponentLocator} and especially Vaadin selectors for more
         * information.
         * 
         * @param subPart
         *            sub-part string to use or null for none
         */
        public void setSubPart(String subPart) {
            this.subPart = subPart;
        }

        @Override
        protected String getPath() {
            return "/" + getWidgetClass() + getIndexString(false)
                    + getSubPartPostfix();
        }

        private String getIndexString(boolean escapeQuotes) {
            if (null != getWidgetCaption()) {
                if (escapeQuotes) {
                    return "[caption=\\\"" + widgetCaption + "\\\"]";
                } else {
                    return "[caption=\"" + widgetCaption + "\"]";
                }
            } else if (widgetIndex >= 0) {
                return "[" + getWidgetIndex() + "]";
            } else {
                return "";
            }
        }

        private String getSubPartPostfix() {
            String subPartString = "";
            if (null != getSubPart()) {
                subPartString = SUBPART_SEPARATOR + getSubPart();
            }
            return subPartString;
        }
    }

    /**
     * TestBench selector path for Vaadin widgets, always using a
     * By.vaadin(path) rather than other convenience methods.
     */
    private static class ByVaadinSelectorPath extends
            AbstractVaadinSelectorPath {
        private final String path;

        /**
         * Vaadin selector path for an exact path (including any preceding
         * slash).
         * 
         * @param path
         *            path of the element (normally with a leading slash), not
         *            null
         * @param parent
         *            parent selector path or null if none
         * @param locator
         *            ComponentLocator to use to find the corresponding element
         */
        public ByVaadinSelectorPath(String path, SelectorPath parent,
                ComponentLocator locator) {
            super(parent, locator);
            this.path = path;
        }

        /**
         * Returns the By.vaadin(...) path relative to the parent element.
         * 
         * @return relative path string
         */
        @Override
        public String getPath() {
            return path;
        }
    }
}