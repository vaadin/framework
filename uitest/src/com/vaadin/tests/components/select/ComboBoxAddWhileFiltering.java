package com.vaadin.tests.components.select;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;

/**
 * TODO can't reproduce the issue with this test case, possibly need some
 * enhancements.
 * 
 */
public class ComboBoxAddWhileFiltering extends TestBase {

    private int i;

    @Override
    protected void setup() {
        final ComboBox comboBox = new ComboBox();
        populate(comboBox);

        Button b = new Button("add item (^N)");
        b.addListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                addItem(comboBox);
            }
        });
        addComponent(b);
        addComponent(comboBox);
        getMainWindow().addAction(new Button.ClickShortcut(b, "^n"));
    }

    private void populate(ComboBox comboBox) {
        for (i = 0; i < 4;) {
            addItem(comboBox);
        }
    }

    private void addItem(ComboBox comboBox) {
        i++;
        comboBox.addItem("Item " + i);

    }

    @Override
    protected String getDescription() {
        return "Filtered list should be updated when new item is added.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3643;
    }

}
