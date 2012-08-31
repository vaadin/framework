package com.vaadin.tests.components.table;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

public class ContainerChangeWithPartlySamePropertyIds extends TestBase {

    @Override
    protected void setup() {
        getLayout().addComponent(new TableTestComponent());
    }

    @Override
    protected String getDescription() {
        return "The client side Table component messes up its internal "
                + "data structures (in header and footer) if the container changes and it has partly"
                + " the same properties (but in different order) than the old container.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6281;
    }

    public static class TableTestComponent extends VerticalLayout {

        final TableTestComponent me = this;

        Table testTable;
        IndexedContainer containerA;
        IndexedContainer containerB;

        String property1 = "property1";
        String property2 = "property2";
        String property3 = "property3";
        String property4 = "property4";

        private void createContainers() {

            containerA = new IndexedContainer();
            containerA.addContainerProperty(property1, String.class, "");
            containerA.addContainerProperty(property2, String.class, "");
            containerA.addContainerProperty(property3, String.class, "");

            Item itemA = containerA.addItem(new Object());
            itemA.getItemProperty(property1).setValue("value1");
            itemA.getItemProperty(property2).setValue("value2");
            itemA.getItemProperty(property3).setValue("value3");

            containerB = new IndexedContainer();
            containerB.addContainerProperty(property4, String.class, "");
            containerB.addContainerProperty(property3, String.class, "");
            containerB.addContainerProperty(property2, String.class, "");

            Item itemB = containerB.addItem(new Object());
            itemB.getItemProperty(property4).setValue("value_prop4");
            itemB.getItemProperty(property3).setValue("value_prop3");
            itemB.getItemProperty(property2).setValue("value_prop2");
        }

        public TableTestComponent() {

            Button switchContainerButton = new Button("switch container");
            switchContainerButton.addListener(new ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    if (testTable.getContainerDataSource() == containerA) {
                        testTable.setContainerDataSource(containerB);
                    } else {
                        testTable.setContainerDataSource(containerA);
                    }
                }
            });
            this.addComponent(switchContainerButton);

            Button clearButton = new Button("clear (click twice)");
            clearButton.addListener(new ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    try {
                        me.removeComponent(testTable);

                        testTable = new Table();
                        createContainers();
                        testTable.setContainerDataSource(containerA);

                        me.addComponent(testTable);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            this.addComponent(clearButton);

            testTable = new Table();
            this.addComponent(testTable);

            createContainers();
            testTable.setContainerDataSource(containerA);
        }
    }

}
