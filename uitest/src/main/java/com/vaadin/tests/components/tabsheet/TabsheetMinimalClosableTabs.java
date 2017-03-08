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

import com.vaadin.server.ExternalResource;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.v7.ui.themes.Reindeer;

public class TabsheetMinimalClosableTabs extends TestBase {

    @Override
    protected void setup() {
        TabSheet ts = new TabSheet();
        for (int tab = 0; tab < 5; tab++) {
            String tabCaption = "Tab";
            for (int c = 0; c < tab; c++) {
                tabCaption += tabCaption;
            }
            tabCaption += " " + tab;

            Tab t = ts.addTab(new Label("Content " + tab), tabCaption);
            t.setClosable(true);

            if (tab % 2 == 0) {
                t.setIcon(new ExternalResource(
                        "/VAADIN/themes/tests-tickets/icons/fi.gif"));
            }
        }

        ts.addStyleName(Reindeer.TABSHEET_MINIMAL);
        addComponent(ts);
    }

    @Override
    protected String getDescription() {
        return "Minimal theme should also show the close button in all browsers";
    }

    @Override
    protected Integer getTicketNumber() {
        return 10610;
    }
}
