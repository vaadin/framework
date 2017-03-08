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
package com.vaadin.tests.components.tabsheet;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.VerticalLayout;

public class TabSheetWithDisappearingContent extends TestBase {

    @Override
    protected void setup() {
        final TabSheet t = new TabSheet();

        for (int i = 1; i < 4; i++) {
            VerticalLayout v = new VerticalLayout();
            v.addComponent(new Label("Content " + i));
            Tab tab = t.addTab(v, "Tab " + i);
            tab.setClosable(true);
        }
        addComponent(t);
    }

    @Override
    protected String getDescription() {
        return "In some browsers, when trying to close the last tab of a tabsheet an IndexOutOfBoundException happens. After that, although an other tab is visually active, no contents are shown.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 7686;
    }

}
