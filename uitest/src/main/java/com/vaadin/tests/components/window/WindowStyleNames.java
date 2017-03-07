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
package com.vaadin.tests.components.window;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class WindowStyleNames extends TestBase {

    @Override
    protected String getDescription() {
        return "Click 'add style' to add a 'new' style to the window. The 'old' style should disappear and only the 'new' style should be set. Verify using e.g. firebug";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3059;
    }

    @Override
    protected void setup() {
        setWindowStyle("old");
        addComponent(new Button("Set style to 'new'", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                setWindowStyle("new");
            }

        }));

        addComponent(new Button("Set style to 'custom'", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                setWindowStyle("custom");
            }

        }));

        addComponent(new Button("Add 'foo' style", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                getMainWindow().addStyleName("foo");
            }

        }));

    }

    protected void setWindowStyle(String string) {
        getMainWindow().setStyleName(string);

    }

}
