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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorMap;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.Util;
import com.vaadin.client.VCaption;
import com.vaadin.client.ui.SubPartAware;
import com.vaadin.client.ui.VCssLayout;
import com.vaadin.client.ui.VGridLayout;
import com.vaadin.client.ui.VOverlay;
import com.vaadin.client.ui.VTabsheetPanel;
import com.vaadin.client.ui.VUI;
import com.vaadin.client.ui.VWindow;
import com.vaadin.client.ui.orderedlayout.Slot;
import com.vaadin.client.ui.orderedlayout.VAbstractOrderedLayout;
import com.vaadin.client.ui.window.WindowConnector;
import com.vaadin.shared.AbstractComponentState;
import com.vaadin.shared.Connector;
import com.vaadin.shared.communication.SharedState;

/**
 * The LegacyLocatorStrategy class handles the legacy locator syntax that was
 * introduced in version 5.4 of the framework. The legacy locator strategy is
 * always used if no other strategy claims responsibility for a locator string.
 * 
 * @since 7.2
 * @author Vaadin Ltd
 */
public class LegacyLocatorStrategy implements LocatorStrategy {

    /**
     * Separator used in the String locator between a parent and a child widget.
     */
    static final String PARENTCHILD_SEPARATOR = "/";
    /**
     * Separator used in the String locator between the part identifying the
     * containing widget and the part identifying the target element within the
     * widget.
     */
    static final String SUBPART_SEPARATOR = "#";
    /**
     * String that identifies the root panel when appearing first in the String
     * locator.
     */
    static final String ROOT_ID = "Root";

    private final ApplicationConnection client;

    private static final RegExp validSyntax = RegExp
            .compile("^((\\w+::)?((PID_S)?\\w[-$_a-zA-Z0-9.' ]*)?)?(/[-$_a-zA-Z0-9]+\\[\\d+\\])*/?(#.*)?$");

    public LegacyLocatorStrategy(ApplicationConnection clientConnection) {
        client = clientConnection;
    }

    @Override
    public boolean validatePath(String path) {
        return validSyntax.test(path);
    }

    @Override
    public String getPathForElement(Element targetElement) {
        ComponentConnector connector = Util
                .findPaintable(client, targetElement);

        Widget w = null;
        if (connector != null) {
            // If we found a Paintable then we use that as reference. We should
            // find the Paintable for all but very special cases (like
            // overlays).
            w = connector.getWidget();

            /*
             * Still if the Paintable contains a widget that implements
             * SubPartAware, we want to use that as a reference
             */
            Widget targetParent = findParentWidget(targetElement, w);
            while (targetParent != w && targetParent != null) {
                if (targetParent instanceof SubPartAware) {
                    /*
                     * The targetParent widget is a child of the Paintable and
                     * the first parent (of the targetElement) that implements
                     * SubPartAware
                     */
                    w = targetParent;
                    break;
                }
                targetParent = targetParent.getParent();
            }
        }
        if (w == null) {
            // Check if the element is part of a widget that is attached
            // directly to the root panel
            RootPanel rootPanel = RootPanel.get();
            int rootWidgetCount = rootPanel.getWidgetCount();
            for (int i = 0; i < rootWidgetCount; i++) {
                Widget rootWidget = rootPanel.getWidget(i);
                if (rootWidget.getElement().isOrHasChild(targetElement)) {
                    // The target element is contained by this root widget
                    w = findParentWidget(targetElement, rootWidget);
                    break;
                }
            }
            if (w != null) {
                // We found a widget but we should still see if we find a
                // SubPartAware implementor (we cannot find the Paintable as
                // there is no link from VOverlay to its paintable/owner).
                Widget subPartAwareWidget = findSubPartAwareParentWidget(w);
                if (subPartAwareWidget != null) {
                    w = subPartAwareWidget;
                }
            }
        }

        if (w == null) {
            // Containing widget not found
            return null;
        }

        // Determine the path for the target widget
        String path = getPathForWidget(w);
        if (path == null) {
            /*
             * No path could be determined for the target widget. Cannot create
             * a locator string.
             */
            return null;
        }

        // The parent check is a work around for Firefox 15 which fails to
        // compare elements properly (#9534)
        if (w.getElement() == targetElement) {
            /*
             * We are done if the target element is the root of the target
             * widget.
             */
            return path;
        } else if (w instanceof SubPartAware) {
            /*
             * If the widget can provide an identifier for the targetElement we
             * let it do that
             */
            String elementLocator = ((SubPartAware) w).getSubPartName(DOM
                    .asOld(targetElement));
            if (elementLocator != null) {
                return path + LegacyLocatorStrategy.SUBPART_SEPARATOR
                        + elementLocator;
            }
        }
        /*
         * If everything else fails we use the DOM path to identify the target
         * element
         */
        String domPath = getDOMPathForElement(targetElement, w.getElement());
        if (domPath == null) {
            return path;
        } else {
            return path + domPath;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Element getElementByPath(String path) {
        return getElementByPathStartingAt(path, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Element getElementByPathStartingAt(String path, Element baseElement) {
        /*
         * Path is of type "targetWidgetPath#componentPart" or
         * "targetWidgetPath".
         */
        String parts[] = path.split(LegacyLocatorStrategy.SUBPART_SEPARATOR, 2);
        String widgetPath = parts[0];

        // Note that this only works if baseElement can be mapped to a
        // widget to which the path is relative. Otherwise, the current
        // implementation simply interprets the path as if baseElement was
        // null.
        Widget baseWidget = Util.findWidget(baseElement, null);

        Widget w = getWidgetFromPath(widgetPath, baseWidget);
        if (w == null || !Util.isAttachedAndDisplayed(w)) {
            return null;
        }
        if (parts.length == 1) {
            int pos = widgetPath.indexOf("domChild");
            if (pos == -1) {
                return w.getElement();
            }

            // Contains dom reference to a sub element of the widget
            String subPath = widgetPath.substring(pos);
            return getElementByDOMPath(w.getElement(), subPath);
        } else if (parts.length == 2) {
            if (w instanceof SubPartAware) {
                return ((SubPartAware) w).getSubPartElement(parts[1]);
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Element> getElementsByPath(String path) {
        // This type of search is not supported in LegacyLocator
        List<Element> array = new ArrayList<Element>();
        Element e = getElementByPath(path);
        if (e != null) {
            array.add(e);
        }
        return array;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Element> getElementsByPathStartingAt(String path, Element root) {
        // This type of search is not supported in LegacyLocator
        List<Element> array = new ArrayList<Element>();
        Element e = getElementByPathStartingAt(path, root);
        if (e != null) {
            array.add(e);
        }
        return array;
    }

    /**
     * Finds the first widget in the hierarchy (moving upwards) that implements
     * SubPartAware. Returns the SubPartAware implementor or null if none is
     * found.
     * 
     * @param w
     *            The widget to start from. This is returned if it implements
     *            SubPartAware.
     * @return The first widget (upwards in hierarchy) that implements
     *         SubPartAware or null
     */
    Widget findSubPartAwareParentWidget(Widget w) {

        while (w != null) {
            if (w instanceof SubPartAware) {
                return w;
            }
            w = w.getParent();
        }
        return null;
    }

    /**
     * Returns the first widget found when going from {@code targetElement}
     * upwards in the DOM hierarchy, assuming that {@code ancestorWidget} is a
     * parent of {@code targetElement}.
     * 
     * @param targetElement
     * @param ancestorWidget
     * @return The widget whose root element is a parent of
     *         {@code targetElement}.
     */
    private Widget findParentWidget(Element targetElement, Widget ancestorWidget) {
        /*
         * As we cannot resolve Widgets from the element we start from the
         * widget and move downwards to the correct child widget, as long as we
         * find one.
         */
        if (ancestorWidget instanceof HasWidgets) {
            for (Widget w : ((HasWidgets) ancestorWidget)) {
                if (w.getElement().isOrHasChild(targetElement)) {
                    return findParentWidget(targetElement, w);
                }
            }
        }

        // No children found, this is it
        return ancestorWidget;
    }

    /**
     * Locates an element based on a DOM path and a base element.
     * 
     * @param baseElement
     *            The base element which the path is relative to
     * @param path
     *            String locator (consisting of domChild[x] parts) that
     *            identifies the element
     * @return The element identified by path, relative to baseElement or null
     *         if the element could not be found.
     */
    private Element getElementByDOMPath(Element baseElement, String path) {
        String parts[] = path.split(PARENTCHILD_SEPARATOR);
        Element element = baseElement;

        for (int i = 0, l = parts.length; i < l; ++i) {
            String part = parts[i];
            if (part.startsWith("domChild[")) {
                String childIndexString = part.substring("domChild[".length(),
                        part.length() - 1);

                if (Util.findWidget(baseElement, null) instanceof VAbstractOrderedLayout) {
                    if (element.hasChildNodes()) {
                        Element e = element.getFirstChildElement().cast();
                        String cn = e.getClassName();
                        if (cn != null
                                && (cn.equals("v-expand") || cn
                                        .contains("v-has-caption"))) {
                            element = e;
                        }
                    }
                }

                try {
                    int childIndex = Integer.parseInt(childIndexString);
                    element = DOM.getChild(element, childIndex);
                } catch (Exception e) {
                    return null;
                }

                if (element == null) {
                    return null;
                }

            } else {

                path = parts[i];
                for (int j = i + 1; j < l; ++j) {
                    path += PARENTCHILD_SEPARATOR + parts[j];
                }

                return getElementByPathStartingAt(path, element);
            }
        }

        return element;
    }

    /**
     * Generates a String locator using domChild[x] parts for the element
     * relative to the baseElement.
     * 
     * @param element
     *            The target element
     * @param baseElement
     *            The starting point for the locator. The generated path is
     *            relative to this element.
     * @return A String locator that can be used to locate the target element
     *         using {@link #getElementByDOMPath(Element, String)} or null if
     *         the locator String cannot be created.
     */
    private String getDOMPathForElement(Element element, Element baseElement) {
        Element e = element;
        String path = "";
        while (true) {
            int childIndex = -1;
            Element siblingIterator = e;
            while (siblingIterator != null) {
                childIndex++;
                siblingIterator = siblingIterator.getPreviousSiblingElement()
                        .cast();
            }

            path = PARENTCHILD_SEPARATOR + "domChild[" + childIndex + "]"
                    + path;

            JavaScriptObject parent = e.getParentElement();
            if (parent == null) {
                return null;
            }
            // The parent check is a work around for Firefox 15 which fails to
            // compare elements properly (#9534)
            if (parent == baseElement) {
                break;
            }

            e = parent.cast();
        }

        return path;
    }

    /**
     * Creates a locator String for the given widget. The path can be used to
     * locate the widget using {@link #getWidgetFromPath(String, Widget)}.
     * <p/>
     * Returns null if no path can be determined for the widget or if the widget
     * is null.
     * 
     * @param w
     *            The target widget
     * @return A String locator for the widget
     */
    private String getPathForWidget(Widget w) {
        if (w == null) {
            return null;
        }
        String elementId = w.getElement().getId();
        if (elementId != null && !elementId.isEmpty()
                && !elementId.startsWith("gwt-uid-")) {
            // Use PID_S+id if the user has set an id but do not use it for auto
            // generated id:s as these might not be consistent
            return "PID_S" + elementId;
        } else if (w instanceof VUI) {
            return "";
        } else if (w instanceof VWindow) {
            Connector windowConnector = ConnectorMap.get(client)
                    .getConnector(w);
            List<WindowConnector> subWindowList = client.getUIConnector()
                    .getSubWindows();
            int indexOfSubWindow = subWindowList.indexOf(windowConnector);
            return PARENTCHILD_SEPARATOR + "VWindow[" + indexOfSubWindow + "]";
        } else if (w instanceof RootPanel) {
            return ROOT_ID;
        }

        Widget parent = w.getParent();

        String basePath = getPathForWidget(parent);
        if (basePath == null) {
            return null;
        }
        String simpleName = Util.getSimpleName(w);

        /*
         * Check if the parent implements Iterable. At least VPopupView does not
         * implement HasWdgets so we cannot check for that.
         */
        if (!(parent instanceof Iterable<?>)) {
            // Parent does not implement Iterable so we cannot find out which
            // child this is
            return null;
        }

        Iterator<?> i = ((Iterable<?>) parent).iterator();
        int pos = 0;
        while (i.hasNext()) {
            Object child = i.next();
            if (child == w) {
                return basePath + PARENTCHILD_SEPARATOR + simpleName + "["
                        + pos + "]";
            }
            String simpleName2 = Util.getSimpleName(child);
            if (simpleName.equals(simpleName2)) {
                pos++;
            }
        }

        return null;
    }

    /**
     * Locates the widget based on a String locator.
     * 
     * @param path
     *            The String locator that identifies the widget.
     * @param baseWidget
     *            the widget to which the path is relative, null if relative to
     *            root
     * @return The Widget identified by the String locator or null if the widget
     *         could not be identified.
     */
    @SuppressWarnings("unchecked")
    private Widget getWidgetFromPath(String path, Widget baseWidget) {
        Widget w = baseWidget;
        String parts[] = path.split(PARENTCHILD_SEPARATOR);

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];

            if (part.equals(ROOT_ID)) {
                w = RootPanel.get();
            } else if (part.equals("")) {
                if (w == null) {
                    w = client.getUIConnector().getWidget();
                }
            } else if (w == null) {
                String id = part;
                // Must be old static pid (PID_S*)
                ServerConnector connector = ConnectorMap.get(client)
                        .getConnector(id);
                if (connector == null) {
                    // Lookup by component id
                    // TODO Optimize this
                    connector = findConnectorById(client.getUIConnector(),
                            id.substring(5));
                }

                if (connector instanceof ComponentConnector) {
                    w = ((ComponentConnector) connector).getWidget();
                } else {
                    // Not found
                    return null;
                }
            } else if (part.startsWith("domChild[")) {
                // The target widget has been found and the rest identifies the
                // element
                break;
            } else if (w instanceof Iterable) {
                // W identifies a widget that contains other widgets, as it
                // should. Try to locate the child
                Iterable<?> parent = (Iterable<?>) w;

                // Part is of type "VVerticalLayout[0]", split this into
                // VVerticalLayout and 0
                String[] split = part.split("\\[", 2);
                String widgetClassName = split[0];
                String indexString = split[1].substring(0,
                        split[1].length() - 1);

                int widgetPosition;
                try {
                    widgetPosition = Integer.parseInt(indexString);
                } catch (NumberFormatException e) {
                    // We've probably been fed a new-style Vaadin locator with a
                    // string-form predicate, that doesn't match anything in the
                    // search space.
                    return null;
                }

                // AbsolutePanel in GridLayout has been removed -> skip it
                if (w instanceof VGridLayout
                        && "AbsolutePanel".equals(widgetClassName)) {
                    continue;
                }

                // FlowPane in CSSLayout has been removed -> skip it
                if (w instanceof VCssLayout
                        && "VCssLayout$FlowPane".equals(widgetClassName)) {
                    continue;
                }

                // ChildComponentContainer and VOrderedLayout$Slot have been
                // replaced with Slot
                if (w instanceof VAbstractOrderedLayout
                        && ("ChildComponentContainer".equals(widgetClassName) || "VOrderedLayout$Slot"
                                .equals(widgetClassName))) {
                    widgetClassName = "Slot";
                }

                if (w instanceof VTabsheetPanel && widgetPosition != 0) {
                    // TabSheetPanel now only contains 1 connector => the index
                    // is always 0 which indicates the widget in the active tab
                    widgetPosition = 0;
                }
                if (w instanceof VOverlay
                        && "VCalendarPanel".equals(widgetClassName)) {
                    // Vaadin 7.1 adds a wrapper for datefield popups
                    parent = (Iterable<?>) ((Iterable<?>) parent).iterator()
                            .next();
                }
                /*
                 * The new grid and ordered layouts do not contain
                 * ChildComponentContainer widgets. This is instead simulated by
                 * constructing a path step that would find the desired widget
                 * from the layout and injecting it as the next search step
                 * (which would originally have found the widget inside the
                 * ChildComponentContainer)
                 */
                if ((w instanceof VGridLayout)
                        && "ChildComponentContainer".equals(widgetClassName)
                        && i + 1 < parts.length) {

                    HasWidgets layout = (HasWidgets) w;

                    String nextPart = parts[i + 1];
                    String[] nextSplit = nextPart.split("\\[", 2);
                    String nextWidgetClassName = nextSplit[0];

                    // Find the n:th child and count the number of children with
                    // the same type before it
                    int nextIndex = 0;
                    for (Widget child : layout) {
                        boolean matchingType = nextWidgetClassName.equals(Util
                                .getSimpleName(child));
                        if (matchingType && widgetPosition == 0) {
                            // This is the n:th child that we looked for
                            break;
                        } else if (widgetPosition < 0) {
                            // Error if we're past the desired position without
                            // a match
                            return null;
                        } else if (matchingType) {
                            // If this was another child of the expected type,
                            // increase the count for the next step
                            nextIndex++;
                        }

                        // Don't count captions
                        if (!(child instanceof VCaption)) {
                            widgetPosition--;
                        }
                    }

                    // Advance to the next step, this time checking for the
                    // actual child widget
                    parts[i + 1] = nextWidgetClassName + '[' + nextIndex + ']';
                    continue;
                }

                // Locate the child
                Iterator<? extends Widget> iterator;

                /*
                 * VWindow and VContextMenu workarounds for backwards
                 * compatibility
                 */
                if (widgetClassName.equals("VWindow")) {
                    List<WindowConnector> windows = client.getUIConnector()
                            .getSubWindows();
                    List<VWindow> windowWidgets = new ArrayList<VWindow>(
                            windows.size());
                    for (WindowConnector wc : windows) {
                        windowWidgets.add(wc.getWidget());
                    }
                    iterator = windowWidgets.iterator();
                } else if (widgetClassName.equals("VContextMenu")) {
                    return client.getContextMenu();
                } else {
                    iterator = (Iterator<? extends Widget>) parent.iterator();
                }

                boolean ok = false;

                // Find the widgetPosition:th child of type "widgetClassName"
                while (iterator.hasNext()) {

                    Widget child = iterator.next();
                    String simpleName2 = Util.getSimpleName(child);

                    if (!widgetClassName.equals(simpleName2)
                            && child instanceof Slot) {
                        /*
                         * Support legacy tests without any selector for the
                         * Slot widget (i.e. /VVerticalLayout[0]/VButton[0]) by
                         * directly checking the stuff inside the slot
                         */
                        child = ((Slot) child).getWidget();
                        simpleName2 = Util.getSimpleName(child);
                    }

                    if (widgetClassName.equals(simpleName2)) {
                        if (widgetPosition == 0) {
                            w = child;
                            ok = true;
                            break;
                        }
                        widgetPosition--;

                    }
                }

                if (!ok) {
                    // Did not find the child
                    return null;
                }
            } else {
                // W identifies something that is not a "HasWidgets". This
                // should not happen as all widget containers should implement
                // HasWidgets.
                return null;
            }
        }

        return w;
    }

    private ServerConnector findConnectorById(ServerConnector root, String id) {
        SharedState state = root.getState();
        if (state instanceof AbstractComponentState
                && id.equals(((AbstractComponentState) state).id)) {
            return root;
        }
        for (ServerConnector child : root.getChildren()) {
            ServerConnector found = findConnectorById(child, id);
            if (found != null) {
                return found;
            }
        }

        return null;
    }

}
