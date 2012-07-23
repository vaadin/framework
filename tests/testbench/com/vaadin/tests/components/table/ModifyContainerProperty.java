package com.vaadin.tests.components.table;

import com.vaadin.data.util.IndexedContainer;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Table;

@SuppressWarnings("serial")
public class ModifyContainerProperty extends TestBase {

    private Table table = new Table();
    private IndexedContainer ic = new IndexedContainer();

    @Override
    protected void setup() {
        addComponent(table);

        ic.addContainerProperty("one", String.class, "one");
        ic.addContainerProperty("two", String.class, "two");

        ic.addItem("foo");

        ic.getContainerProperty("foo", "one").setValue("bar");
        ic.getContainerProperty("foo", "two").setValue("baz");

        table.setContainerDataSource(ic);
        addComponent(new Button("Remove container property",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(com.vaadin.ui.Button.ClickEvent arg0) {
                        ic.removeContainerProperty("one");
                    }
                }));
        addComponent(new Button("Add container property",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(com.vaadin.ui.Button.ClickEvent arg0) {
                        boolean added = ic.addContainerProperty("three",
                                String.class, "three");
                        if (added) {
                            Object[] current = table.getVisibleColumns();
                            Object[] vis = new Object[current.length + 1];
                            for (int i = 0; i < current.length; i++) {
                                vis[i] = current[i];
                            }
                            vis[current.length] = "three";
                            table.setVisibleColumns(vis);
                        }
                    }
                }));
    }

    @Override
    protected String getDescription() {
        return "Clicking on \"Add container property\" adds a property to the container and sets it visible. The table should then show a \"three\" column in addition to the others. Clicking on \"Remove container property\" should remove column \"two\" from the table.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3165;
    }
}
