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

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;

@Theme("valo")
public class VetoTabChange extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final TabSheet ts = new TabSheet();
        ts.addSelectedTabChangeListener(new SelectedTabChangeListener() {
            @Override
            public void selectedTabChange(SelectedTabChangeEvent event) {
                ts.setSelectedTab(0);
            }
        });

        ts.addTab(new Label("Tab 1"), "Tab 1");
        ts.addTab(new Label("Tab 2"), "Tab 2");

        addComponent(ts);
    }

    @Override
    public String getDescription() {
        return "Tests the behavior when there's a listener that always changes back to the first tab.";
    }

}
