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
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet.Tab;

public class AccordionTabStylenames extends TestBase {

    @Override
    protected void setup() {
        Accordion acc = new Accordion();
        addComponent(acc);

        for (int tabIndex = 0; tabIndex < 5; tabIndex++) {
            Tab tab = acc.addTab(new Label("Tab " + tabIndex));
            tab.setCaption("Tab " + tabIndex);
            tab.setStyleName("tab" + tabIndex);
        }
    }

    @Override
    protected String getDescription() {
        return null;
    }

    @Override
    protected Integer getTicketNumber() {
        return 10605;
    }

}
