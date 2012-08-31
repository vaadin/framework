package com.vaadin.tests.components.select;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Select;

public class NullSelectionItemId extends TestBase implements ClickListener {

    private static final String NULL_ITEM_ID = "Null item id";

    private Select mySelect;

    @Override
    protected void setup() {

        mySelect = new Select("My Select");

        // add items
        mySelect.addItem(NULL_ITEM_ID);
        mySelect.addItem("Another item");

        // allow null and set the null item id
        mySelect.setNullSelectionAllowed(true);
        mySelect.setNullSelectionItemId(NULL_ITEM_ID);

        // select the null item
        mySelect.select(NULL_ITEM_ID);

        Button button = new Button("Show selected value", this);

        addComponent(mySelect);
        addComponent(button);

        button = new Button(
                "Select null with select(NULL_ITEM_ID) (should make value null)",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        mySelect.select(NULL_ITEM_ID);
                    }
                });

        addComponent(button);

        button = new Button(
                "Select null with setValue(null) (should make value null)",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        mySelect.setValue(null);
                    }
                });

        addComponent(button);

    }

    @Override
    public void buttonClick(ClickEvent event) {
        getMainWindow().showNotification(
                "mySelect.getValue() returns: " + mySelect.getValue());
    }

    @Override
    protected String getDescription() {
        return "Steps to reproduce:<br />"
                + "<ol><li>Click the button -> value is the item id \"Null item id\" (should be null).</li>"
                + "<li>Select the \"Another item\".</li>"
                + "<li>Select back the first item.</li>"
                + "<li>Click the button -> the value is null (as it should)</li></ol>";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3203;
    }

}
