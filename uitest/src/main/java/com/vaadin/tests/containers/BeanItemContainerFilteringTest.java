package com.vaadin.tests.containers;

import com.vaadin.server.Sizeable;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.TextField;

public class BeanItemContainerFilteringTest extends TestBase {

    private Table table;
    private BeanItemContainer<TestBean> container;
    private TextField filterString;
    private TextField position;
    private int nextToAdd = 1;
    private Label nextLabel;

    protected static class TestBean {
        private String id;
        private String value;

        public TestBean() {
        }

        public TestBean(String id, String value) {
            setId(id);
            setValue(value);
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    @Override
    protected String getDescription() {
        return "Test adding items in a filtered BeanItemContainer.";
    }

    @Override
    protected Integer getTicketNumber() {
        return new Integer(1061);
    }

    @Override
    protected void setup() {
        table = new Table();
        try {
            container = new BeanItemContainer<>(TestBean.class);
            table.setContainerDataSource(container);

            table.setWidth(300, Sizeable.UNITS_PIXELS);
            table.setSelectable(true);
            table.setMultiSelect(false);
            table.setEditable(true);
            table.setImmediate(true);
            // table.addContainerProperty("column1", String.class, "test");

            for (int i = 0; i < 25; ++i) {
                container.addItem(new TestBean("Item " + i, "Value for " + i));
            }

            VerticalLayout vl = new VerticalLayout();

            // activate & deactivate filtering
            filterString = new TextField("Filter string:", "1");
            vl.addComponent(filterString);

            final CheckBox cb = new CheckBox("Filter on value");
            cb.addValueChangeListener(event -> {
                container.removeAllContainerFilters();
                if (event.getValue()) {
                    container.addContainerFilter("value",
                            filterString.getValue(), false, false);
                }
            });
            vl.addComponent(cb);

            nextLabel = new Label();
            nextLabel.setCaption("Next id: " + nextToAdd);
            vl.addComponent(nextLabel);

            // addItemAt(idx), addItemAfter(selection), addItem()

            final Button addItemButton = new Button("addItem()", event -> {
                container.addItem(new TestBean("addItem() " + nextToAdd,
                        "value " + nextToAdd));
                nextToAdd++;
                nextLabel.setCaption("Next id: " + nextToAdd);
            });
            vl.addComponent(addItemButton);

            final Button addItemAfterButton = new Button("addItemAfter()",
                    event -> {
                        Object selection = table.getValue();
                        if (selection == null) {
                            return;
                        }
                        TestBean bean = new TestBean(
                                "addItemAfter() " + nextToAdd,
                                "value " + nextToAdd);
                        Item item = container.addItemAfter(selection, bean);
                        if (item == null) {
                            getMainWindow()
                                    .showNotification("Adding item after "
                                            + selection + " failed");
                        }
                        nextToAdd++;
                        nextLabel.setCaption("Next id: " + nextToAdd);
                    });
            vl.addComponent(addItemAfterButton);

            position = new TextField("Position:", "0");
            vl.addComponent(position);

            final Button addItemAtButton = new Button("addItemAt()", event -> {
                int index = Integer.parseInt(position.getValue());
                TestBean bean = new TestBean("addItemAt() " + nextToAdd,
                        "value " + nextToAdd);
                Item item = container.addItemAt(index, bean);
                if (item == null) {
                    getMainWindow().showNotification("Adding item at index "
                            + position.getValue() + " failed");
                }
                nextToAdd++;
                nextLabel.setCaption("Next id: " + nextToAdd);
            });
            vl.addComponent(addItemAtButton);

            getLayout().addComponent(table);
            getLayout().addComponent(vl);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
