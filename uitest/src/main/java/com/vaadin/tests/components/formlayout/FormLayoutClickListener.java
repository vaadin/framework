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
package com.vaadin.tests.components.formlayout;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;

/**
 * Test UI for Form layout click listener.
 *
 * @author Vaadin Ltd
 */
public class FormLayoutClickListener extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        FormLayout layout = new FormLayout();
        layout.setMargin(true);
        layout.setSpacing(true);

        layout.setId("form");

        Label label = new Label("target");
        label.setId("label");
        layout.addComponent(label);

        layout.addLayoutClickListener(e -> {
            log("Child component: " + (e.getChildComponent() == null ? null
                    : e.getChildComponent().getId()));
            log("Clicked component: " + (e.getClickedComponent() == null ? null
                    : e.getClickedComponent().getId()));
            log("Source component: " + e.getComponent().getId());
        });

        addComponent(layout);
    }

    @Override
    protected String getTestDescription() {
        return "LayoutClickListener should work in FormLayout";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6346;
    }

}
