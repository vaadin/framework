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

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ApplicationConfiguration;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.Util;
import com.vaadin.client.ValueMap;

/**
 * Provides functionality for picking selectors for Vaadin TestBench.
 * 
 * @since 7.1.x
 * @author Vaadin Ltd
 */
public class TestBenchSection implements Section {

    /**
     * Selector widget showing a selector in a program-usable form.
     */
    private static class SelectorWidget extends HTML implements
            MouseOverHandler, MouseOutHandler {
        private static int selectorCounter = 1;

        final private SelectorPath path;
        final private SelectorWidget parent;
        final private int selectorIndex = selectorCounter++;

        public SelectorWidget(final SelectorPath path,
                final SelectorWidget parent) {
            this.path = path;
            this.parent = parent;

            String parentString = (parent != null) ? ("element" + parent.selectorIndex)
                    : null;
            String html = "<div class=\""
                    + VDebugWindow.STYLENAME
                    + "-selector\"><span class=\"tb-selector\">"
                    + Util.escapeHTML("WebElement element" + selectorIndex
                            + " = " + path.getJUnitSelector(parentString) + ";")
                    + "</span></div>";
            setHTML(html);

            addMouseOverHandler(this);
            addMouseOutHandler(this);
        }

        @Override
        public void onMouseOver(MouseOverEvent event) {
            ApplicationConnection a = path.getLocator().getClient();
            Element element = path.findElement();
            if (null != element) {
                Highlight.hideAll();
                Highlight.show(element);
            }
        }

        @Override
        public void onMouseOut(MouseOutEvent event) {
            Highlight.hideAll();
        }
    }

    private final DebugButton tabButton = new DebugButton(Icon.SELECTOR,
            "Pick Vaadin TestBench selectors");

    private final FlowPanel content = new FlowPanel();

    private final HierarchyPanel hierarchyPanel = new HierarchyPanel();

    private final FlowPanel selectorPanel = new FlowPanel();
    // map from full path to SelectorWidget to enable reuse of old selectors
    private Map<SelectorPath, SelectorWidget> selectorWidgets = new HashMap<SelectorPath, SelectorWidget>();

    private final FlowPanel controls = new FlowPanel();

    private final Button find = new DebugButton(Icon.HIGHLIGHT,
            "Select a component on the page to inspect it");
    private final Button refreshHierarchy = new DebugButton(Icon.HIERARCHY,
            "Refresh the connector hierarchy tree");

    private HandlerRegistration highlightModeRegistration = null;

    public TestBenchSection() {
        controls.add(refreshHierarchy);
        refreshHierarchy.setStylePrimaryName(VDebugWindow.STYLENAME_BUTTON);
        refreshHierarchy.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                hierarchyPanel.update();
            }
        });

        controls.add(find);
        find.setStylePrimaryName(VDebugWindow.STYLENAME_BUTTON);
        find.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                toggleFind();
            }
        });

        hierarchyPanel.addListener(new SelectConnectorListener() {
            @Override
            public void select(ServerConnector connector, Element element) {
                pickSelector(connector, element);
            }
        });

        content.setStylePrimaryName(VDebugWindow.STYLENAME + "-testbench");
        content.add(hierarchyPanel);
        content.add(selectorPanel);
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

    @Override
    public void meta(ApplicationConnection ac, ValueMap meta) {
        // NOP
    }

    @Override
    public void uidl(ApplicationConnection ac, ValueMap uidl) {
        // NOP
    }

    private boolean isFindMode() {
        return (highlightModeRegistration != null);
    }

    private void toggleFind() {
        if (isFindMode()) {
            stopFind();
        } else {
            startFind();
        }
    }

    private void startFind() {
        Highlight.hideAll();
        if (!isFindMode()) {
            highlightModeRegistration = Event
                    .addNativePreviewHandler(highlightModeHandler);
            find.addStyleDependentName(VDebugWindow.STYLENAME_ACTIVE);
        }
    }

    private void stopFind() {
        if (isFindMode()) {
            highlightModeRegistration.removeHandler();
            highlightModeRegistration = null;
            find.removeStyleDependentName(VDebugWindow.STYLENAME_ACTIVE);
        }
    }

    private void pickSelector(ServerConnector connector, Element element) {
        SelectorPath path = SelectorPath.findTestBenchSelector(connector,
                element);

        if (null != path) {
            addSelectorWidgets(path);
        }
    }

    private SelectorWidget addSelectorWidgets(SelectorPath path) {
        // add selector widgets recursively from root towards children, reusing
        // old ones
        SelectorPath parent = path.getParent();
        SelectorWidget parentWidget = null;
        if (null != parent) {
            parentWidget = addSelectorWidgets(parent);
        }
        SelectorWidget widget = selectorWidgets.get(path);
        if (null == widget) {
            // the parent has already been added above
            widget = new SelectorWidget(path, parentWidget);
            selectorWidgets.put(path, widget);
            selectorPanel.add(widget);
        }
        return widget;
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
            if (event.getTypeInt() == Event.ONMOUSEMOVE
                    || event.getTypeInt() == Event.ONCLICK) {
                Element eventTarget = Util.getElementFromPoint(event
                        .getNativeEvent().getClientX(), event.getNativeEvent()
                        .getClientY());
                if (VDebugWindow.get().getElement().isOrHasChild(eventTarget)) {
                    return;
                }

                // make sure that not finding the highlight element only
                Highlight.hideAll();
                eventTarget = Util.getElementFromPoint(event.getNativeEvent()
                        .getClientX(), event.getNativeEvent().getClientY());
                ComponentConnector connector = findConnector(eventTarget);

                if (event.getTypeInt() == Event.ONMOUSEMOVE) {
                    if (connector != null) {
                        Highlight.showOnly(connector);
                        event.cancel();
                        event.consume();
                        event.getNativeEvent().stopPropagation();
                        return;
                    }
                } else if (event.getTypeInt() == Event.ONCLICK) {
                    event.cancel();
                    event.consume();
                    event.getNativeEvent().stopPropagation();
                    if (connector != null) {
                        Highlight.showOnly(connector);
                        pickSelector(connector, eventTarget);
                        return;
                    }
                }
            }
            event.cancel();
        }

    };

    private ComponentConnector findConnector(Element element) {
        for (ApplicationConnection a : ApplicationConfiguration
                .getRunningApplications()) {
            ComponentConnector connector = Util.getConnectorForElement(a, a
                    .getUIConnector().getWidget(), element);
            if (connector == null) {
                connector = Util.getConnectorForElement(a, RootPanel.get(),
                        element);
            }
            if (connector != null) {
                return connector;
            }
        }
        return null;
    }

}
