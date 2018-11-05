package com.vaadin.tests.elements.tabsheet;

import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;

public class TabSheetElementTabWithoutCaption extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        // Create a tab sheet with a tab having no caption
        TabSheet ts = new TabSheet();
        Tab newTab;
        for (int i = 1; i <= 5; i++) {
            Component c = createTabContent(i);
            if (i != 3) {
                newTab = ts.addTab(c, "Tab " + i);
            } else {
                newTab = ts.addTab(c);
            }
            newTab.setClosable(true);
        }
        addComponent(ts);
        // Create a tab sheet that has icons instead of text captions
        TabSheet ts2 = new TabSheet();
        newTab = ts2.addTab(createTabContent(10), null,
                new ThemeResource("favicon.ico"));
        newTab.setClosable(true);
        newTab = ts2.addTab(createTabContent(11), null,
                new ThemeResource("window/img/maximize.png"));
        newTab.setClosable(false);
        newTab = ts2.addTab(createTabContent(12));
        newTab.setClosable(false);
        newTab = ts2.addTab(createTabContent(12));
        newTab.setClosable(true);
        newTab = ts2.addTab(createTabContent(13), null,
                new ThemeResource("window/img/restore.png"));
        newTab.setClosable(true);
        addComponent(ts2);
    }

    @Override
    protected String getTestDescription() {
        return "The methods of TabSheetElement should not fail when there are tabs without a caption.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14434;
    }

    private Label createTabContent(int index) {
        return new Label("This is tab " + index);
    }
}
