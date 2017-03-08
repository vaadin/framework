/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.tests.components.panel;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

public class PanelConcurrentModificationException extends TestBase {

    private final VerticalLayout panelLayout = new VerticalLayout();
    private final Panel panel = new Panel(panelLayout);

    @Override
    protected void setup() {
        panelLayout.setMargin(true);

        addComponent(new Button("Click here for exception",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        panelLayout.addComponent(new Label("Label"));
                    }
                }));
        addComponent(
                new Button("Or click here first", new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        Notification.show(
                                "It is now safe to click the other button");
                    }
                }));
        addComponent(panel);
    }

    @Override
    protected String getDescription() {
        return "Modifying Panel content causes Internal Error (ConcurrentModificationException)";
    }

    @Override
    protected Integer getTicketNumber() {
        // TODO Auto-generated method stub
        return null;
    }

}
