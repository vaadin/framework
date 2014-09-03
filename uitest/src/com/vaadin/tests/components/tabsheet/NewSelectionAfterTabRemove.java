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
package com.vaadin.tests.components.tabsheet;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;

/**
 * In case a selected tab is removed the new selected one should be a neighbor.
 * 
 * In case an unselected tab is removed and the selected one is not visible, the
 * scroll should not jump to the selected one.
 * 
 * @since
 * @author Vaadin Ltd
 */
public class NewSelectionAfterTabRemove extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        TabSheet tabSheet = new TabSheet();

        for (int i = 0; i < 20; i++) {

            String caption = "Tab " + i;
            Label label = new Label(caption);

            Tab tab = tabSheet.addTab(label, caption);
            tab.setClosable(true);
        }

        addComponent(tabSheet);
    }

    @Override
    protected String getTestDescription() {
        return "When a selected tab is removed, its neighbor should become selected.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6876;
    }

}
