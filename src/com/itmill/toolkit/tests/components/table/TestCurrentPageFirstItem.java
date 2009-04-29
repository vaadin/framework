package com.itmill.toolkit.tests.components.table;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.data.Container;
import com.itmill.toolkit.data.Item;
import com.itmill.toolkit.data.util.IndexedContainer;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Table;
import com.itmill.toolkit.ui.VerticalLayout;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;
import com.itmill.toolkit.ui.Button.ClickListener;

public class TestCurrentPageFirstItem extends Application implements
        ClickListener {

    private Button buttonIndex;
    private Button buttonItem;
    private Table table;
    private int counter = 0;
    IndexedContainer container = new IndexedContainer();

    @Override
    public void init() {
        try {
            Window main = new Window("Table header Test");
            setMainWindow(main);
            main.setSizeFull();
            // setTheme("testtheme");
            VerticalLayout baseLayout = new VerticalLayout();
            main.setLayout(baseLayout);

            table = new Table();
            container.addContainerProperty("row", String.class, "");
            table.setContainerDataSource(container);
            table.setWidth("100%");
            table.setPageLength(3);
            buttonIndex = new Button("Add row and select last index", this);
            buttonItem = new Button("Add row and select last item", this);

            baseLayout.addComponent(table);
            baseLayout.addComponent(buttonIndex);
            baseLayout.addComponent(buttonItem);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void buttonClick(ClickEvent event) {
        Item item = container.addItem(++counter);
        item.getItemProperty("row").setValue(counter + "");
        table.select(counter);
        if (event.getButton() == buttonIndex) {
            table.setCurrentPageFirstItemIndex(((Container.Indexed) table
                    .getContainerDataSource()).indexOfId(counter));
        } else {
            table.setCurrentPageFirstItemId(counter);
        }
    }
}
