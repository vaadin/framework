package com.itmill.toolkit.tests.components.table;

import com.itmill.toolkit.tests.components.TestBase;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.Table;

public class ColumnGeneratorAddingOrder extends TestBase {

    @Override
    protected String getDescription() {
        return "Column generator must be allowed to be added both before and after data source setting and overriding should work. Bugs in 5.3-rc7 if added after DS.";
    }

    @Override
    protected void setup() {
        Table t = new Table();

        t.addGeneratedColumn("col2", new Table.ColumnGenerator() {
            public Component generateCell(Table source, Object itemId,
                    Object columnId) {
                return new Button("generated b c2");
            }
        });

        t.addContainerProperty("col1", String.class, "col1 ds data");
        t.addContainerProperty("col2", String.class, "col2 ds data");
        t.addContainerProperty("col3", String.class, "col3 ds data");
        for (int i = 0; i < 100; i++) {
            t.addItem();
        }

        t.addGeneratedColumn("col1", new Table.ColumnGenerator() {
            public Component generateCell(Table source, Object itemId,
                    Object columnId) {
                return new Button("generated b c1");
            }
        });

        getLayout().addComponent(t);

    }

}
