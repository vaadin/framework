package com.vaadin.tests.components.table;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.AbstractSelect.ItemDescriptionGenerator;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;

public class TableItemDescriptionGeneratorUI extends AbstractTestUI {

    private final String TEXT_PROPERTY_ID = "Text";
    private final String GEN_WIDGET_PROPERTY_ID = "Generated component";
    private final String WIDGET_PROPERTY_ID = "Component";
    private CheckBox componentDescription;
    private CheckBox tableCellItemDescription;
    private CheckBox tableRowItemDescription;

    @Override
    protected void setup(VaadinRequest request) {
        final Table table = createTable();
        table.setId("table");
        componentDescription = new CheckBox("Tooltip on components");
        componentDescription.addValueChangeListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                table.setContainerDataSource(createContainer(componentDescription
                        .getValue()));
            }
        });
        componentDescription.setImmediate(true);
        componentDescription.setValue(true);
        tableCellItemDescription = new CheckBox("Tooltip on table cells");
        tableCellItemDescription
                .addValueChangeListener(new ValueChangeListener() {

                    @Override
                    public void valueChange(ValueChangeEvent event) {
                        table.refreshRowCache();
                    }
                });
        tableCellItemDescription.setImmediate(true);
        tableCellItemDescription.setValue(true);

        tableRowItemDescription = new CheckBox("Tooltip on table Rows");
        tableRowItemDescription
                .addValueChangeListener(new ValueChangeListener() {

                    @Override
                    public void valueChange(ValueChangeEvent event) {
                        table.refreshRowCache();
                    }
                });
        tableRowItemDescription.setImmediate(true);
        tableRowItemDescription.setValue(true);

        addComponent(componentDescription);
        addComponent(tableCellItemDescription);
        addComponent(tableRowItemDescription);
        addComponent(table);

        table.setItemDescriptionGenerator(new ItemDescriptionGenerator() {
            @Override
            public String generateDescription(Component source, Object itemId,
                    Object propertyId) {
                if (propertyId == null && tableRowItemDescription.getValue()) {
                    return "Row description " + itemId;
                } else if (tableCellItemDescription.getValue()) {
                    return "Cell description " + itemId + ", " + propertyId;
                }
                return null;
            }
        });

        table.addGeneratedColumn(GEN_WIDGET_PROPERTY_ID,
                new Table.ColumnGenerator() {

                    @Override
                    public Component generateCell(Table source, Object itemId,
                            Object columnId) {
                        TextField lbl = new TextField();
                        if (componentDescription.getValue()) {
                            lbl.setDescription("Textfield's own description");
                        }
                        return lbl;
                    }
                });

    }

    protected Table createTable() {
        return new Table();
    }

    @Override
    protected String getTestDescription() {
        return "Cells and rows should have tooltips";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5414;
    }

    @SuppressWarnings("unchecked")
    private Container createContainer(boolean description) {
        IndexedContainer container = new IndexedContainer();
        container.addContainerProperty(TEXT_PROPERTY_ID, String.class, "");
        container.addContainerProperty(WIDGET_PROPERTY_ID, Component.class,
                null);

        for (int i = 0; i < 5; i++) {
            Item item = container.addItem("item " + i);
            item.getItemProperty(TEXT_PROPERTY_ID).setValue("Text " + i);
            Button b = new Button("Button " + i);
            if (description) {
                b.setDescription("Button " + i + " description");
            }
            item.getItemProperty(WIDGET_PROPERTY_ID).setValue(b);
        }

        return container;
    }

}
