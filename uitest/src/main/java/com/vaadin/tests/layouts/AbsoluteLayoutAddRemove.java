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
package com.vaadin.tests.layouts;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;

public class AbsoluteLayoutAddRemove extends TestBase {

    @Override
    protected String getDescription() {
        return "Tests that addComponent() and removeComponent() works";
    }

    @Override
    protected Integer getTicketNumber() {
        return 2915;
    }

    @Override
    protected void setup() {
        Layout main = getLayout();

        final Label l = new Label("A Label");
        final AbsoluteLayout al = new AbsoluteLayout();
        al.setWidth("300px");
        al.setHeight("200px");
        main.addComponent(al);

        final Button b = new Button("Add", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                if (l.getParent() == null) {
                    al.addComponent(l);
                    event.getButton().setCaption("Remove");
                } else {
                    al.removeComponent(l);
                    event.getButton().setCaption("Add");
                }

            }

        });
        main.addComponent(b);

    }

}
