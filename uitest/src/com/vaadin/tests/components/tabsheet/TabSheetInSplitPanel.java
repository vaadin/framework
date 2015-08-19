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
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.themes.ValoTheme;

@Theme("valo")
public class TabSheetInSplitPanel extends UI {

    @Override
    protected void init(VaadinRequest request) {
        VerticalSplitPanel verticalSplitter = new VerticalSplitPanel();
        setContent(verticalSplitter);
        verticalSplitter.setSizeFull();
        TabSheet t = new TabSheet();
        t.setHeight("100%");
        t.addTab(new Label("Hello in tab"), "Hello tab");
        t.setStyleName(ValoTheme.TABSHEET_FRAMED);
        verticalSplitter.addComponent(t);
        verticalSplitter.addComponent(new Label("Hello"));

    }

}
