package com.vaadin.tests.components.table;

import com.vaadin.data.Item;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;

public class TableRowHeight2 extends TestBase {

    private static final int TABLE_EXTRA = 2; // borders
    private static final int COLEXTRASPACE = 6; // cell margins by theme

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

        int COL_TITLE_W = 200;
        int COL_TEST_W = 98;

        table
                .setWidth((COL_TEST_W + COL_TITLE_W + 2 * COLEXTRASPACE + TABLE_EXTRA)
                        + "px");
        table.setPageLength(0);
        table.setColumnWidth("title", COL_TITLE_W);
        table.setColumnWidth("test", COL_TEST_W);
        table.addContainerProperty("title", Button.class, "");
        table.addContainerProperty("test", Button.class, "");
        for (int i = 0; i < 2; i++) {
            Item item = table.addItem(new Object());

            Button b = new Button();
            b.setWidth("100%");
            b.setStyleName(Button.STYLE_LINK);
            b.addStyleName("nowraplink");

            b
                    .setCaption("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi ullamcorper, elit quis elementum iaculis, dui est rutrum risus, at cursus sem leo eget arcu. Proin vel eros ut tortor luctus pretium. Nulla facilisi. Donec in dui. Proin ac diam vitae massa tempus faucibus. Fusce eu risus. Nunc ac risus. Cras libero.");

            item.getItemProperty("title").setValue(b);

            Button c = new Button("test");
            item.getItemProperty("test").setValue(c);
        }

        vl.addComponent(table);

        addComponent(vl);
    }
}
