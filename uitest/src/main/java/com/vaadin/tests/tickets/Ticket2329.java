package com.vaadin.tests.tickets;

import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.VerticalLayout;

public class Ticket2329 extends LegacyApplication {
    private Table table;
    private VerticalLayout mainLo;

    @Override
    public void init() {
        LegacyWindow mainw = new LegacyWindow();
        setMainWindow(mainw);
        mainLo = (VerticalLayout) mainw.getContent();
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
        @Override
        public Component generateCell(Table source, Object rowId,
                Object columnId) {
            return new Button("1");
        }
    }
}
