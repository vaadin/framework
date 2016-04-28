package com.vaadin.tests.components.tabsheet;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextArea;

public class VerticalScrollbarPosition extends TestBase {

    @Override
    protected String getDescription() {
        return "A vertical scrollbar in a TabSheet should always be placed at the right edge";
    }

    @Override
    protected Integer getTicketNumber() {
        return 2473;
    }

    @Override
    protected void setup() {
        TabSheet tabsheet = new TabSheet();
        tabsheet.setWidth(null);
        tabsheet.setHeight("200px");
        TextArea tf = new TextArea();
        tf.setRows(2);
        tf.setHeight("300px");
        tf.setWidth("200px");
        tabsheet.addTab(
                tf,
                "A text area that is 200px wide, the tab bar for the tabsheet is wider",
                null);
        TextArea tf2 = new TextArea("Another tab", "b");
        tf2.setWidth("1000px");
        tf2.setHeight("50px");
        tabsheet.addTab(tf2);
        addComponent(tabsheet);
    }

}
