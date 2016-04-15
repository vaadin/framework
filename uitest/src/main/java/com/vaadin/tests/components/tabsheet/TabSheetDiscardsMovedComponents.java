package com.vaadin.tests.components.tabsheet;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;

public class TabSheetDiscardsMovedComponents extends TestBase {

    private GridLayout grid = new GridLayout();
    private TabSheet tabSheet = new TabSheet();

    @Override
    public void setup() {
        tabSheet.addTab(new Label("The tabSheet"), "Initial content");
        tabSheet.setSizeUndefined();

        grid.setColumns(2);
        TextField textField = new TextField("Text field");
        textField.setValue("Text");
        addTestComponent(textField);
        addTestComponent(new Button("Button"));

        addComponent(tabSheet);
        addComponent(grid);
    }

    private void addTestComponent(final Component component) {
        grid.addComponent(component);
        grid.addComponent(new Button("Move to tab", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                tabSheet.addTab(component);
                grid.removeComponent(event.getButton());
            }
        }));
    }

    @Override
    protected String getDescription() {
        return "Moving an already rendered component to a tabsheet and not immediately selecting the new tab should cause no problems";
    }

    @Override
    protected Integer getTicketNumber() {
        return 2669;
    }

}
