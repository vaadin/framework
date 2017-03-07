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
package com.vaadin.tests;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;

public class TestForTabSheet extends CustomComponent
        implements Button.ClickListener, TabSheet.SelectedTabChangeListener {
    TabSheet tabsheet = new TabSheet();
    Button tab1_root = new Button("Push this button");
    Label tab2_root = new Label("Contents of Second Tab");
    Label tab3_root = new Label("Contents of Third Tab");

    TestForTabSheet() {
        setCompositionRoot(tabsheet);

        tabsheet.addSelectedTabChangeListener(this);

        /* Listen for button click events. */
        tab1_root.addClickListener(this);
        tabsheet.addTab(tab1_root, "First Tab", null);

        /* A tab that is initially disabled. */
        tab2_root.setEnabled(false);
        tabsheet.addTab(tab2_root, "Second Tab", null);

        /* A tab that is initially disabled. */
        tab3_root.setEnabled(false);
        tabsheet.addTab(tab3_root, "Third tab", null);
    }

    @Override
    public void buttonClick(ClickEvent event) {
        System.out.println("tab2=" + tab2_root.isEnabled() + " tab3="
                + tab3_root.isEnabled());
        tab2_root.setEnabled(true);
        tab3_root.setEnabled(true);
    }

    @Override
    public void selectedTabChange(SelectedTabChangeEvent event) {
        /*
         * Cast to a TabSheet. This isn't really necessary in this example, as
         * we have only one TabSheet component, but would be useful if there
         * were multiple TabSheets.
         */
        TabSheet source = (TabSheet) event.getSource();
        if (source == tabsheet) {
            /* If the first tab was selected. */
            if (source.getSelectedTab() == tab1_root) {
                System.out.println("foo");
                tab2_root.setEnabled(false);
                tab3_root.setEnabled(false);
            }
        }
    }
}
