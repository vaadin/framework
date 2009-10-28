package com.vaadin.tests.components.table;

import com.vaadin.Application;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

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
