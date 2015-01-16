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
import java.util.Map;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
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
import com.vaadin.client.WidgetUtil;

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
        private final SelectorPath path;

        public SelectorWidget(final SelectorPath path) {
            this.path = path;

            String html = "<div class=\"" + VDebugWindow.STYLENAME
                    + "-selector\"><span class=\"tb-selector\">"
                    + WidgetUtil.escapeHTML(path.getElementQuery())
                    + "</span></div>";
            setHTML(html);

            addMouseOverHandler(this);
            addMouseOutHandler(this);
        }

        @Override
        public void onMouseOver(MouseOverEvent event) {
            Highlight.hideAll();

            Element element = path.getElement();
            if (null != element) {
                Highlight.show(element);
            }
        }

        @Override
        public void onMouseOut(MouseOutEvent event) {
            Highlight.hideAll();
        }
    }

    private final DebugButton tabButton = new DebugButton(Icon.TESTBENCH,
            "Pick Vaadin TestBench selectors");

    private final FlowPanel content = new FlowPanel();

    private final FlowPanel selectorPanel = new FlowPanel();
    // map from full path to SelectorWidget to enable reuse of old selectors
    private Map<SelectorPath, SelectorWidget> selectorWidgets = new HashMap<SelectorPath, SelectorWidget>();

    private final FlowPanel controls = new FlowPanel();

    private final Button find = new DebugButton(Icon.HIGHLIGHT,
            "Pick an element and generate a query for it");

    private final Button clear = new DebugButton(Icon.CLEAR,
            "Clear current elements");

    private HandlerRegistration highlightModeRegistration = null;

    public TestBenchSection() {

        controls.add(find);
        find.setStylePrimaryName(VDebugWindow.STYLENAME_BUTTON);
        find.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                toggleFind();
            }
        });

        controls.add(clear);
        clear.setStylePrimaryName(VDebugWindow.STYLENAME_BUTTON);
        clear.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                clearResults();
            }
        });

        content.setStylePrimaryName(VDebugWindow.STYLENAME + "-testbench");
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
        Highlight.hideAll();
    }

    private void pickSelector(ServerConnector connector, Element element) {

        SelectorPath p = new SelectorPath(connector, Util
                .findPaintable(connector.getConnection(), element).getWidget()
                .getElement());
        SelectorWidget w = new SelectorWidget(p);

        content.add(w);
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
                Element eventTarget = WidgetUtil.getElementFromPoint(event
                        .getNativeEvent().getClientX(), event.getNativeEvent()
                        .getClientY());
                if (VDebugWindow.get().getElement().isOrHasChild(eventTarget)) {
                    if (isFindMode() && event.getTypeInt() == Event.ONCLICK) {
                        stopFind();
                        event.cancel();
                    }
                    return;
                }

                // make sure that not finding the highlight element only
                Highlight.hideAll();

                eventTarget = WidgetUtil.getElementFromPoint(event
                        .getNativeEvent().getClientX(), event.getNativeEvent()
                        .getClientY());
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

    private void clearResults() {
        content.clear();
    }

}
