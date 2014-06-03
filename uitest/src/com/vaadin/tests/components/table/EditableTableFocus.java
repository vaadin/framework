package com.vaadin.tests.components.table;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Table;

public class EditableTableFocus extends TestBase {
    @Override
    public void setup() {
        Table table = new Table();

        table.addContainerProperty("TextField", String.class, null);
        table.setColumnWidth("TextField", 150);
        for (int i = 1; i < 100; i++) {
            table.addItem(new String[] { "" }, new Integer(i));
        }
        table.setEditable(true);

        addComponent(table);
    }

    @Override
    protected String getDescription() {
        return "<b>IE-Problem: TextFields in table lose their focus, no input possible</b><p>"
                + "Try inputs in the table's textfields in the freshly started programm. For the moment all works fine.<p>"
                + "Then scroll the table down one page or more.<br>"
                + "Try again to make some inputs. Nothing happens...<br>"
                + "Now the textfields always lose their focus immediately after they got it and no input is taken.<p>"
                + "<b>This problem is exclusive to Microsoft's Internet Explorer!</b>";
    }

    @Override
    protected Integer getTicketNumber() {
        return 7965;
    }
}
