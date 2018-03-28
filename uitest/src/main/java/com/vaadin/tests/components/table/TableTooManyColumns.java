package com.vaadin.tests.components.table;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.Table.ColumnGenerator;

public class TableTooManyColumns extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Table table = new Table();

        table.setColumnCollapsingAllowed(true);

        for (int i = 0; i < 91; i++) {
            table.addGeneratedColumn("COLUMN " + i, new ColumnGenerator() {

                @Override
                public Object generateCell(Table source, Object itemId,
                        Object columnId) {
                    return columnId;
                }
            });
        }

        addComponent(table);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#getTestDescription()
     */
    @Override
    protected String getTestDescription() {
        return "Table column drop down becomes too large to fit the screen.";
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#getTicketNumber()
     */
    @Override
    protected Integer getTicketNumber() {
        return 14156;
    }

}
