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

package com.vaadin.tests;

import java.util.Locale;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

public class TestForBasicApplicationLayout extends CustomComponent {

    private final Button click;
    private final Button click2;
    private final TabSheet tab;

    public TestForBasicApplicationLayout() {

        click = new Button("Set height -1", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                tab.setHeight(null);
            }

        });

        click2 = new Button("Set height 100%", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                tab.setHeight(100, TabSheet.UNITS_PERCENTAGE);
            }

        });

        final HorizontalSplitPanel sp = new HorizontalSplitPanel();
        sp.setSplitPosition(290, Sizeable.UNITS_PIXELS);

        final HorizontalSplitPanel sp2 = new HorizontalSplitPanel();
        sp2.setSplitPosition(255, Sizeable.UNITS_PIXELS);

        VerticalLayout pl = new VerticalLayout();
        pl.setMargin(true);
        final Panel p = new Panel("Accordion Panel", pl);
        p.setSizeFull();

        tab = new TabSheet();
        tab.setSizeFull();

        VerticalLayout reportLayout = new VerticalLayout();
        final Panel report = new Panel("Monthly Program Runs", reportLayout);
        final VerticalLayout controls = reportLayout;
        controls.setMargin(true);
        controls.addComponent(new Label("Report tab"));
        controls.addComponent(click);
        controls.addComponent(click2);
        reportLayout.addComponent(controls);
        final DateField cal = new DateField();
        cal.setResolution(DateField.RESOLUTION_DAY);
        cal.setLocale(new Locale("en", "US"));
        reportLayout.addComponent(cal);
        reportLayout.setExpandRatio(controls, 1);
        report.addStyleName(Reindeer.PANEL_LIGHT);
        report.setHeight(100, Sizeable.UNITS_PERCENTAGE);

        sp2.setFirstComponent(report);

        final Table table = TestForTablesInitialColumnWidthLogicRendering
                .getTestTable(5, 200);
        table.setPageLength(15);
        table.setSelectable(true);
        table.setRowHeaderMode(Table.ROW_HEADER_MODE_INDEX);
        table.setColumnCollapsingAllowed(true);
        table.setColumnReorderingAllowed(true);
        table.setSortDisabled(false);
        table.setSizeFull();
        table.addStyleName("table-inline");
        sp2.setSecondComponent(table);

        tab.addTab(new Label("Tab1"), "Summary", null);
        tab.addTab(sp2, "Reports", null);
        tab.addTab(new Label("Tab 3"), "Statistics", null);
        tab.addTab(new Label("Tab 4"), "Error Tracking", null);
        tab.setSelectedTab(sp2);

        sp.setFirstComponent(p);
        sp.setSecondComponent(tab);

        setCompositionRoot(sp);
    }

}
