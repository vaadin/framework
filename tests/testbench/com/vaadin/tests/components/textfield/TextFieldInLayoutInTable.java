package com.vaadin.tests.components.textfield;

import com.vaadin.Application;
import com.vaadin.ui.Component;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class TextFieldInLayoutInTable extends Application {

    @Override
    public void init() {

        final Window mainWindow = new Window(this.getClass().getName());
        setMainWindow(mainWindow);

        final Table table = new Table();
        table.addContainerProperty("column1", Component.class, null);
        final Panel panel = new Panel("Panel");
        ((VerticalLayout) panel.getContent()).setMargin(false);
        VerticalLayout vl = new VerticalLayout();
        final TextField textField = new TextField();
        vl.addComponent(textField);

        table.addItem(new Object[] { vl }, 1);

        table.setSizeFull();
        mainWindow.addComponent(table);
    }

}
