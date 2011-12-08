/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.tests.components.treetable;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Table;
import com.vaadin.ui.TreeTable;

public class TreeTableOutOfSync extends TestBase {

    @Override
    protected void setup() {
        TreeTable tt = new TreeTable();
        tt.addContainerProperty("i", Integer.class, null);
        tt.addGeneratedColumn("text", new Table.ColumnGenerator() {
            public Object generateCell(Table source, Object itemId,
                    Object columnId) {
                if ("text".equals(columnId)) {
                    Button button = new Button("text "
                            + source.getContainerDataSource().getItem(itemId)
                                    .getItemProperty("i").getValue());
                    button.addListener(new Button.ClickListener() {
                        public void buttonClick(ClickEvent event) {
                            getMainWindow().showNotification("click");
                        }
                    });
                    return button;
                }
                return null;
            }
        });

        Object item1 = tt.addItem(new Object[] { 1 }, null);
        Object item2 = tt.addItem(new Object[] { 2 }, null);
        tt.addItem(new Object[] { 3 }, null);
        tt.setParent(item2, item1);

        addComponent(tt);
    }

    @Override
    protected String getDescription() {
        return "When a root node is expanded, components created by a column generator go out of sync";
    }

    @Override
    protected Integer getTicketNumber() {
        return 7620;
    }
}
