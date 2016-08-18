package com.vaadin.tests.components.textfield;

import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.Component;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.LegacyTextField;

public class TextFieldInLayoutInTable extends LegacyApplication {

    @Override
    public void init() {

        final LegacyWindow mainWindow = new LegacyWindow(
                this.getClass().getName());
        setMainWindow(mainWindow);

        final Table table = new Table();
        table.addContainerProperty("column1", Component.class, null);
        VerticalLayout vl = new VerticalLayout();
        final LegacyTextField textField = new LegacyTextField();
        vl.addComponent(textField);

        table.addItem(new Object[] { vl }, 1);

        table.setSizeFull();
        mainWindow.addComponent(table);
    }

}
