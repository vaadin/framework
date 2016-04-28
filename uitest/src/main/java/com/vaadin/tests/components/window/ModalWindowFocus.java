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
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Window;

public class ModalWindowFocus extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest req) {

        Button button = new Button("Open windows");
        button.setId("firstButton");
        addComponent(button);
        button.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                Window w = new Window("This is first window");
                w.setModal(true);
                addWindow(w);

                Window w2 = new Window("This is second window");
                w2.setModal(true);
                addWindow(w2);

                HorizontalLayout lay = new HorizontalLayout();
                Button buttonInWindow = new Button("Open window");
                buttonInWindow.setId("windowButton");
                lay.addComponent(buttonInWindow);
                w2.setContent(lay);

                buttonInWindow.addClickListener(new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent e) {
                        Window w3 = new Window("This is third window");
                        w3.setModal(true);
                        w3.setId("window3");
                        addWindow(w3);
                    }
                });
            }
        });
    }

    @Override
    protected String getTestDescription() {
        return "Topmost modal window should be focused on opening "
                + "and on closing an overlying window";
    }

    @Override
    protected Integer getTicketNumber() {
        return 17021;
    }

}