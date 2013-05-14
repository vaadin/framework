package com.vaadin.tests.containers.sqlcontainer;

import com.vaadin.Application;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;

public class SqlcontainertableApplication extends Application {
    private Window mainWindow;
    private Table table;
    private HorizontalSplitPanel panel;
    private Label label = new Label();

    @Override
    public void init() {
        mainWindow = new Window("SQLContainer Test");
        setMainWindow(mainWindow);
        mainWindow.getContent().setSizeFull();

        panel = new HorizontalSplitPanel();

        final DatabaseHelper helper = new DatabaseHelper();
        table = new Table();
        table.setSizeFull();
        table.setContainerDataSource(helper.getLargeContainer());
        table.setColumnCollapsingAllowed(true);
        table.setColumnReorderingAllowed(true);
        table.setSelectable(true);
        table.setImmediate(true);
        table.setNullSelectionAllowed(false);
        table.setMultiSelect(true);
        table.addListener(new ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
                if (event.getProperty() == table) {
                    if (panel.getSecondComponent() != null) {
                        panel.removeComponent(label);
                    }
                    label.setValue(table.getValue().toString());
                    panel.addComponent(label);
                }

            }
        });

        panel.setSizeFull();
        panel.addComponent(table);

        mainWindow.addComponent(panel);

    }

}