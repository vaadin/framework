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

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.themes.Runo;

@Theme("runo")
public class ExtraScrollbarsInTabSheet extends UI {

    @Override
    public void init(VaadinRequest request) {

        VerticalLayout vl = new VerticalLayout();
        vl.setSizeFull();

        HorizontalSplitPanel horizontalSplit = new HorizontalSplitPanel();

        TabSheet ts = new TabSheet();

        VerticalLayout tabContent = new VerticalLayout();
        tabContent.setSizeFull();

        Panel p = new Panel();
        p.addStyleName(Runo.PANEL_LIGHT);
        p.setHeight("400px");
        tabContent.addComponent(p);

        ts.addTab(tabContent);
        horizontalSplit.setSecondComponent(ts);

        vl.addComponent(horizontalSplit);

        setContent(vl);

    }

}
