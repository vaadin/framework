package com.vaadin.tests.components.table;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.v7.data.util.MethodProperty;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.TextField;

public class TablePageLengthUpdate extends TestBase {

    private Label pageLengthLabel;
    private Table table;

    @Override
    protected String getDescription() {
        return "When the height is set for a table, the pagelength should be updated according to what is actually displayed. The table pagelength is initially 100 and the height is 100px. After clicking update the pageLength label should display the correct value (?).";
    }

    @Override
    protected Integer getTicketNumber() {
        return 1623;
    }

    @Override
    protected void setup() {
        table = new Table();
        table.setWidth("400px");
        table.setHeight("100px");
        table.setPageLength(100);
        table.addContainerProperty("p1", String.class, null);
        table.addContainerProperty("p2", String.class, null);
        table.addContainerProperty("p3", String.class, null);

        for (int i = 0; i < 10; i++) {
            table.addItem(new Object[] { "a" + i, "b" + i, "c" + i }, "" + i);
        }

        addComponent(table);

        pageLengthLabel = new Label("");
        updatePageLengthLabel();
        addComponent(pageLengthLabel);

        Button updateButton = new Button("Update pageLength",
                event -> updatePageLengthLabel());
        addComponent(updateButton);

        TextField tableHeight = new TextField("Table height",
                new MethodProperty<String>(this, "tableHeight"));
        tableHeight.setImmediate(true);
        addComponent(tableHeight);
    }

    public String getTableHeight() {
        return "" + (int) table.getHeight()
                + table.getHeightUnits().getSymbol();
    }

    public void setTableHeight(String height) {
        table.setHeight(height);
    }

    protected void updatePageLengthLabel() {
        pageLengthLabel.setValue("Pagelength: " + table.getPageLength());
    }

}
