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

package com.vaadin.tests.components.abstractcomponent;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.widgetset.TestingWidgetSet;
import com.vaadin.tests.widgetset.server.UseStateFromHierachyComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;

@Widgetset(TestingWidgetSet.NAME)
public class UseStateFromHierachy extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final UseStateFromHierachyComponent component = new UseStateFromHierachyComponent();
        component.setContent(new Label("Content child"));

        addComponent(component);
        addComponent(new Button("Remove component", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                removeComponent(component);
            }
        }));
    }

    @Override
    protected String getTestDescription() {
        return "Tests that shared state and connector hierarchy is consistent when removing components from the hierachy";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(10151);
    }

}
