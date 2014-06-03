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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Style.TextDecoration;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ApplicationConfiguration;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ComputedStyle;
import com.vaadin.client.ConnectorMap;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.SimpleTree;
import com.vaadin.client.Util;
import com.vaadin.client.ValueMap;

/**
 * Analyze layouts view panel of the debug window.
 * 
 * @since 7.1.4
 */
public class AnalyzeLayoutsPanel extends FlowPanel {

    private List<SelectConnectorListener> listeners = new ArrayList<SelectConnectorListener>();

    public void update() {
        clear();
        add(new Label("Analyzing layouts..."));
        List<ApplicationConnection> runningApplications = ApplicationConfiguration
                .getRunningApplications();
        for (ApplicationConnection applicationConnection : runningApplications) {
            applicationConnection.analyzeLayouts();
        }
    }

    public void meta(ApplicationConnection ac, ValueMap meta) {
        clear();
        JsArray<ValueMap> valueMapArray = meta
                .getJSValueMapArray("invalidLayouts");
        int size = valueMapArray.length();

        if (size > 0) {
            SimpleTree root = new SimpleTree("Layouts analyzed, " + size
                    + " top level problems");
            for (int i = 0; i < size; i++) {
                printLayoutError(ac, valueMapArray.get(i), root);
            }
            root.open(false);
            add(root);
        } else {
            add(new Label("Layouts analyzed, no top level problems"));
        }

        Set<ComponentConnector> zeroHeightComponents = new HashSet<ComponentConnector>();
        Set<ComponentConnector> zeroWidthComponents = new HashSet<ComponentConnector>();
        findZeroSizeComponents(zeroHeightComponents, zeroWidthComponents,
                ac.getUIConnector());
        if (zeroHeightComponents.size() > 0 || zeroWidthComponents.size() > 0) {
            add(new HTML("<h4> Client side notifications</h4>"
                    + " <em>The following relative sized components were "
                    + "rendered to a zero size container on the client side."
                    + " Note that these are not necessarily invalid "
                    + "states, but reported here as they might be.</em>"));
            if (zeroHeightComponents.size() > 0) {
                add(new HTML("<p><strong>Vertically zero size:</strong></p>"));
                printClientSideDetectedIssues(zeroHeightComponents, ac);
            }
            if (zeroWidthComponents.size() > 0) {
                add(new HTML("<p><strong>Horizontally zero size:</strong></p>"));
                printClientSideDetectedIssues(zeroWidthComponents, ac);
            }
        }

    }

    private void printClientSideDetectedIssues(
            Set<ComponentConnector> zeroSized, ApplicationConnection ac) {

        // keep track of already highlighted parents
        HashSet<String> parents = new HashSet<String>();

        for (final ComponentConnector connector : zeroSized) {
            final ServerConnector parent = connector.getParent();
            final String parentId = parent.getConnectorId();

            final Label errorDetails = new Label(Util.getSimpleName(connector)
                    + "[" + connector.getConnectorId() + "]" + " inside "
                    + Util.getSimpleName(parent));

            if (parent instanceof ComponentConnector) {
                final ComponentConnector parentConnector = (ComponentConnector) parent;
                if (!parents.contains(parentId)) {
                    parents.add(parentId);
                    Highlight.show(parentConnector, "yellow");
                }

                errorDetails.addMouseOverHandler(new MouseOverHandler() {
                    @Override
                    public void onMouseOver(MouseOverEvent event) {
                        Highlight.hideAll();
                        Highlight.show(parentConnector, "yellow");
                        Highlight.show(connector);
                        errorDetails.getElement().getStyle()
                                .setTextDecoration(TextDecoration.UNDERLINE);
                    }
                });
                errorDetails.addMouseOutHandler(new MouseOutHandler() {
                    @Override
                    public void onMouseOut(MouseOutEvent event) {
                        Highlight.hideAll();
                        errorDetails.getElement().getStyle()
                                .setTextDecoration(TextDecoration.NONE);
                    }
                });
                errorDetails.addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        fireSelectEvent(connector);
                    }
                });

            }

            Highlight.show(connector);
            add(errorDetails);

        }
    }

    private void printLayoutError(ApplicationConnection ac, ValueMap valueMap,
            SimpleTree root) {
        final String pid = valueMap.getString("id");

        // find connector
        final ComponentConnector connector = (ComponentConnector) ConnectorMap
                .get(ac).getConnector(pid);

        if (connector == null) {
            root.add(new SimpleTree("[" + pid + "] NOT FOUND"));
            return;
        }

        Highlight.show(connector);

        final SimpleTree errorNode = new SimpleTree(
                Util.getSimpleName(connector) + " id: " + pid);
        errorNode.addDomHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent event) {
                Highlight.showOnly(connector);
                ((Widget) event.getSource()).getElement().getStyle()
                        .setTextDecoration(TextDecoration.UNDERLINE);
            }
        }, MouseOverEvent.getType());
        errorNode.addDomHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent event) {
                Highlight.hideAll();
                ((Widget) event.getSource()).getElement().getStyle()
                        .setTextDecoration(TextDecoration.NONE);
            }
        }, MouseOutEvent.getType());

        errorNode.addDomHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (event.getNativeEvent().getEventTarget().cast() == errorNode
                        .getElement().getChild(1).cast()) {
                    fireSelectEvent(connector);
                }
            }
        }, ClickEvent.getType());

        VerticalPanel errorDetails = new VerticalPanel();

        if (valueMap.containsKey("heightMsg")) {
            errorDetails.add(new Label("Height problem: "
                    + valueMap.getString("heightMsg")));
        }
        if (valueMap.containsKey("widthMsg")) {
            errorDetails.add(new Label("Width problem: "
                    + valueMap.getString("widthMsg")));
        }
        if (errorDetails.getWidgetCount() > 0) {
            errorNode.add(errorDetails);
        }
        if (valueMap.containsKey("subErrors")) {
            HTML l = new HTML(
                    "<em>Expand this node to show problems that may be dependent on this problem.</em>");
            errorDetails.add(l);
            JsArray<ValueMap> suberrors = valueMap
                    .getJSValueMapArray("subErrors");
            for (int i = 0; i < suberrors.length(); i++) {
                ValueMap value = suberrors.get(i);
                printLayoutError(ac, value, errorNode);
            }

        }
        root.add(errorNode);
    }

    private void findZeroSizeComponents(
            Set<ComponentConnector> zeroHeightComponents,
            Set<ComponentConnector> zeroWidthComponents,
            ComponentConnector connector) {
        Widget widget = connector.getWidget();
        ComputedStyle computedStyle = new ComputedStyle(widget.getElement());
        if (computedStyle.getIntProperty("height") == 0) {
            zeroHeightComponents.add(connector);
        }
        if (computedStyle.getIntProperty("width") == 0) {
            zeroWidthComponents.add(connector);
        }
        List<ServerConnector> children = connector.getChildren();
        for (ServerConnector serverConnector : children) {
            if (serverConnector instanceof ComponentConnector) {
                findZeroSizeComponents(zeroHeightComponents,
                        zeroWidthComponents,
                        (ComponentConnector) serverConnector);
            }
        }
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

}
