package com.itmill.toolkit.tests.components.table;

import com.itmill.toolkit.data.Item;
import com.itmill.toolkit.tests.components.TestBase;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.HorizontalLayout;
import com.itmill.toolkit.ui.Table;

public class TableRowHeight2 extends TestBase {

    @Override
    protected String getDescription() {
        return "The table contains 2 rows, which both should be shown completely as the table height is undefined.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 2747;
    }

    @Override
    protected void setup() {
        setTheme("tests-tickets");
        HorizontalLayout vl = new HorizontalLayout();
        vl.setSizeFull();

        Table table = new Table();
        table.setWidth("300px");
        table.setPageLength(0);
        table.setColumnWidth("title", 200);
        table.setColumnWidth("test", 98);
        table.addContainerProperty("title", Button.class, "");
        table.addContainerProperty("test", Button.class, "");
        for (int i = 0; i < 2; i++) {
            Item item = table.addItem(new Object());

            Button b = new Button();

            b
                    .setCaption("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi ullamcorper, elit quis elementum iaculis, dui est rutrum risus, at cursus sem leo eget arcu. Proin vel eros ut tortor luctus pretium. Nulla facilisi. Donec in dui. Proin ac diam vitae massa tempus faucibus. Fusce eu risus. Nunc ac risus. Cras libero.");

            b.setStyleName(Button.STYLE_LINK);
            item.getItemProperty("title").setValue(b);

            Button c = new Button("test");
            item.getItemProperty("test").setValue(c);
        }

        vl.addComponent(table);

        addComponent(vl);
    }
}
