/*
 * Copyright 2000-2013 Vaadin Ltd.
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
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.VerticalLayout;

/**
 * Test UI for Accordion: tabs should stay selectable after remove tab.
 * 
 * @since 7.2
 * @author Vaadin Ltd
 */
public class AccordionRemoveTab extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final Accordion tabs = new Accordion();
        addComponent(tabs);
        tabs.setHeight(300, Unit.PIXELS);
        final VerticalLayout one = new VerticalLayout();
        one.setCaption("One");
        one.addComponent(new Label("On first tab"));
        tabs.addTab(one);
        VerticalLayout two = new VerticalLayout();
        two.setCaption("Two");
        two.addComponent(new Label("On second tab"));
        tabs.addTab(two);

        tabs.setSelectedTab(two);

        VerticalLayout l = new VerticalLayout();
        l.addComponent(new Label("On third tab"));
        Tab last = tabs.addTab(l);
        last.setCaption("Three");

        Button remove = new Button("Remove First");
        remove.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                tabs.removeComponent(tabs.iterator().next());
            }
        });

        addComponent(remove);
    }

    @Override
    protected String getTestDescription() {
        return "Tabs should stay selectable after remove tab.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 11366;
    }

}
