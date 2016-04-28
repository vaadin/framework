package com.vaadin.tests.components.tabsheet;

import com.vaadin.server.ExternalResource;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.themes.Reindeer;

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
