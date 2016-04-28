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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.HasDoubleClickHandlers;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ApplicationConfiguration;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.FastStringSet;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.SimpleTree;
import com.vaadin.client.Util;

/**
 * Hierarchy view panel of the debug window. This class can be used in various
 * debug window sections to show the current connector hierarchy.
 * 
 * @since 7.1.4
 */
public class HierarchyPanel extends FlowPanel {

    // TODO separate click listeners for simple selection and doubleclick
    private List<SelectConnectorListener> listeners = new ArrayList<SelectConnectorListener>();

    public void update() {
        // Try to keep track of currently open nodes and reopen them
        FastStringSet openNodes = FastStringSet.create();
        Iterator<Widget> it = iterator();
        while (it.hasNext()) {
            collectOpenNodes(it.next(), openNodes);
        }

        clear();

        SimplePanel trees = new SimplePanel();

        for (ApplicationConnection application : ApplicationConfiguration
                .getRunningApplications()) {
            ServerConnector uiConnector = application.getUIConnector();
            Widget connectorTree = buildConnectorTree(uiConnector, openNodes);

            trees.add(connectorTree);
        }

        add(trees);
    }

    /**
     * Adds the captions of all open (non-leaf) nodes in the hierarchy tree
     * recursively.
     * 
     * @param widget
     *            the widget in which to search for open nodes (if SimpleTree)
     * @param openNodes
     *            the set in which open nodes should be added
     */
    private void collectOpenNodes(Widget widget, FastStringSet openNodes) {
        if (widget instanceof SimpleTree) {
            SimpleTree tree = (SimpleTree) widget;
            if (tree.isOpen()) {
                openNodes.add(tree.getCaption());
            } else {
                // no need to look inside closed nodes
                return;
            }
        }
        if (widget instanceof HasWidgets) {
            Iterator<Widget> it = ((HasWidgets) widget).iterator();
            while (it.hasNext()) {
                collectOpenNodes(it.next(), openNodes);
            }
        }
    }

    private Widget buildConnectorTree(final ServerConnector connector,
            FastStringSet openNodes) {
        String connectorString = Util.getConnectorString(connector);

        List<ServerConnector> children = connector.getChildren();

        Widget widget;
        if (children == null || children.isEmpty()) {
            // Leaf node, just add a label
            Label label = new Label(connectorString);
            label.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    Highlight.showOnly(connector);
                    showServerDebugInfo(connector);
                }
            });
            widget = label;
        } else {
            SimpleTree tree = new SimpleTree(connectorString) {
                @Override
                protected void select(ClickEvent event) {
                    super.select(event);
                    Highlight.showOnly(connector);
                    showServerDebugInfo(connector);
                }
            };
            for (ServerConnector child : children) {
                tree.add(buildConnectorTree(child, openNodes));
            }
            if (openNodes.contains(connectorString)) {
                tree.open(false);
            }
            widget = tree;
        }

        if (widget instanceof HasDoubleClickHandlers) {
            HasDoubleClickHandlers has = (HasDoubleClickHandlers) widget;
            has.addDoubleClickHandler(new DoubleClickHandler() {
                @Override
                public void onDoubleClick(DoubleClickEvent event) {
                    fireSelectEvent(connector);
                }
            });
        }

        return widget;
    }

    public void addListener(SelectConnectorListener listener) {
        listeners.add(listener);
    }

    public void removeListener(SelectConnectorListener listener) {
        listeners.remove(listener);
    }

    private void fireSelectEvent(ServerConnector connector) {
        for (SelectConnectorListener listener : listeners) {
            listener.select(connector, null);
        }
    }

    /**
     * Outputs debug information on the server - usually in the console of an
     * IDE, with a clickable reference to the relevant code location.
     * 
     * @since 7.1
     * @param connector
     *            show debug info for this connector
     */
    static void showServerDebugInfo(ServerConnector connector) {
        if (connector != null) {
            connector.getConnection().getUIConnector()
                    .showServerDebugInfo(connector);
        }
    }

}
