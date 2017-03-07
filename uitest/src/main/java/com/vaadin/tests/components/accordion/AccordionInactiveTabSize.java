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
package com.vaadin.tests.components.accordion;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.v7.ui.TextField;

public class AccordionInactiveTabSize extends TestBase {

    @Override
    protected String getDescription() {
        return "Select the second tab and move the splitter to the right. Both the inactive and the active tab should be resized.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3065;
    }

    @Override
    protected void setup() {
        HorizontalSplitPanel sp = new HorizontalSplitPanel();
        sp.setWidth("100%");
        sp.setHeight("100px");

        Accordion acc = new Accordion();

        Tab tab1 = acc.addTab(new TextField("first field"));
        tab1.setCaption("First tab");

        Tab tab2 = acc.addTab(new TextField("second field"));
        tab2.setCaption("Second tab");

        acc.setSizeFull();

        sp.addComponent(acc);
        addComponent(sp);

    }
}
