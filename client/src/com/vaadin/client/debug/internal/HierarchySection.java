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

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ApplicationConfiguration;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.Util;
import com.vaadin.client.ValueMap;
import com.vaadin.client.WidgetUtil;

/**
 * Provides functionality for examining the UI component hierarchy.
 * 
 * @since 7.1
 * @author Vaadin Ltd
 */
public class HierarchySection implements Section {
    private final DebugButton tabButton = new DebugButton(Icon.HIERARCHY,
            "Examine component hierarchy");

    private final SimplePanel content = new SimplePanel();

    // TODO highlighting logic is split between these, should be refactored
    private final FlowPanel helpPanel = new FlowPanel();
    private final ConnectorInfoPanel infoPanel = new ConnectorInfoPanel();
    private final HierarchyPanel hierarchyPanel = new HierarchyPanel();
    private final OptimizedWidgetsetPanel widgetsetPanel = new OptimizedWidgetsetPanel();
    private final AnalyzeLayoutsPanel analyzeLayoutsPanel = new AnalyzeLayoutsPanel();

    private final FlowPanel controls = new FlowPanel();

    private final Button find = new DebugButton(Icon.HIGHLIGHT,
            "Select a component on the page to inspect it");
    private final Button analyze = new DebugButton(Icon.ANALYZE,
            "Check layouts for potential problems");
    private final Button generateWS = new DebugButton(Icon.OPTIMIZE,
            "Show used connectors and how to optimize widgetset");
    private final Button showHierarchy = new DebugButton(Icon.HIERARCHY,
            "Show the connector hierarchy tree");
    private final Button generateDesign = new DebugButton(Icon.SHOW_DESIGN,
            "Generate a declarative design for the given component sub tree");

    private HandlerRegistration highlightModeRegistration = null;

    public interface FindHandler {

        /**
         * Called when the user hovers over a connector, which is highlighted.
         * Also called when hovering outside the tree, e.g. over the debug
         * console, but in this case the connector is null
         * 
         * @param connector
         */
        void onHover(ComponentConnector connector);

        /**
         * Called when the user clicks on a highlighted connector.
         * 
         * @param connector
         */
        void onSelected(ComponentConnector connector);

    }

    private FindHandler inspectComponent = new FindHandler() {

        @Override
        public void onHover(ComponentConnector connector) {
            if (connector == null) {
                infoPanel.clear();
            } else {
                printState(connector, false);
            }
        }

        @Override
        public void onSelected(ComponentConnector connector) {
            stopFind();
            printState(connector, true);
        }

    };
    private FindHandler showComponentDesign = new FindHandler() {

        @Override
        public void onHover(ComponentConnector connector) {
            Highlight.showOnly(connector);
        }

        @Override
        public void onSelected(ComponentConnector connector) {
            stopFind();
            connector.getConnection().getUIConnector()
                    .showServerDesign(connector);
            content.setWidget(new HTML(
                    "Design file for component sent to server log"));
        }

    };

    private FindHandler activeFindHandler;

    public HierarchySection() {
        controls.add(showHierarchy);
        showHierarchy.setStylePrimaryName(VDebugWindow.STYLENAME_BUTTON);
        showHierarchy.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                showHierarchy();
            }
        });

        controls.add(find);
        find.setStylePrimaryName(VDebugWindow.STYLENAME_BUTTON);
        find.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                toggleFind(inspectComponent);
            }
        });

        controls.add(analyze);
        analyze.setStylePrimaryName(VDebugWindow.STYLENAME_BUTTON);
        analyze.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                stopFind();
                analyzeLayouts();
            }
        });

        controls.add(generateWS);
        generateWS.setStylePrimaryName(VDebugWindow.STYLENAME_BUTTON);
        generateWS.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                generateWidgetset();
            }
        });

        controls.add(generateDesign);
        generateDesign.setStylePrimaryName(VDebugWindow.STYLENAME_BUTTON);
        generateDesign.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                content.setWidget(new HTML(
                        "Select a layout or component to generate the declarative design"));
                toggleFind(showComponentDesign);
            }
        });

        hierarchyPanel.addListener(new SelectConnectorListener() {
            @Override
            public void select(ServerConnector connector, Element element) {
                printState(connector, true);
            }
        });

        analyzeLayoutsPanel.addListener(new SelectConnectorListener() {
            @Override
            public void select(ServerConnector connector, Element element) {
                printState(connector, true);
            }
        });

        content.setStylePrimaryName(VDebugWindow.STYLENAME + "-hierarchy");

        initializeHelpPanel();
        content.setWidget(helpPanel);
    }

    private void initializeHelpPanel() {
        HTML info = new HTML(showHierarchy.getHTML() + " "
                + showHierarchy.getTitle() + "<br/>" + find.getHTML() + " "
                + find.getTitle() + "<br/>" + analyze.getHTML() + " "
                + analyze.getTitle() + "<br/>" + generateWS.getHTML() + " "
                + generateWS.getTitle() + "<br/>" + generateDesign.getHTML()
                + " " + generateDesign.getTitle() + "<br/>");
        info.setStyleName(VDebugWindow.STYLENAME + "-info");
        helpPanel.add(info);
    }

    private void showHierarchy() {
        Highlight.hideAll();
        hierarchyPanel.update();
        content.setWidget(hierarchyPanel);
    }

    @Override
    public DebugButton getTabButton() {
        return tabButton;
    }

    @Override
    public Widget getControls() {
        return controls;
    }

    @Override
    public Widget getContent() {
        return content;
    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {
        stopFind();
    }

    private void generateWidgetset() {
        widgetsetPanel.update();
        content.setWidget(widgetsetPanel);
    }

    private void analyzeLayouts() {
        analyzeLayoutsPanel.update();
        content.setWidget(analyzeLayoutsPanel);
    }

    @Override
    public void meta(ApplicationConnection ac, ValueMap meta) {
        // show the results of analyzeLayouts
        analyzeLayoutsPanel.meta(ac, meta);
    }

    @Override
    public void uidl(ApplicationConnection ac, ValueMap uidl) {
        // NOP
    }

    private boolean isFindMode(FindHandler handler) {
        return activeFindHandler == handler;
    }

    private boolean isFindMode() {
        return (activeFindHandler != null);
    }

    private void toggleFind(FindHandler handler) {
        if (isFindMode()) {
            // Currently finding something
            if (isFindMode(handler)) {
                // Toggle off, stop finding
                stopFind();
                return;
            } else {
                // Stop finding something else, start finding this
                stopFind();
                startFind(handler);
            }
        } else {
            // Not currently finding anything
            startFind(handler);
        }
    }

    private void startFind(FindHandler handler) {
        if (isFindMode()) {
            stopFind();
        }
        Highlight.hideAll();

        highlightModeRegistration = Event
                .addNativePreviewHandler(highlightModeHandler);
        activeFindHandler = handler;
        if (handler == inspectComponent) {
            find.addStyleDependentName(VDebugWindow.STYLENAME_ACTIVE);
        } else if (handler == showComponentDesign) {
            generateDesign.addStyleDependentName(VDebugWindow.STYLENAME_ACTIVE);
        }

    }

    /**
     * Stop any current find operation, regardless of the handler
     */
    private void stopFind() {
        if (!isFindMode()) {
            return;
        }

        highlightModeRegistration.removeHandler();
        highlightModeRegistration = null;
        find.removeStyleDependentName(VDebugWindow.STYLENAME_ACTIVE);
        generateDesign.removeStyleDependentName(VDebugWindow.STYLENAME_ACTIVE);
        activeFindHandler = null;
    }

    private void printState(ServerConnector connector, boolean serverDebug) {
        Highlight.showOnly(connector);
        if (serverDebug) {
            HierarchyPanel.showServerDebugInfo(connector);
        }

        infoPanel.update(connector);
        content.setWidget(infoPanel);
    }

    private final NativePreviewHandler highlightModeHandler = new NativePreviewHandler() {

        @Override
        public void onPreviewNativeEvent(NativePreviewEvent event) {

            if (event.getTypeInt() == Event.ONKEYDOWN
                    && event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ESCAPE) {
                stopFind();
                Highlight.hideAll();
                return;
            }
            if (event.getTypeInt() == Event.ONMOUSEMOVE) {
                Highlight.hideAll();
                Element eventTarget = WidgetUtil.getElementFromPoint(event
                        .getNativeEvent().getClientX(), event.getNativeEvent()
                        .getClientY());

                if (VDebugWindow.get().getElement().isOrHasChild(eventTarget)) {
                    // Do not prevent using debug window controls
                    infoPanel.clear();
                    return;
                }

                for (ApplicationConnection a : ApplicationConfiguration
                        .getRunningApplications()) {
                    ComponentConnector connector = Util.getConnectorForElement(
                            a, a.getUIConnector().getWidget(), eventTarget);
                    if (connector == null) {
                        connector = Util.getConnectorForElement(a,
                                RootPanel.get(), eventTarget);
                    }
                    if (connector != null) {
                        activeFindHandler.onHover(connector);
                        event.cancel();
                        event.consume();
                        event.getNativeEvent().stopPropagation();
                        return;
                    }
                }
                // Not over any connector
                activeFindHandler.onHover(null);
            }
            if (event.getTypeInt() == Event.ONCLICK) {
                Highlight.hideAll();
                event.cancel();
                event.consume();
                event.getNativeEvent().stopPropagation();
                Element eventTarget = WidgetUtil.getElementFromPoint(event
                        .getNativeEvent().getClientX(), event.getNativeEvent()
                        .getClientY());
                for (ApplicationConnection a : ApplicationConfiguration
                        .getRunningApplications()) {
                    ComponentConnector connector = Util.getConnectorForElement(
                            a, a.getUIConnector().getWidget(), eventTarget);
                    if (connector == null) {
                        connector = Util.getConnectorForElement(a,
                                RootPanel.get(), eventTarget);
                    }

                    if (connector != null) {
                        activeFindHandler.onSelected(connector);
                        return;
                    }
                }
                // Click on something else -> stop find operation
                stopFind();
            }
            event.cancel();
        }

    };

}
