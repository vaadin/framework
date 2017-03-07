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
/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.tests.overlays;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;

public class OverlayTouchScrolling extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {

        final CssLayout green = new CssLayout();
        green.setSizeFull();
        final CssLayout layout = new CssLayout() {
            @Override
            protected String getCss(Component c) {
                return "background:green;";
            }
        };
        layout.setSizeFull();
        layout.addComponent(green);
        setContent(layout);

        Button button = new Button("Tap me with a touch device");
        button.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {

                Notification.show(
                        "Now close this and you can scroll in mad places.");
                green.addComponent(new Label(
                        "Thank you for clicking, now scroll (with touch device) to area without green background, which shouldn't be possible."));
            }
        });
        green.addComponent(button);
    }

    @Override
    protected String getTestDescription() {
        return "Using overlays breaks top level scrolling on touch devices";
    }

    @Override
    protected Integer getTicketNumber() {
        return 10860;
    }
}
