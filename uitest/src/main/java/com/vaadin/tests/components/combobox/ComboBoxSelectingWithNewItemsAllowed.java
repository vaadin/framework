package com.vaadin.tests.components.combobox;

import com.vaadin.data.provider.Query;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Label;

public class ComboBoxSelectingWithNewItemsAllowed extends ComboBoxSelecting {

    @Override
    protected void setup(VaadinRequest request) {
        super.setup(request);
        final Label label = new Label(String.valueOf(items.size()));
        label.setCaption("Item count:");
        label.setId("count");

        comboBox.setNewItemHandler(text -> {
            items.add(text);
            comboBox.setItems(items);
            comboBox.setValue(text);
            label.setValue(String.valueOf(items.size()));
        });

        comboBox.addValueChangeListener(event -> label.setValue(
                String.valueOf(comboBox.getDataProvider().size(new Query()))));
        addComponent(label);
    }

    @Override
    protected String getTestDescription() {
        return "ComboBox should select value on TAB also when new items are allowed.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 9369;
    }
}
