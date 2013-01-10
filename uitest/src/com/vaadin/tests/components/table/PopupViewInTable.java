/*
 * Copyright 2012 Vaadin Ltd.
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
package com.vaadin.tests.components.table;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

public class PopupViewInTable extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Table t = new Table();
        t.addContainerProperty("text", String.class, "");
        t.addContainerProperty("pv", Component.class, null);
        t.addItem(new Object[] { "Foo", createPopupView() }, "foo");
        addComponent(t);
    }

    private PopupView createPopupView() {
        PopupView pv = new PopupView("Click me", createContent());
        return pv;
    }

    private Component createContent() {
        VerticalLayout vl = new VerticalLayout(new Label("Hello"), new Button(
                "World"));
        return vl;
    }

    @Override
    protected String getTestDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer getTicketNumber() {
        // TODO Auto-generated method stub
        return null;
    }

}
