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
import com.vaadin.ui.Label;

public class AbsoluteLayoutClipping extends TestBase {

    @Override
    protected void setup() {
        setTheme("tests-tickets");
        AbsoluteLayout abs = new AbsoluteLayout();
        abs.setStyleName("borders");
        abs.setWidth("100px");
        abs.setHeight("100px");

        Label l = new Label("This should be clipped at 100px");
        l.setSizeUndefined();
        abs.addComponent(l, "top:50px;left:50px");

        Label l2 = new Label("This should not be visible");
        l2.setSizeUndefined();
        abs.addComponent(l2, "top:80px;left:150px");

        Label l3 = new Label("This should be clipped vertically at 100px");
        l3.setWidth("50px");
        abs.addComponent(l3, "top:20px;left:0px");

        addComponent(abs);
    }

    @Override
    protected String getDescription() {
        return "An AbsoluteLayout with fixed size should clip at its borders. Nothing outside the black square should be visible.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 2913;
    }

}
