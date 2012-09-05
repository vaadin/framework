package com.vaadin.tests.components.table;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;

public class TestCurrentPageFirstItem extends TestBase implements ClickListener {

    private Button buttonIndex;
    private Button buttonItem;
    private Table[] tables = new Table[4];
    private int counter = 0;
    IndexedContainer container = new IndexedContainer();

    @Override
    public void setup() {
        container.addContainerProperty("row", String.class, "");

        HorizontalLayout baseLayout = new HorizontalLayout();
        baseLayout.setHeight("115px");
        getMainWindow().setContent(baseLayout);

        for (int i = 0; i < tables.length; ++i) {
            Table t = new Table();
            t.setContainerDataSource(container);
            t.setWidth("100px");
            baseLayout.addComponent(t);
            tables[i] = t;
        }
        tables[0].setSizeFull();
        tables[0].setCaption("Full");
        tables[1].setHeight("100px");
        tables[1].setCaption("100px");
        tables[2].setHeight("95%");
        tables[2].setCaption("95%");
        tables[3].setPageLength(3);
        tables[3].setCaption("3 rows");

        buttonIndex = new Button("Add row and select last index", this);
        buttonItem = new Button("Add row and select last item", this);
        baseLayout.addComponent(buttonIndex);
        baseLayout.addComponent(buttonItem);
    }

    @Override
    public void buttonClick(ClickEvent event) {
        Item item = container.addItem(++counter);
        item.getItemProperty("row").setValue(counter + "");
        for (int i = 0; i < tables.length; ++i) {
            Table t = tables[i];
            t.select(counter);
            if (event.getButton() == buttonIndex) {
                t.setCurrentPageFirstItemIndex(((Container.Indexed) t
                        .getContainerDataSource()).indexOfId(counter));
            } else {
                t.setCurrentPageFirstItemId(counter);
            }
        }
    }

    @Override
    protected String getDescription() {
        return "Table height changes when using setCurrentPageFirstItemId";
    }

    @Override
    protected Integer getTicketNumber() {
        return 2864;
    }
}
