package com.vaadin.tests.components.treetable;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.TreeTable;

@Theme("valo")
public class MinimalWidthColumns extends AbstractTestUI {

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.components.AbstractTestUI#setup(com.vaadin.server.
     * VaadinRequest)
     */
    @Override
    protected void setup(VaadinRequest request) {
        TreeTable tt = new TreeTable();
        tt.addContainerProperty("Foo", String.class, "");
        tt.addContainerProperty("Bar", String.class, "");

        Object item1 = tt.addItem(new Object[] { "f", "Bar" }, null);
        Object item2 = tt.addItem(new Object[] { "Foo2", "Bar2" }, null);

        tt.setParent(item2, item1);

        tt.setColumnWidth("Foo", 0);
        tt.setColumnWidth("Bar", 50);
        tt.setWidth("300px");
        addComponent(tt);
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(15118);
    }

    @Override
    protected String getTestDescription() {
        return "There should be no 1px discrepancy between vertical borders in headers and rows";
    }

}
