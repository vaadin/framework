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
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.VerticalLayout;

/**
 * Test UI for Accordion: old widget should be removed from the tab.
 * 
 * @since 7.2
 * @author Vaadin Ltd
 */
public class AccordionAddTab extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final Accordion tabs = new Accordion();
        addComponent(tabs);
        tabs.setHeight(500, Unit.PIXELS);
        Button remove = new Button("Remove 'First'");
        final Tab me = tabs.addTab(addTab("First"));
        remove.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                tabs.removeTab(me);
                Tab tab = tabs.addTab(addTab("Next"));
                tabs.setSelectedTab(tab);
            }
        });
        addComponent(remove);
    }

    private Component addTab(String tag) {
        VerticalLayout layout = new VerticalLayout();
        layout.addComponent(new Label("On tab: " + tag));
        return new Panel(tag, layout);
    }

    @Override
    protected String getTestDescription() {
        return "Remove previous widget in the accordion tab when content is replaced";
    }

    @Override
    protected Integer getTicketNumber() {
        return 11367;
    }

}
