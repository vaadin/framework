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
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CssLayout;
import com.vaadin.v7.ui.TextField;

public class CssLayoutRemoveComponentWithCaption extends TestBase {

    @Override
    protected void setup() {
        final CssLayout layout = new CssLayout();
        final TextField tf = new TextField("Caption");
        Button b = new Button("Remove field and add new", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                layout.removeComponent(tf);
                addComponent(new TextField("new field"));

            }

        });
        layout.addComponent(tf);
        layout.addComponent(b);

        addComponent(layout);
    }

    @Override
    protected String getDescription() {
        return "Clicking on the button should remove the text field and add a new 'new field' text field";
    }

    @Override
    protected Integer getTicketNumber() {
        return 4204;
    }

}
