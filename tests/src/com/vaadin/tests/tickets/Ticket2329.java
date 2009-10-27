package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Table.ColumnGenerator;

public class Ticket2329 extends Application {
    private Table table;
    private VerticalLayout mainLo;

    @Override
    public void init() {
        Window mainw = new Window();
        setMainWindow(mainw);
        mainLo = (VerticalLayout) mainw.getLayout();
        table = new Table();
        for (int i = 0; i < 10000; i++) {
            table.addItem(i);
        }
        TestColumnGenerator cgen = new TestColumnGenerator();
        table.addGeneratedColumn("col1", cgen);
        table.addGeneratedColumn("col2", cgen);
        table.addGeneratedColumn("col3", cgen);
        table.addGeneratedColumn("col4", cgen);
        table.addGeneratedColumn("col5", cgen);
        table.addGeneratedColumn("col6", cgen);
        table.addGeneratedColumn("col7", cgen);
        table.setHeight("500px");
        mainLo.addComponent(table);
    }

    class TestColumnGenerator implements ColumnGenerator {
        public Component generateCell(Table source, Object rowId,
                Object columnId) {
            return new Button("1");
        }
    }
}
