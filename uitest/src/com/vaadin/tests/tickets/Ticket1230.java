package com.vaadin.tests.tickets;

import com.vaadin.data.Item;
import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.Select;

public class Ticket1230 extends LegacyApplication {

    private static final Object PROPERTY_ID = new Object();
    private static final Object NULL_ITEM_ID = new Object();
    private Select selectWithoutNullItem;
    private Select selectWithNullItem;

    @Override
    public void init() {
        LegacyWindow w = new LegacyWindow(getClass().getName());
        setMainWindow(w);

        GridLayout layout = new GridLayout(5, 5);
        w.setContent(layout);

        layout.setSpacing(true);

        {
            selectWithoutNullItem = createSelect();

            layout.addComponent(selectWithoutNullItem);
            Button b = new Button("Select NULL_PROPERTY", new ClickListener() {

                @Override
                public void buttonClick(ClickEvent event) {
                    selectWithoutNullItem.select(NULL_ITEM_ID);
                    printState();

                }
            });
            layout.addComponent(b);
            b = new Button("Select 1", new ClickListener() {

                @Override
                public void buttonClick(ClickEvent event) {
                    selectWithoutNullItem.select("1");
                    printState();

                }
            });
            layout.addComponent(b);
            b = new Button("Select 2", new ClickListener() {

                @Override
                public void buttonClick(ClickEvent event) {
                    selectWithoutNullItem.select("2");
                    printState();

                }
            });
            layout.addComponent(b);

            b = new Button("Select null", new ClickListener() {

                @Override
                public void buttonClick(ClickEvent event) {
                    selectWithoutNullItem.select(null);
                    printState();

                }
            });
            layout.addComponent(b);
        }

        {
            selectWithNullItem = createSelect();
            Item nullItem = selectWithNullItem.addItem(NULL_ITEM_ID);
            nullItem.getItemProperty(PROPERTY_ID).setValue("NULL");
            selectWithNullItem.setNullSelectionItemId(NULL_ITEM_ID);

            layout.addComponent(selectWithNullItem);
            selectWithNullItem.setCaption("Select with null item id");
            Button b = new Button("Select NULL_PROPERTY", new ClickListener() {

                @Override
                public void buttonClick(ClickEvent event) {
                    selectWithNullItem.select(NULL_ITEM_ID);
                    printState();

                }
            });
            layout.addComponent(b);

            b = new Button("Select 1", new ClickListener() {

                @Override
                public void buttonClick(ClickEvent event) {
                    selectWithNullItem.select("1");
                    printState();

                }
            });
            layout.addComponent(b);
            b = new Button("Select 2", new ClickListener() {

                @Override
                public void buttonClick(ClickEvent event) {
                    selectWithNullItem.select("2");
                    printState();
                }
            });
            layout.addComponent(b);

            b = new Button("Select null", new ClickListener() {

                @Override
                public void buttonClick(ClickEvent event) {
                    selectWithNullItem.select(null);
                    printState();
                }
            });
            layout.addComponent(b);

        }

        w.addComponent(new Button("print select values",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        printState();
                    }
                }));
    }

    @SuppressWarnings("deprecation")
    private Select createSelect() {
        Select select = new Select();
        select.addContainerProperty(PROPERTY_ID, String.class, "");
        select.setItemCaptionPropertyId(PROPERTY_ID);

        Item item1 = select.addItem("1");
        item1.getItemProperty(PROPERTY_ID).setValue("1");
        Item item2 = select.addItem("2");
        item2.getItemProperty(PROPERTY_ID).setValue("2");

        select.setNullSelectionAllowed(true);

        return select;
    }

    void printState() {
        System.out.println(" Select without null item "
                + selectWithoutNullItem.getValue());
        System.out.println(" Select with null item "
                + selectWithNullItem.getValue());

    }

}
