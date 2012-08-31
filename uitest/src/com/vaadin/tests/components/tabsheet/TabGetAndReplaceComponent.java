package com.vaadin.tests.components.tabsheet;

import java.util.Iterator;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;

public class TabGetAndReplaceComponent extends TestBase {

    @Override
    protected void setup() {
        final TabSheet tabs = new TabSheet();

        tabs.addTab(new Label("Content 1"), "Content 1", null);
        tabs.addTab(new Label("Content 2"), "Content 2", null);
        tabs.addTab(new Label("Content 3"), "Content 3", null);
        tabs.addTab(new Label("Content 4"), "Content 4", null);
        tabs.addTab(new Label("Content 5"), "Content 5", null);
        addComponent(tabs);

        Button replace2 = new Button("Replace Content 2",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        Iterator<Component> iter = tabs.getComponentIterator();
                        iter.next();

                        Component content2 = iter.next();
                        Tab tab = tabs.getTab(content2);

                        // Replace content
                        tabs.replaceComponent(tab.getComponent(), new Label(
                                "Replacement 2"));

                    }
                });
        addComponent(replace2);
    }

    @Override
    protected String getDescription() {
        return "The tab should have a reference to the component it holds";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6188;
    }

}
