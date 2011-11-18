package com.vaadin.tests.components.window;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button;
import com.vaadin.ui.Table;
import com.vaadin.ui.TabSheet;

public class SubWindowWithUndefinedHeight extends TestBase {

    @Override
    protected String getDescription() {
        return "Setting subwindow height to undefined after initial rendering does not update visual height";
    }

    @Override
    protected Integer getTicketNumber() {
        return 7916;
    }

    @Override
    protected void setup() {
        final Window subwindow = new Window("subwindow");
        subwindow.center();
        subwindow.setSizeUndefined();
        subwindow.getContent().setSizeUndefined();

        final Button tabButton = new Button("A button");
        tabButton.setCaption("Tab 1");
        tabButton.setWidth("200px");

        final Table table = new Table();
        table.setCaption("tab 2");
        table.setWidth("100%");
        table.setHeight("100%");
        
        final TabSheet tabsheet = new TabSheet();
        tabsheet.addComponent(tabButton);
        tabsheet.addComponent(table);
        tabsheet.addListener(new TabSheet.SelectedTabChangeListener() {
            public void selectedTabChange(
                    TabSheet.SelectedTabChangeEvent event) {
                if (tabsheet.getSelectedTab() == tabButton) {
                    tabsheet.setSizeUndefined();
                    subwindow.getContent().setSizeUndefined();
                    subwindow.setSizeUndefined();
                } else if (tabsheet.getSelectedTab() == table) {
                    subwindow.setWidth("500px");
                    subwindow.setHeight("500px");
                    subwindow.getContent().setSizeFull();
                    tabsheet.setSizeFull();
                }
            }
        });
        subwindow.addComponent(tabsheet);

        Button button = new Button("click me", new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                getMainWindow().addWindow(subwindow);
            }
        });
        getMainWindow().addComponent(button);
    }
}
