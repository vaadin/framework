package com.vaadin.tests.containers;

import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.Sizeable;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

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
            container = new BeanItemContainer<TestBean>(TestBean.class);
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
            cb.addListener(new ValueChangeListener() {

                @Override
                public void valueChange(ValueChangeEvent event) {
                    container.removeAllContainerFilters();
                    if (((CheckBox) event.getProperty()).getValue()) {
                        container.addContainerFilter("value", filterString
                                .getValue().toString(), false, false);
                    }
                }
            });
            cb.setImmediate(true);
            vl.addComponent(cb);

            nextLabel = new Label();
            nextLabel.setCaption("Next id: " + nextToAdd);
            vl.addComponent(nextLabel);

            // addItemAt(idx), addItemAfter(selection), addItem()

            final Button addItemButton = new Button("addItem()",
                    new Button.ClickListener() {
                        @Override
                        public void buttonClick(ClickEvent event) {
                            container.addItem(new TestBean("addItem() "
                                    + nextToAdd, "value " + nextToAdd));
                            nextToAdd++;
                            nextLabel.setCaption("Next id: " + nextToAdd);
                        }
                    });
            addItemButton.setImmediate(true);
            vl.addComponent(addItemButton);

            final Button addItemAfterButton = new Button("addItemAfter()",
                    new Button.ClickListener() {
                        @Override
                        public void buttonClick(ClickEvent event) {
                            Object selection = table.getValue();
                            if (selection == null) {
                                return;
                            }
                            TestBean bean = new TestBean("addItemAfter() "
                                    + nextToAdd, "value " + nextToAdd);
                            Item item = container.addItemAfter(selection, bean);
                            if (item == null) {
                                getMainWindow().showNotification(
                                        "Adding item after " + selection
                                                + " failed");
                            }
                            nextToAdd++;
                            nextLabel.setCaption("Next id: " + nextToAdd);
                        }
                    });
            addItemAfterButton.setImmediate(true);
            vl.addComponent(addItemAfterButton);

            position = new TextField("Position:", "0");
            vl.addComponent(position);

            final Button addItemAtButton = new Button("addItemAt()",
                    new Button.ClickListener() {
                        @Override
                        public void buttonClick(ClickEvent event) {
                            int index = Integer.parseInt(position.getValue()
                                    .toString());
                            TestBean bean = new TestBean("addItemAt() "
                                    + nextToAdd, "value " + nextToAdd);
                            Item item = container.addItemAt(index, bean);
                            if (item == null) {
                                getMainWindow().showNotification(
                                        "Adding item at index "
                                                + position.getValue()
                                                + " failed");
                            }
                            nextToAdd++;
                            nextLabel.setCaption("Next id: " + nextToAdd);
                        }
                    });
            addItemAtButton.setImmediate(true);
            vl.addComponent(addItemAtButton);

            getLayout().addComponent(table);
            getLayout().addComponent(vl);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
