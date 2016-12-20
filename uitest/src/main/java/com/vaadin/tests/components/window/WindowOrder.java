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
package com.vaadin.tests.components.window;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.WindowOrderChangeEvent;
import com.vaadin.ui.Window.WindowOrderChangeListener;

/**
 * Test UI for accessing to window order position.
 *
 * @author Vaadin Ltd
 */
public class WindowOrder extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        w1 = new Window();
        w1.setCaption("Window1");
        w1.addStyleName("window1");

        w2 = new Window();
        w2.setCaption("Window2");
        w2.addStyleName("window2");

        w3 = new Window();
        w3.setCaption("Window3");
        w3.addStyleName("window3");

        getUI().addWindow(w1);
        getUI().addWindow(w2);
        getUI().addWindow(w3);
        OrderListener listener = new OrderListener();
        for (Window window : getUI().getWindows()) {
            window.addWindowOrderChangeListener(listener);
        }

        w4 = new Window();
        w4.setCaption("Window4");
        w4.addStyleName("window4");
        w4.addWindowOrderChangeListener(listener);

        infoLabel = createLabel("info-label");
        uiLabel = createLabel("ui-label");

        getUI().addWindowOrderUpdateListener(new WindowOrderListener());

        addComponent(infoLabel);
        addComponent(uiLabel);

        Button first = new Button("Bring first to front", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                w1.bringToFront();
            }
        });
        first.addStyleName("bring-to-front-first");
        addComponent(first);
        getLayout().setComponentAlignment(first, Alignment.MIDDLE_RIGHT);

        Button all = new Button("Bring to front all windows",
                new ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        w3.bringToFront();
                        w1.bringToFront();
                        w2.bringToFront();
                    }
                });
        all.addStyleName("bring-to-front-all");
        addComponent(all);
        getLayout().setComponentAlignment(all, Alignment.MIDDLE_RIGHT);

        Button detach = new Button("Detach last window", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                getUI().removeWindow(w3);
            }
        });
        detach.addStyleName("detach-window");
        addComponent(detach);
        getLayout().setComponentAlignment(detach, Alignment.MIDDLE_RIGHT);

        Button add = new Button("Add new window", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                getUI().addWindow(w4);
            }
        });
        add.addStyleName("add-window");
        addComponent(add);
        getLayout().setComponentAlignment(add, Alignment.MIDDLE_RIGHT);
    }

    @Override
    protected String getTestDescription() {
        return "Window order position access and listeners for order change events.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14325;
    }

    private Label createLabel(String style) {
        Label label = new Label();
        label.addStyleName(style);
        return label;
    }

    private class OrderListener implements WindowOrderChangeListener {

        @Override
        public void windowOrderChanged(WindowOrderChangeEvent event) {
            infoLabel.removeStyleName("w4--1");
            infoLabel.addStyleName("w4-" + w4.getOrderPosition());

            if (event.getWindow() == w3 && event.getOrder() == -1) {
                Label detached = new Label("Window 3 is detached");
                detached.addStyleName("w3-detached");
                detached.addStyleName("w3-" + w3.getOrderPosition());
                addComponent(detached);
            }

            Window window = event.getWindow();
            Label label = new Label(String.valueOf(window.getOrderPosition()));
            label.addStyleName("event-order" + event.getOrder());
            window.setContent(label);
        }
    }

    private class WindowOrderListener implements WindowOrderUpdateListener {

        @Override
        public void windowOrderUpdated(WindowOrderUpdateEvent event) {
            uiLabel.removeStyleName(infoLabel.getStyleName());
            for (Window window : event.getWindows()) {
                uiLabel.addStyleName(window.getStyleName() + "-"
                        + window.getOrderPosition());
            }
        }
    }

    private Window w1;
    private Window w2;
    private Window w3;
    private Window w4;
    private Label infoLabel;

    private Label uiLabel;
}