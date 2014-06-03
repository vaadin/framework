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

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class BackspaceKeyWithModalOpened extends AbstractTestUI {

    private static final String DEFAULT_VIEW_ID = "";
    private static final String SECOND_VIEW_ID = "second";

    public static final String BTN_NEXT_ID = "btn_next";
    public static final String BTN_OPEN_MODAL_ID = "btn_open_modal";
    public static final String TEXT_FIELD_IN_MODAL = "txt_in_modal";

    private Navigator navigator;

    class DefaultView extends Label implements View {

        @Override
        public void enter(ViewChangeEvent event) {
            Button btnNext = new Button("Next", new Button.ClickListener() {

                @Override
                public void buttonClick(ClickEvent event) {
                    navigator.navigateTo(SECOND_VIEW_ID);
                }
            });

            btnNext.setId(BTN_NEXT_ID);
            addComponent(btnNext);
        }
    }

    class SecondView extends Label implements View {

        @Override
        public void enter(ViewChangeEvent event) {
            Button btnOpenModal = new Button("Open modal",
                    new Button.ClickListener() {

                        @Override
                        public void buttonClick(ClickEvent event) {
                            Window window = new Window("Caption");

                            VerticalLayout layout = new VerticalLayout();
                            layout.setWidth("300px");
                            layout.setHeight("300px");

                            TextField textField = new TextField();
                            textField.setId(TEXT_FIELD_IN_MODAL);

                            layout.addComponent(textField);
                            window.setContent(layout);

                            addWindow(window);

                            window.setModal(true);

                            setFocusedComponent(window);
                        }
                    });

            btnOpenModal.setId(BTN_OPEN_MODAL_ID);
            addComponent(btnOpenModal);
        }
    }

    @Override
    protected void setup(VaadinRequest request) {
        Layout navigatorLayout = new VerticalLayout();
        navigator = new Navigator(this, navigatorLayout);

        addComponent(navigatorLayout);

        navigator.addView(DEFAULT_VIEW_ID, new DefaultView());

        navigator.addView(SECOND_VIEW_ID, new SecondView());
    }

    @Override
    protected String getTestDescription() {
        return "Navigator should not go back with modal opened.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13180;
    }
}
