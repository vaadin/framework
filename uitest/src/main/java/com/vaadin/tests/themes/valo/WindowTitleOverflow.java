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
package com.vaadin.tests.themes.valo;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Window;

public class WindowTitleOverflow extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        addButton("Open Resizable", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                addWindow(getWindow(true, false));
            }
        });

        addButton("Open Closable", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                addWindow(getWindow(false, true));
            }
        });

        addButton("Open Resizable and Closable", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                addWindow(getWindow(true, true));
            }
        });

        addButton("Open Non-Resizable and Non-Closable",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        addWindow(getWindow(false, false));
                    }
                });
    }

    private Window getWindow(boolean resizable, boolean closable) {
        Window window = new Window();

        window.setModal(true);
        window.setResizable(resizable);
        window.setClosable(closable);
        window.setCaption("Long Foobar Foobar Foobar Foobar Foobar Foobar");

        return window;
    }

    @Override
    protected Integer getTicketNumber() {
        return 15408;
    }

    @Override
    protected String getTestDescription() {
        return "In Valo, header title should use the space of hidden buttons.";
    }
}
