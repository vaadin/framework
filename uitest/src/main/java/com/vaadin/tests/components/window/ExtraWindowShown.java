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
package com.vaadin.tests.components.window;

import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class ExtraWindowShown extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Button b = new Button("Open window", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {

                VerticalLayout layout = new VerticalLayout();
                layout.setMargin(true);
                final Window w = new Window("Sub window", layout);
                w.center();
                layout.addComponent(
                        new Button("Close", new Button.ClickListener() {

                            @Override
                            public void buttonClick(ClickEvent event) {
                                w.close();
                            }
                        }));
                Button iconButton = new Button("A button with icon");
                iconButton
                        .setIcon(new ThemeResource("../runo/icons/16/ok.png"));
                layout.addComponent(iconButton);
                event.getButton().getUI().addWindow(w);
            }

        });
        getLayout().getParent().setSizeFull();
        getLayout().setSizeFull();
        getLayout().addComponent(b);
        getLayout().setComponentAlignment(b, Alignment.MIDDLE_CENTER);
    }

    @Override
    protected String getTestDescription() {
        return "Sub window shouldn't reappear after closing.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5987;
    }

}
