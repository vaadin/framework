package com.vaadin.tests.containers;

import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.Sizeable;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class IndexedContainerFilteringTest extends TestBase {

    private Table table;
    private IndexedContainer container;
    private TextField filterString;
    private TextField position;
    private int nextToAdd = 1;
    private Label nextLabel;

    @Override
    protected String getDescription() {
        return "Adding items to a filtered IndexedContainer inserts the items at the wrong location.";
    }

    @Override
    protected Integer getTicketNumber() {
        return new Integer(2809);
    }

    @Override
    protected void setup() {
        table = new Table();
        container = (IndexedContainer) table.getContainerDataSource();

        table.setWidth(300, Sizeable.UNITS_PIXELS);
        table.setSelectable(true);
        table.setMultiSelect(false);
        table.addContainerProperty("column1", String.class, "test");

        for (int i = 0; i < 25; ++i) {
            table.addItem(new Object[] { "Item " + i }, "Item " + i);
        }

        VerticalLayout vl = new VerticalLayout();

        // activate & deactivate filtering
        filterString = new TextField("Filter string:", "1");
        vl.addComponent(filterString);

        final CheckBox cb = new CheckBox("Filter");
        cb.addListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                container.removeAllContainerFilters();
                if (((CheckBox) event.getProperty()).getValue()) {
                    container.addContainerFilter("column1", filterString
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
                        Item item = container.addItem("addItem() " + nextToAdd);
                        if (item != null) {
                            item.getItemProperty("column1").setValue(
                                    "addItem() " + nextToAdd);
                        }
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
                        String id = "addItemAfter() " + nextToAdd;
                        Item item = container.addItemAfter(selection, id);
                        if (item != null) {
                            item.getItemProperty("column1").setValue(id);
                            table.setValue(id);
                        } else {
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
                        String id = "addItemAt() " + nextToAdd;
                        Item item = container.addItemAt(index, id);
                        if (item != null) {
                            item.getItemProperty("column1").setValue(id);
                            table.setValue(id);
                        } else {
                            getMainWindow().showNotification(
                                    "Adding item at index "
                                            + position.getValue() + " failed");
                        }
                        nextToAdd++;
                        nextLabel.setCaption("Next id: " + nextToAdd);
                    }
                });
        addItemAtButton.setImmediate(true);
        vl.addComponent(addItemAtButton);

        getLayout().addComponent(table);
        getLayout().addComponent(vl);
    }
}
