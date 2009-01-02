package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.Table;
import com.itmill.toolkit.ui.VerticalLayout;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Table.ColumnGenerator;

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
