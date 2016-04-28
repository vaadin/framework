package com.vaadin.tests.components.tabsheet;

import com.vaadin.server.ThemeResource;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;

public class TabSheetIcons extends TestBase {

    @Override
    protected String getDescription() {
        return "Tests rendering of a Tabsheet with fixed/dynamic width when the TabSheet contains icons";
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }

    @Override
    protected void setup() {
        TabSheet ts1 = createTabsheet();
        ts1.setHeight("100px");
        TabSheet ts2 = createTabsheet();
        ts2.setHeight("100px");
        ts2.setWidth("400px");

        addComponent(ts1);
        addComponent(ts2);
    }

    private TabSheet createTabsheet() {
        TabSheet tabsheet = new TabSheet();
        tabsheet.setSizeUndefined();

        Component[] tab = new Component[3];
        tab[0] = new Label("This is tab 1");
        tab[0].setIcon(new ThemeResource("../runo/icons/32/folder-add.png"));
        tab[0].setCaption("tab number 1");
        tab[1] = new TextField("This is tab 2", "Contents of tab 2 textfield");
        tab[2] = new Label("This is tab 3");
        tab[2].setIcon(new ThemeResource("../runo/icons/16/folder-add.png"));
        tab[2].setCaption("tab number 3");

        for (Component c : tab) {
            tabsheet.addTab(c);
            tabsheet.getTab(c).setIconAlternateText(
                    "iconalt" + tabsheet.getComponentCount());
        }

        return tabsheet;
    }

}
