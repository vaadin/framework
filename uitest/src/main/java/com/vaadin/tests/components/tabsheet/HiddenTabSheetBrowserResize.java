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
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;

public class HiddenTabSheetBrowserResize extends TestBase {

    @Override
    public void setup() {
        final TabSheet tabSheet = new TabSheet();

        tabSheet.addTab(new Label("Label1"), "Tab1");
        tabSheet.addTab(new Label("Label2"), "Tab2");

        Button toggleButton = new Button("Toggle TabSheet",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        tabSheet.setVisible(!tabSheet.isVisible());
                    }
                });
        addComponent(toggleButton);
        addComponent(tabSheet);
    }

    @Override
    protected String getDescription() {
        return "TabSheet content disappears if browser window resized when the TabSheet is hidden";
    }

    @Override
    protected Integer getTicketNumber() {
        return 9508;
    }

}
