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
package com.vaadin.tests.components.accordion;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;

/**
 * Test for removing component from Accordion.
 * 
 * @author Vaadin Ltd
 */
public class AccordionRemoveComponent extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final Accordion accordion = new Accordion();
        Button button = new Button("remove");
        button.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                accordion.removeComponent(event.getButton());
            }
        });
        accordion.addComponent(button);
        addComponent(accordion);
    }

    @Override
    protected String getTestDescription() {
        return "Reset selected index when tab is removed";
    }

    @Override
    protected Integer getTicketNumber() {
        return 17248;
    }
}
