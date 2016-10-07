package com.vaadin.tests.components.table;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.v7.data.Container;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.IndexedContainer;
import com.vaadin.v7.ui.AbstractSelect.ItemDescriptionGenerator;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.TextField;

public class TableItemDescriptionGeneratorUI extends AbstractReindeerTestUI {

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
        componentDescription
                .addValueChangeListener(event -> table.setContainerDataSource(
                        createContainer(componentDescription.getValue())));
        componentDescription.setValue(true);
        tableCellItemDescription = new CheckBox("Tooltip on table cells");
        tableCellItemDescription
                .addValueChangeListener(event -> table.refreshRowCache());
        tableCellItemDescription.setValue(true);

        tableRowItemDescription = new CheckBox("Tooltip on table Rows");
        tableRowItemDescription
                .addValueChangeListener(event -> table.refreshRowCache());
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
