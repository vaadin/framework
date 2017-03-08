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
package com.vaadin.tests.components.ui;

import com.vaadin.event.ShortcutAction;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class WindowAndUIShortcuts extends UI {

    @Override
    protected void init(VaadinRequest request) {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setMargin(true);

        final VerticalLayout mainLayout = new VerticalLayout();

        mainLayout.addComponent(
                new Button("Show page", new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent clickEvent) {
                        final VerticalLayout pageLayout = new VerticalLayout();
                        pageLayout.setSpacing(true);

                        pageLayout.addComponent(new Label("Page"));
                        pageLayout.addComponent(new Button("Open dialog window",
                                new Button.ClickListener() {
                                    @Override
                                    public void buttonClick(
                                            Button.ClickEvent clickEvent) {
                                        Window dialog = new Window();
                                        dialog.setModal(true);
                                        dialog.setCaption("Press ESC shortcut");
                                        dialog.setWidth("300px");
                                        dialog.setHeight("100px");

                                        dialog.setContent(new TextField(
                                                "TextField in window"));
                                        addWindow(dialog);
                                    }
                                }));
                        Button closeButton = new Button("Close page",
                                new Button.ClickListener() {
                                    @Override
                                    public void buttonClick(
                                            Button.ClickEvent clickEvent) {
                                        mainLayout.removeComponent(pageLayout);

                                        Notification.show(
                                                "OMG! Page is also closed!");
                                    }
                                });
                        closeButton.setClickShortcut(
                                ShortcutAction.KeyCode.ESCAPE);
                        pageLayout.addComponent(closeButton);

                        mainLayout.addComponent(pageLayout);
                        mainLayout.setExpandRatio(pageLayout, 1);
                    }
                }));

        layout.addComponent(mainLayout);
        layout.setExpandRatio(mainLayout, 1);

        setContent(layout);
    }
}
