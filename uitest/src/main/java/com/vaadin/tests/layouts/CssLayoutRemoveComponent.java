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

@SuppressWarnings("serial")
public class CssLayoutRemoveComponent extends TestBase {

    @Override
    protected void setup() {
        final CssLayout layout = new CssLayout();
        final TextField tf = new TextField("Caption1");
        Button b = new Button("Remove field ", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                layout.removeComponent(tf);
            }

        });
        layout.addComponent(tf);
        layout.addComponent(b);
        layout.addComponent(new TextField("Caption2"));
        layout.addComponent(new TextField("Caption3"));

        addComponent(layout);
    }

    @Override
    protected String getDescription() {
        return "Clicking on the button should remove one text field but other textfields and their captions should stay intact.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5778;
    }

}
