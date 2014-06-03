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
package com.vaadin.tests.components.popupview;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.VerticalLayout;

/**
 * Resizing PopupView's popup component while it is visible should also resize
 * the drop shadow of the overlay.
 * 
 * @author Vaadin Ltd
 */
public class PopupViewResizeWhileOpen extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        PopupView pv = new PopupView(new PopupView.Content() {
            @Override
            public Component getPopupComponent() {
                final VerticalLayout popupComponent = new VerticalLayout();
                popupComponent.setId("content-vl");
                popupComponent.setWidth("640px");
                popupComponent.setHeight("480px");

                Button button = new Button("Change height!",
                        new Button.ClickListener() {
                            @Override
                            public void buttonClick(Button.ClickEvent event) {
                                popupComponent.setHeight("320px");
                            }
                        });

                popupComponent.addComponent(button);
                return popupComponent;
            }

            @Override
            public String getMinimizedValueAsHTML() {
                return "Click me!";
            }
        });
        pv.setHideOnMouseOut(false);
        addComponent(pv);
    }

    @Override
    protected String getTestDescription() {
        return "Resize PopupView's content component while visible";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13666;
    }
}
