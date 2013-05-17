package com.vaadin.tests.components.treetable;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.VerticalLayout;

public class TreeTableInternalError extends TestBase {
    private TreeTable t;

    @Override
    protected void setup() {
        VerticalLayout content = getLayout();
        content.setSizeFull();

        t = new TreeTable() {
            {
                setSizeFull();
                fillTreeTable(this);
            }
        };
        t.setId("treetable");
        content.addComponent(t);
        content.setExpandRatio(t, 1);

        Button button = new Button("Resize") {
            {
                addClickListener(new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        t.setHeight("300px");
                    }
                });
            }
        };
        button.setId("resize");
        content.addComponent(button);
    }

    @Override
    protected String getDescription() {
        return "Internal Error when scrolling down enough that more rows are loaded (cache updated), then scrolling down just a few rows and expanding rows";
    }

    @Override
    protected Integer getTicketNumber() {
        return 10057;
    }

    private void fillTreeTable(TreeTable t) {
        t.addContainerProperty("name", String.class, null);
        t.addGeneratedColumn("toggle", new ButtonColumnGenerator());
        for (int i = 0; i < 1000; i++) {
            t.addItem(i);
            t.getContainerProperty(i, "name").setValue("Item-" + i);
            t.addItem(i + "c");
            t.getContainerProperty(i + "c", "name").setValue("Child-" + i);
            t.setParent(i + "c", i);
            t.setChildrenAllowed(i + "c", false);
        }
    }

    public class ButtonColumnGenerator implements ColumnGenerator {
        @Override
        public Component generateCell(final com.vaadin.ui.Table source,
                final Object itemId, Object columnId) {
            String identifier = "Expand/Collapse";
            Button btnCol = new NativeButton(identifier);
            btnCol.setId("cacheTestButtonToggle-" + itemId);
            btnCol.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    t.setCollapsed(itemId, !t.isCollapsed(itemId));
                }
            });
            return btnCol;
        }

    }

}
