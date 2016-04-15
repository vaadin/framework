package com.vaadin.tests.containers.sqlcontainer;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.Table;

public class SqlcontainertableApplication extends LegacyApplication {
    private LegacyWindow mainWindow;
    private Table table;
    private HorizontalSplitPanel panel;
    private Label label = new Label();

    @Override
    public void init() {
        mainWindow = new LegacyWindow("SQLContainer Test");
        setMainWindow(mainWindow);
        mainWindow.getContent().setSizeFull();

        panel = new HorizontalSplitPanel();
        panel.setSecondComponent(label);

        final DatabaseHelper helper = new DatabaseHelper();
        table = new Table();
        table.setSizeFull();
        table.setContainerDataSource(helper.getLargeContainer());
        table.setSelectable(true);
        table.setImmediate(true);
        table.setMultiSelect(true);
        table.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                label.setValue(table.getValue().toString());
            }
        });

        panel.setSizeFull();
        panel.addComponent(table);

        mainWindow.addComponent(panel);

    }

}
