package com.vaadin.tests.components.table;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.tests.fieldgroup.ComplexPerson;
import com.vaadin.ui.Button;
import com.vaadin.v7.ui.Table;

public class TableColumnWidthsAndSorting extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        final Table t = new Table();
        t.setContainerDataSource(ComplexPerson.createContainer(100));
        t.setVisibleColumns("firstName", "lastName", "age", "gender", "salary");
        t.setColumnWidth("firstName", 200);
        t.setColumnWidth("lastName", 200);
        t.setSelectable(true);
        addComponent(t);

        Button b = new Button("Sort according to gender", event -> t
                .sort(new Object[] { "gender" }, new boolean[] { true }));

        addComponent(b);
    }
}
