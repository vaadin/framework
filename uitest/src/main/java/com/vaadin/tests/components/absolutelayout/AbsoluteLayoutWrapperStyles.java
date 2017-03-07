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
package com.vaadin.tests.components.absolutelayout;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;

public class AbsoluteLayoutWrapperStyles extends TestBase {

    @Override
    protected void setup() {
        AbsoluteLayout layout = new AbsoluteLayout();
        layout.setWidth("500px");
        layout.setHeight("500px");

        Label lbl = new Label("Label");
        lbl.setStyleName("my-label");
        lbl.addStyleName("my-second-label");
        layout.addComponent(lbl);

        Button btn = new Button("Button");
        btn.setStyleName("my-button");
        btn.addStyleName("my-second-button");
        layout.addComponent(btn, "top:50px;");

        addComponent(layout);
    }

    @Override
    protected String getDescription() {
        return "Absolutelayout wrapper should get child stylenames";
    }

    @Override
    protected Integer getTicketNumber() {
        return 9051;
    }

}
