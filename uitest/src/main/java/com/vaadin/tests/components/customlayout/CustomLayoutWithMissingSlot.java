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
package com.vaadin.tests.components.customlayout;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.TextField;

public class CustomLayoutWithMissingSlot extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        CustomLayout cl;
        try {
            cl = new CustomLayout(
                    new ByteArrayInputStream(
                            "<div>First: <div location='first'></div><p>Second: <div location='second'></div><p>"
                                    .getBytes("UTF-8")));
            cl.addComponent(new TextField("This should be visible"), "first");
            Button button = new Button(
                    "This button is visible, together with one label");
            button.addClickListener(new ClickListener() {

                @Override
                public void buttonClick(ClickEvent event) {
                    log("Button clicked");
                }
            });
            cl.addComponent(button, "second");
            cl.addComponent(new TextField(
                    "This won't be as the slot is missing"), "third");

            addComponent(cl);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
