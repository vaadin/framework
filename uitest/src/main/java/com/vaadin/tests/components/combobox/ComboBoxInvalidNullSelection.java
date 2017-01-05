package com.vaadin.tests.components.combobox;

import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;

public class ComboBoxInvalidNullSelection extends TestBase {

    private boolean biggerData = true;
    private ComboBox<String> combo;
    private Log log = new Log(5);

    @Override
    protected void setup() {

        Button b = new Button("Swap data provider");
        b.addClickListener(event -> {
            if (biggerData) {
                combo.setItems("Item 3");
            } else {
                combo.setItems("Item 1", "Item 2", "Item 3", "Item 4");
            }
            biggerData = !biggerData;
        });

        combo = new ComboBox<>();
        combo.setItems("Item 1", "Item 2", "Item 3", "Item 4");
        combo.addValueChangeListener(
                event -> log.log("Value is now: " + combo.getValue()));
        addComponent(log);
        addComponent(b);
        addComponent(combo);
        addComponent(new Button("Dummy for TestBench"));
    }

    @Override
    protected String getDescription() {
        return "Select \"Item 3\" in the ComboBox, change the data provider, focus and blur the ComboBox. The value should temporarily change to null when changing data provider but not when focusing and blurring the ComboBox";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6170;
    }

}
