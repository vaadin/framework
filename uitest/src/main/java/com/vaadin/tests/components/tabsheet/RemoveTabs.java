package com.vaadin.tests.components.tabsheet;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.AbstractComponentContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;

public class RemoveTabs extends TestBase {

    protected TabSheet tabsheet;

    protected Component[] tab = new Component[5];

    private Button closeCurrent;
    private Button closeCurrentWithTab;
    private Button closeFirst;
    private Button closeLast;
    private Button reorderTabs;

    @Override
    protected Integer getTicketNumber() {
        return 2425;
    }

    @Override
    protected String getDescription() {
        return "Tests the removal of individual tabs from a Tabsheet. No matter what is done in this test the tab caption \"Tab X\" should always match the content \"Tab X\". Use \"remove first\" and \"remove active\" buttons to remove the first or the active tab. The \"reorder\" button reverses the order by adding and removing all components.";
    }

    @Override
    protected void setup() {
        tabsheet = new TabSheet();
        for (int i = 1; i <= tab.length; i++) {
            tab[i - 1] = new Label("This is the contents of tab " + i);
            tab[i - 1].setCaption("Tab " + i);

            tabsheet.addComponent(tab[i - 1]);
        }

        getLayout().addComponent(tabsheet);

        closeCurrent = new Button("Close current tab");
        closeCurrent.addClickListener(event -> closeCurrentTab());

        closeCurrentWithTab = new Button("Close current tab with Tab");
        closeCurrentWithTab.addClickListener(event -> closeCurrentTabWithTab());

        closeFirst = new Button("close first tab");
        closeFirst.addClickListener(event -> closeFirstTab());

        closeLast = new Button("close last tab");
        closeLast.addClickListener(event -> closeLastTab());

        reorderTabs = new Button("reorder");
        reorderTabs.addClickListener(event -> reorder());

        getLayout().addComponent(closeFirst);
        getLayout().addComponent(closeLast);
        getLayout().addComponent(closeCurrent);
        getLayout().addComponent(closeCurrentWithTab);
        getLayout().addComponent(reorderTabs);
    }

    private void closeCurrentTab() {
        Component c = tabsheet.getSelectedTab();
        if (c != null) {
            tabsheet.removeComponent(c);
        }
    }

    private void closeCurrentTabWithTab() {
        Component c = tabsheet.getSelectedTab();
        if (c != null) {
            Tab t = tabsheet.getTab(c);
            tabsheet.removeTab(t);
        }
    }

    private void closeFirstTab() {
        tabsheet.removeComponent(tabsheet.getComponentIterator().next());
    }

    private void closeLastTab() {
        Component last = null;
        for (Component c : tabsheet) {
            last = c;
        }
        tabsheet.removeComponent(last);
    }

    private void reorder() {
        AbstractComponentContainer container = tabsheet;

        if (container != null) {
            List<Component> c = new ArrayList<>();
            for (Component comp : container) {
                c.add(comp);
            }
            container.removeAllComponents();

            for (int j = c.size() - 1; j >= 0; j--) {
                container.addComponent(c.get(j));
            }

        }
    }
}
