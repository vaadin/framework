package com.vaadin.tests.components.table;

import com.vaadin.data.Item;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.themes.BaseTheme;

public class TableRowHeight3 extends TestBase {

    @Override
    protected String getDescription() {
        return "All rows should be visible and the table height should match the height of the rows (no vertical scrollbar)";
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
        table.setWidth("320px");
        table.setPageLength(0);
        table.setColumnWidth("title", 200);
        table.setColumnWidth("test", 98);
        table.addContainerProperty("title", Button.class, "");
        table.addContainerProperty("test", Button.class, "");
        for (int i = 0; i < 6; i++) {
            Item item = table.addItem(new Object());

            Button b = new Button();
            b.setWidth("100%");
            b.setStyleName(BaseTheme.BUTTON_LINK);
            b.addStyleName("nowraplink");
            if (i < 2) {
                b.setCaption("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi ullamcorper, elit quis elementum iaculis, dui est rutrum risus, at cursus sem leo eget arcu. Proin vel eros ut tortor luctus pretium. Nulla facilisi. Donec in dui. Proin ac diam vitae massa tempus faucibus. Fusce eu risus. Nunc ac risus. Cras libero.");
            } else if (2 <= i && i < 4) {
                b.setCaption("One line");
            } else {
                b.setCaption("This button caption should use up two lines");
            }
            item.getItemProperty("title").setValue(b);

            Button c = new Button("test");
            item.getItemProperty("test").setValue(c);
        }

        vl.addComponent(table);

        addComponent(vl);

    }

}
