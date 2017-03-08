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
package com.vaadin.tests.components.select;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.TwinColSelect;

public class TwinColSelectCaptionStyles extends TestBase {

    @Override
    protected void setup() {
        setTheme("tests-tickets");
        final TwinColSelect<String> sel = new TwinColSelect<>(
                "Component caption");
        sel.setLeftColumnCaption("Left caption");
        sel.setRightColumnCaption("Right caption");
        sel.setStyleName("styled-twincol-captions");
        sel.setWidth("300px");
        addComponent(sel);

        Button b = new Button("Set height and width to 500px",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        sel.setHeight("500px");
                        sel.setWidth("500px");

                    }
                });
        addComponent(b);
    }

    @Override
    protected String getDescription() {
        return "Tests that caption styling for TwinColSelect captions work properly. The left caption should be red and the right caption blue and larger than the left one.";
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }

}
